package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.domain.VarslingKanalCode;

public class VarselInfoBuilder extends Builder<VarselInfo> {

	private VarselInfoBuilder() {
	}

	public static VarselInfoBuilder with() {
		return new VarselInfoBuilder();
	}

	private Long varselInfoId;
	private String varslingstekst;
	private String epostAdresse;
	private String mobiltelefonNummer;
	private Integer antallRepetisjoner;
	private VarslingKanalCode varslingKanal;


	@Override
	public VarselInfo build() {
		VarselInfo varselInfo = new VarselInfo(varselInfoId, 1);
		varselInfo.setVarslingstekst(varslingstekst);
		varselInfo.setEpostAdresse(epostAdresse);
		varselInfo.setMobiltelefonNummer(mobiltelefonNummer);
		varselInfo.setAntallRepetisjoner(antallRepetisjoner);
		varselInfo.setVarslingKanal(varslingKanal);
		return varselInfo;
	}

	public VarselInfoBuilder varselInfoId(Long varselInfoId) {
		this.varselInfoId = varselInfoId;
		return this;
	}

	public VarselInfoBuilder varslingstekst(String varslingstekst) {
		this.varslingstekst = varslingstekst;
		return this;
	}

	public VarselInfoBuilder epostAdresse(String epostAdresse) {
		this.epostAdresse = epostAdresse;
		return this;
	}

	public VarselInfoBuilder mobiltelefonNummer(String mobiltelefonNummer) {
		this.mobiltelefonNummer = mobiltelefonNummer;
		return this;
	}

	public VarselInfoBuilder antallRepetisjoner(Integer antallRepetisjoner) {
		this.antallRepetisjoner = antallRepetisjoner;
		return this;
	}

	public VarselInfoBuilder varslingKanal(VarslingKanalCode varslingKanal) {
		this.varslingKanal = varslingKanal;
		return this;
	}
}
