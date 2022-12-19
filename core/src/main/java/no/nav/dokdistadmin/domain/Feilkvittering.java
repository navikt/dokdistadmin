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
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "FEILKVITTERING")
public class Feilkvittering extends AbstractDomainObject {

	private static final long serialVersionUID = 8794614861864861348L;
	private static final String FEILKVITTERING_SEQ = "FEILKVITTERING_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FEILKVITTERING_SEQ)
	@SequenceGenerator(name = FEILKVITTERING_SEQ, sequenceName = FEILKVITTERING_SEQ, allocationSize = 1)
	@Column(name = "feilkvittering_id", nullable = false)
	@Setter(NONE)
	private Long feilkvitteringId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_feil_type")
	private FeilTypeCode feiltype;

	@Column(name = "feilpart")
	private String feilpart;

	@Column(name = "detaljer")
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
