package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;


@Builder
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStamp implements Serializable {

	private static final long serialVersionUID = 61541164562562288L;

	@Column(name = "opprettet_av", nullable = false, updatable = false)
	private String opprettetAv;

	@Column(name = "opprettet_dato", nullable = false, updatable = false)
	private LocalDateTime opprettetDato;

	@Column(name = "endret_av")
	private String endretAv;

	@Column(name = "endret_dato")
	private LocalDateTime endretDato;

	public ChangeStamp(String userId) {
		this.opprettetAv = userId;
		opprettetDato = LocalDateTime.now();
	}

	public void updatedBy(String userId) {
		endretAv = userId;
		endretDato = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "ChangeStamp [opprettetAv=" + opprettetAv + ", opprettetDato=" + opprettetDato + ", endretAv=" + endretAv
				+ ", endretDato=" + endretDato + "]";
	}

}
