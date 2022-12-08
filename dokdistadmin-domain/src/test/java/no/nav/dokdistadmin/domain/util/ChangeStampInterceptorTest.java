package no.nav.dokdistadmin.domain.util;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import no.nav.dokdistadmin.domain.ChangeStamp;
import no.nav.dokdistadmin.domain.DistribusjonInfo;

import org.hibernate.type.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;

/**
 * Tests of ChangeStampInterceptor.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangeStampInterceptorTest {

	private static final String USER = "test";

	@Mock
	private Type changeStampType;
	
	DistribusjonInfo entity = new DistribusjonInfo(1L, 1L);
	
	private ChangeStampInterceptor interceptor = new ChangeStampInterceptor();
	
	@Before
	public void setUp() {
		when(changeStampType.getReturnedClass()).thenReturn(ChangeStamp.class);
		MDC.put(Constants.USER_ID, USER);
	}
	
	@Test
	public void shouldCreateChangeStampOnSave() throws Exception {
		Object[] state = new Object[1];
		interceptor.onSave(entity, null, state, null, new Type[] { changeStampType });
		
		ChangeStamp changeStamp = (ChangeStamp) state[0];
		assertThat(changeStamp.getOpprettetAv(), is(USER));
		assertThat(changeStamp.getOpprettetDato(), is(notNullValue()));
	}

	@Test
	public void shouldUpdateChangeStampOnUpdate() throws Exception {
		ChangeStamp changeStamp = new ChangeStamp("Other user");
		Object[] currentState = new Object[] { changeStamp };
		
		interceptor.onFlushDirty(entity, null, currentState, null, null, new Type[] { changeStampType });
		
		assertThat(changeStamp.getEndretAv(), is(USER));
		assertThat(changeStamp.getEndretDato(), is(notNullValue()));
	}

}
