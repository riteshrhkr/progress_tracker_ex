package examples.java21;

import java.time.LocalDate;
import java.util.*;

/**
 * Java 21 Pattern Matching and Sequenced Collections Demo
 *
 * This example demonstrates:
 * 1. Pattern matching for switch (finalized in Java 21)
 * 2. Guards in pattern matching
 * 3. Null handling in switch
 * 4. Record patterns (finalized in Java 21)
 * 5. Nested record patterns
 * 6. Sequenced Collections (getFirst, getLast, reversed)
 * 7. Real-world order processing with all features
 *
 * These features make Java more expressive and type-safe.
 */
public class PatternMatchingDemo {

    public static void main(String[] args) {
        System.out.println("=== Java 21: Pattern Matching & Sequenced Collections ===\n");

        // Example 1: Basic pattern matching for switch
        example1_BasicPatternMatching();

        // Example 2: Guards in switch
        example2_GuardsInSwitch();

        // Example 3: Null handling in switch
        example3_NullHandling();

        // Example 4: Record patterns
        example4_RecordPatterns();

        // Example 5: Nested record patterns
        example5_NestedRecordPatterns();

        // Example 6: Sequenced collections
        example6_SequencedCollections();

        // Example 7: Real-world order processing
        example7_RealWorldOrderProcessing();
    }

    /**
     * Example 1: Basic Pattern Matching for switch
     * Type patterns in switch expressions
     */
    private static void example1_BasicPatternMatching() {
        System.out.println("--- Example 1: Basic Pattern Matching for switch ---");

        Object[] objects = {
            "Hello",
            42,
            3.14,
            List.of(1, 2, 3),
            new int[]{1, 2, 3}
        };

        System.out.println("Processing different types:");
        for (Object obj : objects) {
            String result = switch (obj) {
                case String s -> "String: \"" + s + "\" (length " + s.length() + ")";
                case Integer i -> "Integer: " + i + " (doubled: " + (i * 2) + ")";
                case Double d -> "Double: " + d + " (squared: " + (d * d) + ")";
                case List<?> list -> "List with " + list.size() + " elements";
                case int[] arr -> "Array with " + arr.length + " elements";
                default -> "Unknown type: " + obj.getClass().getSimpleName();
            };
            System.out.println("  " + result);
        }

        System.out.println();
    }

    /**
     * Example 2: Guards in Switch
     * Adding conditions to pattern matching
     */
    private static void example2_GuardsInSwitch() {
        System.out.println("--- Example 2: Guards in Switch ---");

        Object[] values = {
            "hello",
            "This is a very long string",
            "",
            42,
            -10,
            0,
            List.of(1, 2, 3),
            List.of()
        };

        System.out.println("Classifying values with guards:");
        for (Object value : values) {
            String classification = switch (value) {
                case String s when s.isEmpty() -> "Empty string";
                case String s when s.length() > 20 -> "Long string: " + s.substring(0, 20) + "...";
                case String s when s.length() > 10 -> "Medium string: " + s;
                case String s -> "Short string: " + s;
                case Integer i when i > 0 -> "Positive integer: " + i;
                case Integer i when i < 0 -> "Negative integer: " + i;
                case Integer i -> "Zero";
                case List<?> list when list.isEmpty() -> "Empty list";
                case List<?> list when list.size() > 5 -> "Large list (" + list.size() + " elements)";
                case List<?> list -> "Small list: " + list;
                default -> "Other: " + value;
            };
            System.out.println("  " + classification);
        }

        System.out.println();
    }

    /**
     * Example 3: Null Handling in Switch
     * Switch can now handle null cases
     */
    private static void example3_NullHandling() {
        System.out.println("--- Example 3: Null Handling in Switch ---");

        Object[] values = {
            "Hello",
            null,
            42,
            null,
            List.of(1, 2, 3)
        };

        System.out.println("Processing with null handling:");
        for (Object value : values) {
            String result = switch (value) {
                case null -> "Null value received";
                case String s -> "String: " + s;
                case Integer i -> "Integer: " + i;
                case List<?> list -> "List: " + list;
                default -> "Other: " + value;
            };
            System.out.println("  " + result);
        }

        // Special null handling with guards
        System.out.println("\nNull or empty string check:");
        String[] strings = {"hello", null, "", "world", null};
        for (String s : strings) {
            String result = switch (s) {
                case null -> "null";
                case String str when str.isEmpty() -> "empty";
                case String str -> "value: " + str;
            };
            System.out.println("  " + result);
        }

        System.out.println();
    }

