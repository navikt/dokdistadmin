package no.nav.dokdistadmin.config.jms;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "dokdistadmin.jms")
public class JmsProperties {

	@Valid
	private final Broker broker = new Broker();
	@Valid
	private final Queues queues = new Queues();

	@Data
	@Validated
	public static class Broker {
		@NotEmpty
		private String hostname;
		@NotEmpty
		private String name;
		@Min(0)
		private int port;
		@NotEmpty
		private String channel;
	}

	@Data
	@Validated
	public static class Queues {
		@NotEmpty
		private String qdist009Print;
		@NotEmpty
		private String qdist010Dittnav;
		@NotEmpty
		private String qdist011Sdp;
		@NotEmpty
		private String qdist016Dpvt;
	}
}
