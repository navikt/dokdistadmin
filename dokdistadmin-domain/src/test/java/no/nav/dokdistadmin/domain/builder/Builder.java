package no.nav.dokdistadmin.domain.builder;

import javax.persistence.EntityManager;

import no.nav.dokdistadmin.domain.util.Constants;

import org.slf4j.MDC;


/**
 * Base class for builders.
 * 
 * @author Thomas Eugen Bjørge, Visma Consulting
 * @param <T> The type to build.
 */
public abstract class Builder<T> {

	private String userId = "builderUserId";

	public abstract T build();

	public Builder<T> userId(String userId) {
		this.userId = userId;
		return this;
	}

	public T buildAndPersist(EntityManager entityManager) {
		MDC.put(Constants.USER_ID, userId);
		
		T objectToPersist = build();
		entityManager.persist(objectToPersist);
		entityManager.flush();
		return objectToPersist;
	}
}
