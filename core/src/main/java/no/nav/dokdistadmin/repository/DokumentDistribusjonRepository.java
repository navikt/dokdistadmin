package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import org.springframework.data.repository.CrudRepository;

public interface DokumentDistribusjonRepository extends CrudRepository<DistribusjonInfo, Long>, CustomDokumentDistribusjonRepository {

	DistribusjonInfo getDistribusjonInfoByDistribusjonId(String distribusjonId);

}
