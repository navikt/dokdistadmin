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
@Table(name = "FEILKVITTERING")
public class Feilkvittering extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = 8794614861864861348L;
	private static final String FEILKVITTERING_SEQ = "FEILKVITTERING_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FEILKVITTERING_SEQ)
	@SequenceGenerator(name = FEILKVITTERING_SEQ, sequenceName = FEILKVITTERING_SEQ, allocationSize = 1)
	@Column(name = "feilkvittering_id", nullable = false)
	@Setter(NONE)
	private Long feilkvitteringId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_feil_type", length = 20)
	private FeilTypeCode feiltype;

	@Column(name = "feilpart", length = 20)
	private String feilpart;

	@Column(name = "detaljer", length = 1000)
	private String detaljer;

	@Column(name = "feilet_tidspunkt")
	private LocalDateTime feiletTidspunkt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	public Feilkvittering(Long feilkvitteringId, long version) {
		this.feilkvitteringId = feilkvitteringId;
		setVersion(version);
	}
}
