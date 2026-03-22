package examples.java9;

import java.util.*;
import java.util.stream.*;

/**
 * Java 9 Collections, Stream, and Optional Improvements Demo
 *
 * This example demonstrates the practical usage of:
 * 1. Collection Factory Methods (List.of, Set.of, Map.of)
 * 2. Stream API Improvements (takeWhile, dropWhile, ofNullable, iterate)
 * 3. Optional Improvements (ifPresentOrElse, or, stream)
 *
 * Real-world scenario: E-commerce order processing system
 */
public class Java9CollectionsDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 9: Collections, Stream & Optional Improvements ===\n");

        // Example 1: Collection Factory Methods
        example1_CollectionFactories();

        // Example 2: Stream takeWhile and dropWhile
        example2_StreamTakeWhileDrop();

        // Example 3: Stream.ofNullable
        example3_StreamOfNullable();

        // Example 4: Stream.iterate with predicate
        example4_StreamIterateWithPredicate();

        // Example 5: Optional improvements
        example5_OptionalImprovements();

        // Example 6: Real-world combined scenario
        example6_RealWorldScenario();
    }

    /**
     * Example 1: Collection Factory Methods
     * Immutable collections created easily
     */
    private static void example1_CollectionFactories() {
        System.out.println("--- Example 1: Collection Factory Methods ---");

        // List.of - immutable list
        List<String> productCategories = List.of("Electronics", "Books", "Clothing", "Food");
        System.out.println("Product categories: " + productCategories);

        // Set.of - immutable set (no duplicates)
        Set<String> supportedCurrencies = Set.of("USD", "EUR", "GBP", "JPY");
        System.out.println("Supported currencies: " + supportedCurrencies);

        // Map.of - immutable map (up to 10 entries)
        Map<String, Double> shippingRates = Map.of(
            "Standard", 5.99,
            "Express", 12.99,
            "Overnight", 24.99
        );
        System.out.println("Shipping rates: " + shippingRates);

        // Map.ofEntries - for larger maps
        Map<String, String> countryToCurrency = Map.ofEntries(
            Map.entry("USA", "USD"),
            Map.entry("UK", "GBP"),
            Map.entry("Japan", "JPY"),
            Map.entry("Germany", "EUR"),
            Map.entry("France", "EUR"),
            Map.entry("Canada", "CAD")
        );
        System.out.println("Country to currency: " + countryToCurrency);

        // Try to modify - will throw UnsupportedOperationException
        try {
            productCategories.add("Toys");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify immutable list: " + e.getClass().getSimpleName());
        }

        System.out.println();
    }

    /**
     * Example 2: takeWhile and dropWhile
     * Process ordered data efficiently
     */
    private static void example2_StreamTakeWhileDrop() {
        System.out.println("--- Example 2: Stream takeWhile & dropWhile ---");

        // Order amounts sorted by date (oldest first)
        List<Double> orderAmounts = List.of(
            45.99, 89.50, 120.00, 199.99,  // Q1 - under $200
            250.00, 310.00, 450.00,        // Q2 - $200+
            180.00, 95.50, 60.00           // Q3 - back under $200
        );

        // takeWhile - get orders under $200 from the start
        System.out.println("Orders under $200 (from start):");
        orderAmounts.stream()
            .takeWhile(amount -> amount < 200)
            .forEach(amount -> System.out.println("  $" + amount));

        // dropWhile - skip small orders, process the rest
        System.out.println("\nOrders after skipping small ones:");
        orderAmounts.stream()
            .dropWhile(amount -> amount < 200)
            .forEach(amount -> System.out.println("  $" + amount));

        // Real scenario: Process log entries until error
        List<String> logEntries = List.of(
            "INFO: Service started",
            "INFO: Processing request 1",
            "INFO: Processing request 2",
            "ERROR: Database connection failed",
            "INFO: Retry attempt 1",
            "INFO: Retry attempt 2"
        );

        System.out.println("\nLog entries before first error:");
        logEntries.stream()
            .takeWhile(log -> !log.startsWith("ERROR"))
            .forEach(System.out::println);

        System.out.println();
    }

    /**
     * Example 3: Stream.ofNullable
     * Handle potential null values elegantly
     */
    private static void example3_StreamOfNullable() {
        System.out.println("--- Example 3: Stream.ofNullable ---");

        // Simulating optional customer notes
        String customerNote1 = "Please leave package at door";
        String customerNote2 = null;  // No note provided
        String customerNote3 = "Fragile - handle with care";

        System.out.println("Processing customer notes:");
        // Cannot use List.of() with null values - creates ArrayList instead
        List<String> notes = new ArrayList<>(Arrays.asList(customerNote1, customerNote2, customerNote3));

        // Before Java 9 - manual null filtering
        System.out.println("Before Java 9:");
        notes.stream()
            .filter(Objects::nonNull)
            .forEach(note -> System.out.println("  - " + note));

        // Java 9 - using flatMap with ofNullable
        System.out.println("\nJava 9 (with ofNullable):");
        notes.stream()
            .flatMap(Stream::ofNullable)  // Filters out nulls automatically
            .forEach(note -> System.out.println("  - " + note));

        // Practical use: Collecting non-null user preferences
        String preference1 = getUserPreference("theme");      // Returns "dark"
        String preference2 = getUserPreference("language");   // Returns null
        String preference3 = getUserPreference("timezone");   // Returns "UTC"

        List<String> activePreferences = Stream.of(preference1, preference2, preference3)
            .flatMap(Stream::ofNullable)
            .collect(Collectors.toList());

        System.out.println("\nActive user preferences: " + activePreferences);
        System.out.println();
    }

    /**
     * Example 4: Stream.iterate with predicate
     * Generate bounded sequences elegantly
     */
    private static void example4_StreamIterateWithPredicate() {
        System.out.println("--- Example 4: Stream.iterate with predicate ---");

        // Generate order IDs: ORD-1000, ORD-1001, ... ORD-1009
        System.out.println("Generating order IDs:");
        Stream.iterate(1000, n -> n < 1010, n -> n + 1)
            .map(id -> "ORD-" + id)
            .forEach(System.out::println);

        // Calculate discount tiers
        System.out.println("\nDiscount tiers:");
        Stream.iterate(100.0, price -> price <= 1000.0, price -> price * 1.5)
            .forEach(price -> {
                double discount = getDiscountForAmount(price);
                System.out.printf("  Order $%.2f → %.0f%% discount%n", price, discount * 100);
            });

        // Generate date sequence (simplified with integers representing days)
        System.out.println("\nShipping schedule (next 7 days):");
        Stream.iterate(1, day -> day <= 7, day -> day + 1)
            .forEach(day -> System.out.println("  Day " + day + ": Ship batch #" + day));

        System.out.println();
    }

    /**
     * Example 5: Optional Improvements
     * ifPresentOrElse, or, and stream
     */
    private static void example5_OptionalImprovements() {
        System.out.println("--- Example 5: Optional Improvements ---");

        // ifPresentOrElse - handle both present and absent cases
        System.out.println("Processing customer orders:");

        Optional<String> order1 = findOrder("ORD-001");
        order1.ifPresentOrElse(
            order -> System.out.println("  Found order: " + order),
            () -> System.out.println("  Order not found - sending notification")
        );

        Optional<String> order2 = findOrder("ORD-999");
        order2.ifPresentOrElse(
            order -> System.out.println("  Found order: " + order),
            () -> System.out.println("  Order not found - sending notification")
        );

        // or() - lazy optional chaining (fallback sources)
        System.out.println("\nMulti-source product lookup:");
        String productId = "PROD-123";

        Optional<Product> product = findInCache(productId)
            .or(() -> findInDatabase(productId))
            .or(() -> findInWarehouse(productId));

        product.ifPresentOrElse(
            p -> System.out.println("  Found: " + p),
            () -> System.out.println("  Product not available")
        );

        // stream() - convert Optional to Stream for flatMap operations
        System.out.println("\nExtracting valid email addresses:");
        List<Customer> customers = List.of(
            new Customer("Alice", "alice@example.com"),
            new Customer("Bob", null),  // No email
            new Customer("Charlie", "charlie@example.com"),
            new Customer("Diana", null)
        );

        List<String> emails = customers.stream()
            .map(Customer::getEmail)           // Stream<Optional<String>>
            .flatMap(Optional::stream)         // Stream<String> (nulls filtered)
            .collect(Collectors.toList());

        System.out.println("  Valid emails: " + emails);
        System.out.println();
    }

    /**
     * Example 6: Real-world Combined Scenario
     * E-commerce order processing with all Java 9 features
     */
    private static void example6_RealWorldScenario() {
        System.out.println("--- Example 6: Real-World Order Processing ---");

        // Immutable configuration
        Map<String, Double> tierDiscounts = Map.of(
            "Bronze", 0.05,
            "Silver", 0.10,
            "Gold", 0.15
        );

        // Process daily orders (sorted by amount)
        List<Order> orders = List.of(
            new Order("ORD-001", 45.00, "Bronze"),
            new Order("ORD-002", 120.00, "Silver"),
            new Order("ORD-003", 250.00, "Gold"),
            new Order("ORD-004", 80.00, "Bronze"),
            new Order("ORD-005", 350.00, "Gold")
        );

        System.out.println("Processing orders with discounts:");

        orders.forEach(order -> {
            // Get discount using Optional.or() for fallback
            Optional<Double> discountOpt = Optional.ofNullable(tierDiscounts.get(order.tier))
                .or(() -> Optional.of(0.0));  // Default to no discount

            discountOpt.ifPresentOrElse(
                discount -> {
                    double finalAmount = order.amount * (1 - discount);
                    System.out.printf("  %s: $%.2f → $%.2f (%.0f%% off)%n",
                        order.id, order.amount, finalAmount, discount * 100);
                },
                () -> System.out.println("  " + order.id + ": No discount applied")
            );
        });

        // Filter high-value orders using takeWhile (assuming sorted)
        List<Order> sortedOrders = orders.stream()
            .sorted(Comparator.comparingDouble(o -> o.amount))
            .collect(Collectors.toList());

        System.out.println("\nOrders under $200:");
        sortedOrders.stream()
            .takeWhile(order -> order.amount < 200)
            .forEach(order -> System.out.println("  " + order.id + ": $" + order.amount));

        // Extract customer tiers using stream()
        Set<String> activeTiers = orders.stream()
            .map(order -> Optional.ofNullable(order.tier))
            .flatMap(Optional::stream)  // Filter out nulls
            .collect(Collectors.toSet());

        System.out.println("\nActive customer tiers: " + activeTiers);

        // Generate invoice numbers
        System.out.println("\nGenerating invoice IDs:");
        Stream.iterate(5001, n -> n <= 5005, n -> n + 1)
            .forEach(n -> System.out.println("  INV-" + n));

        System.out.println();
    }

    // ========== Helper Methods ==========

    private static String getUserPreference(String key) {
        return switch (key) {
            case "theme" -> "dark";
            case "timezone" -> "UTC";
            default -> null;
        };
    }

    private static double getDiscountForAmount(double amount) {
        if (amount >= 500) return 0.15;
        if (amount >= 250) return 0.10;
        if (amount >= 100) return 0.05;
        return 0.0;
    }

    private static Optional<String> findOrder(String orderId) {
        return orderId.equals("ORD-001")
            ? Optional.of("Order for Electronics ($299.99)")
            : Optional.empty();
    }

    private static Optional<Product> findInCache(String productId) {
        System.out.println("  Searching cache...");
        return Optional.empty();  // Simulating cache miss
    }

    private static Optional<Product> findInDatabase(String productId) {
        System.out.println("  Searching database...");
        return Optional.of(new Product(productId, "Laptop", 999.99));
    }

    private static Optional<Product> findInWarehouse(String productId) {
        System.out.println("  Searching warehouse...");
        return Optional.empty();
    }

    // ========== Domain Classes ==========

    static class Customer {
        final String name;
        final String email;

        Customer(String name, String email) {
            this.name = name;
            this.email = email;
        }

        Optional<String> getEmail() {
            return Optional.ofNullable(email);
        }

        @Override
        public String toString() {
            return name + " (" + email + ")";
        }
    }

    static class Product {
        final String id;
        final String name;
        final double price;

        Product(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + " ($" + price + ")";
        }
    }

    static class Order {
        final String id;
        final double amount;
        final String tier;

        Order(String id, double amount, String tier) {
            this.id = id;
            this.amount = amount;
            this.tier = tier;
        }
    }
}
