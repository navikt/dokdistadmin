package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.administrerforsendelse.post.OppdaterPostadresseRequest;
import no.nav.dokdistadmin.administrerforsendelse.post.PostadresseMapper;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.PostdestinasjonIkkeFunnetException;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Service
@Transactional(readOnly = true)
public class PostService {

	final LandkodePostDestRepository landkodePostDestRepository;
	final DokumentInfoRepository dokumentInfoRepository;

	public PostService(LandkodePostDestRepository landkodePostDestRepository,
					   DokumentInfoRepository dokumentInfoRepository) {
		this.landkodePostDestRepository = landkodePostDestRepository;
		this.dokumentInfoRepository = dokumentInfoRepository;
	}

	public HentPostdestinasjonResponse hentPostdestinasjon(String landkode) {
		LandkodePostDest landkodePostDest = landkodePostDestRepository.findLandkodePostDestByLandkode(landkode);

		if (landkodePostDest == null || landkodePostDest.getPostDest() == null) {
			throw new PostdestinasjonIkkeFunnetException("Postdestinasjon for landkode=%s ikke funnet i dokdistDb".formatted(landkode));
		}

		return new HentPostdestinasjonResponse(landkodePostDest.getPostDest());
	}

	@Transactional
	public void oppdaterPostadresse(OppdaterPostadresseRequest request) {

		DokumentInfo dokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(request.getForsendelseId());

		if (dokumentInfo == null) {
			throw new ForsendelseIkkeFunnetException(format(
					"rdist001 kunne ikke oppdatere postadresse. Feilmelding: Fant ikke forsendelse med forsendelseId=%s",
					request.getForsendelseId()));
		}

		if (dokumentInfo.getPostadresse() == null) {
			dokumentInfo.setPostadresse(PostadresseMapper.map(request));
		} else {
			PostadresseMapper.oppdaterPostadresse(request, dokumentInfo.getPostadresse());
		}

		dokumentInfoRepository.save(dokumentInfo);
	}
}
