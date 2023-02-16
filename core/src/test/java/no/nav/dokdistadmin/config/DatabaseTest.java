package no.nav.dokdistadmin.config;

import org.springframework.test.context.transaction.TestTransaction;

public interface DatabaseTest {

	default void commitAndBeginNewTransaction() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}

}
