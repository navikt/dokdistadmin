package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;

public interface DokumentDistribusjonRepository extends BaseJpaRepository<DistribusjonInfo, Long>, HibernateRepository<DistribusjonInfo>, CustomDokumentDistribusjonRepository {

	DistribusjonInfo getDistribusjonInfoByDistribusjonId(String distribusjonId);
}
