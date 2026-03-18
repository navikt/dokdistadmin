package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.LandkodePostDest;

public interface LandkodePostDestRepository extends BaseJpaRepository<LandkodePostDest, String>, HibernateRepository<LandkodePostDest> {

	LandkodePostDest findLandkodePostDestByLandkode(String landkode);
}
