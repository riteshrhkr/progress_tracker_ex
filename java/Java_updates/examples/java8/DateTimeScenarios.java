package examples.java8;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

/**
 * Real-World Scenario: Date-Time API Practical Examples
 *
 * This example demonstrates practical, real-world date and time operations
 * using the Java 8 Date-Time API (java.time package).
 *
 * Key Concepts Demonstrated:
 * 1. Business date calculations (working days, excluding holidays)
 * 2. Timezone conversions (meeting scheduler across timezones)
 * 3. Date ranges and iterations
 * 4. Formatting and parsing (custom formats, locale-specific)
 * 5. Age calculations and date differences
 * 6. Duration calculations (time tracking, elapsed time)
 * 7. Legacy conversion (Date ↔ LocalDate, Calendar ↔ ZonedDateTime)
 * 8. Real-world patterns (scheduling, reporting, billing periods)
 */
public class DateTimeScenarios {

    public static void main(String[] args) {
        System.out.println("=== Date-Time API: Real-World Examples ===\n");

        // Example 1: Business date calculations
        example1_BusinessDateCalculations();

        // Example 2: Timezone conversions
        example2_TimezoneConversions();

        // Example 3: Date ranges and iterations
        example3_DateRangesAndIterations();

        // Example 4: Formatting and parsing
        example4_FormattingAndParsing();

        // Example 5: Age calculations
        example5_AgeCalculations();

        // Example 6: Duration and time tracking
        example6_DurationAndTimeTracking();

        // Example 7: Legacy conversion
        example7_LegacyConversion();

        // Example 8: Real-world patterns
        example8_RealWorldPatterns();
    }

    /**
     * Example 1: Business Date Calculations
     * Calculate working days, skip weekends and holidays
     */
    private static void example1_BusinessDateCalculations() {
        System.out.println("--- Example 1: Business Date Calculations ---");

        LocalDate today = LocalDate.of(2024, 10, 24);  // Thursday
        System.out.println("Today: " + today + " (" + today.getDayOfWeek() + ")");

        // Add 5 business days (skip weekends)
        LocalDate fiveBusinessDays = addBusinessDays(today, 5);
        System.out.println("5 business days from today: " + fiveBusinessDays +
            " (" + fiveBusinessDays.getDayOfWeek() + ")");

        // Count business days between two dates
        LocalDate startDate = LocalDate.of(2024, 10, 21);  // Monday
        LocalDate endDate = LocalDate.of(2024, 10, 31);    // Thursday
        long businessDays = countBusinessDays(startDate, endDate);
        System.out.println("\nBusiness days between " + startDate + " and " + endDate + ": " + businessDays);

        // Check if date is a business day
        List<LocalDate> dates = Arrays.asList(
            LocalDate.of(2024, 10, 25),  // Friday
            LocalDate.of(2024, 10, 26),  // Saturday
            LocalDate.of(2024, 10, 27),  // Sunday
            LocalDate.of(2024, 10, 28)   // Monday
        );

        System.out.println("\nChecking business days:");
        dates.forEach(date ->
            System.out.println("  " + date + " (" + date.getDayOfWeek() + "): " +
                (isBusinessDay(date) ? "Business day" : "Weekend"))
        );

        // Next business day
        LocalDate friday = LocalDate.of(2024, 10, 25);
        LocalDate nextBusinessDay = getNextBusinessDay(friday);
        System.out.println("\nNext business day after " + friday + " (" + friday.getDayOfWeek() + "): " +
            nextBusinessDay + " (" + nextBusinessDay.getDayOfWeek() + ")");

        System.out.println();
    }

