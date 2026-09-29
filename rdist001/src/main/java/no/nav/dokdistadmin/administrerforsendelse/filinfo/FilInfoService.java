package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import no.nav.dokdistadmin.repository.FilinfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.dokdistadmin.administrerforsendelse.filinfo.FilInfoMapper.mapTilFilInfo;
import static no.nav.dokdistadmin.administrerforsendelse.filinfo.FilInfoValidator.validerFilInfoRequest;
import static no.nav.dokdistadmin.domain.FilStatusCode.valueOf;

@Service
public class FilInfoService {

	private final FilinfoRepository filinfoRepository;

	public FilInfoService(FilinfoRepository filinfoRepository) {
		this.filinfoRepository = filinfoRepository;
	}

	@Transactional
	public FilInfoResponse oppdaterFilInfo(FilInfoRequest filInfoRequest) {
		validerFilInfoRequest(filInfoRequest);

		if (filInfoRequest.filInfoId() != null) {
			FilInfo filInfo = filinfoRepository.findById(filInfoRequest.filInfoId())
					.orElseThrow(() -> new UgyldigInputException(
							"Fil med filInfoId=%s finnes ikke".formatted(filInfoRequest.filInfoId())));

			filInfo.setFilStatus(valueOf(filInfoRequest.status()));

			return new FilInfoResponse(filInfo.getFilInfoId());
		}

		FilInfo filInfo = filinfoRepository.persist(mapTilFilInfo(filInfoRequest));

		return new FilInfoResponse(filInfo.getFilInfoId());
	}
}
