package no.nav.dokdistadmin.repository;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Test util methods
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class TestUtils {

	/**
	 * Get a log mock for a given class, use {@link MockAppender#verify(String)} to assert logs
	 */
	public static MockAppender getMockedAppender(String name) {
		Logger testLogger = (Logger) LoggerFactory.getLogger(name);
		return getMockAppender(testLogger);
	}

	/**
	 * Get a log mock for a given class, use {@link MockAppender#verify(String)} to assert logs
	 */
	public static MockAppender getMockedAppender(Class clazz) {
		Logger testLogger = (Logger) LoggerFactory.getLogger(clazz);
		return getMockAppender(testLogger);
	}

	@SuppressWarnings("unchecked")
	private static MockAppender getMockAppender(Logger testLogger) {
		Appender<ILoggingEvent> mockAppender = Mockito.mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		testLogger.addAppender(mockAppender);
		return new MockAppender(mockAppender);
	}

	public static class MockAppender {

		private final Appender<ILoggingEvent> mockAppender;

		MockAppender(Appender<ILoggingEvent> mockAppender) {
			this.mockAppender = mockAppender;
		}

		public void verify(String token) {
			Mockito.verify(mockAppender).doAppend(argThat(hasMessageContaining(token)));
		}

		private static ArgumentMatcher<ILoggingEvent> hasMessageContaining(final String token) {
			return iLoggingEvent -> iLoggingEvent.getFormattedMessage().contains(token);
		}
	}

	/**
	 * Matcher that asserts a time to be less than 1000ms away
	 */
	public static Matcher<? super LocalDateTime> aboutNow() {
		return new BaseMatcher<LocalDateTime>() {
			@Override
			public boolean matches(Object o) {
				if (o instanceof LocalDateTime) {
					long ms = System.currentTimeMillis() - ((LocalDateTime) o).atZone(ZoneId.systemDefault()).toInstant()
							.toEpochMilli();
					return ms < 1000;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("timestamp is not about now");
			}
		};
	}
}
