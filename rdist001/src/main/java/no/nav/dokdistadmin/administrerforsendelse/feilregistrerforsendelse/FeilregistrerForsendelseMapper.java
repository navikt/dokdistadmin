package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.Feilkvittering;

public class FeilregistrerForsendelseMapper {

    public static Feilkvittering toFeilkvittering(FeilregistrerForsendelseRequest feilregistrerForsendelse, DokumentInfo dokumentInfo) {

        return Feilkvittering.builder()
                .dokumentInfo(dokumentInfo)
                .feiletTidspunkt(feilregistrerForsendelse.getTidspunkt())
                .detaljer(feilregistrerForsendelse.getDetaljer())
                .feilpart(feilregistrerForsendelse.getPart())
                .feiltype(feilregistrerForsendelse.getFeilTypeCode())
                .build();
    }
}