package examples.java12_16;


/**
 * Java 12-16 Switch Expressions and Text Blocks Demo
 *
 * This example demonstrates:
 * 1. Switch expressions with arrow syntax
 * 2. Multiple case labels
 * 3. yield keyword for complex blocks
 * 4. Text blocks for multi-line strings
 * 5. Text blocks with JSON, SQL, HTML
 * 6. formatted() method with text blocks
 *
 * Real-world scenario: Order processing and notification system
 */
public class SwitchExpressionsDemo {

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

    enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, CRYPTO, CASH
    }

    public static void main(String[] args) {
        System.out.println("=== Java 12-16: Switch Expressions & Text Blocks ===\n");

        // Example 1: Basic switch expressions
        example1_BasicSwitchExpressions();

        // Example 2: Switch with yield
        example2_SwitchWithYield();

        // Example 3: Exhaustive switch with enums
        example3_ExhaustiveSwitch();

        // Example 4: Text blocks basics
        example4_TextBlocksBasics();

        // Example 5: Text blocks for JSON
        example5_TextBlocksJson();

        // Example 6: Text blocks for SQL
        example6_TextBlocksSql();

        // Example 7: Real-world order processing
        example7_RealWorldOrderProcessing();
    }

    /**
     * Example 1: Basic Switch Expressions
     * Shows arrow syntax and multiple case labels
     */
    private static void example1_BasicSwitchExpressions() {
        System.out.println("--- Example 1: Basic Switch Expressions ---");

        // Day name length
        for (Day day : new Day[]{Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY}) {
            int numLetters = switch (day) {
                case MONDAY, FRIDAY, SUNDAY -> 6;
                case TUESDAY -> 7;
                case THURSDAY, SATURDAY -> 8;
                case WEDNESDAY -> 9;
            };
            System.out.println(day + " has " + numLetters + " letters");
        }

        // Weekend check
        System.out.println("\nWeekend check:");
        for (Day day : Day.values()) {
            boolean isWeekend = switch (day) {
                case SATURDAY, SUNDAY -> true;
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> false;
            };
            if (isWeekend) {
                System.out.println("  " + day + " is a weekend");
            }
        }

        // Season by month
        System.out.println("\nSeasons:");
        int[] months = {1, 4, 7, 10};
        for (int month : months) {
            String season = switch (month) {
                case 12, 1, 2 -> "Winter";
                case 3, 4, 5 -> "Spring";
                case 6, 7, 8 -> "Summer";
                case 9, 10, 11 -> "Fall";
                default -> "Unknown";
            };
            System.out.println("  Month " + month + ": " + season);
        }

        System.out.println();
    }

    /**
     * Example 2: Switch with yield
     * Demonstrates yield for complex case blocks
     */
    private static void example2_SwitchWithYield() {
        System.out.println("--- Example 2: Switch with yield ---");

        // Calculator with yield
        int a = 10, b = 5;
        String[] operations = {"add", "multiply", "divide"};

        for (String operation : operations) {
            int result = switch (operation) {
                case "add" -> a + b;
                case "subtract" -> a - b;
                case "multiply" -> {
                    System.out.println("  Multiplying " + a + " * " + b);
                    yield a * b;  // yield from block
                }
                case "divide" -> {
                    if (b == 0) {
                        System.out.println("  Division by zero!");
                        yield 0;
                    }
                    yield a / b;
                }
                default -> {
                    System.out.println("  Unknown operation: " + operation);
                    yield 0;
                }
            };
            System.out.println("  " + a + " " + operation + " " + b + " = " + result);
        }

        System.out.println();
    }

    /**
     * Example 3: Exhaustive Switch with Enums
     * Shows compiler-enforced exhaustiveness
     */
    private static void example3_ExhaustiveSwitch() {
        System.out.println("--- Example 3: Exhaustive Switch with Enums ---");

        OrderStatus[] statuses = {
            OrderStatus.PENDING,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
        };

        for (OrderStatus status : statuses) {
            // No default needed - enum is exhaustive
            String message = switch (status) {
                case PENDING -> "Order received, awaiting processing";
                case PROCESSING -> "Order is being prepared";
                case SHIPPED -> "Order is on the way";
                case DELIVERED -> "Order has been delivered";
                case CANCELLED -> "Order was cancelled";
            };

            double fee = switch (status) {
                case PENDING -> 0.0;
                case PROCESSING -> 5.0;
                case SHIPPED -> 10.0;
                case DELIVERED -> 0.0;
                case CANCELLED -> 0.0;
            };

            System.out.println("  " + status + ": " + message + " (fee: $" + fee + ")");
        }

        System.out.println();
    }

    /**
     * Example 4: Text Blocks Basics
     * Introduction to text blocks syntax
     */
    private static void example4_TextBlocksBasics() {
        System.out.println("--- Example 4: Text Blocks Basics ---");

        // Simple multi-line text
        String poem = """
            Roses are red,
            Violets are blue,
            Java is awesome,
            And so are you!
            """;

        System.out.println("Poem:");
        System.out.println(poem);

        // No need to escape quotes
        String quote = """
            She said, "Java text blocks are great!"
            He replied, "I agree, they're much better than \\"escape hell\\"!"
            """;

        System.out.println("Quote:");
        System.out.println(quote);

        // Using formatted()
        String name = "Alice";
        int age = 30;
        String greeting = """
            Hello, %s!
            You are %d years old.
            Welcome to our application.
            """.formatted(name, age);

        System.out.println("Formatted greeting:");
        System.out.println(greeting);

        System.out.println();
    }

    /**
     * Example 5: Text Blocks for JSON
     * Clean JSON string creation
     */
    private static void example5_TextBlocksJson() {
        System.out.println("--- Example 5: Text Blocks for JSON ---");

        // Simple JSON
        String userJson = """
            {
              "id": 1001,
              "name": "John Doe",
              "email": "john@example.com",
              "active": true
            }
            """;

        System.out.println("User JSON:");
        System.out.println(userJson);

        // Complex nested JSON
        String orderJson = """
            {
              "orderId": "ORD-12345",
              "customer": {
                "name": "Alice Smith",
                "email": "alice@example.com"
              },
              "items": [
                {
                  "productId": "P001",
                  "name": "Laptop",
                  "price": 999.99,
                  "quantity": 1
                },
                {
                  "productId": "P002",
                  "name": "Mouse",
                  "price": 29.99,
                  "quantity": 2
                }
              ],
              "total": 1059.97,
              "status": "PROCESSING"
            }
            """;

        System.out.println("Order JSON:");
        System.out.println(orderJson);

        // JSON with formatted() for dynamic values
        String orderId = "ORD-99999";
        String customerName = "Bob Johnson";
        double total = 499.99;

        String dynamicJson = """
            {
              "orderId": "%s",
              "customer": "%s",
              "total": %.2f,
              "timestamp": "%s"
            }
            """.formatted(orderId, customerName, total, java.time.Instant.now());

        System.out.println("Dynamic JSON:");
        System.out.println(dynamicJson);

        System.out.println();
    }

    /**
     * Example 6: Text Blocks for SQL
     * Clean SQL query creation
     */
    private static void example6_TextBlocksSql() {
        System.out.println("--- Example 6: Text Blocks for SQL ---");

        // Simple SELECT query
        String selectQuery = """
            SELECT id, name, email, created_at
            FROM users
            WHERE active = true
            ORDER BY created_at DESC
            LIMIT 10
            """;

        System.out.println("SELECT query:");
        System.out.println(selectQuery);

        // Complex JOIN query
        String joinQuery = """
            SELECT
                u.id,
                u.name,
                u.email,
                COUNT(o.id) as order_count,
                SUM(o.total) as total_spent
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            WHERE u.active = true
            GROUP BY u.id, u.name, u.email
            HAVING COUNT(o.id) > 0
            ORDER BY total_spent DESC
            """;

        System.out.println("JOIN query:");
        System.out.println(joinQuery);

        // INSERT statement
        String insertQuery = """
            INSERT INTO orders (user_id, product_id, quantity, price, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        System.out.println("INSERT query:");
        System.out.println(insertQuery);

        // Formatted SQL with values
        String tableName = "products";
        String minPrice = "100.00";

        String dynamicQuery = """
            SELECT * FROM %s
            WHERE price > %s
            AND in_stock = true
            """.formatted(tableName, minPrice);

        System.out.println("Dynamic query:");
        System.out.println(dynamicQuery);

        System.out.println();
    }

    /**
     * Example 7: Real-World Order Processing
     * Combining switch expressions and text blocks
     */
    private static void example7_RealWorldOrderProcessing() {
        System.out.println("--- Example 7: Real-World Order Processing ---");

        Order[] orders = {
            new Order("ORD-001", "Alice", 299.99, OrderStatus.PROCESSING, PaymentMethod.CREDIT_CARD),
            new Order("ORD-002", "Bob", 49.99, OrderStatus.SHIPPED, PaymentMethod.PAYPAL),
            new Order("ORD-003", "Charlie", 1299.99, OrderStatus.PENDING, PaymentMethod.CRYPTO)
        };

        for (Order order : orders) {
            // Process order based on status
            String action = switch (order.status) {
                case PENDING -> "Validate payment and begin processing";
                case PROCESSING -> "Prepare items for shipment";
                case SHIPPED -> "Track package and notify customer";
                case DELIVERED -> "Request customer feedback";
                case CANCELLED -> "Process refund";
            };

            // Calculate processing fee
            double fee = switch (order.paymentMethod) {
                case CREDIT_CARD, DEBIT_CARD -> order.total * 0.029; // 2.9%
                case PAYPAL -> order.total * 0.034; // 3.4%
                case CRYPTO -> order.total * 0.01;  // 1%
                case CASH -> 0.0;
            };

            // Generate notification email
            String emailSubject = switch (order.status) {
                case PENDING -> "Order Confirmation";
                case PROCESSING -> "Order Being Prepared";
                case SHIPPED -> "Order Shipped";
                case DELIVERED -> "Order Delivered";
                case CANCELLED -> "Order Cancelled";
            };

            String emailBody = """
                Dear %s,

                %s

                Order Details:
                - Order ID: %s
                - Total: $%.2f
                - Processing Fee: $%.2f
                - Payment Method: %s
                - Status: %s

                Next Action: %s

                Thank you for your business!

                Best regards,
                Customer Service Team
                """.formatted(
                    order.customerName,
                    getStatusMessage(order.status),
                    order.orderId,
                    order.total,
                    fee,
                    order.paymentMethod,
                    order.status,
                    action
                );

            System.out.println("Email for " + order.orderId + ":");
            System.out.println("Subject: " + emailSubject);
            System.out.println(emailBody);
            System.out.println("-".repeat(60));
        }
    }

    private static String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PENDING -> "We have received your order and it is pending processing.";
            case PROCESSING -> "Your order is being prepared for shipment.";
            case SHIPPED -> "Your order has been shipped and is on its way!";
            case DELIVERED -> "Your order has been successfully delivered.";
            case CANCELLED -> "Your order has been cancelled as requested.";
        };
    }

    // Domain classes
    static class Order {
        final String orderId;
        final String customerName;
        final double total;
        final OrderStatus status;
        final PaymentMethod paymentMethod;

        Order(String orderId, String customerName, double total, OrderStatus status, PaymentMethod paymentMethod) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.total = total;
            this.status = status;
            this.paymentMethod = paymentMethod;
        }
    }
}
