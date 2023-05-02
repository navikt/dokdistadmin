package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import no.nav.dokdistadmin.exception.functional.PostdestinasjonIkkeFunnetException;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

	LandkodePostDestRepository landkodePostDestRepository;

	public PostService(LandkodePostDestRepository landkodePostDestRepository) {
		this.landkodePostDestRepository = landkodePostDestRepository;
	}

	public HentPostdestinasjonResponse hentPostdestinasjon(String landkode) {
		LandkodePostDest landkodePostDest = landkodePostDestRepository.findLandkodePostDestByLandkode(landkode);

		if (landkodePostDest == null || landkodePostDest.getPostDest() == null) {
			throw new PostdestinasjonIkkeFunnetException("Postdestinasjon ikke funnet i dokdistDb");
		}

		return new HentPostdestinasjonResponse(landkodePostDest.getPostDest());
	}

}
