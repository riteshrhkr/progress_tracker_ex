package examples.java10;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Java 10 Local Variable Type Inference (var keyword) Demo
 *
 * This example demonstrates:
 * 1. Basic var usage with collections and primitives
 * 2. var with streams and complex generics
 * 3. var in loops and try-with-resources
 * 4. Common pitfalls and how to avoid them
 * 5. Best practices and anti-patterns
 * 6. Real-world scenarios
 *
 * Real-world scenario: E-commerce order processing system
 */
public class VarKeywordDemo {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Java 10: var Keyword (Local Variable Type Inference) ===\n");

        // Example 1: Basic var usage
        example1_BasicVarUsage();

        // Example 2: var with collections
        example2_VarWithCollections();

        // Example 3: var with streams
        example3_VarWithStreams();

        // Example 4: var in loops
        example4_VarInLoops();

        // Example 5: var with try-with-resources
        example5_VarWithTryWithResources();

        // Example 6: Common pitfalls
        example6_CommonPitfalls();

        // Example 7: Real-world order processing
        example7_RealWorldOrderProcessing();
    }

    /**
     * Example 1: Basic var Usage
     * Shows simple var declarations and what types are inferred
     */
    private static void example1_BasicVarUsage() {
        System.out.println("--- Example 1: Basic var Usage ---");

        // Primitives
        var count = 10;              // int
        var price = 99.99;           // double
        var isActive = true;         // boolean
        var initial = 'A';           // char
        var largeNumber = 1000L;     // long

        System.out.println("count: " + count + " (type: int)");
        System.out.println("price: " + price + " (type: double)");
        System.out.println("isActive: " + isActive + " (type: boolean)");
        System.out.println("initial: " + initial + " (type: char)");
        System.out.println("largeNumber: " + largeNumber + " (type: long)");

        // Strings
        var name = "John Doe";
        var email = "john@example.com";

        System.out.println("\nname: " + name);
        System.out.println("email: " + email);

        // Objects
        var path = Paths.get("/tmp/data.txt");
        var builder = new StringBuilder();
        var random = new Random();

        System.out.println("\npath: " + path.getClass().getSimpleName());
        System.out.println("builder: " + builder.getClass().getSimpleName());
        System.out.println("random: " + random.getClass().getSimpleName());

        System.out.println();
    }

    /**
     * Example 2: var with Collections
     * Demonstrates var with complex generic types
     */
    private static void example2_VarWithCollections() {
        System.out.println("--- Example 2: var with Collections ---");

        // Basic collections - MUST provide type arguments with diamond operator
        var names = new ArrayList<String>();
        var scores = new HashMap<String, Integer>();
        var uniqueIds = new HashSet<Long>();

        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");

        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);

        uniqueIds.add(1001L);
        uniqueIds.add(1002L);

        System.out.println("Names: " + names);
        System.out.println("Scores: " + scores);
        System.out.println("Unique IDs: " + uniqueIds);

        // Complex nested generics - var shines here!
        var ordersByCustomer = new HashMap<String, List<Order>>();
        ordersByCustomer.put("Alice", Arrays.asList(
            new Order("ORD-001", 99.99),
            new Order("ORD-002", 149.99)
        ));

        System.out.println("\nOrders by customer: " + ordersByCustomer);

        // Map of maps - very verbose without var
        var customerData = new HashMap<String, Map<String, Object>>();
        var aliceData = new HashMap<String, Object>();
        aliceData.put("age", 30);
        aliceData.put("city", "New York");
        customerData.put("Alice", aliceData);

        System.out.println("Customer data: " + customerData);

        System.out.println();
    }

    /**
     * Example 3: var with Streams
     * Shows how var works beautifully with Stream operations
     */
    private static void example3_VarWithStreams() {
        System.out.println("--- Example 3: var with Streams ---");

        var orders = Arrays.asList(
            new Order("ORD-001", 99.99),
            new Order("ORD-002", 149.99),
            new Order("ORD-003", 49.99),
            new Order("ORD-004", 299.99),
            new Order("ORD-005", 199.99)
        );

        // Filtering with var
        var expensiveOrders = orders.stream()
            .filter(order -> order.amount > 100)
            .collect(Collectors.toList());

        System.out.println("Expensive orders (>$100):");
        expensiveOrders.forEach(System.out::println);

        // Mapping with var
        var orderIds = orders.stream()
            .map(Order::getOrderId)
            .collect(Collectors.toList());

        System.out.println("\nOrder IDs: " + orderIds);

        // Grouping by price range
        var ordersByPriceRange = orders.stream()
            .collect(Collectors.groupingBy(order -> {
                if (order.amount < 100) return "Budget";
                else if (order.amount < 200) return "Standard";
                else return "Premium";
            }));

        System.out.println("\nOrders by price range:");
        ordersByPriceRange.forEach((range, orderList) -> {
            System.out.println("  " + range + ": " + orderList.size() + " orders");
        });

        // Statistics
        var stats = orders.stream()
            .collect(Collectors.summarizingDouble(order -> order.amount));

        System.out.println("\nOrder statistics:");
        System.out.println("  Total: " + stats.getCount());
        System.out.println("  Average: $" + String.format("%.2f", stats.getAverage()));
        System.out.println("  Min: $" + stats.getMin());
        System.out.println("  Max: $" + stats.getMax());

        System.out.println();
    }

    /**
     * Example 4: var in Loops
     * Demonstrates var in for-each and traditional for loops
     */
    private static void example4_VarInLoops() {
        System.out.println("--- Example 4: var in Loops ---");

        var products = Arrays.asList("Laptop", "Mouse", "Keyboard", "Monitor");

        // For-each loop
        System.out.println("Products:");
        for (var product : products) {
            System.out.println("  - " + product);
        }

        // Map iteration
        var inventory = new HashMap<String, Integer>();
        inventory.put("Laptop", 10);
        inventory.put("Mouse", 50);
        inventory.put("Keyboard", 30);

        System.out.println("\nInventory:");
        for (var entry : inventory.entrySet()) {
            var productName = entry.getKey();
            var quantity = entry.getValue();
            System.out.println("  " + productName + ": " + quantity + " units");
        }

        // Traditional for loop (less common with var)
        System.out.println("\nCounting:");
        for (var i = 1; i <= 5; i++) {
            System.out.println("  Count: " + i);
        }

        // Nested loop with var
        var matrix = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("\nMatrix:");
        for (var row : matrix) {
            for (var value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    /**
     * Example 5: var with try-with-resources
     * Shows clean resource management with var
     */
    private static void example5_VarWithTryWithResources() throws IOException {
        System.out.println("--- Example 5: var with try-with-resources ---");

        // Create a temporary file
        var tempFile = Files.createTempFile("demo", ".txt");
        System.out.println("Created temp file: " + tempFile);

        // Write to file with var
        try (var writer = Files.newBufferedWriter(tempFile)) {
            writer.write("Order-001,99.99\n");
            writer.write("Order-002,149.99\n");
            writer.write("Order-003,49.99\n");
            System.out.println("Wrote data to file");
        }

        // Read from file with var
        System.out.println("\nReading from file:");
        try (var reader = Files.newBufferedReader(tempFile)) {
            var line = reader.readLine();
            while (line != null) {
                System.out.println("  " + line);
                line = reader.readLine();
            }
        }

        // Multiple resources
        System.out.println("\nProcessing with multiple resources:");
        var outputFile = Files.createTempFile("output", ".txt");
        try (var in = Files.newBufferedReader(tempFile);
             var out = Files.newBufferedWriter(outputFile)) {

            var line = in.readLine();
            while (line != null) {
                var parts = line.split(",");
                var orderId = parts[0];
                var amount = Double.parseDouble(parts[1]);
                out.write(orderId + " -> $" + amount + "\n");
                line = in.readLine();
            }
            System.out.println("Processed and wrote to: " + outputFile);
        }

        // Cleanup
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(outputFile);
        System.out.println("Cleaned up temp files");

        System.out.println();
    }

    /**
     * Example 6: Common Pitfalls
     * Demonstrates what NOT to do with var
     */
    private static void example6_CommonPitfalls() {
        System.out.println("--- Example 6: Common Pitfalls ---");

        // Pitfall 1: Diamond operator without type - infers Object
        System.out.println("Pitfall 1: Diamond operator without type");
        // var badList = new ArrayList<>();  // Infers ArrayList<Object>
        var goodList = new ArrayList<String>();  // Explicit type argument
        goodList.add("item");
        System.out.println("  Good list: " + goodList);

        // Pitfall 2: Numeric literals - be careful with precision
        System.out.println("\nPitfall 2: Numeric literals");
        var smallNumber = 100;      // int
        var bigNumber = 5000000000L;  // Must use L suffix for long
        System.out.println("  smallNumber: " + smallNumber + " (int)");
        System.out.println("  bigNumber: " + bigNumber + " (long)");

        // Pitfall 3: When type is not obvious
        System.out.println("\nPitfall 3: Type clarity");
        // var unclear = processData();  // What type is returned?
        // Better: OrderReport report = processData();

        // Pitfall 4: Array initializer
        System.out.println("\nPitfall 4: Array initialization");
        // var array = {1, 2, 3};  // ❌ Compilation error
        var array = new int[]{1, 2, 3};  // ✅ Correct
        System.out.println("  Array: " + Arrays.toString(array));

        // Pitfall 5: Losing interface type
        System.out.println("\nPitfall 5: Coding to interface");
        var concreteList = new ArrayList<String>();  // Type is ArrayList
        // Later you can call ArrayList-specific methods
        concreteList.trimToSize();
        System.out.println("  Called ArrayList-specific method trimToSize()");

        // If you want List interface, be explicit
        List<String> interfaceList = new ArrayList<>();
        // interfaceList.trimToSize();  // ❌ Won't compile
        System.out.println("  interfaceList only has List methods available");

        System.out.println();
    }

    /**
     * Example 7: Real-World Order Processing
     * Comprehensive example showing var in a realistic scenario
     */
    private static void example7_RealWorldOrderProcessing() {
        System.out.println("--- Example 7: Real-World Order Processing ---");

        // Create sample orders
        var orders = createSampleOrders();

        // Calculate total revenue
        var totalRevenue = orders.stream()
            .mapToDouble(order -> order.amount)
            .sum();
        System.out.println("Total revenue: $" + String.format("%.2f", totalRevenue));

        // Find high-value orders
        var highValueOrders = orders.stream()
            .filter(order -> order.amount > 150)
            .collect(Collectors.toList());
        System.out.println("\nHigh-value orders (>$150): " + highValueOrders.size());
        highValueOrders.forEach(order ->
            System.out.println("  " + order.orderId + ": $" + order.amount)
        );

        // Group by price tier
        var ordersByTier = orders.stream()
            .collect(Collectors.groupingBy(order -> {
                if (order.amount < 100) return "Bronze";
                else if (order.amount < 200) return "Silver";
                else return "Gold";
            }, Collectors.counting()));

        System.out.println("\nOrders by tier:");
        ordersByTier.forEach((tier, count) ->
            System.out.println("  " + tier + ": " + count + " orders")
        );

        // Calculate average order value
        var averageOrderValue = orders.stream()
            .collect(Collectors.averagingDouble(order -> order.amount));
        System.out.println("\nAverage order value: $" + String.format("%.2f", averageOrderValue));

        // Find top 3 orders
        var topOrders = orders.stream()
            .sorted((o1, o2) -> Double.compare(o2.amount, o1.amount))
            .limit(3)
            .collect(Collectors.toList());

        System.out.println("\nTop 3 orders:");
        for (var i = 0; i < topOrders.size(); i++) {
            var order = topOrders.get(i);
            System.out.println("  " + (i + 1) + ". " + order.orderId + ": $" + order.amount);
        }

        // Build order summary map
        var orderSummary = new HashMap<String, Object>();
        orderSummary.put("totalOrders", orders.size());
        orderSummary.put("totalRevenue", totalRevenue);
        orderSummary.put("averageValue", averageOrderValue);
        orderSummary.put("highValueCount", highValueOrders.size());

        System.out.println("\nOrder Summary: " + orderSummary);

        System.out.println();
    }

    /**
     * Helper method to create sample orders
     */
    private static List<Order> createSampleOrders() {
        return Arrays.asList(
            new Order("ORD-001", 99.99),
            new Order("ORD-002", 149.99),
            new Order("ORD-003", 49.99),
            new Order("ORD-004", 299.99),
            new Order("ORD-005", 199.99),
            new Order("ORD-006", 79.99),
            new Order("ORD-007", 129.99),
            new Order("ORD-008", 249.99)
        );
    }

    // Domain class
    static class Order {
        final String orderId;
        final double amount;

        Order(String orderId, double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }

        public String getOrderId() {
            return orderId;
        }

        @Override
        public String toString() {
            return orderId + "($" + amount + ")";
        }
    }
}
