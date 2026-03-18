package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.FilInfo;

public interface FilinfoRepository extends BaseJpaRepository<FilInfo, Long>, HibernateRepository<FilInfo> {
	FilInfo findFilInfoByFilnavn(String filnavn);
}
