package no.nav.dokdistadmin.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
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
