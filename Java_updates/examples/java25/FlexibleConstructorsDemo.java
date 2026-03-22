package examples.java25;

import java.time.LocalDate;
import java.util.*;

/**
 * Java 25 Flexible Constructor Bodies Demo
 *
 * This example demonstrates:
 * 1. Basic validation before super()
 * 2. Preprocessing before super()
 * 3. Complex validation scenarios
 * 4. Real-world use cases
 * 5. Comparison with old workarounds
 * 6. Best practices
 * 7. Common patterns
 *
 * Java 25 allows statements before super()/this() calls,
 * making validation and preprocessing cleaner.
 */
public class FlexibleConstructorsDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 25: Flexible Constructor Bodies ===\n");

        // Example 1: Basic validation
        example1_BasicValidation();

        // Example 2: Preprocessing before super()
        example2_Preprocessing();

        // Example 3: Complex validation
        example3_ComplexValidation();

        // Example 4: Real-world bank account
        example4_BankAccount();

        // Example 5: Email validation
        example5_EmailValidation();

        // Example 6: Range validation
        example6_RangeValidation();

        // Example 7: Before vs After comparison
        example7_BeforeAndAfter();
    }

    /**
     * Example 1: Basic Validation Before super()
     * Simple null and range checks
     */
    private static void example1_BasicValidation() {
        System.out.println("--- Example 1: Basic Validation ---");

        // ✅ Valid positive number
        try {
            PositiveNumber num1 = new PositiveNumber(42);
            System.out.println("  ✓ Created: " + num1);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        // ❌ Invalid negative number
        try {
            PositiveNumber num2 = new PositiveNumber(-5);
            System.out.println("  ✓ Created: " + num2);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        // ❌ Invalid zero
        try {
            PositiveNumber num3 = new PositiveNumber(0);
            System.out.println("  ✓ Created: " + num3);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    // Java 25 - validation before field initialization
    static class PositiveNumber {
        private final int value;

        public PositiveNumber(int value) {
            // ✅ Now allowed in Java 25 - validate before construction
            if (value <= 0) {
                throw new IllegalArgumentException(
                    "Value must be positive, got: " + value);
            }
            this.value = value;
        }

        @Override
        public String toString() {
            return "PositiveNumber(" + value + ")";
        }
    }

    /**
     * Example 2: Preprocessing Before super()
     * Transform inputs before calling parent constructor
     */
    private static void example2_Preprocessing() {
        System.out.println("--- Example 2: Preprocessing Before super() ---");

        Person person1 = new Person("  Alice  ");  // Extra whitespace
        System.out.println("  Created: " + person1);

        Person person2 = new Person("bob");  // Lowercase
        System.out.println("  Created: " + person2);

        try {
            Person person3 = new Person("");  // Empty after trim
            System.out.println("  Created: " + person3);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    static class Person {
        private final String name;

        public Person(String name) {
            // ✅ Preprocess before initializing
            Objects.requireNonNull(name, "Name cannot be null");
            String processed = name.trim();

            if (processed.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            // Store normalized version
            this.name = capitalizeFirstLetter(processed);
        }

        private static String capitalizeFirstLetter(String str) {
            return str.substring(0, 1).toUpperCase() +
                   str.substring(1).toLowerCase();
        }

        @Override
        public String toString() {
            return "Person(name=" + name + ")";
        }
    }

    /**
     * Example 3: Complex Validation
     * Multiple validation rules before construction
     */
    private static void example3_ComplexValidation() {
        System.out.println("--- Example 3: Complex Validation ---");

        // ✅ Valid email
        try {
            EmailAddress email1 = new EmailAddress("alice@example.com");
            System.out.println("  ✓ Valid: " + email1);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        // ❌ No @ symbol
        try {
            EmailAddress email2 = new EmailAddress("invalid");
            System.out.println("  ✓ Valid: " + email2);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        // ❌ No domain
        try {
            EmailAddress email3 = new EmailAddress("user@");
            System.out.println("  ✓ Valid: " + email3);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    static class EmailAddress {
        private final String address;

        public EmailAddress(String address) {
            // Multiple validation steps before construction
            Objects.requireNonNull(address, "Email cannot be null");

            String normalized = address.trim().toLowerCase();

            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            if (!normalized.contains("@")) {
                throw new IllegalArgumentException("Email must contain @");
            }

            String[] parts = normalized.split("@");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Email must have exactly one @");
            }

            if (parts[0].isEmpty()) {
                throw new IllegalArgumentException("Email must have username before @");
            }

            if (parts[1].isEmpty() || !parts[1].contains(".")) {
                throw new IllegalArgumentException("Email must have valid domain");
            }

            this.address = normalized;
        }

        @Override
        public String toString() {
            return address;
        }
    }

    /**
     * Example 4: Real-World Bank Account
     * Validation for financial transactions
     */
    private static void example4_BankAccount() {
        System.out.println("--- Example 4: Bank Account ---");

        // ✅ Valid account
        try {
            BankAccount account1 = new BankAccount("Alice", 1000.0);
            System.out.println("  ✓ " + account1);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        // ❌ Negative balance
        try {
            BankAccount account2 = new BankAccount("Bob", -100.0);
            System.out.println("  ✓ " + account2);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        // ❌ Invalid name
        try {
            BankAccount account3 = new BankAccount("", 500.0);
            System.out.println("  ✓ " + account3);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    static class BankAccount {
        private final String accountHolder;
        private final double balance;

        public BankAccount(String accountHolder, double initialBalance) {
            // Validate account holder
            Objects.requireNonNull(accountHolder, "Account holder cannot be null");
            String name = accountHolder.trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Account holder name cannot be empty");
            }

            // Validate initial balance
            if (initialBalance < 0) {
                throw new IllegalArgumentException(
                    "Initial balance cannot be negative: " + initialBalance);
            }

            if (initialBalance > 1_000_000) {
                throw new IllegalArgumentException(
                    "Initial balance too large (max 1M): " + initialBalance);
            }

            this.accountHolder = name;
            this.balance = initialBalance;
        }

        @Override
        public String toString() {
            return String.format("BankAccount[holder=%s, balance=%.2f]",
                accountHolder, balance);
        }
    }

    /**
     * Example 5: Email Validation with Inheritance
     * Shows validation before calling super()
     */
    private static void example5_EmailValidation() {
        System.out.println("--- Example 5: Email Validation with Inheritance ---");

        try {
            ValidatedUser user1 = new ValidatedUser("alice@example.com", 25);
            System.out.println("  ✓ " + user1);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        try {
            ValidatedUser user2 = new ValidatedUser("invalid-email", 30);
            System.out.println("  ✓ " + user2);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    static class User {
        private final String email;
        private final int age;

        public User(String email, int age) {
            this.email = email;
            this.age = age;
        }

        @Override
        public String toString() {
            return String.format("User[email=%s, age=%d]", email, age);
        }
    }

    static class ValidatedUser extends User {
        public ValidatedUser(String email, int age) {
            // ✅ Java 25 - validate and preprocess before super()
            Objects.requireNonNull(email, "Email cannot be null");

            String normalized = email.trim().toLowerCase();

            if (!normalized.contains("@") || !normalized.contains(".")) {
                throw new IllegalArgumentException("Invalid email format: " + email);
            }

            if (age < 18 || age > 120) {
                throw new IllegalArgumentException("Age must be 18-120: " + age);
            }

            // Call parent constructor with validated data
            super(normalized, age);
        }
    }

    /**
     * Example 6: Range Validation
     * Ensuring min <= max for ranges
     */
    private static void example6_RangeValidation() {
        System.out.println("--- Example 6: Range Validation ---");

        try {
            Range range1 = new Range(1, 10);
            System.out.println("  ✓ " + range1);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        try {
            Range range2 = new Range(10, 1);  // Invalid: min > max
            System.out.println("  ✓ " + range2);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        try {
            Range range3 = new Range(5, 5);  // Valid: min == max
            System.out.println("  ✓ " + range3);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    static class Range {
        private final int min;
        private final int max;

        public Range(int min, int max) {
            // ✅ Validate relationship between parameters
            if (min > max) {
                throw new IllegalArgumentException(
                    String.format("min (%d) must be <= max (%d)", min, max));
            }

            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return String.format("Range[%d..%d]", min, max);
        }
    }

    /**
     * Example 7: Before and After Comparison
     * Shows the improvement over old workarounds
     */
    private static void example7_BeforeAndAfter() {
        System.out.println("--- Example 7: Before and After Comparison ---");

        System.out.println("Old way (Java < 25) required workarounds:");
        System.out.println("  - Helper methods");
        System.out.println("  - Private constructors");
        System.out.println("  - Factory methods");

        System.out.println("\nNew way (Java 25) is natural:");
        System.out.println("  - Direct validation in constructor");
        System.out.println("  - Clear error messages");
        System.out.println("  - No workarounds needed");

        // Demonstrate the new natural way
        try {
            ModernPassword password = new ModernPassword("short");
            System.out.println("  ✓ " + password);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        try {
            ModernPassword password = new ModernPassword("SecurePassword123");
            System.out.println("  ✓ " + password);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ " + e.getMessage());
        }

        System.out.println();
    }

    static class ModernPassword {
        private final String value;

        public ModernPassword(String password) {
            // ✅ Natural validation - no workarounds!
            Objects.requireNonNull(password, "Password cannot be null");

            if (password.length() < 8) {
                throw new IllegalArgumentException(
                    "Password must be at least 8 characters (got " + password.length() + ")");
            }

            if (password.length() > 128) {
                throw new IllegalArgumentException(
                    "Password too long (max 128 characters)");
            }

            if (!password.matches(".*[0-9].*")) {
                throw new IllegalArgumentException(
                    "Password must contain at least one digit");
            }

            this.value = password;
        }

        @Override
        public String toString() {
            return "Password(length=" + value.length() + ", secure=true)";
        }
    }
}
