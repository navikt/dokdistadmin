package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.RESENDING_DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseValidator.validerDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseValidator.validerDokumentInfo;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

public class FeilregistrerForsendelseValidatorTest {

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalValidereDistribusjonInfo(String resendingDistribusjonId) {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
						.resendingDistribusjonId(resendingDistribusjonId)
						.build();

		assertThatCode(() -> validerDistribusjonInfo(distribusjonInfo)).doesNotThrowAnyException();
	}

	@ParameterizedTest
	@EnumSource(value = DokumentStatusCode.class, mode = EXCLUDE, names = {"FEILET"})
	void skalValidereDokumentInfo(DokumentStatusCode dokumentStatusCode) {
		DokumentInfo dokumentInfo = DokumentInfo.builder()
						.dokumentStatus(dokumentStatusCode)
						.build();

		assertThatCode(() -> validerDokumentInfo(dokumentInfo)).doesNotThrowAnyException();
	}

	@Test
	void skalFeilvalidereDistribusjonInfoMedResendingDistribusjonId() {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
						.resendingDistribusjonId(RESENDING_DISTRIBUSJON_ID)
						.build();

		assertThatThrownBy(() -> validerDistribusjonInfo(distribusjonInfo))
				.isInstanceOf(ValideringFeiletException.class)
				.hasMessageContaining("Feltet resendingDistribusjonId på forsendelsen du prøver å feilregistrere kan ikke ha en verdi");
	}

	@Test
	void skalFeilvalidereDokumentInfoMedDokumentStatusFeilet() {
		DokumentInfo dokumentInfo = DokumentInfo.builder()
						.dokumentStatus(FEILET)
						.build();

		assertThatThrownBy(() -> validerDokumentInfo(dokumentInfo))
				.isInstanceOf(ValideringFeiletException.class)
				.hasMessageContaining("Feltet dokumentStatusCode på forsendelsen du prøver å feilregistrere kan ikke ha verdien FEILET");
	}

}
