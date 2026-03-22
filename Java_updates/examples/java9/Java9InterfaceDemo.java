package examples.java9;

/**
 * Java 9 Private Interface Methods Demo
 *
 * This example demonstrates:
 * 1. Private methods in interfaces (code reuse)
 * 2. Private static methods in interfaces
 * 3. How they help avoid code duplication in default methods
 *
 * Real-world scenario: Payment processing system with multiple payment methods
 */
public class Java9InterfaceDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 9: Private Interface Methods ===\n");

        // Example 1: Payment processors using private interface methods
        example1_PaymentProcessing();

        // Example 2: Logger with private static helper methods
        example2_LoggerInterface();

        // Example 3: Validator with complex validation logic
        example3_ValidationInterface();

        // Example 4: Real-world scenario - multiple payment methods
        example4_RealWorldPayments();
    }

    /**
     * Example 1: Payment Processing with Private Methods
     * Shows how private methods reduce code duplication
     */
    private static void example1_PaymentProcessing() {
        System.out.println("--- Example 1: Payment Processing ---");

        PaymentProcessor creditCard = new CreditCardProcessor();
        PaymentProcessor paypal = new PayPalProcessor();
        PaymentProcessor bitcoin = new BitcoinProcessor();

        double amount = 100.00;

        System.out.println("\nProcessing $" + amount + " payments:");
        creditCard.processPayment(amount);
        paypal.processPayment(amount);
        bitcoin.processPayment(amount);

        System.out.println("\nProcessing refunds:");
        creditCard.processRefund(amount);
        paypal.processRefund(amount);
        bitcoin.processRefund(amount);

        System.out.println();
    }

    /**
     * Example 2: Logger with Private Static Methods
     * Demonstrates private static helpers for formatting
     */
    private static void example2_LoggerInterface() {
        System.out.println("--- Example 2: Logger Interface ---");

        Logger appLogger = new ApplicationLogger();

        appLogger.logInfo("Application started successfully");
        appLogger.logWarning("Memory usage at 75%");
        appLogger.logError("Database connection timeout");
        appLogger.logDebug("User session initialized");

        System.out.println();
    }

    /**
     * Example 3: Validator with Complex Validation Logic
     * Shows how private methods handle complex validation steps
     */
    private static void example3_ValidationInterface() {
        System.out.println("--- Example 3: Validation Interface ---");

        UserValidator validator = new UserValidatorImpl();

        // Valid user
        User validUser = new User("john.doe@example.com", "SecurePass123!", 25);
        System.out.println("Validating valid user:");
        boolean isValid = validator.validateUser(validUser);
        System.out.println("  Result: " + (isValid ? "VALID" : "INVALID") + "\n");

        // Invalid email
        User invalidEmail = new User("invalid-email", "SecurePass123!", 25);
        System.out.println("Validating user with invalid email:");
        validator.validateUser(invalidEmail);
        System.out.println();

        // Invalid password
        User invalidPassword = new User("jane@example.com", "weak", 25);
        System.out.println("Validating user with weak password:");
        validator.validateUser(invalidPassword);
        System.out.println();

        // Invalid age
        User invalidAge = new User("kid@example.com", "SecurePass123!", 15);
        System.out.println("Validating underage user:");
        validator.validateUser(invalidAge);
        System.out.println();
    }

    /**
     * Example 4: Real-World Combined Scenario
     * E-commerce checkout with multiple payment options
     */
    private static void example4_RealWorldPayments() {
        System.out.println("--- Example 4: Real-World E-commerce Checkout ---");

        // Shopping cart
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Laptop", 999.99);
        cart.addItem("Mouse", 29.99);
        cart.addItem("Keyboard", 79.99);

        double total = cart.getTotal();
        System.out.println("Cart total: $" + String.format("%.2f", total));
        System.out.println();

        // Try different payment methods
        PaymentProcessor[] processors = {
            new CreditCardProcessor(),
            new PayPalProcessor(),
            new BitcoinProcessor()
        };

        for (PaymentProcessor processor : processors) {
            processor.processPayment(total);
        }

        System.out.println();
    }

    // ========== Payment Processing Interface ==========

    /**
     * Payment processor interface demonstrating private methods
     * Private methods avoid code duplication in default methods
     */
    interface PaymentProcessor {

        // Abstract method - must be implemented
        String getProcessorName();

        // Default method for payment processing
        default void processPayment(double amount) {
            if (validateAmount(amount)) {
                logTransaction("PAYMENT", amount);
                System.out.println("  [" + getProcessorName() + "] Payment of $" +
                    String.format("%.2f", amount) + " processed successfully");
            } else {
                System.out.println("  [" + getProcessorName() + "] Invalid amount");
            }
        }

        // Default method for refund processing
        default void processRefund(double amount) {
            if (validateAmount(amount)) {
                logTransaction("REFUND", amount);
                System.out.println("  [" + getProcessorName() + "] Refund of $" +
                    String.format("%.2f", amount) + " processed successfully");
            } else {
                System.out.println("  [" + getProcessorName() + "] Invalid refund amount");
            }
        }

        // JAVA 9: Private method - code reuse between default methods
        // This avoids duplicating validation logic in both processPayment and processRefund
        private boolean validateAmount(double amount) {
            return amount > 0 && amount <= 10000;  // Max transaction limit
        }

        // JAVA 9: Private method for logging
        // Shared by both payment and refund operations
        private void logTransaction(String type, double amount) {
            // In real app, this would write to database or log file
            System.out.println("  [LOG] " + type + " transaction: $" + String.format("%.2f", amount));
        }
    }

    // ========== Logger Interface ==========

    /**
     * Logger interface demonstrating private static methods
     * Private static methods provide utility functions
     */
    interface Logger {

        // Abstract method
        void log(String level, String message);

        // Default methods for different log levels
        default void logInfo(String message) {
            log("INFO", formatMessage("INFO", message));
        }

        default void logWarning(String message) {
            log("WARNING", formatMessage("WARNING", message));
        }

        default void logError(String message) {
            log("ERROR", formatMessage("ERROR", message));
        }

        default void logDebug(String message) {
            log("DEBUG", formatMessage("DEBUG", message));
        }

        // JAVA 9: Private static method - formatting utility
        // Static because it doesn't depend on instance state
        private static String formatMessage(String level, String message) {
            return String.format("[%s] %s", level, message);
        }

        // JAVA 9: Private static method - timestamp formatting
        private static String getTimestamp() {
            return java.time.LocalDateTime.now().toString();
        }
    }

    // ========== Validation Interface ==========

    /**
     * User validator interface demonstrating complex private validation methods
     */
    interface UserValidator {

        // Default method using multiple private helpers
        default boolean validateUser(User user) {
            boolean emailValid = validateEmail(user.email);
            boolean passwordValid = validatePassword(user.password);
            boolean ageValid = validateAge(user.age);

            return emailValid && passwordValid && ageValid;
        }

        // JAVA 9: Private validation methods
        // These break down complex validation into manageable pieces

        private boolean validateEmail(String email) {
            boolean isValid = email != null && email.contains("@") && email.contains(".");
            if (!isValid) {
                System.out.println("  ✗ Email validation failed: " + email);
            } else {
                System.out.println("  ✓ Email is valid: " + email);
            }
            return isValid;
        }

        private boolean validatePassword(String password) {
            boolean hasMinLength = password != null && password.length() >= 8;
            boolean hasDigit = password != null && password.matches(".*\\d.*");
            boolean hasUpper = password != null && password.matches(".*[A-Z].*");

            boolean isValid = hasMinLength && hasDigit && hasUpper;

            if (!isValid) {
                System.out.println("  ✗ Password validation failed:");
                if (!hasMinLength) System.out.println("    - Must be at least 8 characters");
                if (!hasDigit) System.out.println("    - Must contain at least one digit");
                if (!hasUpper) System.out.println("    - Must contain at least one uppercase letter");
            } else {
                System.out.println("  ✓ Password is valid");
            }

            return isValid;
        }

        private boolean validateAge(int age) {
            boolean isValid = age >= 18 && age <= 120;
            if (!isValid) {
                System.out.println("  ✗ Age validation failed: " + age + " (must be 18-120)");
            } else {
                System.out.println("  ✓ Age is valid: " + age);
            }
            return isValid;
        }
    }

    // ========== Implementation Classes ==========

    static class CreditCardProcessor implements PaymentProcessor {
        @Override
        public String getProcessorName() {
            return "Credit Card";
        }
    }

    static class PayPalProcessor implements PaymentProcessor {
        @Override
        public String getProcessorName() {
            return "PayPal";
        }
    }

    static class BitcoinProcessor implements PaymentProcessor {
        @Override
        public String getProcessorName() {
            return "Bitcoin";
        }
    }

    static class ApplicationLogger implements Logger {
        @Override
        public void log(String level, String message) {
            System.out.println("  " + message);
        }
    }

    static class UserValidatorImpl implements UserValidator {
        // Uses all default methods from interface
    }

    // ========== Domain Classes ==========

    static class User {
        final String email;
        final String password;
        final int age;

        User(String email, String password, int age) {
            this.email = email;
            this.password = password;
            this.age = age;
        }
    }

    static class ShoppingCart {
        private double total = 0;

        void addItem(String name, double price) {
            total += price;
            System.out.println("  Added: " + name + " ($" + String.format("%.2f", price) + ")");
        }

        double getTotal() {
            return total;
        }
    }
}
