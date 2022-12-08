package no.nav.dokdistadmin.repository.support;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.repository.FilInfoRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

/**
 * JPA 2 implementation of FilInfoRepository.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public class Jpa2FilInfoRepository extends AbstractJpa2Repository implements FilInfoRepository {

	@Override
	public FilInfo saveNewFilInfo(FilInfo filInfo) {
		entityManager.persist(filInfo);
		return filInfo;
	}

	@Override
	public FilInfo updateFilInfo(FilInfo filInfo) {
		FilInfo merge = entityManager.merge(filInfo);
		entityManager.flush();
		return merge;
	}

	@Override
	public void deleteFilInfoById(Long filInfoId) {
		entityManager.remove(findFilInfoById(filInfoId));
	}

	@Override
	public FilInfo findFilInfoById(Long filInfoId) {
		return entityManager.find(FilInfo.class, filInfoId);
	}

	@Override
	public FilInfo findFilInfoByFilnavn(String filnavn) {

		String jpql = "select fi from FilInfo fi where fi.filnavn = :filnavn";
		try {
			return entityManager
					.createQuery(jpql, FilInfo.class)
					.setParameter("filnavn", filnavn)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}


	@Override
	@Transactional
	public FilInfo updateFilInfoStatus(Long filInfoId, FilStatusCode statusCode) {
		FilInfo sdpMetadataFilInfo = findFilInfoById(filInfoId);
		sdpMetadataFilInfo.setFilStatus(statusCode);
		return updateFilInfo(sdpMetadataFilInfo);
	}
}
