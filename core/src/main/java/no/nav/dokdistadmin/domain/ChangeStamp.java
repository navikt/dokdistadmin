package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Builder
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeStamp implements Serializable {

	@Serial
	private static final long serialVersionUID = 61541164562562288L;

	@Column(name = "opprettet_av", nullable = false, updatable = false, length = 20)
	private String opprettetAv;

	@Column(name = "opprettet_dato", nullable = false, updatable = false)
	private LocalDateTime opprettetDato;

	@Column(name = "endret_av", length = 20)
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

}
