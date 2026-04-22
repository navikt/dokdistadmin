package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import java.util.Set;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DPVT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static no.nav.dokdistadmin.domain.FeilTypeCode.MELDINGSFEIL;
import static no.nav.dokdistadmin.domain.FeilTypeCode.VARSLINGSFEIL;

@Slf4j
public class DistribuerTilNyKanalValidator {

    private static final Set<String> GYLDIGE_KANALER = Set.of(PRINT.name(), SDP.name(), DITTNAV.name(), DPVT.name());
    private static final Set<String> GYLDIGE_ARSAKER = Set.of(VARSLINGSFEIL.name(), MELDINGSFEIL.name());
    private static final Set<String> UGYLDIGE_STATUSER = Set.of(RETURPOSTBEHANDLET.name(), FEILET.name());

    private DistribuerTilNyKanalValidator() {}

    public static void validerRequest(DistribuerTilNyKanalRequest request) {
        validerKanal(request.getKanal());
        validerArsak(request.getArsak());
    }

    public static void validerForsendelseStatus(String forsendelseStatus) {
        if (UGYLDIGE_STATUSER.contains(forsendelseStatus)) {
            avbrytBehandling("Forsendelsen har status '%s' og kan ikke distribueres til ny kanal. Forsendelser med status %s kan ikke behandles."
                    .formatted(forsendelseStatus, UGYLDIGE_STATUSER));
        }
    }

    static void validerKanal(String kanal) {
        if (!GYLDIGE_KANALER.contains(kanal)) {
            avbrytBehandling("Ugyldig kanal '%s'. Gyldige kanaler er: %s".formatted(kanal, GYLDIGE_KANALER));
        }
    }

    static void validerArsak(String arsak) {
        if (!GYLDIGE_ARSAKER.contains(arsak)) {
            avbrytBehandling("Ugyldig årsak '%s'. Gyldige årsaker er: %s".formatted(arsak, GYLDIGE_ARSAKER));
        }
    }

    static void avbrytBehandling(String feilmelding) {
        log.error("distribuerTilNyKanal feilet: {}", feilmelding);
        throw new UgyldigInputException(feilmelding);
    }

}

