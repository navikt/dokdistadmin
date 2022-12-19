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
@Table(name = "DOKUMENT_REFERANSE")
public class DokumentReferanse extends AbstractDomainObject {

	private static final long serialVersionUID = 7594297889066501383L;
	private static final String DOKUMENT_REFERANSE_SEQ = "DOKUMENT_REFERANSE_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DOKUMENT_REFERANSE_SEQ)
	@SequenceGenerator(name = DOKUMENT_REFERANSE_SEQ, sequenceName = DOKUMENT_REFERANSE_SEQ, allocationSize = 1)
	@Column(name = "dokument_referanse_id", nullable = false)
	@Setter(NONE)
	private Long dokumentReferanseId;

	@Column(name = "dokument_uri", nullable = false)
	private String dokumentUri;

	@Column(name = "fil_storrelse")
	private Long filStorrelse;

	@Column(name = "rekkefolge")
	private Integer rekkefolge;

	@Column(name = "arkiv_dokument_info_id")
	private String arkivDokumentInfoId;

	@Column(name = "dokumenttype_id")
	private String dokumenttypeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_refererer_til", nullable = false)
	private RefererTilCode refererTil;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	public DokumentReferanse(Long dokumentReferanseId, long version) {
		this.dokumentReferanseId = dokumentReferanseId;
		setVersion(version);
	}
}
