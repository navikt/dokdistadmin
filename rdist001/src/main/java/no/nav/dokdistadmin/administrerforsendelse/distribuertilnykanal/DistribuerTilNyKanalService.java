package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.AdministrerForsendelseService;
import no.nav.dokdistadmin.administrerforsendelse.FeilregistrerForsendelseService;
import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;
import no.nav.dokdistadmin.administrerforsendelse.OppdaterForsendelseService;
import no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.OppdaterForsendelseRequest;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.FeilTypeCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal.DistribuerTilNyKanalMapper.mapTilOpprettForsendelseRequest;
import static no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal.DistribuerTilNyKanalValidator.validerForsendelseStatus;
import static no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal.DistribuerTilNyKanalValidator.validerRequest;
import static no.nav.dokdistadmin.domain.DistribusjonStatusCode.KLAR_FOR_DIST;

@Slf4j
@Service
public class DistribuerTilNyKanalService {

    private final AdministrerForsendelseService administrerForsendelseService;
    private final FeilregistrerForsendelseService feilregistrerForsendelseService;
    private final OppdaterForsendelseService oppdaterForsendelseService;
    private final DistribusjonService distributionService;

    public DistribuerTilNyKanalService(AdministrerForsendelseService administrerForsendelseService,
                                       FeilregistrerForsendelseService feilregistrerForsendelseService,
                                       OppdaterForsendelseService oppdaterForsendelseService,
                                       DistribusjonService distribusjonQueueService) {
        this.administrerForsendelseService = administrerForsendelseService;
        this.feilregistrerForsendelseService = feilregistrerForsendelseService;
        this.oppdaterForsendelseService = oppdaterForsendelseService;
        this.distributionService = distribusjonQueueService;
    }

    @Transactional
    public long distribuerTilNyKanal(DistribuerTilNyKanalRequest request) {
        //1: Valider input
        validerRequest(request);

        DistribusjonKanalCode kanal = DistribusjonKanalCode.valueOf(request.getKanal());

        //2: Hent original forsendelse. Kaster exception hvis ikke funnet.
        HentForsendelseResponse originalForsendelse = administrerForsendelseService.hentForsendelse(request.getForsendelseId());

        //3: Valider status på den originale forsendelsen
        validerForsendelseStatus(originalForsendelse.getForsendelseStatus());

        //4: Opprett ny forsendelse
        OpprettForsendelseRequest opprettRequest = mapTilOpprettForsendelseRequest(originalForsendelse, kanal);
        Forsendelse nyForsendelse = administrerForsendelseService.opprettForsendelse(opprettRequest);

        //5: Feilregistrer original forsendelse
        FeilregistrerForsendelseRequest feilregistrerRequest = FeilregistrerForsendelseRequest.builder()
                .forsendelseId(request.getForsendelseId())
                .feilTypeCode(FeilTypeCode.valueOf(request.getArsak()))
                .tidspunkt(LocalDateTime.now())
                .detaljer(request.getArsakBeskrivelse())
                .resendingDistribusjonId(opprettRequest.getBestillingsId())
                .build();

        feilregistrerForsendelseService.feilregistrerForsendelse(feilregistrerRequest);

        //6: Sett status på ny forsendelse til KLAR_FOR_DIST
        OppdaterForsendelseRequest oppdaterRequest = OppdaterForsendelseRequest.builder()
                .forsendelseId(nyForsendelse.getForsendelseId())
                .forsendelseStatus(KLAR_FOR_DIST.name())
                .build();
        oppdaterForsendelseService.oppdaterForsendelse(oppdaterRequest);

        //7: Distribuer ny forsendelse (legg på kø)
        distributionService.distribuerTilKanal(nyForsendelse.getForsendelseId(), kanal);

        return nyForsendelse.getForsendelseId();
    }
}

