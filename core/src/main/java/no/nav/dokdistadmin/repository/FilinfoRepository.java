package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FilinfoRepository extends BaseJpaRepository<FilInfo, Long>, HibernateRepository<FilInfo> {

	FilInfo findFilInfoByFilnavn(String filnavn);

	@Modifying
	@Query(""" 
			update FilInfo fil set fil.filStatus = :filStatus,
						    fil.changeStamp.endretAv = :kilde,
						    fil.changeStamp.endretDato = current_timestamp
						where fil.filInfoId = :filInfoId
			""")
	void updateFilInfoFilStatusCode(@Param("filInfoId") Long filInfoId,
									@Param("filStatus") FilStatusCode filstatus,
									@Param("kilde") String kilde);

}