    /**
     * Example 4: Record Patterns
     * Deconstructing records in switch
     */
    private static void example4_RecordPatterns() {
        System.out.println("--- Example 4: Record Patterns ---");

        // Define records
        record Point(int x, int y) {}
        record Circle(Point center, double radius) {}
        record Rectangle(Point topLeft, Point bottomRight) {}

        Object[] shapes = {
            new Point(0, 0),
            new Point(5, 10),
            new Circle(new Point(0, 0), 5.0),
            new Rectangle(new Point(0, 0), new Point(10, 10))
        };

        System.out.println("Processing shapes with record patterns:");
        for (Object shape : shapes) {
            String description = switch (shape) {
                case Point(int x, int y) ->
                    "Point at (" + x + ", " + y + ")";
                case Circle(Point center, double radius) ->
                    "Circle with radius " + radius + " centered at " + center;
                case Rectangle(Point topLeft, Point bottomRight) ->
                    "Rectangle from " + topLeft + " to " + bottomRight;
                default -> "Unknown shape";
            };
            System.out.println("  " + description);
        }

        // Calculate areas
        System.out.println("\nCalculating areas:");
        for (Object shape : shapes) {
            double area = switch (shape) {
                case Point(int x, int y) -> 0.0; // Point has no area
                case Circle(Point center, double radius) -> Math.PI * radius * radius;
                case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) ->
                    Math.abs((x2 - x1) * (y2 - y1));
                default -> 0.0;
            };
            System.out.println("  " + shape.getClass().getSimpleName() + " area: " +
                             String.format("%.2f", area));
        }

