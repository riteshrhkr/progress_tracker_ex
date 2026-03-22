package examples.java17;

import java.time.*;
import java.util.*;
import java.util.stream.*;

/**
 * Java 17 Records, Sealed Classes, and Pattern Matching Demo
 *
 * This example demonstrates:
 * 1. Records for immutable data carriers
 * 2. Records with validation (compact constructor)
 * 3. Sealed classes for controlled hierarchies
 * 4. Pattern matching for instanceof
 * 5. Combining all three features together
 *
 * Real-world scenario: E-commerce order processing system
 */
public class RecordsDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 17: Records, Sealed Classes & Pattern Matching ===\n");

        // Example 1: Basic records
        example1_BasicRecords();

        // Example 2: Records with validation
        example2_RecordsWithValidation();

        // Example 3: Nested records
        example3_NestedRecords();

        // Example 4: Sealed classes
        example4_SealedClasses();

        // Example 5: Pattern matching for instanceof
        example5_PatternMatching();

        // Example 6: Combining all features
        example6_CombinedFeatures();

        // Example 7: Real-world order processing
        example7_RealWorldOrderProcessing();
    }

    /**
     * Example 1: Basic Records
     * Simple data carriers with automatic methods
     */
    private static void example1_BasicRecords() {
        System.out.println("--- Example 1: Basic Records ---");

        // Create records
        Product laptop = new Product("P001", "Laptop", 999.99);
        Product mouse = new Product("P002", "Mouse", 29.99);

        System.out.println("Products:");
        System.out.println("  " + laptop);  // toString() automatic
        System.out.println("  " + mouse);

        // Accessor methods (not getters!)
        System.out.println("\nAccessing fields:");
        System.out.println("  Laptop name: " + laptop.name());
        System.out.println("  Laptop price: $" + laptop.price());

        // Equals and hashCode automatic
        Product laptop2 = new Product("P001", "Laptop", 999.99);
        System.out.println("\nEquals check:");
        System.out.println("  laptop.equals(laptop2): " + laptop.equals(laptop2));
        System.out.println("  laptop == laptop2: " + (laptop == laptop2));

        // Perfect for collections
        Set<Product> products = new HashSet<>();
        products.add(laptop);
        products.add(laptop2);  // Won't be added - equals
        System.out.println("  Set size: " + products.size());

        System.out.println();
    }

    /**
     * Example 2: Records with Validation
     * Using compact constructor for validation
     */
    private static void example2_RecordsWithValidation() {
        System.out.println("--- Example 2: Records with Validation ---");

        // Valid customer
        try {
            Customer customer = new Customer("john@example.com", 30);
            System.out.println("Valid customer: " + customer);
            System.out.println("  Is adult: " + customer.isAdult());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Invalid email
        try {
            Customer invalid = new Customer("invalid-email", 25);
            System.out.println("Created: " + invalid);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid email caught: " + e.getMessage());
        }

        // Invalid age
        try {
            Customer invalid = new Customer("test@example.com", -5);
            System.out.println("Created: " + invalid);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid age caught: " + e.getMessage());
        }

        // Using static factory method
        Customer alice = Customer.of("alice@example.com", 28);
        System.out.println("\nUsing factory: " + alice);

        System.out.println();
    }

    /**
     * Example 3: Nested Records
     * Records containing other records
     */
    private static void example3_NestedRecords() {
        System.out.println("--- Example 3: Nested Records ---");

        Address address = new Address("123 Main St", "New York", "NY", "10001");
        Contact contact = new Contact("john@example.com", "555-1234", address);
        Person person = new Person("John Doe", 30, contact);

        System.out.println("Person: " + person);
        System.out.println("\nAccessing nested data:");
        System.out.println("  Name: " + person.name());
        System.out.println("  Email: " + person.contact().email());
        System.out.println("  City: " + person.contact().address().city());
        System.out.println("  ZIP: " + person.contact().address().zipCode());

        System.out.println();
    }

    /**
     * Example 4: Sealed Classes
     * Controlled type hierarchies
     */
    private static void example4_SealedClasses() {
        System.out.println("--- Example 4: Sealed Classes ---");

        // Create different payment methods
        PaymentMethod cc = new CreditCard("4111111111111111", "123");
        PaymentMethod paypal = new PayPal("john@example.com");
        PaymentMethod bank = new BankTransfer("ACC123456");

        System.out.println("Processing fees:");
        System.out.println("  Credit Card: " + getProcessingFee(cc, 100.0));
        System.out.println("  PayPal: " + getProcessingFee(paypal, 100.0));
        System.out.println("  Bank Transfer: " + getProcessingFee(bank, 100.0));

        // Order states
        OrderState pending = new Pending();
        OrderState processing = new Processing("Worker-1");
        OrderState shipped = new Shipped("TRACK123");
        OrderState delivered = new Delivered(LocalDateTime.now());

        System.out.println("\nOrder state messages:");
        System.out.println("  " + getStateMessage(pending));
        System.out.println("  " + getStateMessage(processing));
        System.out.println("  " + getStateMessage(shipped));
        System.out.println("  " + getStateMessage(delivered));

        System.out.println();
    }

    /**
     * Example 5: Pattern Matching for instanceof
     * Eliminating explicit casts
     */
    private static void example5_PatternMatching() {
        System.out.println("--- Example 5: Pattern Matching for instanceof ---");

        Object[] objects = {
            "Hello World",
            42,
            List.of("a", "b", "c"),
            new Product("P001", "Laptop", 999.99),
            LocalDate.of(2024, 10, 26)
        };

        System.out.println("Processing different types:");
        for (Object obj : objects) {
            processObject(obj);
        }

        System.out.println("\nConditional pattern matching:");
        Object longString = "This is a long string";
        Object shortString = "Hi";

        checkLongString(longString);
        checkLongString(shortString);
        checkLongString(12345);

        System.out.println();
    }

    /**
     * Example 6: Combining All Features
     * Records + Sealed Classes + Pattern Matching
     */
    private static void example6_CombinedFeatures() {
        System.out.println("--- Example 6: Combining All Features ---");

        // Create different results
        ApiResult<String> success = new Success<>("Data loaded successfully");
        ApiResult<String> error = new Failure<>("Network timeout");
        ApiResult<String> notFound = new NotFound<>("User not found");

        System.out.println("API Results:");
        System.out.println("  " + formatResult(success));
        System.out.println("  " + formatResult(error));
        System.out.println("  " + formatResult(notFound));

        // Extract values safely
        System.out.println("\nExtracting values:");
        System.out.println("  Success value: " + extractValue(success, "default"));
        System.out.println("  Error value: " + extractValue(error, "default"));
        System.out.println("  NotFound value: " + extractValue(notFound, "default"));

        System.out.println();
    }

    /**
     * Example 7: Real-World Order Processing
     * Complete example combining all Java 17 features
     */
    private static void example7_RealWorldOrderProcessing() {
        System.out.println("--- Example 7: Real-World Order Processing ---");

        // Create orders
        Order order1 = new Order(
            "ORD-001",
            new Customer("alice@example.com", 30),
            List.of(
                new OrderItem(new Product("P001", "Laptop", 999.99), 1),
                new OrderItem(new Product("P002", "Mouse", 29.99), 2)
            ),
            new CreditCard("4111111111111111", "123"),
            new Processing("Worker-1")
        );

        Order order2 = new Order(
            "ORD-002",
            new Customer("bob@example.com", 25),
            List.of(
                new OrderItem(new Product("P003", "Keyboard", 79.99), 1)
            ),
            new PayPal("bob@example.com"),
            new Shipped("TRACK456")
        );

        List<Order> orders = List.of(order1, order2);

        // Process orders
        System.out.println("Order Summary:");
        for (Order order : orders) {
            processOrder(order);
            System.out.println();
        }

        // Group by payment method
        Map<String, Long> byPaymentMethod = orders.stream()
            .collect(Collectors.groupingBy(
                order -> getPaymentMethodName(order.paymentMethod()),
                Collectors.counting()
            ));

        System.out.println("Orders by payment method:");
        byPaymentMethod.forEach((method, count) ->
            System.out.println("  " + method + ": " + count)
        );

        // Filter by state
        List<Order> inProgress = orders.stream()
            .filter(order -> order.state() instanceof Processing)
            .toList();

        System.out.println("\nOrders in processing: " + inProgress.size());
    }

    // ========== Helper Methods ==========

    private static double getProcessingFee(PaymentMethod method, double amount) {
        // Pattern matching in switch is preview in Java 17, use if-else
        if (method instanceof CreditCard) {
            return amount * 0.029;
        } else if (method instanceof DebitCard) {
            return amount * 0.015;
        } else if (method instanceof PayPal) {
            return amount * 0.034;
        } else if (method instanceof BankTransfer) {
            return 0.0;
        }
        return 0.0;
    }

    private static String getStateMessage(OrderState state) {
        // Pattern matching in switch is Java 21+, use if-else for Java 17
        if (state instanceof Pending) {
            return "Order received, awaiting processing";
        } else if (state instanceof Processing p) {
            return "Being processed by " + p.assignedTo();
        } else if (state instanceof Shipped s) {
            return "Shipped with tracking: " + s.trackingNumber();
        } else if (state instanceof Delivered d) {
            return "Delivered at " + d.deliveredAt();
        } else if (state instanceof Cancelled c) {
            return "Cancelled: " + c.reason();
        }
        return "Unknown state";
    }

    private static void processObject(Object obj) {
        // Pattern matching - no explicit cast needed!
        if (obj instanceof String s) {
            System.out.println("  String (length " + s.length() + "): " + s);
        } else if (obj instanceof Integer i) {
            System.out.println("  Integer (doubled): " + (i * 2));
        } else if (obj instanceof List<?> list) {
            System.out.println("  List (size " + list.size() + "): " + list);
        } else if (obj instanceof Product p) {
            System.out.println("  Product: " + p.name() + " ($" + p.price() + ")");
        } else if (obj instanceof LocalDate date) {
            System.out.println("  Date: " + date);
        }
    }

    private static void checkLongString(Object obj) {
        // Pattern matching with conditions
        if (obj instanceof String s && s.length() > 10) {
            System.out.println("  Long string: \"" + s + "\"");
        } else if (obj instanceof String s) {
            System.out.println("  Short string: \"" + s + "\"");
        } else {
            System.out.println("  Not a string: " + obj);
        }
    }

    private static <T> String formatResult(ApiResult<T> result) {
        // Pattern matching in switch is Java 21+, use if-else for Java 17
        if (result instanceof Success<T> s) {
            return "✓ " + s.data();
        } else if (result instanceof Failure<T> f) {
            return "✗ Error: " + f.error();
        } else if (result instanceof NotFound<T> n) {
            return "? Not Found: " + n.message();
        }
        return "Unknown result";
    }

    private static <T> T extractValue(ApiResult<T> result, T defaultValue) {
        if (result instanceof Success<T> s) {
            return s.data();
        }
        return defaultValue;
    }

    private static void processOrder(Order order) {
        double subtotal = order.items().stream()
            .mapToDouble(item -> item.product().price() * item.quantity())
            .sum();

        double fee = getProcessingFee(order.paymentMethod(), subtotal);
        double total = subtotal + fee;

        System.out.println("Order: " + order.id());
        System.out.println("  Customer: " + order.customer().email());
        System.out.println("  Items: " + order.items().size());
        System.out.println("  Subtotal: $" + String.format("%.2f", subtotal));
        System.out.println("  Processing Fee: $" + String.format("%.2f", fee));
        System.out.println("  Total: $" + String.format("%.2f", total));
        System.out.println("  Payment: " + getPaymentMethodName(order.paymentMethod()));
        System.out.println("  Status: " + getStateMessage(order.state()));
    }

    private static String getPaymentMethodName(PaymentMethod method) {
        if (method instanceof CreditCard) {
            return "Credit Card";
        } else if (method instanceof DebitCard) {
            return "Debit Card";
        } else if (method instanceof PayPal) {
            return "PayPal";
        } else if (method instanceof BankTransfer) {
            return "Bank Transfer";
        }
        return "Unknown";
    }

    // ========== Records ==========

    record Product(String id, String name, double price) { }

    record Customer(String email, int age) {
        // Compact constructor with validation
        public Customer {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email: " + email);
            }
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Invalid age: " + age);
            }
        }

        // Additional methods
        public boolean isAdult() {
            return age >= 18;
        }

        // Static factory method
        public static Customer of(String email, int age) {
            return new Customer(email, age);
        }
    }

    record Address(String street, String city, String state, String zipCode) { }

    record Contact(String email, String phone, Address address) { }

    record Person(String name, int age, Contact contact) { }

    record OrderItem(Product product, int quantity) { }

    record Order(
        String id,
        Customer customer,
        List<OrderItem> items,
        PaymentMethod paymentMethod,
        OrderState state
    ) {
        // Defensive copy for mutable list
        public Order {
            items = List.copyOf(items);
        }
    }

    // ========== Sealed Classes - Payment Methods ==========

    sealed interface PaymentMethod
        permits CreditCard, DebitCard, PayPal, BankTransfer { }

    record CreditCard(String number, String cvv) implements PaymentMethod { }

    record DebitCard(String number, String pin) implements PaymentMethod { }

    record PayPal(String email) implements PaymentMethod { }

    record BankTransfer(String accountNumber) implements PaymentMethod { }

    // ========== Sealed Classes - Order States ==========

    sealed interface OrderState
        permits Pending, Processing, Shipped, Delivered, Cancelled { }

    record Pending() implements OrderState { }

    record Processing(String assignedTo) implements OrderState { }

    record Shipped(String trackingNumber) implements OrderState { }

    record Delivered(LocalDateTime deliveredAt) implements OrderState { }

    record Cancelled(String reason) implements OrderState { }

    // ========== Sealed Classes - API Results ==========

    sealed interface ApiResult<T>
        permits Success, Failure, NotFound { }

    record Success<T>(T data) implements ApiResult<T> { }

    record Failure<T>(String error) implements ApiResult<T> { }

    record NotFound<T>(String message) implements ApiResult<T> { }
}
