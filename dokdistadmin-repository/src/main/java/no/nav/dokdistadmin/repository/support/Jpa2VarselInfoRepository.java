package no.nav.dokdistadmin.repository.support;

import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.repository.VarselInfoRepository;

public class Jpa2VarselInfoRepository extends AbstractJpa2Repository implements VarselInfoRepository {

	@Override
	public VarselInfo saveVarselInfo(VarselInfo varselInfo) {
		entityManager.persist(varselInfo);
		return varselInfo;
	}
}
