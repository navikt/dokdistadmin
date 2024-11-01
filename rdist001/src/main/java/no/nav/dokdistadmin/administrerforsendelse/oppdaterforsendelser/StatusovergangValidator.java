package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import no.nav.dokdistadmin.domain.DokumentStatusCode;

import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.KLAR_FOR_DIST;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;

public class StatusovergangValidator {

	private StatusovergangValidator() {
	}

	public static boolean isLovligDokumentstatusovergang(String dokumentstatus, String forsendelsestatus) {
		return statusOvergang(dokumentstatus, forsendelsestatus, OPPRETTET, KLAR_FOR_DIST) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, KLAR_FOR_DIST, OVERSENDT) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, KLAR_FOR_DIST, EKSPEDERT) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, OVERSENDT, BEKREFTET) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, OVERSENDT, EKSPEDERT) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, OVERSENDT, FEILET) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, BEKREFTET, EKSPEDERT) ||
			   statusOvergang(dokumentstatus, forsendelsestatus, BEKREFTET, FEILET);
	}

	private static boolean statusOvergang(String oldDokumentStatus, String nyForsendelseStatus, DokumentStatusCode fraStatus, DokumentStatusCode tilStatus) {
		return fraStatus.name().equals(oldDokumentStatus) && tilStatus.name().equals(nyForsendelseStatus);
	}

}
