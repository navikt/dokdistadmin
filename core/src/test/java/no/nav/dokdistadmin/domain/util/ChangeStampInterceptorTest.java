package no.nav.dokdistadmin.domain.util;

import no.nav.dokdistadmin.domain.ChangeStamp;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import org.hibernate.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChangeStampInterceptorTest {

	private static final String USER = "test";

	@Mock
	private Type changeStampType;
	
	DistribusjonInfo entity = new DistribusjonInfo(1L, 1L);
	
	private final ChangeStampInterceptor interceptor = new ChangeStampInterceptor();
	
	@BeforeEach
	public void setUp() {
		when(changeStampType.getReturnedClass()).thenReturn(ChangeStamp.class);
		MDC.put(USER_ID, USER);
	}
	
	@Test
	public void shouldCreateChangeStampOnSave() {
		Object[] state = new Object[1];
		interceptor.onSave(entity, null, state, null, new Type[] { changeStampType });
		
		ChangeStamp changeStamp = (ChangeStamp) state[0];
		assertEquals(USER, changeStamp.getOpprettetAv());
		assertNotNull(changeStamp.getOpprettetDato());
	}

	@Test
	public void shouldUpdateChangeStampOnUpdate() {
		ChangeStamp changeStamp = new ChangeStamp("Other user");
		Object[] currentState = new Object[] { changeStamp };
		
		interceptor.onFlushDirty(entity, null, currentState, null, null, new Type[] { changeStampType });
		
		assertEquals(USER, changeStamp.getEndretAv());
		assertNotNull(changeStamp.getEndretDato());
	}

}
