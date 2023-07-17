package no.nav.dokdistadmin.domain.util;

import no.nav.dokdistadmin.domain.AbstractDomainObject;
import no.nav.dokdistadmin.domain.ChangeStamp;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.slf4j.MDC;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

public class ChangeStampInterceptor implements Interceptor {

	@Override
	public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
		return updateChangeStamp(entity, currentState, types);
	}

	@Override
	public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		return createChangeStamp(entity, state, types);
	}

	private static boolean updateChangeStamp(final Object entity, final Object[] currentState, final Type[] types) {
		if (entity instanceof AbstractDomainObject) {
			for (int i = 0; i < currentState.length; i++) {
				Type type = types[i];
				if (type.getReturnedClass().equals(ChangeStamp.class)) {
					ChangeStamp current = (ChangeStamp) currentState[i];
					if (current == null) {
						throw new UnsupportedOperationException("No ChangeStamp to update");
					}
					current.updatedBy(getUserId());
					return true;
				}
			}
		}
		return false;
	}

	private static boolean createChangeStamp(final Object entity, final Object[] state, final Type[] types) {
		if (entity instanceof AbstractDomainObject) {
			for (int i = 0; i < state.length; i++) {
				Type type = types[i];
				if (type.getReturnedClass().equals(ChangeStamp.class)) {
					state[i] = new ChangeStamp(getUserId());
					return true;
				}
			}
		}
		return false;
	}

	private static String getUserId() {
		return MDC.get(USER_ID);
	}

}
