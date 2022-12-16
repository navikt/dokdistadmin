package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.LandkodePostDest;
import org.springframework.data.repository.CrudRepository;

public interface LandkodePostDestRepository extends CrudRepository<LandkodePostDest, String> {
	LandkodePostDest findLandkodePostDestByLandkode(String landkode);
}
