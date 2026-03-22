package examples.java11;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Java 11 String Methods and File I/O Demo
 *
 * This example demonstrates:
 * 1. New String methods: isBlank(), strip(), lines(), repeat()
 * 2. Files.readString() and Files.writeString()
 * 3. Real-world text processing scenarios
 * 4. Combined usage of String methods with streams
 *
 * Real-world scenario: Log file processing and configuration management
 */
public class StringMethodsDemo {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Java 11: String Methods & File I/O ===\n");

        // Example 1: isBlank() - validation
        example1_IsBlank();

        // Example 2: strip() variants
        example2_Strip();

        // Example 3: lines() - text processing
        example3_Lines();

        // Example 4: repeat() - formatting
        example4_Repeat();

        // Example 5: Files.readString() and writeString()
        example5_FileOperations();

        // Example 6: Real-world log processing
        example6_LogProcessing();

        // Example 7: Configuration file handling
        example7_ConfigurationManagement();
    }

    /**
     * Example 1: isBlank() - User Input Validation
     * Demonstrates difference between isEmpty() and isBlank()
     */
    private static void example1_IsBlank() {
        System.out.println("--- Example 1: isBlank() for Validation ---");

        String[] inputs = {
            "John Doe",
            "",
            "   ",
            "\t\n",
            "  Alice  "
        };

        System.out.println("Input validation:");
        for (String input : inputs) {
            String display = input.isEmpty() ? "(empty)" :
                           input.isBlank() ? "(whitespace)" :
                           input;

            System.out.println("  Input: \"" + display + "\"");
            System.out.println("    isEmpty():  " + input.isEmpty());
            System.out.println("    isBlank():  " + input.isBlank());
            System.out.println("    Valid user input: " + isValidInput(input));
            System.out.println();
        }
    }

    private static boolean isValidInput(String input) {
        return input != null && !input.isBlank();
    }

    /**
     * Example 2: strip(), stripLeading(), stripTrailing()
     * Shows Unicode-aware whitespace removal
     */
    private static void example2_Strip() {
        System.out.println("--- Example 2: strip() Variants ---");

        // Regular whitespace
        String text1 = "  Hello World  ";
        System.out.println("Original: \"" + text1 + "\"");
        System.out.println("strip():         \"" + text1.strip() + "\"");
        System.out.println("stripLeading():  \"" + text1.stripLeading() + "\"");
        System.out.println("stripTrailing(): \"" + text1.stripTrailing() + "\"");
        System.out.println("trim():          \"" + text1.trim() + "\"");

        // Unicode whitespace (strip vs trim difference)
        String text2 = "\u2000Hello\u2000";  // Unicode EN QUAD spaces
        System.out.println("\nWith Unicode whitespace:");
        System.out.println("Original: \"" + text2 + "\" (with Unicode spaces)");
        System.out.println("strip(): \"" + text2.strip() + "\"");
        System.out.println("trim():  \"" + text2.trim() + "\" (doesn't remove Unicode spaces)");

        // Real-world: cleaning user input
        System.out.println("\nCleaning user input:");
        String[] userInputs = {"  john@example.com  ", "\tpassword123\n", "  John Doe  "};
        for (String input : userInputs) {
            System.out.println("  Input: \"" + input + "\" → \"" + input.strip() + "\"");
        }

        System.out.println();
    }

    /**
     * Example 3: lines() - Stream of Lines
     * Process multi-line text with streams
     */
    private static void example3_Lines() {
        System.out.println("--- Example 3: lines() for Text Processing ---");

        String logData = """
            INFO: Application started
            DEBUG: Loading configuration
            INFO: Database connected
            ERROR: Failed to load plugin
            WARN: Memory usage high
            INFO: Request processed
            ERROR: Timeout occurred
            """;

        // Count lines by level
        System.out.println("Log analysis:");
        Map<String, Long> countByLevel = logData.lines()
            .filter(line -> !line.isBlank())
            .collect(Collectors.groupingBy(
                line -> line.split(":")[0],
                Collectors.counting()
            ));

        countByLevel.forEach((level, count) ->
            System.out.println("  " + level + ": " + count + " entries")
        );

        // Extract only errors
        System.out.println("\nError messages:");
        logData.lines()
            .filter(line -> line.startsWith("ERROR"))
            .forEach(line -> System.out.println("  " + line));

        // Process CSV data
        String csvData = """
            Name,Age,City
            Alice,30,New York
            Bob,25,London
            Charlie,35,Tokyo
            """;

        System.out.println("\nParsing CSV:");
        List<Person> people = csvData.lines()
            .skip(1)  // Skip header
            .filter(line -> !line.isBlank())
            .map(line -> line.split(","))
            .map(parts -> new Person(parts[0], Integer.parseInt(parts[1]), parts[2]))
            .collect(Collectors.toList());

        people.forEach(person ->
            System.out.println("  " + person.name + " (" + person.age + ") from " + person.city)
        );

        System.out.println();
    }

    /**
     * Example 4: repeat() - String Repetition
     * Creating formatted output
     */
    private static void example4_Repeat() {
        System.out.println("--- Example 4: repeat() for Formatting ---");

        // Simple separators
        String separator = "=".repeat(60);
        System.out.println(separator);
        System.out.println("REPORT TITLE");
        System.out.println(separator);

        // Indentation
        String indent = "  ".repeat(3);
        System.out.println(indent + "Indented text");

        // Creating a simple table
        System.out.println("\nFormatted table:");
        System.out.println("-".repeat(40));
        System.out.println("Product" + " ".repeat(13) + "Price");
        System.out.println("-".repeat(40));
        System.out.println("Laptop" + " ".repeat(14) + "$999.99");
        System.out.println("Mouse" + " ".repeat(15) + "$29.99");
        System.out.println("-".repeat(40));

        // Progress bar simulation
        System.out.println("\nProgress bars:");
        for (int i = 0; i <= 100; i += 20) {
            int filled = i / 5;
            int empty = 20 - filled;
            String bar = "[" + "=".repeat(filled) + " ".repeat(empty) + "] " + i + "%";
            System.out.println("  " + bar);
        }

        System.out.println();
    }

    /**
     * Example 5: Files.readString() and writeString()
     * Simple file I/O operations
     */
    private static void example5_FileOperations() throws IOException {
        System.out.println("--- Example 5: Files.readString() & writeString() ---");

        // Create a temporary file
        Path tempFile = Files.createTempFile("demo", ".txt");
        System.out.println("Created temp file: " + tempFile);

        // Write string to file
        String content = """
            Hello, Java 11!
            This is a multi-line string.
            Written using Files.writeString().
            """;

        Files.writeString(tempFile, content, StandardCharsets.UTF_8);
        System.out.println("Wrote content to file");

        // Read string from file
        String readContent = Files.readString(tempFile, StandardCharsets.UTF_8);
        System.out.println("\nRead content:");
        System.out.println(readContent);

        // Append to file
        String additionalContent = "Appended line\n";
        Files.writeString(tempFile, additionalContent, StandardCharsets.UTF_8,
                         StandardOpenOption.APPEND);

        // Read again
        String updatedContent = Files.readString(tempFile);
        System.out.println("After appending:");
        System.out.println(updatedContent);

        // Count lines
        long lineCount = updatedContent.lines().count();
        System.out.println("Total lines: " + lineCount);

        // Cleanup
        Files.deleteIfExists(tempFile);
        System.out.println("Cleaned up temp file\n");
    }

    /**
     * Example 6: Real-World Log Processing
     * Analyze log files using String methods and streams
     */
    private static void example6_LogProcessing() throws IOException {
        System.out.println("--- Example 6: Real-World Log Processing ---");

        // Create sample log file
        Path logFile = Files.createTempFile("application", ".log");

        String logContent = """
            2024-10-26 10:00:01 INFO  Application started
            2024-10-26 10:00:02 DEBUG Loading configuration from /etc/app/config
            2024-10-26 10:00:03 INFO  Database connection established
            2024-10-26 10:15:30 WARN  High memory usage: 85%
            2024-10-26 10:30:45 ERROR Failed to process order #12345
            2024-10-26 10:30:46 ERROR Stack trace: NullPointerException at...
            2024-10-26 10:45:00 INFO  Order #12346 processed successfully
            2024-10-26 11:00:00 ERROR Connection timeout to payment gateway
            2024-10-26 11:15:20 WARN  Retry attempt 1 of 3
            2024-10-26 11:15:25 INFO  Payment processed successfully
            """;

        Files.writeString(logFile, logContent);

        // Read and process log
        String logs = Files.readString(logFile);

        // Count by log level
        System.out.println("Log statistics:");
        Map<String, Long> stats = logs.lines()
            .filter(line -> !line.isBlank())
            .map(line -> line.split("\\s+"))
            .filter(parts -> parts.length >= 3)
            .collect(Collectors.groupingBy(
                parts -> parts[2],  // Log level
                Collectors.counting()
            ));

        stats.forEach((level, count) ->
            System.out.println("  " + level + ": " + count)
        );

        // Extract error messages
        System.out.println("\nError messages:");
        List<String> errors = logs.lines()
            .filter(line -> line.contains("ERROR"))
            .map(String::strip)
            .collect(Collectors.toList());

        errors.forEach(error ->
            System.out.println("  " + error)
        );

        // Find warnings
        System.out.println("\nWarnings:");
        long warningCount = logs.lines()
            .filter(line -> line.contains("WARN"))
            .count();
        System.out.println("  Total warnings: " + warningCount);

        // Time range
        System.out.println("\nLog time range:");
        logs.lines()
            .filter(line -> !line.isBlank())
            .limit(1)
            .forEach(line -> System.out.println("  First: " + line.substring(0, 19)));

        logs.lines()
            .filter(line -> !line.isBlank())
            .reduce((first, second) -> second)
            .ifPresent(line -> System.out.println("  Last:  " + line.substring(0, 19)));

        // Cleanup
        Files.deleteIfExists(logFile);
        System.out.println();
    }

    /**
     * Example 7: Configuration File Management
     * Read, modify, and write configuration files
     */
    private static void example7_ConfigurationManagement() throws IOException {
        System.out.println("--- Example 7: Configuration Management ---");

        // Create config file
        Path configFile = Files.createTempFile("app", ".properties");

        String initialConfig = """
            # Application Configuration
            app.name=MyApplication
            app.version=1.0.0

            # Database Settings
            db.host=localhost
            db.port=5432
            db.name=mydb

            # Feature Flags
            feature.dark_mode=true
            feature.analytics=false
            """;

        Files.writeString(configFile, initialConfig);
        System.out.println("Created configuration file");

        // Read and parse config
        String config = Files.readString(configFile);

        Map<String, String> settings = config.lines()
            .filter(line -> !line.isBlank())
            .filter(line -> !line.strip().startsWith("#"))
            .map(line -> line.split("=", 2))
            .filter(parts -> parts.length == 2)
            .collect(Collectors.toMap(
                parts -> parts[0].strip(),
                parts -> parts[1].strip()
            ));

        System.out.println("\nParsed settings:");
        settings.forEach((key, value) ->
            System.out.println("  " + key + " = " + value)
        );

        // Update a setting
        settings.put("app.version", "2.0.0");
        settings.put("feature.dark_mode", "false");

        // Write back to file
        String updatedConfig = "# Updated Configuration\n" +
            settings.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));

        Files.writeString(configFile, updatedConfig);
        System.out.println("\nUpdated configuration written");

        // Verify
        String verifyConfig = Files.readString(configFile);
        System.out.println("\nVerified content:");
        verifyConfig.lines()
            .forEach(line -> System.out.println("  " + line));

        // Cleanup
        Files.deleteIfExists(configFile);
        System.out.println("\nCleaned up config file");
    }

    // Domain class
    static class Person {
        final String name;
        final int age;
        final String city;

        Person(String name, int age, String city) {
            this.name = name;
            this.age = age;
            this.city = city;
        }
    }
}