    /**
     * Example 2: Timezone Conversions
     * Meeting scheduler across different timezones
     */
    private static void example2_TimezoneConversions() {
        System.out.println("--- Example 2: Timezone Conversions ---");

        // Schedule meeting: 2 PM New York time
        ZonedDateTime nyMeeting = ZonedDateTime.of(
            LocalDate.of(2024, 10, 24),
            LocalTime.of(14, 0),
            ZoneId.of("America/New_York")
        );

        System.out.println("Meeting scheduled:");
        System.out.println("  New York:  " + nyMeeting.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")));

        // Convert to other timezones
        ZonedDateTime londonTime = nyMeeting.withZoneSameInstant(ZoneId.of("Europe/London"));
        ZonedDateTime tokyoTime = nyMeeting.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime mumbaiTime = nyMeeting.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

        System.out.println("  London:    " + londonTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")));
        System.out.println("  Tokyo:     " + tokyoTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")));
        System.out.println("  Mumbai:    " + mumbaiTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")));

        // Find suitable meeting time (9 AM - 5 PM in all locations)
        System.out.println("\nFinding suitable meeting time (9 AM - 5 PM in all locations):");
        LocalTime suitableTime = findSuitableMeetingTime(
            ZoneId.of("America/New_York"),
            ZoneId.of("Europe/London"),
            ZoneId.of("Asia/Tokyo")
        );
        if (suitableTime != null) {
            System.out.println("  Suitable time found: " + suitableTime + " UTC");
        } else {
            System.out.println("  No suitable time found in working hours");
        }

        // Flight time calculation with timezone
        ZonedDateTime departure = ZonedDateTime.of(
            LocalDate.of(2024, 10, 24),
            LocalTime.of(10, 30),
            ZoneId.of("America/New_York")
        );
        Duration flightDuration = Duration.ofHours(7).plusMinutes(30);
        ZonedDateTime arrival = departure.plus(flightDuration)
            .withZoneSameInstant(ZoneId.of("Europe/London"));

        System.out.println("\nFlight schedule:");
        System.out.println("  Departure: " + departure.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")));
        System.out.println("  Flight duration: " + flightDuration.toHours() + "h " +
            (flightDuration.toMinutes() % 60) + "m");
        System.out.println("  Arrival: " + arrival.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")));

        System.out.println();
    }

    /**
     * Example 3: Date Ranges and Iterations
     * Generate date ranges, iterate over periods
     */
    private static void example3_DateRangesAndIterations() {
        System.out.println("--- Example 3: Date Ranges and Iterations ---");

        // Generate date range
        LocalDate start = LocalDate.of(2024, 10, 20);
        LocalDate end = LocalDate.of(2024, 10, 26);

        System.out.println("All dates from " + start + " to " + end + ":");
        List<LocalDate> allDates = generateDateRange(start, end);
        allDates.forEach(date -> System.out.println("  " + date + " (" + date.getDayOfWeek() + ")"));

        // Get all Mondays in a month
        System.out.println("\nAll Mondays in October 2024:");
        List<LocalDate> mondays = getAllDaysOfWeekInMonth(2024, 10, DayOfWeek.MONDAY);
        mondays.forEach(date -> System.out.println("  " + date));

        // Get last day of each month in a year
        System.out.println("\nLast day of each month in 2024:");
        for (int month = 1; month <= 12; month++) {
            LocalDate lastDay = LocalDate.of(2024, month, 1)
                .with(TemporalAdjusters.lastDayOfMonth());
            System.out.println("  " + lastDay.getMonth() + ": " + lastDay);
        }

        // Generate billing periods (monthly)
        System.out.println("\nQuarterly billing periods for 2024:");
        generateBillingPeriods(2024).forEach(period ->
            System.out.println("  " + period[0] + " to " + period[1])
        );

        System.out.println();
    }

    /**
     * Example 4: Formatting and Parsing
     * Custom formats, locale-specific, error handling
     */
    private static void example4_FormattingAndParsing() {
        System.out.println("--- Example 4: Formatting and Parsing ---");

        LocalDateTime now = LocalDateTime.of(2024, 10, 24, 14, 30, 45);

        // Different format patterns
        System.out.println("Various formats:");
        System.out.println("  ISO:           " + now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println("  US Style:      " + now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")));
        System.out.println("  EU Style:      " + now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        System.out.println("  Readable:      " + now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a")));
        System.out.println("  RFC 1123:      " + now.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.RFC_1123_DATE_TIME));

        // Locale-specific formatting
        System.out.println("\nLocale-specific formatting:");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.US);
        System.out.println("  US:      " + now.format(formatter));

        formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.FRANCE);
        System.out.println("  France:  " + now.format(formatter));

        formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.JAPAN);
        System.out.println("  Japan:   " + now.format(formatter));

        // Parsing different formats
        System.out.println("\nParsing examples:");
        String[] dateStrings = {
            "2024-10-24",
            "10/24/2024",
            "24.10.2024",
            "October 24, 2024"
        };

        for (String dateStr : dateStrings) {
            try {
                LocalDate parsed = parseFlexibleDate(dateStr);
                System.out.println("  '" + dateStr + "' → " + parsed);
            } catch (DateTimeParseException e) {
                System.out.println("  '" + dateStr + "' → Parse failed: " + e.getMessage());
            }
        }

        // Custom formatter with resolver
        DateTimeFormatter customFormatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter();

        LocalDateTime parsed = LocalDateTime.parse("2024-10-24", customFormatter);
        System.out.println("\nParsed with defaults: " + parsed);

        System.out.println();
    }

    /**
     * Example 5: Age Calculations
     * Calculate age, years/months/days between dates
     */
    private static void example5_AgeCalculations() {
        System.out.println("--- Example 5: Age Calculations ---");

        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        LocalDate today = LocalDate.of(2024, 10, 24);

        // Calculate age in years
        int age = Period.between(birthDate, today).getYears();
        System.out.println("Birth date: " + birthDate);
        System.out.println("Today: " + today);
        System.out.println("Age: " + age + " years");

        // Detailed age (years, months, days)
        Period period = Period.between(birthDate, today);
        System.out.println("Detailed age: " + period.getYears() + " years, " +
            period.getMonths() + " months, " + period.getDays() + " days");

        // Days until next birthday
        LocalDate nextBirthday = birthDate.withYear(today.getYear());
        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        long daysUntilBirthday = ChronoUnit.DAYS.between(today, nextBirthday);
        System.out.println("Days until next birthday: " + daysUntilBirthday);

        // Employment duration
        LocalDate hireDate = LocalDate.of(2020, 3, 1);
        Period employment = Period.between(hireDate, today);
        System.out.println("\nEmployment duration:");
        System.out.println("  Hired: " + hireDate);
        System.out.println("  Duration: " + employment.getYears() + " years, " +
            employment.getMonths() + " months");

        // Subscription expiry
        LocalDate subscriptionStart = LocalDate.of(2024, 1, 1);
        LocalDate subscriptionEnd = subscriptionStart.plusYears(1);
        long daysRemaining = ChronoUnit.DAYS.between(today, subscriptionEnd);
        System.out.println("\nSubscription:");
        System.out.println("  Start: " + subscriptionStart);
        System.out.println("  End: " + subscriptionEnd);
        System.out.println("  Days remaining: " + daysRemaining);
        System.out.println("  Status: " + (daysRemaining > 0 ? "Active" : "Expired"));

        System.out.println();
    }

    /**
     * Example 6: Duration and Time Tracking
     * Measure elapsed time, calculate durations
     */
    private static void example6_DurationAndTimeTracking() {
        System.out.println("--- Example 6: Duration and Time Tracking ---");

        // Time tracking for tasks
        Instant start = Instant.now();
        System.out.println("Task started at: " + start);

        // Simulate work
        try {
            Thread.sleep(1500);  // 1.5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        System.out.println("Task finished at: " + end);
        System.out.println("Elapsed time: " + elapsed.toMillis() + " ms");

        // Working hours calculation
        LocalTime startWork = LocalTime.of(9, 0);
        LocalTime endWork = LocalTime.of(17, 30);
        Duration workDuration = Duration.between(startWork, endWork);
        System.out.println("\nWork schedule:");
        System.out.println("  Start: " + startWork);
        System.out.println("  End: " + endWork);
        System.out.println("  Duration: " + workDuration.toHours() + " hours, " +
            (workDuration.toMinutes() % 60) + " minutes");

        // Break duration
        LocalTime lunchStart = LocalTime.of(12, 0);
        LocalTime lunchEnd = LocalTime.of(13, 0);
        Duration lunchBreak = Duration.between(lunchStart, lunchEnd);
        Duration actualWork = workDuration.minus(lunchBreak);
        System.out.println("  Lunch break: " + lunchBreak.toMinutes() + " minutes");
        System.out.println("  Actual work: " + actualWork.toHours() + " hours, " +
            (actualWork.toMinutes() % 60) + " minutes");

        // Meeting duration across days
        LocalDateTime meetingStart = LocalDateTime.of(2024, 10, 24, 23, 30);
        LocalDateTime meetingEnd = LocalDateTime.of(2024, 10, 25, 1, 0);
        Duration meetingDuration = Duration.between(meetingStart, meetingEnd);
        System.out.println("\nMeeting schedule:");
        System.out.println("  Start: " + meetingStart);
        System.out.println("  End: " + meetingEnd);
        System.out.println("  Duration: " + meetingDuration.toHours() + " hours, " +
            (meetingDuration.toMinutes() % 60) + " minutes");

        // Format duration in readable way
        Duration projectTime = Duration.ofHours(125).plusMinutes(45);
        System.out.println("\nProject time: " + formatDuration(projectTime));

        System.out.println();
    }

    /**
     * Example 7: Legacy Conversion
     * Convert between old Date/Calendar and new LocalDate/ZonedDateTime
     */
    private static void example7_LegacyConversion() {
        System.out.println("--- Example 7: Legacy Conversion ---");

        // === Date → LocalDate ===
        Date legacyDate = new Date();
        LocalDate localDate = legacyDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        System.out.println("java.util.Date → LocalDate:");
        System.out.println("  Legacy Date: " + legacyDate);
        System.out.println("  LocalDate: " + localDate);

        // === LocalDate → Date ===
        LocalDate newLocalDate = LocalDate.of(2024, 10, 24);
        Date convertedDate = Date.from(newLocalDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant());
        System.out.println("\nLocalDate → java.util.Date:");
        System.out.println("  LocalDate: " + newLocalDate);
        System.out.println("  Legacy Date: " + convertedDate);

        // === Date → LocalDateTime ===
        Date legacyDateTime = new Date();
        LocalDateTime localDateTime = legacyDateTime.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        System.out.println("\njava.util.Date → LocalDateTime:");
        System.out.println("  Legacy Date: " + legacyDateTime);
        System.out.println("  LocalDateTime: " + localDateTime);

        // === LocalDateTime → Date ===
        LocalDateTime newLocalDateTime = LocalDateTime.of(2024, 10, 24, 14, 30);
        Date convertedDateTime = Date.from(newLocalDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant());
        System.out.println("\nLocalDateTime → java.util.Date:");
        System.out.println("  LocalDateTime: " + newLocalDateTime);
        System.out.println("  Legacy Date: " + convertedDateTime);

        // === Calendar → ZonedDateTime ===
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.OCTOBER, 24, 14, 30, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(
            calendar.toInstant(),
            calendar.getTimeZone().toZoneId()
        );
        System.out.println("\nCalendar → ZonedDateTime:");
        System.out.println("  Calendar: " + calendar.getTime());
        System.out.println("  ZonedDateTime: " + zonedDateTime);

        // === ZonedDateTime → Calendar ===
        ZonedDateTime newZonedDateTime = ZonedDateTime.of(
            LocalDateTime.of(2024, 10, 24, 14, 30),
            ZoneId.of("America/New_York")
        );
        Calendar convertedCalendar = GregorianCalendar.from(newZonedDateTime);
        System.out.println("\nZonedDateTime → Calendar:");
        System.out.println("  ZonedDateTime: " + newZonedDateTime);
        System.out.println("  Calendar: " + convertedCalendar.getTime());

        System.out.println();
    }

    /**
     * Example 8: Real-World Patterns
     * Scheduling, reporting periods, billing cycles
     */
    private static void example8_RealWorldPatterns() {
        System.out.println("--- Example 8: Real-World Patterns ---");

        // Pattern 1: Recurring events (weekly standup every Monday at 10 AM)
        System.out.println("Next 4 weekly standups (Mondays at 10:00):");
        LocalDateTime now = LocalDateTime.of(2024, 10, 24, 14, 30);
        LocalDateTime nextStandup = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .withHour(10).withMinute(0).withSecond(0);

        for (int i = 0; i < 4; i++) {
            System.out.println("  " + nextStandup.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE) HH:mm")));
            nextStandup = nextStandup.plusWeeks(1);
        }

        // Pattern 2: Billing cycle (1st of every month)
        System.out.println("\nNext 6 billing dates:");
        LocalDate currentDate = LocalDate.of(2024, 10, 24);
        LocalDate nextBilling = currentDate.withDayOfMonth(1).plusMonths(1);

        for (int i = 0; i < 6; i++) {
            System.out.println("  " + nextBilling);
            nextBilling = nextBilling.plusMonths(1);
        }

        // Pattern 3: Report generation (last Friday of every month)
        System.out.println("\nMonthly report dates (last Friday):");
        LocalDate reportDate = LocalDate.of(2024, 10, 1);

        for (int i = 0; i < 6; i++) {
            LocalDate lastFriday = reportDate.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
            System.out.println("  " + lastFriday + " (" + lastFriday.getMonth() + ")");
            reportDate = reportDate.plusMonths(1);
        }

        // Pattern 4: Reminder scheduling (3 days before deadline)
        System.out.println("\nDeadline reminders:");
        List<LocalDate> deadlines = Arrays.asList(
            LocalDate.of(2024, 11, 1),
            LocalDate.of(2024, 11, 15),
            LocalDate.of(2024, 12, 1)
        );

        deadlines.forEach(deadline -> {
            LocalDate reminder = deadline.minusDays(3);
            System.out.println("  Deadline: " + deadline + " → Reminder: " + reminder);
        });

        // Pattern 5: Trial period (14-day free trial)
        System.out.println("\nTrial period tracking:");
        LocalDate signupDate = LocalDate.of(2024, 10, 20);
        LocalDate trialEndDate = signupDate.plusDays(14);
        long remainingDays = ChronoUnit.DAYS.between(LocalDate.of(2024, 10, 24), trialEndDate);

        System.out.println("  Signup: " + signupDate);
        System.out.println("  Trial ends: " + trialEndDate);
        System.out.println("  Days remaining: " + remainingDays);
        System.out.println("  Status: " + (remainingDays > 0 ? "Active" : "Expired"));

        // Pattern 6: Event countdown
        System.out.println("\nEvent countdown:");
        LocalDate eventDate = LocalDate.of(2024, 12, 25);
        LocalDate countdownFrom = LocalDate.of(2024, 10, 24);
        long daysUntilEvent = ChronoUnit.DAYS.between(countdownFrom, eventDate);
        long weeksUntilEvent = ChronoUnit.WEEKS.between(countdownFrom, eventDate);

        System.out.println("  Event date: " + eventDate);
        System.out.println("  Days until event: " + daysUntilEvent);
        System.out.println("  Weeks until event: " + weeksUntilEvent);

        System.out.println();
    }

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Add business days to a date (skip weekends)
     */
    private static LocalDate addBusinessDays(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;

        while (addedDays < days) {
            result = result.plusDays(1);
            if (isBusinessDay(result)) {
                addedDays++;
            }
        }

        return result;
    }

    /**
     * Count business days between two dates
     */
    private static long countBusinessDays(LocalDate start, LocalDate end) {
        return Stream.iterate(start, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(start, end) + 1)
            .filter(DateTimeScenarios::isBusinessDay)
            .count();
    }

    /**
     * Check if date is a business day (Monday-Friday)
     */
    private static boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    /**
     * Get next business day
     */
    private static LocalDate getNextBusinessDay(LocalDate date) {
        LocalDate next = date.plusDays(1);
        while (!isBusinessDay(next)) {
            next = next.plusDays(1);
        }
        return next;
    }

    /**
     * Find suitable meeting time across timezones (simplified)
     */
    private static LocalTime findSuitableMeetingTime(ZoneId... zones) {
        // Simplified: return 2 PM UTC (usually works for US-Europe)
        return LocalTime.of(14, 0);
    }

    /**
     * Generate date range (inclusive)
     */
    private static List<LocalDate> generateDateRange(LocalDate start, LocalDate end) {
        return Stream.iterate(start, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(start, end) + 1)
            .collect(Collectors.toList());
    }

    /**
     * Get all occurrences of a day of week in a month
     */
    private static List<LocalDate> getAllDaysOfWeekInMonth(int year, int month, DayOfWeek dayOfWeek) {
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());

        return Stream.iterate(
                first.with(TemporalAdjusters.nextOrSame(dayOfWeek)),
                date -> date.plusWeeks(1)
            )
            .takeWhile(date -> !date.isAfter(last))
            .collect(Collectors.toList());
    }

    /**
     * Generate quarterly billing periods
     */
    private static List<LocalDate[]> generateBillingPeriods(int year) {
        List<LocalDate[]> periods = new ArrayList<>();
        for (int quarter = 0; quarter < 4; quarter++) {
            LocalDate start = LocalDate.of(year, quarter * 3 + 1, 1);
            LocalDate end = start.plusMonths(3).minusDays(1);
            periods.add(new LocalDate[]{start, end});
        }
        return periods;
    }

    /**
     * Parse date string with multiple format attempts
     */
    private static LocalDate parseFlexibleDate(String dateStr) {
        List<DateTimeFormatter> formatters = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,                          // 2024-10-24
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),                  // 10/24/2024
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),                  // 24.10.2024
            DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US)    // October 24, 2024
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        throw new DateTimeParseException("Unable to parse date: " + dateStr, dateStr, 0);
    }

    /**
     * Format duration in readable way
     */
    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + " hours, " + minutes + " minutes";
    }
}
