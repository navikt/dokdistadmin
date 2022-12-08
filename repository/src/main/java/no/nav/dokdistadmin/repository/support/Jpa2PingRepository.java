package no.nav.dokdistadmin.repository.support;

import no.nav.dokdistadmin.repository.PingRepository;

/**
 * Implementation of PingRepository.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public class Jpa2PingRepository extends AbstractJpa2Repository implements PingRepository{

	@Override
	public Long countDistStatusRows() {
		String sql = "select count(*) from K_DIST_STATUS";
		Number result = (Number) entityManager.createNativeQuery(sql).getResultList().get(0);
		return result.longValue();
	}

}
