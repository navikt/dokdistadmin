package no.nav.dokdistadmin.domain.builder;

import org.slf4j.MDC;

import javax.persistence.EntityManager;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;


public abstract class Builder<T> {

	private String userId = "builderUserId";

	public abstract T build();

	public Builder<T> userId(String userId) {
		this.userId = userId;
		return this;
	}

	public T buildAndPersist(EntityManager entityManager) {
		MDC.put(USER_ID, userId);
		
		T objectToPersist = build();
		entityManager.persist(objectToPersist);
		entityManager.flush();
		return objectToPersist;
	}
}
