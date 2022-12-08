package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.domain.RefererTilCode;

/**
 * Builder for DokumentReferanse.
 *
 * @author Joakim Bjørnstad, Visma Consulting
 */
public class DokumentReferanseBuilder extends Builder<DokumentReferanse> {

	private DokumentReferanseBuilder() {
	}

	public static DokumentReferanseBuilder with() {
		return new DokumentReferanseBuilder();
	}

	private Long dokumentReferanseId;
	private String dokumenttypeId;
	private String dokumentUri;
	private Long filStorrelse;
	private RefererTilCode refererTil;
	private DokumentInfo dokumentInfo;
	private int rekkefolge;
	private String arkivDokumentInfoId;

	@Override
	public DokumentReferanse build() {
		DokumentReferanse dokumentReferanse = new DokumentReferanse(dokumentReferanseId, 1);
		dokumentReferanse.setDokumenttypeId(dokumenttypeId);
		dokumentReferanse.setDokumentUri(dokumentUri);
		dokumentReferanse.setFilStorrelse(filStorrelse);
		dokumentReferanse.setRefererTil(refererTil);
		dokumentReferanse.setDokumentInfo(dokumentInfo);
		dokumentReferanse.setRekkefolge(rekkefolge);
		dokumentReferanse.setArkivDokumentInfoId(arkivDokumentInfoId);
		return dokumentReferanse;
	}

	public DokumentReferanseBuilder dokumentReferanseId(Long dokumentReferanseId) {
		this.dokumentReferanseId = dokumentReferanseId;
		return this;
	}

	public DokumentReferanseBuilder dokumenttypeId(String dokumenttypeId) {
		this.dokumenttypeId = dokumenttypeId;
		return this;
	}

	public DokumentReferanseBuilder dokumentUri(String dokumentUri) {
		this.dokumentUri = dokumentUri;
		return this;
	}


	public DokumentReferanseBuilder filStorrelse(Long filStorrelse) {
		this.filStorrelse = filStorrelse;
		return this;
	}


	public DokumentReferanseBuilder refererTil(RefererTilCode refererTil) {
		this.refererTil = refererTil;
		return this;
	}

	public DokumentReferanseBuilder rekkefolge(int rekkefolge) {
		this.rekkefolge = rekkefolge;
		return this;
	}

	public DokumentReferanseBuilder dokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
		return this;
	}

	public DokumentReferanseBuilder arkivDokumentInfoId(String arkivDokumentInfoId) {
		this.arkivDokumentInfoId = arkivDokumentInfoId;
		return this;
	}
}
