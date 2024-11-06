package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;

import java.util.EnumSet;

import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.KLAR_FOR_DIST;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;

public class StatusovergangValidator {
	private static final EnumSet<VarselStatusCode> GYLDIGE_NYE_VARSELSTATUSER = EnumSet.of(VarselStatusCode.FEILET, VarselStatusCode.FERDIGSTILT);

	private StatusovergangValidator() {
	}

	public static boolean isLovligDokumentstatusOvergang(String dokumentstatus, String forsendelsestatus) {
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

	public static boolean isLovligVarselstatusOvergang(VarselStatusCode opprinneligVarselStatus, VarselStatusCode nyVarselStatus) {
		if (opprinneligVarselStatus == null) {
			return true;
		}
		return VarselStatusCode.OPPRETTET.equals(opprinneligVarselStatus) && GYLDIGE_NYE_VARSELSTATUSER.contains(nyVarselStatus);
	}

}
