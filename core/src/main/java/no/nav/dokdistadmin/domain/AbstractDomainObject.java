package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@SuppressWarnings("serial")
public abstract class AbstractDomainObject implements Serializable {

	@Embedded
	private ChangeStamp changeStamp;

	@Version
	@Column(name = "versjon", nullable = false)
	private long version;

}
