package examples.java8;

import java.util.*;

/**
 * Real-World Scenario: Default Methods and Interface Evolution
 *
 * This example demonstrates how default methods enable interface evolution
 * and how to handle conflicts (diamond problem).
 *
 * Key Concepts Demonstrated:
 * 1. Interface evolution without breaking implementations
 * 2. Diamond problem and conflict resolution
 * 3. Multiple inheritance of behavior
 * 4. Static methods in interfaces
 * 5. Class method priority over default methods
 * 6. Real-world API design patterns
 */
public class DefaultMethodsConflict {

    public static void main(String[] args) {
        System.out.println("=== Default Methods Examples ===\n");

        // Example 1: Interface Evolution
        example1_InterfaceEvolution();

        // Example 2: Diamond Problem
        example2_DiamondProblem();

        // Example 3: Multiple Inheritance of Behavior
        example3_MultipleInheritance();

        // Example 4: Class Method Priority
        example4_ClassMethodPriority();

        // Example 5: Static Methods in Interfaces
        example5_StaticMethods();

        // Example 6: Real-World Pattern - Mixins
        example6_MixinPattern();
    }

    /**
     * Example 1: Interface Evolution
     * Adding new methods without breaking existing implementations
     */
    private static void example1_InterfaceEvolution() {
        System.out.println("--- Example 1: Interface Evolution ---");

        // Old implementation (written before default methods were added)
        PaymentProcessor oldProcessor = new CreditCardProcessor();
        oldProcessor.processPayment(100.0);

        // Still works! Gets default implementations for free
        oldProcessor.processRefund(50.0);  // Uses default implementation
        System.out.println("Valid payment: " + oldProcessor.validatePayment(100.0));

        // New implementation can override defaults
        PaymentProcessor newProcessor = new AdvancedPaymentProcessor();
        newProcessor.processPayment(100.0);
        newProcessor.processRefund(50.0);  // Custom implementation

        System.out.println();
    }

    /**
     * Example 2: Diamond Problem
     * Multiple interfaces with same default method signature
     */
    private static void example2_DiamondProblem() {
        System.out.println("--- Example 2: Diamond Problem ---");

        // Duck implements both Flyable and Swimmable
        // Both have a move() default method - must resolve conflict
        Duck duck = new Duck();
        duck.move();  // Uses Flyable's implementation (explicitly chosen)

        // Bird only implements Flyable
        Bird bird = new Bird();
        bird.move();  // Uses Flyable's default

        // Fish only implements Swimmable
        Fish fish = new Fish();
        fish.move();  // Uses Swimmable's default

        System.out.println();
    }

    /**
     * Example 3: Multiple Inheritance of Behavior
     * Combining behavior from multiple interfaces
     */
    private static void example3_MultipleInheritance() {
        System.out.println("--- Example 3: Multiple Inheritance ---");

        // UserService implements both Loggable and Validatable
        UserService service = new UserService();

        service.log();  // From Loggable interface
        System.out.println("User valid: " + service.validate("user123"));

        // Gets utility methods from both interfaces
        service.logValidation("checking user");

        System.out.println();
    }

    /**
     * Example 4: Class Method Priority
     * Class methods take priority over interface default methods
     */
    private static void example4_ClassMethodPriority() {
        System.out.println("--- Example 4: Class Method Priority ---");

        // Child extends Parent and implements Greeter
        // Parent has greet() method, Greeter has default greet()
        // Parent's method wins
        Child child = new Child();
        child.greet();  // Calls Parent's method, not Greeter's default

        // Direct implementation without parent class
        DirectGreeter direct = new DirectGreeter();
        direct.greet();  // Uses Greeter's default

        System.out.println();
    }

    /**
     * Example 5: Static Methods in Interfaces
     * Utility methods that don't require instance
     */
    private static void example5_StaticMethods() {
        System.out.println("--- Example 5: Static Methods ---");

        // Static methods called on interface name
        int sum = Calculator.add(5, 3);
        int product = Calculator.multiply(5, 3);
        double average = Calculator.average(5, 3);

        System.out.println("Sum: " + sum);
        System.out.println("Product: " + product);
        System.out.println("Average: " + average);

        // Validator utilities
        System.out.println("Valid email: " +
            ValidationUtils.isValidEmail("test@example.com"));
        System.out.println("Valid email: " +
            ValidationUtils.isValidEmail("invalid"));

        System.out.println();
    }

