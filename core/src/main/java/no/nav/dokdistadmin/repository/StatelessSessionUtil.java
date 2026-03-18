/*
 *    Copyright {2017-2020} {Mihalcea Vlad-Alexandru}
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package no.nav.dokdistadmin.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Objects;

/**
 * @author Vlad Mihalcea
 */
class StatelessSessionUtil implements Serializable {
	private final Connection connection;

	public StatelessSessionUtil(Connection connection) {
		this.connection = connection;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StatelessSessionUtil)) return false;
		StatelessSessionUtil that = (StatelessSessionUtil) o;
		return Objects.equals(connection, that.connection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(connection);
	}

	public static StatelessSession statelessSession(EntityManager entityManager) {
		Session session = entityManager.unwrap(Session.class);
		return session.doReturningWork(connection -> {
			StatelessSessionUtil statelessSessionKey = new StatelessSessionUtil(connection);
			StatelessSession statelessSession = (StatelessSession) TransactionSynchronizationManager.getResource(statelessSessionKey);
			if (statelessSession != null) {
				return statelessSession;
			}
			statelessSession = session.getSessionFactory().openStatelessSession(connection);
			TransactionSynchronizationManager.bindResource(statelessSessionKey, statelessSession);
			return statelessSession;
		});
	}
}