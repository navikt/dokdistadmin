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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FIL_INFO")
public class FilInfo extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = 5843997999899784645L;
	private static final String FIL_INFO_SEQ = "FIL_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FIL_INFO_SEQ)
	@SequenceGenerator(name = FIL_INFO_SEQ, sequenceName = FIL_INFO_SEQ, allocationSize = 1)
	@Column(name = "fil_info_id", nullable = false)
	@Setter(NONE)
	private Long filInfoId;

	@Column(name = "filnavn", nullable = false)
	private String filnavn;

	@Column(name = "mottatt_dato")
	private LocalDateTime mottattDato;

	@Column(name = "sendt_dato")
	private LocalDateTime sendtDato;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_fil_type", nullable = false, length = 20)
	private FilTypeCode filType;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_komm_retning", nullable = false, length = 20)
	private KommunikasjonRetningCode kommunikasjonRetning;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_fil_status", nullable = false, length = 20)
	private FilStatusCode filStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_kilde_type", nullable = false, length = 20)
	private KildeTypeCode kildeType;

	@Builder.Default
	@ManyToMany(mappedBy = "filInfos")
	private Set<DistribusjonInfo> distribusjonInfos = new HashSet<>();

	@Builder.Default
	@ManyToMany(mappedBy = "filInfos")
	private Set<DokumentInfo> dokumentInfos = new HashSet<>();

	public FilInfo(Long filInfoId, long version) {
		this.filInfoId = filInfoId;
		setVersion(version);
	}

	public void addDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
		if (distribusjonInfo != null) {
			distribusjonInfos.add(distribusjonInfo);
		}
	}

	public Set<DistribusjonInfo> getDistribusjonInfos() {
		return Collections.unmodifiableSet(distribusjonInfos);
	}

	public void addDokumentInfo(DokumentInfo dokumentInfo) {
		if (dokumentInfo != null) {
			dokumentInfos.add(dokumentInfo);
		}
	}

	public Set<DokumentInfo> getDokumentInfos() {
		return Collections.unmodifiableSet(dokumentInfos);
	}

}
