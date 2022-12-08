package no.nav.dokdistadmin.domain.util;

import java.io.Serializable;

import no.nav.dokdistadmin.domain.AbstractDomainObject;
import no.nav.dokdistadmin.domain.ChangeStamp;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.MDC;

/**
 * Interceptor that handles domain objects' changestamp.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public class ChangeStampInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 973776995128953194L;

    @Override
	public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState,
			final Object[] previousState, final String[] propertyNames, final Type[] types) {
		return updateChangeStamp(entity, currentState, types);
	}

    @Override
	public boolean onSave(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames,
			final Type[] types) {
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
        return MDC.get(Constants.USER_ID);
    }
    
}