    /**
     * Example 6: Mixin Pattern with Default Methods
     * Composing behavior from multiple sources
     */
    private static void example6_MixinPattern() {
        System.out.println("--- Example 6: Mixin Pattern ---");

        // Entity combines multiple behaviors
        Entity entity = new Entity();

        entity.save();                    // From Persistable
        entity.log();                     // From Loggable
        entity.validate("entity_data");  // From Validatable

        // All behaviors combined in one class
        entity.performOperation();

        System.out.println();
    }
}

// =============================================================================
// EXAMPLE 1: Interface Evolution
// =============================================================================

// Version 1: Original interface
interface PaymentProcessor {
    void processPayment(double amount);

    // Version 2: Add new methods without breaking existing implementations
    default void processRefund(double amount) {
        System.out.println("  [Default] Processing refund: $" + amount);
    }

    default boolean validatePayment(double amount) {
        return amount > 0 && amount < 10000;
    }
}

// Old implementation (exists before defaults were added)
class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("  Processing credit card payment: $" + amount);
    }
    // Gets default implementations automatically
}

// New implementation can override defaults
class AdvancedPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("  Advanced payment processing: $" + amount);
    }

    @Override
    public void processRefund(double amount) {
        System.out.println("  [Custom] Advanced refund processing: $" + amount);
    }
}

// =============================================================================
// EXAMPLE 2: Diamond Problem
// =============================================================================

interface Flyable {
    default void move() {
        System.out.println("  Flying through the air");
    }
}

interface Swimmable {
    default void move() {
        System.out.println("  Swimming through water");
    }
}

// Must resolve conflict when implementing both
class Duck implements Flyable, Swimmable {
    @Override
    public void move() {
        // Explicitly choose which implementation to use
        Flyable.super.move();  // Choose flying
        // or provide completely new implementation
    }
}

class Bird implements Flyable {
    // Gets Flyable's move() by default
}

class Fish implements Swimmable {
    // Gets Swimmable's move() by default
}

// =============================================================================
// EXAMPLE 3: Multiple Inheritance of Behavior
// =============================================================================

interface Loggable {
    default void log() {
        System.out.println("  [LOG] " + getLogMessage());
    }

    default String getLogMessage() {
        return "Operation performed";
    }
}

interface Validatable {
    default boolean validate(String input) {
        return input != null && !input.isEmpty();
    }

    default void logValidation(String input) {
        System.out.println("  [VALIDATION] Validating: " + input);
    }
}

// Combines behavior from multiple interfaces
class UserService implements Loggable, Validatable {
    @Override
    public String getLogMessage() {
        return "User service operation";
    }
    // Gets both log() and validate() methods
}

// =============================================================================
// EXAMPLE 4: Class Method Priority
// =============================================================================

interface Greeter {
    default void greet() {
        System.out.println("  [Interface] Hello!");
    }
}

class Parent {
    public void greet() {
        System.out.println("  [Parent Class] Greetings!");
    }
}

// Class method takes priority over interface default
class Child extends Parent implements Greeter {
    // No override needed - Parent's method wins
}

class DirectGreeter implements Greeter {
    // Gets default greet() from interface
}

// =============================================================================
// EXAMPLE 5: Static Methods in Interfaces
// =============================================================================

interface Calculator {
    static int add(int a, int b) {
        return a + b;
    }

    static int multiply(int a, int b) {
        return a * b;
    }

    static double average(int a, int b) {
        return (a + b) / 2.0;
    }
}

interface ValidationUtils {
    static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{3}-\\d{4}");
    }
}

// =============================================================================
// EXAMPLE 6: Mixin Pattern
// =============================================================================

interface Persistable {
    default void save() {
        System.out.println("  [Persistable] Saving to database");
    }

    default void delete() {
        System.out.println("  [Persistable] Deleting from database");
    }
}

// Combining multiple behaviors (mixins)
class Entity implements Persistable, Loggable, Validatable {
    @Override
    public String getLogMessage() {
        return "Entity operation";
    }

    public void performOperation() {
        log();
        if (validate("data")) {
            save();
            System.out.println("  [Entity] Operation completed successfully");
        }
    }
}