        System.out.println();
    }

    /**
     * Example 5: Nested Record Patterns
     * Deconstructing nested records
     */
    private static void example5_NestedRecordPatterns() {
        System.out.println("--- Example 5: Nested Record Patterns ---");

        record Address(String street, String city, String zipCode) {}
        record Person(String name, int age, Address address) {}
        record Company(String name, Address address, int employees) {}

        Object[] entities = {
            new Person("Alice", 30, new Address("123 Main St", "New York", "10001")),
            new Person("Bob", 25, new Address("456 Oak Ave", "Boston", "02101")),
            new Company("TechCorp", new Address("789 Tech Blvd", "San Francisco", "94105"), 500)
        };

        System.out.println("Processing entities with nested patterns:");
        for (Object entity : entities) {
            String info = switch (entity) {
                case Person(String name, int age, Address(String street, String city, String zip)) ->
                    name + " (" + age + " years old) lives at " + street + ", " + city + " " + zip;
                case Company(String name, Address(String street, String city, String zip), int emp) ->
                    name + " located at " + street + ", " + city + " " + zip + " with " + emp + " employees";
                default -> "Unknown entity";
            };
            System.out.println("  " + info);
        }

        // Extract just cities
        System.out.println("\nCities:");
        for (Object entity : entities) {
            String city = switch (entity) {
                case Person(String name, int age, Address(String s, String c, String z)) -> c;
                case Company(String name, Address(String s, String c, String z), int e) -> c;
                default -> "Unknown";
            };
            System.out.println("  " + city);
        }

        System.out.println();
    }

    /**
     * Example 6: Sequenced Collections
     * New methods for collections with defined order
     */
    private static void example6_SequencedCollections() {
        System.out.println("--- Example 6: Sequenced Collections ---");

        // List - always had order
        List<String> list = new ArrayList<>(List.of("apple", "banana", "cherry", "date"));
        System.out.println("List: " + list);
        System.out.println("  First: " + list.getFirst());
        System.out.println("  Last: " + list.getLast());
        System.out.println("  Reversed: " + list.reversed());

        // LinkedHashSet - maintains insertion order
        LinkedHashSet<String> linkedSet = new LinkedHashSet<>(List.of("one", "two", "three", "four"));
        System.out.println("\nLinkedHashSet: " + linkedSet);
        System.out.println("  First: " + linkedSet.getFirst());
        System.out.println("  Last: " + linkedSet.getLast());
        System.out.println("  Reversed: " + linkedSet.reversed());

        // Deque - double-ended queue
        Deque<Integer> deque = new ArrayDeque<>(List.of(10, 20, 30, 40));
        System.out.println("\nDeque: " + deque);
        System.out.println("  First: " + deque.getFirst());
        System.out.println("  Last: " + deque.getLast());
        System.out.println("  Reversed: " + deque.reversed());

        // Add first and last
        List<String> fruits = new ArrayList<>(List.of("banana", "cherry"));
        System.out.println("\nOriginal: " + fruits);
        fruits.addFirst("apple");
        System.out.println("After addFirst('apple'): " + fruits);
        fruits.addLast("date");
        System.out.println("After addLast('date'): " + fruits);

        // Remove first and last
        fruits.removeFirst();
        System.out.println("After removeFirst(): " + fruits);
        fruits.removeLast();
        System.out.println("After removeLast(): " + fruits);

        // Reversed view is live
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> reversedView = numbers.reversed();
        System.out.println("\nNumbers: " + numbers);
        System.out.println("Reversed view: " + reversedView);
        numbers.add(6);
        System.out.println("After adding 6 to original: " + numbers);
        System.out.println("Reversed view (live): " + reversedView);

        System.out.println();
    }

    /**
     * Example 7: Real-World Order Processing
     * Combining pattern matching, record patterns, and sequenced collections
     */
    private static void example7_RealWorldOrderProcessing() {
        System.out.println("--- Example 7: Real-World Order Processing ---");

        // Domain records
        record Product(String id, String name, double price) {}
        record Customer(String email, String name, String tier) {}
        record OrderItem(Product product, int quantity) {}
        record Order(String id, Customer customer, List<OrderItem> items, LocalDate orderDate) {}

        // Payment types
        sealed interface Payment permits CreditCard, PayPal, BankTransfer {}
        record CreditCard(String last4, String type) implements Payment {}
        record PayPal(String email) implements Payment {}
        record BankTransfer(String accountNumber) implements Payment {}

        // Create sample orders
        List<Order> orders = List.of(
            new Order("ORD-001",
                new Customer("alice@example.com", "Alice Smith", "GOLD"),
                List.of(
                    new OrderItem(new Product("P001", "Laptop", 999.99), 1),
                    new OrderItem(new Product("P002", "Mouse", 29.99), 2)
                ),
                LocalDate.now().minusDays(5)
            ),
            new Order("ORD-002",
                new Customer("bob@example.com", "Bob Johnson", "SILVER"),
                List.of(
                    new OrderItem(new Product("P003", "Keyboard", 79.99), 1)
                ),
                LocalDate.now().minusDays(2)
            ),
            new Order("ORD-003",
                new Customer("charlie@example.com", "Charlie Brown", "BRONZE"),
                List.of(
                    new OrderItem(new Product("P004", "Monitor", 299.99), 2)
                ),
                LocalDate.now()
            )
        );

        List<Payment> payments = List.of(
            new CreditCard("1234", "Visa"),
            new PayPal("bob@example.com"),
            new BankTransfer("ACC123456")
        );

        // Process orders using pattern matching
        System.out.println("Processing orders:\n");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            Payment payment = payments.get(i);

            // Calculate total using sequenced collections
            double subtotal = order.items().stream()
                .mapToDouble(item -> item.product().price() * item.quantity())
                .sum();

            // Calculate discount based on customer tier
            double discount = switch (order.customer().tier()) {
                case "GOLD" -> subtotal * 0.15;    // 15% discount
                case "SILVER" -> subtotal * 0.10;  // 10% discount
                case "BRONZE" -> subtotal * 0.05;  // 5% discount
                default -> 0.0;
            };

            // Calculate processing fee based on payment method
            double fee = switch (payment) {
                case CreditCard(String last4, String type) -> {
                    double rate = type.equals("Amex") ? 0.035 : 0.029;
                    yield subtotal * rate;
                }
                case PayPal(String email) -> subtotal * 0.034;
                case BankTransfer(String account) -> 0.0;
            };

            double total = subtotal - discount + fee;

            // Get first and last items using sequenced collections
            String firstItem = order.items().getFirst().product().name();
            String lastItem = order.items().getLast().product().name();

            // Payment method description
            String paymentDesc = switch (payment) {
                case CreditCard(String last4, String type) ->
                    type + " ending in " + last4;
                case PayPal(String email) ->
                    "PayPal (" + email + ")";
                case BankTransfer(String account) ->
                    "Bank Transfer (Account: " + account + ")";
            };

            // Print order summary
            System.out.println("Order: " + order.id());
            System.out.println("  Customer: " + order.customer().name() +
                             " (" + order.customer().tier() + " tier)");
            System.out.println("  Items: " + order.items().size());
            System.out.println("    First item: " + firstItem);
            System.out.println("    Last item: " + lastItem);
            System.out.println("  Subtotal: $" + String.format("%.2f", subtotal));
            System.out.println("  Discount: $" + String.format("%.2f", discount));
            System.out.println("  Processing fee: $" + String.format("%.2f", fee));
            System.out.println("  Total: $" + String.format("%.2f", total));
            System.out.println("  Payment: " + paymentDesc);
            System.out.println("  Order date: " + order.orderDate());
            System.out.println();
        }

        // Summary using sequenced collections
        System.out.println("Order Summary:");
        System.out.println("  Total orders: " + orders.size());
        System.out.println("  First order: " + orders.getFirst().id() +
                         " on " + orders.getFirst().orderDate());
        System.out.println("  Most recent: " + orders.getLast().id() +
                         " on " + orders.getLast().orderDate());

        // Process in reverse chronological order
        System.out.println("\nReverse chronological order:");
        for (Order order : orders.reversed()) {
            System.out.println("  " + order.id() + " - " + order.customer().name() +
                             " (" + order.orderDate() + ")");
        }

        System.out.println();
    }
}
