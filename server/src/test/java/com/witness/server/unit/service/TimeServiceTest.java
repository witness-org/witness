package com.witness.server.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.witness.server.service.TimeService;
import com.witness.server.service.impl.TimeServiceImpl;
import com.witness.server.unit.BaseUnitTest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.assertj.core.data.TemporalUnitOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TimeServiceImpl.class)
class TimeServiceTest extends BaseUnitTest {
  /**
   * The primary timezone for unit tests is overridden via the "date-times.zone-id" property of the "application-unit-test.yml" configuration file.
   * We do not assert against a ZoneId instance that is created utilizing a field which dynamically retrieves the ZoneId via an annotation
   * '@Value("${date-times.zone-id}")'. The tests would still succeed if the configuration changed, e.g. overridden values removed or unintentionally
   * changed. Therefore, this constant has to be kept in sync manually such that changes in the configuration are noticed and examined right away.
   */
  private static final ZoneId TEST_TIMEZONE = ZoneId.of("Asia/Kolkata");

  private static final ZoneId UTC_TIMEZONE = ZoneId.of("UTC");

  /**
   * {@code DateTime}-related functions can be implemented with different APIs. Those APIs have varying precision and therefore the results are not
   * comparable in a clear-cut, exact way. This constant defines a maximum deviation from the expected value (tolerance).
   */
  private static final TemporalUnitOffset DATETIME_PRECISION = within(500, ChronoUnit.MILLIS);

  @Autowired
  private TimeService timeService;

  @Test
  void getCurrentTime_givenNoZoneId_returnTimeInPrimaryTimezone() {
    // Precondition for the test, otherwise the assertion below does not make sense. Ignoring (instead of failing) test is okay since, if the
    // assumption is not met, getPrimaryTimeZone_givenProfileSpecificZoneId_respectConfiguration() fails.
    assumeThat(timeService.getPrimaryTimeZone()).isEqualTo(TEST_TIMEZONE);

    assertThat(timeService.getCurrentTime()).isCloseTo(ZonedDateTime.now(TEST_TIMEZONE), DATETIME_PRECISION);
  }

  @Test
  void getCurrentTime_givenZoneId_returnTimeInCorrectTimeZone() {
    var testZone = ZoneId.of("Antarctica/Syowa"); // some random timezone other than the primary one

    assertThat(timeService.getCurrentTime(testZone)).isCloseTo(ZonedDateTime.now(testZone), DATETIME_PRECISION);
  }

  @Test
  void getCurrentTime_givenNullZoneId_throwException() {
    assertThatThrownBy(() -> timeService.getCurrentTime(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getCurrentTimeUtc() {
    assertThat(timeService.getCurrentTimeUtc()).isCloseTo(ZonedDateTime.now(UTC_TIMEZONE), DATETIME_PRECISION);
  }

  @Test
  void getPrimaryTimeZone_givenProfileSpecificZoneId_respectConfiguration() {
    assertThat(timeService.getPrimaryTimeZone()).isNotNull();
    assertThat(timeService.getPrimaryTimeZone()).isEqualTo(TEST_TIMEZONE);
  }

  @Test
  void getUtcTimeZone() {
    assertThat(timeService.getUtcTimeZone()).isEqualTo(UTC_TIMEZONE);
  }
}
