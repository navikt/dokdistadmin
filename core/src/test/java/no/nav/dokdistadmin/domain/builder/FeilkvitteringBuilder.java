package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.FeilTypeCode;
import no.nav.dokdistadmin.domain.Feilkvittering;

import java.time.LocalDateTime;

/**
 * Builder for Feilkvittering
 *
 * @author Andreas Berg Skomedal, Visma Consulting.
 */
public class FeilkvitteringBuilder extends Builder<Feilkvittering> {

	public FeilkvitteringBuilder() {
	}

	public static FeilkvitteringBuilder with() {
		return new FeilkvitteringBuilder();
	}

	private Long feilkvitteringId;
	private FeilTypeCode feiltype;
	private String detaljer;
	private LocalDateTime feiletTidspunkt;
	private DokumentInfo dokumentInfo;

	@Override
	public Feilkvittering build() {
		Feilkvittering feilkvittering = new Feilkvittering(feilkvitteringId, 1);
		feilkvittering.setFeiltype(feiltype);
		feilkvittering.setDetaljer(detaljer);
		feilkvittering.setFeiletTidspunkt(feiletTidspunkt);
		feilkvittering.setDokumentInfo(dokumentInfo);
		return feilkvittering;
	}

	public FeilkvitteringBuilder feilkvitteringId(Long feilkvitteringId) {
		this.feilkvitteringId = feilkvitteringId;
		return this;
	}

	public FeilkvitteringBuilder feiltype(FeilTypeCode feiltype) {
		this.feiltype = feiltype;
		return this;
	}

	public FeilkvitteringBuilder detaljer(String detaljer) {
		this.detaljer = detaljer;
		return this;
	}

	public FeilkvitteringBuilder feiletTidspunkt(LocalDateTime feiletTidspunkt) {
		this.feiletTidspunkt = feiletTidspunkt;
		return this;
	}

	public FeilkvitteringBuilder dokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
		return this;
	}
}
