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

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DOKUMENT_REFERANSE")
public class DokumentReferanse extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = 7594297889066501383L;
	private static final String DOKUMENT_REFERANSE_SEQ = "DOKUMENT_REFERANSE_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DOKUMENT_REFERANSE_SEQ)
	@SequenceGenerator(name = DOKUMENT_REFERANSE_SEQ, sequenceName = DOKUMENT_REFERANSE_SEQ, allocationSize = 1)
	@Column(name = "dokument_referanse_id", nullable = false)
	@Setter(NONE)
	private Long dokumentReferanseId;

	@Column(name = "dokument_uri", nullable = false, length = 200)
	private String dokumentUri;

	@Column(name = "fil_storrelse")
	private Long filStorrelse;

	@Column(name = "rekkefolge")
	private Integer rekkefolge;

	@Column(name = "arkiv_dokument_info_id", length = 20)
	private String arkivDokumentInfoId;

	@Column(name = "dokumenttype_id", length = 50)
	private String dokumenttypeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_refererer_til", nullable = false, length = 20)
	private RefererTilCode refererTil;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	public DokumentReferanse(Long dokumentReferanseId, long version) {
		this.dokumentReferanseId = dokumentReferanseId;
		setVersion(version);
	}
}
