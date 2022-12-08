package no.nav.dokdistadmin.repository.support;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.Feilkvittering;
import no.nav.dokdistadmin.domain.exception.DuplicateResponseException;
import no.nav.dokdistadmin.domain.util.Constants;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import org.slf4j.MDC;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.time.LocalDateTime.now;
import static no.nav.dokdistadmin.domain.ArkivSystemCode.JOARK;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;

/**
 * JPA2 implementation of DokumentDistribusjonRepository
 *
 * @author Joakim Bjørnstad, Visma Consulting
 */
public class Jpa2DokumentDistribusjonRepository extends AbstractJpa2Repository implements DokumentDistribusjonRepository {

    static final int OPPRETTET_ANTALL_DAGER_SIDEN = 60;
    static final LocalDateTime EKSPEDERT_DATO = LocalDateTime.of(2022, 10, 1,0,0);

    @Override
    public DistribusjonInfo saveNewDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
        entityManager.persist(distribusjonInfo);
        return distribusjonInfo;
    }

    @Override
    public void updateDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
        entityManager.merge(distribusjonInfo);
        entityManager.flush();
    }

    @Override
    public void updateDokumentInfo(DokumentInfo dokumentInfo) {
        entityManager.merge(dokumentInfo);
        entityManager.flush();
    }

    @Override
    public DistribusjonInfo findDistribusjonInfoById(Long distribusjonInfoId) {
        if (distribusjonInfoId == null) {
            return null;
        }

        return entityManager.find(DistribusjonInfo.class, distribusjonInfoId);
    }

    @Override
    public DistribusjonInfo findDistribusjonInfoByDistribusjonId(String distribusjonId) {
        if (distribusjonId == null) {
            return null;
        }

        String jpql = "select dist from DistribusjonInfo dist where dist.distribusjonId = :distribusjonId";
        try {
            return entityManager
                    .createQuery(jpql, DistribusjonInfo.class)
                    .setParameter("distribusjonId", distribusjonId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public DokumentInfo findDokumentInfoByDokumentId(String dokumentId) {
        if (dokumentId == null) {
            return null;
        }

        String jpql = "select dok from DokumentInfo dok where dok.dokumentId = :dokumentId";
        try {
            return entityManager
                    .createQuery(jpql, DokumentInfo.class)
                    .setParameter("dokumentId", dokumentId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public DokumentInfo findDokumentInfoByDokumentInfoId(Long dokumentInfoId) {
        if (dokumentInfoId == null) {
            return null;
        }
        String jpql = "select dok from DokumentInfo dok where dok.dokumentInfoId = :dokumentInfoId";
        try {
            return entityManager
                    .createQuery(jpql, DokumentInfo.class)
                    .setParameter("dokumentInfoId", dokumentInfoId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    public void updateStatusForAllDokumentInfosRelatedTo(DistribusjonInfo distribusjonInfo, DokumentStatusCode dokumentStatus) {
        String jpql = "update DokumentInfo di set di.dokumentStatus = :dokumentStatus, " +
                "di.changeStamp.endretAv = :endretAv, " +
                "di.changeStamp.endretDato = current_timestamp, " +
                "di.version = di.version + 1" +
                "where di.distribusjonInfo = :distribusjonInfo";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("dokumentStatus", dokumentStatus);
        query.setParameter("distribusjonInfo", distribusjonInfo);
        query.setParameter("endretAv", MDC.get(Constants.USER_ID));
        query.executeUpdate();
    }

    @Override
    public void updateStatusToEkspedertForAllDokumentInfosRelatedTo(DistribusjonInfo distribusjonInfo) {
        String jpql = "update DokumentInfo di set di.dokumentStatus = :dokumentStatus, " +
                "di.ekspedertDato = current_timestamp, " +
                "di.changeStamp.endretAv = :endretAv, " +
                "di.changeStamp.endretDato = current_timestamp, " +
                "di.version = di.version + 1" +
                "where di.distribusjonInfo = :distribusjonInfo";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("dokumentStatus", EKSPEDERT);
        query.setParameter("distribusjonInfo", distribusjonInfo);
        query.setParameter("endretAv", MDC.get(Constants.USER_ID));
        query.executeUpdate();
    }

    @Override
    public DokumentInfo findDokumentInfoByKonversasjonId(String konversasjonId) throws DuplicateResponseException {
        if (konversasjonId == null) {
            return null;
        }

        String jpql = "select dok from DokumentInfo dok where dok.konversasjonId = :konversasjonId";
        try {
            return entityManager
                    .createQuery(jpql, DokumentInfo.class)
                    .setParameter("konversasjonId", konversasjonId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new DuplicateResponseException("NonUnique konversasjonsId", e);
        }
    }

	@Override
	public DokumentInfo findDokumentInfoByJournalpostId(String arkivkode) {
		if (arkivkode == null) {
			return null;
		}
		String jpql = "select dok from DokumentInfo dok where dok.arkivkode = :arkivkode";

		try {
			return entityManager
					.createQuery(jpql, DokumentInfo.class)
					.setParameter("arkivkode", arkivkode)
                    .setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

    @Override
    public void saveNewDokumentInfo(DokumentInfo dokumentInfo) {
        entityManager.persist(dokumentInfo);
    }

    @Override
    public String findPostDestinasjon(String landkode) {
        if (landkode == null) {
            return null;
        }

        String sql = "select POST_DEST from K_LANDKODE_POST_DEST where K_LANDKODE = :landkode";
        try {
            return (String) entityManager.createNativeQuery(sql)
                    .setParameter("landkode", landkode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<DokumentInfo> findDokumentInfoByDokumentStatusAndDistribusjonKanal(List<DokumentStatusCode> dokumentStatusList, DistribusjonKanalCode distribusjonKanal, LocalDateTime opprettetEtter) {
        if (dokumentStatusList == null || dokumentStatusList.isEmpty()) {
            return new ArrayList<>();
        }
        if (distribusjonKanal == null) {
            return new ArrayList<>();
        }

        String jpql = "select dok from DokumentInfo dok, DistribusjonInfo dis where dok.dokumentStatus IN (:dokumentStatusList) and " +
                "dok.distribusjonInfo = dis and dis.distribusjonKanal = :distribusjonKanal and dok.changeStamp.opprettetDato >= :opprettetEtter";

        return entityManager
                .createQuery(jpql, DokumentInfo.class)
                .setParameter("dokumentStatusList", dokumentStatusList)
                .setParameter("distribusjonKanal", distribusjonKanal)
                .setParameter("opprettetEtter", opprettetEtter)
                .getResultList();
    }

    @Override
    public List<DistribusjonInfo> findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(EnumSet<DokumentStatusCode> dokumentStatusCodes, DistribusjonKanalCode distribusjonKanal, long antallTimer) {
        if (dokumentStatusCodes == null || dokumentStatusCodes.isEmpty()) {
            return new ArrayList<>();
        }
        if (distribusjonKanal == null) {
            return new ArrayList<>();
        }

        String jpql = "select dist " +
				"from DistribusjonInfo dist join fetch dist.dokumentInfos dok " +
				"where dok.dokumentStatus not in :dokumentStatus " +
                "and dok.avstemtDato is null " +
				"and dist.distribusjonKanal = :distribusjonKanal " +
				"and dist.changeStamp.opprettetDato between :etterAntallDagerSiden and :foerAntallTimerSiden " +
                "order by dist.distribusjonInfoId, dok.dokumentId";
        LocalDateTime now = now();
        return entityManager.createQuery(jpql, DistribusjonInfo.class)
                .setParameter("dokumentStatus", dokumentStatusCodes)
                .setParameter("distribusjonKanal", distribusjonKanal)
                .setParameter("etterAntallDagerSiden", now.minusDays(OPPRETTET_ANTALL_DAGER_SIDEN))
                .setParameter("foerAntallTimerSiden", now.minusHours(antallTimer))
                .getResultList();
    }

    @Override
    public DistribusjonInfo findDistribusjonInfoByDokumentInfoId(Long dokumentInfoId) {
        if (dokumentInfoId == null) {
            return null;
        }
        String jpql = "select dist from DistribusjonInfo dist join dist.dokumentInfos dok where dok.dokumentInfoId=:dokumentInfoId";
        try {
            return entityManager.createQuery(jpql, DistribusjonInfo.class)
                    .setParameter("dokumentInfoId", dokumentInfoId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Feilkvittering saveFeilkvittering(Feilkvittering feilkvittering) {
        entityManager.persist(feilkvittering);
        return feilkvittering;
    }

    @Override
    public List<DokumentInfo> findEkspedertDokumentInfo(int maxResult) {
        String jpql = "select dok from  DokumentInfo dok  inner join DistribusjonInfo dist on dist.distribusjonInfoId = dok.distribusjonInfo.distribusjonInfoId " +
				"left outer join VarselInfo  vai on vai.dokumentInfo.dokumentInfoId = dok.dokumentInfoId " +
                "left outer join Postadresse pa on pa.postadresseId = dok.postadresse.postadresseId " +
                "where dok.dokumentStatus = :dokumentStatus " +
                "and dok.avstemtArkivDato is null " +
                "and dok.arkivSystem = : arkivSystem " +
                "and dok.arkivkode is not null " +
                "and dok.ekspedertDato is not null " +
                "and dok.ekspedertDato >= : ekspedertDatoGreaterThan " +
                "order by dok.ekspedertDato";

        TypedQuery<DokumentInfo> dokumentInfoTypedQuery = entityManager.createQuery(jpql, DokumentInfo.class)
                .setParameter("dokumentStatus", EKSPEDERT)
                .setParameter("arkivSystem", JOARK)
                .setParameter("ekspedertDatoGreaterThan", EKSPEDERT_DATO);

        return maxResult > 0 ? dokumentInfoTypedQuery.setMaxResults(maxResult).getResultList(): dokumentInfoTypedQuery.getResultList();
    }

	@Override
	public void updateDokumentInfosAvstemtArkivDato(List<Long> dokumentInfoIds) {
		if (dokumentInfoIds == null || dokumentInfoIds.isEmpty()) {
			return;
		}
		String jpql = "update DokumentInfo dok " +
				"set dok.avstemtArkivDato = current_timestamp, " +
                "dok.changeStamp.endretAv = :endretAv " +
				"where dok.dokumentInfoId in :dokumentInfoIds";

		Query query = entityManager.createQuery(jpql)
                .setParameter("dokumentInfoIds", dokumentInfoIds)
                .setParameter("endretAv", MDC.get(Constants.USER_ID));

		query.executeUpdate();
	}

}
