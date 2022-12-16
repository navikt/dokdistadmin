package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.FilInfo;
import org.springframework.data.repository.CrudRepository;

public interface FilinfoRepository extends CrudRepository<FilInfo, Long> {
	FilInfo findFilInfoByFilnavn(String filnavn);
}
