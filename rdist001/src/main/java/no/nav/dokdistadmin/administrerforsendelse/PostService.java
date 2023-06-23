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
import no.nav.dokdistadmin.repository.PostadresseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Service
@Transactional(readOnly = true)
public class PostService {

	public static final String OPPDATERPOSTADRESSE_FEILMELDING = "rdist001 kunne ikke feilregistrere forsendelse. Feilmelding=%s";

	LandkodePostDestRepository landkodePostDestRepository;
	PostadresseRepository postadresseRepository;
	DokumentInfoRepository dokumentInfoRepository;

	public PostService(LandkodePostDestRepository landkodePostDestRepository,
					   PostadresseRepository postadresseRepository,
					   DokumentInfoRepository dokumentInfoRepository) {
		this.landkodePostDestRepository = landkodePostDestRepository;
		this.postadresseRepository = postadresseRepository;
		this.dokumentInfoRepository = dokumentInfoRepository;
	}

	public HentPostdestinasjonResponse hentPostdestinasjon(String landkode) {
		LandkodePostDest landkodePostDest = landkodePostDestRepository.findLandkodePostDestByLandkode(landkode);

		if (landkodePostDest == null || landkodePostDest.getPostDest() == null) {
			throw new PostdestinasjonIkkeFunnetException("Postdestinasjon ikke funnet i dokdistDb");
		}

		return new HentPostdestinasjonResponse(landkodePostDest.getPostDest());
	}

	@Transactional
	public void oppdaterPostadresse(OppdaterPostadresseRequest request) {

		DokumentInfo dokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(request.getForsendelseId());

		if (dokumentInfo == null) {
			throw new ForsendelseIkkeFunnetException(format(OPPDATERPOSTADRESSE_FEILMELDING, format("Fant ikke forsendelse med forsendelseId=%s", request.getForsendelseId())));
		}

		if (dokumentInfo.getPostadresse() == null) {
			dokumentInfo.setPostadresse(PostadresseMapper.map(request));
		} else {
			PostadresseMapper.oppdaterPostadresse(request, dokumentInfo.getPostadresse());
		}

		dokumentInfoRepository.save(dokumentInfo);
		System.out.println("hei");
	}
}
