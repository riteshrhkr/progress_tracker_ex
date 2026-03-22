package examples.java25;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Java 25 Scoped Values Demo
 *
 * This example demonstrates:
 * 1. Basic Scoped Values usage
 * 2. Scoped Values vs ThreadLocal comparison
 * 3. Using Scoped Values with virtual threads
 * 4. Nested scopes
 * 5. Multiple scoped values
 * 6. Real-world request processing with context
 * 7. Performance benefits with millions of virtual threads
 *
 * Scoped Values are the modern replacement for ThreadLocal,
 * optimized for virtual threads.
 */
public class ScopedValuesDemo {

    // Define scoped values
    private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<String> TRANSACTION_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 25: Scoped Values ===\n");

        // Example 1: Basic usage
        example1_BasicUsage();

        // Example 2: Scoped Values vs ThreadLocal
        example2_ScopedValuesVsThreadLocal();

        // Example 3: Virtual threads with Scoped Values
        example3_VirtualThreadsWithScopedValues();

        // Example 4: Nested scopes
        example4_NestedScopes();

        // Example 5: Multiple scoped values
        example5_MultipleScopedValues();

        // Example 6: Real-world request processing
        example6_RealWorldRequestProcessing();

        // Example 7: Performance with millions of threads
        example7_PerformanceDemo();
    }

    /**
     * Example 1: Basic Scoped Values Usage
     * Shows how to bind and access scoped values
     */
    private static void example1_BasicUsage() {
        System.out.println("--- Example 1: Basic Scoped Values Usage ---");

        User alice = new User("alice@example.com", "Alice", "ADMIN");

        // Bind a value to a scoped value
        ScopedValue.where(CURRENT_USER, alice).run(() -> {
            // Value is available within this scope
            User currentUser = CURRENT_USER.get();
            System.out.println("  Current user: " + currentUser.name());
            System.out.println("  Email: " + currentUser.email());
            System.out.println("  Role: " + currentUser.role());

            // Call method that uses the scoped value
            processUserAction();
        });

        // Outside the scope, value is not available
        try {
            User user = CURRENT_USER.get();
            System.out.println("  This won't print: " + user);
        } catch (NoSuchElementException e) {
            System.out.println("  ✓ Outside scope: NoSuchElementException thrown");
        }

        // Safe access with orElse
        User user = CURRENT_USER.orElse(null);
        System.out.println("  Safe access outside scope: " + user);

        System.out.println();
    }

    private static void processUserAction() {
        // Can access CURRENT_USER anywhere in the call stack
        System.out.println("  Processing action for: " + CURRENT_USER.get().name());
    }

    /**
     * Example 2: Scoped Values vs ThreadLocal
     * Performance and safety comparison
     */
    private static void example2_ScopedValuesVsThreadLocal() {
        System.out.println("--- Example 2: Scoped Values vs ThreadLocal ---");

        // ThreadLocal approach - mutable, requires cleanup
        ThreadLocal<User> threadLocalUser = new ThreadLocal<>();

        System.out.println("ThreadLocal approach:");
        try {
            threadLocalUser.set(new User("bob@example.com", "Bob", "USER"));
            System.out.println("  User: " + threadLocalUser.get().name());
            // Must remember to clean up!
        } finally {
            threadLocalUser.remove();
            System.out.println("  ✓ Cleaned up (must remember to do this!)");
        }

        // Scoped Values approach - immutable, automatic cleanup
        System.out.println("\nScoped Values approach:");
        User carol = new User("carol@example.com", "Carol", "USER");

        ScopedValue.where(CURRENT_USER, carol).run(() -> {
            System.out.println("  User: " + CURRENT_USER.get().name());
            System.out.println("  ✓ Automatically cleaned up when scope ends");
        });

        System.out.println("  Outside scope: " + CURRENT_USER.orElse(null));

        System.out.println("\nKey differences:");
        System.out.println("  - Scoped Values are immutable");
        System.out.println("  - Automatic cleanup (no memory leaks)");
        System.out.println("  - Optimized for virtual threads");
        System.out.println("  - Better performance with millions of threads");

        System.out.println();
    }

    /**
     * Example 3: Virtual Threads with Scoped Values
     * Shows propagation across virtual threads
     */
    private static void example3_VirtualThreadsWithScopedValues() throws Exception {
        System.out.println("--- Example 3: Virtual Threads with Scoped Values ---");

        User user = new User("david@example.com", "David", "ADMIN");

        ScopedValue.where(CURRENT_USER, user).run(() -> {
            System.out.println("Main thread: " + CURRENT_USER.get().name());

            // Scoped value automatically available in virtual threads
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<String>> futures = new ArrayList<>();

                // Create 5 virtual threads, all can access the scoped value
                for (int i = 0; i < 5; i++) {
                    final int taskId = i + 1;
                    Future<String> future = executor.submit(() -> {
                        // Scoped value propagates automatically!
                        String userName = CURRENT_USER.get().name();
                        return "Task " + taskId + " processed by " + userName;
                    });
                    futures.add(future);
                }

                // Collect results
                for (Future<String> future : futures) {
                    System.out.println("  " + future.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println();
    }

    /**
     * Example 4: Nested Scopes
     * Shows how scopes can be nested and overridden
     */
    private static void example4_NestedScopes() {
        System.out.println("--- Example 4: Nested Scopes ---");

        User outerUser = new User("alice@example.com", "Alice", "USER");
        User innerUser = new User("bob@example.com", "Bob", "ADMIN");

        ScopedValue.where(CURRENT_USER, outerUser).run(() -> {
            System.out.println("  Outer scope: " + CURRENT_USER.get().name());

            // Inner scope with different value
            ScopedValue.where(CURRENT_USER, innerUser).run(() -> {
                System.out.println("    Inner scope: " + CURRENT_USER.get().name());
                System.out.println("    Inner role: " + CURRENT_USER.get().role());
            });

            // Back to outer scope
            System.out.println("  Back to outer: " + CURRENT_USER.get().name());
        });

        System.out.println();
    }

    /**
     * Example 5: Multiple Scoped Values
     * Shows using multiple scoped values together
     */
    private static void example5_MultipleScopedValues() {
        System.out.println("--- Example 5: Multiple Scoped Values ---");

        User user = new User("carol@example.com", "Carol", "ADMIN");
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);

        // Bind multiple scoped values at once
        ScopedValue.where(CURRENT_USER, user)
            .where(REQUEST_ID, requestId)
            .where(TRANSACTION_ID, transactionId)
            .run(() -> {
                logOperation("Process payment");
                logOperation("Update inventory");
                logOperation("Send confirmation");
            });

        System.out.println();
    }

    private static void logOperation(String operation) {
        System.out.printf("  [%s] [%s] User: %s, Operation: %s%n",
            REQUEST_ID.get(),
            TRANSACTION_ID.get(),
            CURRENT_USER.get().name(),
            operation
        );
    }

    /**
     * Example 6: Real-World Request Processing
     * Simulates handling HTTP requests with context
     */
    private static void example6_RealWorldRequestProcessing() throws Exception {
        System.out.println("--- Example 6: Real-World Request Processing ---");

        // Simulate incoming requests
        List<Request> requests = List.of(
            new Request("GET /orders", new User("alice@example.com", "Alice", "USER")),
            new Request("POST /checkout", new User("bob@example.com", "Bob", "USER")),
            new Request("GET /admin/users", new User("carol@example.com", "Carol", "ADMIN"))
        );

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Request request : requests) {
                executor.submit(() -> {
                    handleRequest(request);
                });
            }
        }

        System.out.println();
    }

    private static void handleRequest(Request request) {
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);

        // Set up request context
        ScopedValue.where(CURRENT_USER, request.user())
            .where(REQUEST_ID, requestId)
            .run(() -> {
                System.out.println("\n  Processing: " + request.path());
                System.out.println("    Request ID: " + REQUEST_ID.get());
                System.out.println("    User: " + CURRENT_USER.get().name());

                // Authorization check - has access to CURRENT_USER
                if (requiresAdmin(request.path()) && !isAdmin()) {
                    System.out.println("    ❌ Access denied");
                    return;
                }

                // Process request - automatically has context
                processRequest(request.path());
                logAudit("Request completed");
            });
    }

    private static boolean requiresAdmin(String path) {
        return path.startsWith("/admin");
    }

    private static boolean isAdmin() {
        return "ADMIN".equals(CURRENT_USER.get().role());
    }

    private static void processRequest(String path) {
        System.out.println("    ✓ Processing: " + path);
        // All methods have access to CURRENT_USER and REQUEST_ID
    }

    private static void logAudit(String message) {
        System.out.printf("    [AUDIT] %s - User: %s, Request: %s%n",
            message,
            CURRENT_USER.get().email(),
            REQUEST_ID.get()
        );
    }

    /**
     * Example 7: Performance Demo
     * Shows scalability with many virtual threads
     */
    private static void example7_PerformanceDemo() throws Exception {
        System.out.println("--- Example 7: Performance with Many Virtual Threads ---");

        int numThreads = 100_000;
        User user = new User("perf@example.com", "PerfTest", "USER");

        System.out.println("Creating " + numThreads + " virtual threads with Scoped Values...");

        Instant start = Instant.now();
        CountDownLatch latch = new CountDownLatch(numThreads);

        ScopedValue.where(CURRENT_USER, user).run(() -> {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < numThreads; i++) {
                    executor.submit(() -> {
                        try {
                            // Each thread can access scoped value
                            String email = CURRENT_USER.get().email();
                            // Simulate minimal work
                            Thread.sleep(1);
                        } catch (Exception e) {
                            // Ignore
                        } finally {
                            latch.countDown();
                        }
                    });
                }
            }
        });

        latch.await();
        long duration = java.time.Duration.between(start, Instant.now()).toMillis();

        System.out.println("  ✓ Completed " + numThreads + " threads in " + duration + "ms");
        System.out.println("  Scoped Values are lightweight and scale to millions of threads!");
        System.out.println("  (ThreadLocal would struggle with this many threads)");

        System.out.println();
    }

    // Domain records
    record User(String email, String name, String role) {}
    record Request(String path, User user) {}
}
