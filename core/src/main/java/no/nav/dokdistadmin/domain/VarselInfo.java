package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "VARSEL_INFO")
public class VarselInfo extends AbstractDomainObject {

	private static final long serialVersionUID = -3793260172915981033L;
	private static final String VARSEL_INFO_SEQ = "VARSEL_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VARSEL_INFO_SEQ)
	@SequenceGenerator(name = VARSEL_INFO_SEQ, sequenceName = VARSEL_INFO_SEQ, allocationSize = 1)
	@Column(name = "varsel_info_id", nullable = false)
	@Setter(NONE)
	private Long varselInfoId;

	@Column(name = "varslingstekst")
	private String varslingstekst;

	@Column(name = "epost_adresse")
	private String epostAdresse;

	@Column(name = "mobiltelefon_nummer")
	private String mobiltelefonNummer;

	@Column(name = "antall_repetisjoner")
	private Integer antallRepetisjoner;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_varsling_kanal", nullable = false)
	private VarslingKanalCode varslingKanal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	public VarselInfo(Long varselInfoId, long version) {
		this.varselInfoId = varselInfoId;
		setVersion(version);
	}

}
