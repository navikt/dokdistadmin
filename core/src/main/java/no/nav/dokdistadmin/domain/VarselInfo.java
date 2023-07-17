package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "VARSEL_INFO")
public class VarselInfo extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = -3793260172915981033L;
	private static final String VARSEL_INFO_SEQ = "VARSEL_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VARSEL_INFO_SEQ)
	@SequenceGenerator(name = VARSEL_INFO_SEQ, sequenceName = VARSEL_INFO_SEQ, allocationSize = 1)
	@Column(name = "varsel_info_id", nullable = false)
	@Setter(NONE)
	private Long varselInfoId;

	@Column(name = "varslingstittel", length = 60)
	private String varslingstittel;

	@Column(name = "varslingstekst", length = 500)
	private String varslingstekst;

	@Column(name = "epost_adresse", length = 100)
	private String epostAdresse;

	@Column(name = "mobiltelefon_nummer", length = 20)
	private String mobiltelefonNummer;

	@Column(name = "varslingstidspunkt")
	private LocalDateTime varslingstidspunkt;

	@Column(name = "antall_repetisjoner")
	private Integer antallRepetisjoner;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_varsling_kanal", nullable = false, length = 20)
	private VarslingKanalCode varslingKanal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	public VarselInfo(Long varselInfoId, long version) {
		this.varselInfoId = varselInfoId;
		setVersion(version);
	}

}
