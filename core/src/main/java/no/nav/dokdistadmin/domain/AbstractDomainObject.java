package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
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
