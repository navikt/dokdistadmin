package no.nav.dokdistadmin.repository.support;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base class for repository implementations.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public abstract class AbstractJpa2Repository {

	@PersistenceContext
	protected EntityManager entityManager;
	
}
