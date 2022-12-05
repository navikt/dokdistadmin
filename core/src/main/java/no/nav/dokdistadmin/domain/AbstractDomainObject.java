package no.nav.dokdistadmin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import java.io.Serializable;

@MappedSuperclass
public class AbstractDomainObject implements Serializable {

	@Embedded
	private ChangeStamp changeStamp;

	@Version
	@Column(name = "versjon", nullable = false)
	private long version;

	/**
	 * Getter for the changeStamp property.
	 *
	 * @return the changeStamp
	 */
	public ChangeStamp getChangeStamp() {
		return changeStamp;
	}

	/**
	 * Setter for the changeStamp property.
	 *
	 * @param changeStamp the changeStamp to set
	 */
	public void setChangeStamp(ChangeStamp changeStamp) {
		this.changeStamp = changeStamp;
	}

	/**
	 * Getter for the version property.
	 *
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Setter for the version property.
	 *
	 * @param version the version to set
	 */
	protected void setVersion(long version) {
		this.version = version;
	}
}
