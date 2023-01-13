package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.CoreConfig;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
		classes = {CoreConfig.class},
		properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ActiveProfiles("itest")
public abstract class AbstractItest {

	@Autowired
	private DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	private DokumentDistribusjonRepository dokumentDistribusjonRepository;

}
