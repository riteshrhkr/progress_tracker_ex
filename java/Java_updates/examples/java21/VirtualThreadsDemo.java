package examples.java21;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 21 Virtual Threads Demo
 *
 * This example demonstrates:
 * 1. Creating virtual threads
 * 2. Virtual threads vs platform threads
 * 3. Executors.newVirtualThreadPerTaskExecutor()
 * 4. Handling massive concurrency with virtual threads
 * 5. Virtual threads with blocking I/O
 * 6. ReentrantLock vs synchronized with virtual threads
 * 7. Real-world web scraping scenario
 *
 * Virtual Threads are THE biggest change to Java concurrency ever.
 */
public class VirtualThreadsDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 21: Virtual Threads (Project Loom) ===\n");

        // Example 1: Basic virtual threads
        example1_BasicVirtualThreads();

        // Example 2: Virtual threads vs platform threads (performance)
        example2_VirtualVsPlatformThreads();

        // Example 3: Virtual thread executor
        example3_VirtualThreadExecutor();

        // Example 4: Massive concurrency (millions of threads)
        example4_MassiveConcurrency();

        // Example 5: Virtual threads with blocking I/O
        example5_BlockingIO();

        // Example 6: ReentrantLock vs synchronized
        example6_LockComparison();

        // Example 7: Real-world web scraping
        example7_RealWorldWebScraping();
    }

    /**
     * Example 1: Basic Virtual Threads
     * Shows how to create virtual threads
     */
    private static void example1_BasicVirtualThreads() throws Exception {
        System.out.println("--- Example 1: Basic Virtual Threads ---");

        // Method 1: Thread.startVirtualThread()
        Thread vThread1 = Thread.startVirtualThread(() -> {
            System.out.println("  Virtual thread 1: " + Thread.currentThread());
        });
        vThread1.join();

        // Method 2: Thread.ofVirtual().start()
        Thread vThread2 = Thread.ofVirtual().start(() -> {
            System.out.println("  Virtual thread 2: " + Thread.currentThread());
        });
        vThread2.join();

        // Method 3: Thread.ofVirtual() with name
        Thread vThread3 = Thread.ofVirtual()
            .name("MyVirtualThread")
            .start(() -> {
                System.out.println("  Virtual thread 3: " + Thread.currentThread());
            });
        vThread3.join();

        // Compare with platform thread
        Thread platformThread = Thread.ofPlatform().start(() -> {
            System.out.println("  Platform thread: " + Thread.currentThread());
        });
        platformThread.join();

        System.out.println();
    }

    /**
     * Example 2: Virtual Threads vs Platform Threads
     * Performance comparison for I/O-bound tasks
     */
    private static void example2_VirtualVsPlatformThreads() throws Exception {
        System.out.println("--- Example 2: Virtual vs Platform Threads (Performance) ---");

        int numTasks = 10_000;
        Duration sleepDuration = Duration.ofMillis(100);

        // Platform threads (with thread pool)
        System.out.println("Creating " + numTasks + " tasks with platform thread pool...");
        Instant start = Instant.now();
        try (ExecutorService executor = Executors.newFixedThreadPool(200)) {
            for (int i = 0; i < numTasks; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(sleepDuration.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        Duration platformDuration = Duration.between(start, Instant.now());
        System.out.println("  Platform threads completed in: " + platformDuration.toMillis() + "ms");

        // Virtual threads
        System.out.println("\nCreating " + numTasks + " tasks with virtual threads...");
        start = Instant.now();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numTasks; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(sleepDuration.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        Duration virtualDuration = Duration.between(start, Instant.now());
        System.out.println("  Virtual threads completed in: " + virtualDuration.toMillis() + "ms");

        System.out.println("\n  Speedup: " +
            String.format("%.2f", (double) platformDuration.toMillis() / virtualDuration.toMillis()) + "x faster");

        System.out.println();
    }

    /**
     * Example 3: Virtual Thread Executor
     * Using Executors.newVirtualThreadPerTaskExecutor()
     */
    private static void example3_VirtualThreadExecutor() throws Exception {
        System.out.println("--- Example 3: Virtual Thread Executor ---");

        List<String> tasks = List.of(
            "Process order #1001",
            "Process order #1002",
            "Process order #1003",
            "Send email to customer A",
            "Send email to customer B",
            "Update inventory for product X",
            "Update inventory for product Y"
        );

        // Using try-with-resources for automatic shutdown
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("Submitting " + tasks.size() + " tasks...");

            List<Future<String>> futures = new ArrayList<>();
            for (String task : tasks) {
                Future<String> future = executor.submit(() -> {
                    Thread.sleep(100); // Simulate I/O
                    return "Completed: " + task + " on " + Thread.currentThread();
                });
                futures.add(future);
            }

            // Get results
            System.out.println("\nResults:");
            for (Future<String> future : futures) {
                System.out.println("  " + future.get());
            }
        }  // executor auto-closes, waits for all tasks

        System.out.println("\nAll tasks completed!");
        System.out.println();
    }

    /**
     * Example 4: Massive Concurrency
     * Create hundreds of thousands of virtual threads
     */
    private static void example4_MassiveConcurrency() throws Exception {
        System.out.println("--- Example 4: Massive Concurrency ---");

        int numThreads = 100_000;
        System.out.println("Creating " + numThreads + " virtual threads...");

        CountDownLatch latch = new CountDownLatch(numThreads);
        var counter = new Object() { int count = 0; };

        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numThreads; i++) {
                final int threadNum = i;
                executor.submit(() -> {
                    try {
                        // Simulate some work
                        Thread.sleep(10);
                        synchronized (counter) {
                            counter.count++;
                        }
                        if (threadNum % 10_000 == 0) {
                            System.out.println("  Thread " + threadNum + " completed");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for all to complete
            latch.await();
        }

        Duration duration = Duration.between(start, Instant.now());

        System.out.println("\n  Total threads: " + numThreads);
        System.out.println("  Completed: " + counter.count);
        System.out.println("  Time: " + duration.toMillis() + "ms");
        System.out.println("  This would be impossible with platform threads!");

        System.out.println();
    }

    /**
     * Example 5: Virtual Threads with Blocking I/O
     * Virtual threads make blocking I/O code simple again
     */
    private static void example5_BlockingIO() throws Exception {
        System.out.println("--- Example 5: Virtual Threads with Blocking I/O ---");

        // Simulate database queries
        class Database {
            String query(String sql) throws InterruptedException {
                Thread.sleep(50); // Simulate I/O delay
                return "Result for: " + sql;
            }
        }

        Database db = new Database();
        List<String> queries = List.of(
            "SELECT * FROM orders WHERE status='PENDING'",
            "SELECT * FROM users WHERE active=true",
            "SELECT * FROM products WHERE in_stock=true",
            "SELECT * FROM payments WHERE processed=false"
        );

        System.out.println("Executing " + queries.size() + " database queries concurrently...");
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = queries.stream()
                .map(query -> executor.submit(() -> {
                    // Simple synchronous code - but doesn't block OS threads!
                    String result = db.query(query);
                    return query.substring(0, 30) + "... -> " + result.substring(0, 20) + "...";
                }))
                .toList();

            System.out.println("\nResults:");
            for (Future<String> future : futures) {
                System.out.println("  " + future.get());
            }
        }

        Duration duration = Duration.between(start, Instant.now());
        System.out.println("\nCompleted in " + duration.toMillis() + "ms");
        System.out.println("Simple synchronous code, but all queries ran concurrently!");

        System.out.println();
    }

    /**
     * Example 6: ReentrantLock vs synchronized
     * ReentrantLock is better for virtual threads
     */
    private static void example6_LockComparison() throws Exception {
        System.out.println("--- Example 6: ReentrantLock vs synchronized ---");

        class Counter {
            private int value = 0;
            private final Lock lock = new ReentrantLock();

            // Using ReentrantLock - recommended for virtual threads
            void incrementWithLock() {
                lock.lock();
                try {
                    value++;
                } finally {
                    lock.unlock();
                }
            }

            // Using synchronized - can cause thread pinning
            synchronized void incrementSynchronized() {
                value++;
            }

            int getValue() {
                return value;
            }
        }

        Counter counter = new Counter();
        int numIncrements = 10_000;

        // Test with ReentrantLock
        System.out.println("Testing ReentrantLock with " + numIncrements + " virtual threads...");
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numIncrements; i++) {
                executor.submit(counter::incrementWithLock);
            }
        }

        Duration lockDuration = Duration.between(start, Instant.now());
        System.out.println("  ReentrantLock: " + lockDuration.toMillis() + "ms, final value: " + counter.value);

        // Reset counter
        counter = new Counter();

        // Test with synchronized
        System.out.println("\nTesting synchronized with " + numIncrements + " virtual threads...");
        start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numIncrements; i++) {
                executor.submit(counter::incrementSynchronized);
            }
        }

        Duration syncDuration = Duration.between(start, Instant.now());
        System.out.println("  synchronized: " + syncDuration.toMillis() + "ms, final value: " + counter.value);

        System.out.println("\nNote: ReentrantLock is recommended for virtual threads to avoid pinning.");
        System.out.println();
    }

    /**
     * Example 7: Real-World Web Scraping
     * Fetch data from multiple endpoints concurrently
     */
    private static void example7_RealWorldWebScraping() throws Exception {
        System.out.println("--- Example 7: Real-World Web Scraping Scenario ---");

        // Simulate a product aggregator that fetches prices from multiple vendors
        class VendorAPI {
            private final String name;
            private final Random random = new Random();

            VendorAPI(String name) {
                this.name = name;
            }

            ProductPrice fetchPrice(String productId) throws InterruptedException {
                // Simulate network I/O delay
                Thread.sleep(50 + random.nextInt(100));

                double price = 99.99 + random.nextDouble() * 100;
                boolean inStock = random.nextBoolean();

                return new ProductPrice(name, productId, price, inStock);
            }
        }

        record ProductPrice(String vendor, String productId, double price, boolean inStock) {}

        // Create vendor APIs
        List<VendorAPI> vendors = List.of(
            new VendorAPI("Amazon"),
            new VendorAPI("Walmart"),
            new VendorAPI("BestBuy"),
            new VendorAPI("Target"),
            new VendorAPI("Newegg")
        );

        List<String> products = List.of("LAPTOP-001", "PHONE-002", "TABLET-003");

        System.out.println("Fetching prices for " + products.size() + " products from " +
                         vendors.size() + " vendors...");
        System.out.println("Total API calls: " + (products.size() * vendors.size()));

        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<String, List<ProductPrice>> pricesByProduct = new ConcurrentHashMap<>();

            List<Future<Void>> futures = new ArrayList<>();

            // For each product, fetch from all vendors concurrently
            for (String productId : products) {
                pricesByProduct.put(productId, new CopyOnWriteArrayList<>());

                for (VendorAPI vendor : vendors) {
                    Future<Void> future = executor.submit(() -> {
                        try {
                            ProductPrice price = vendor.fetchPrice(productId);
                            pricesByProduct.get(productId).add(price);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return null;
                    });
                    futures.add(future);
                }
            }

            // Wait for all to complete
            for (Future<Void> future : futures) {
                future.get();
            }

            Duration duration = Duration.between(start, Instant.now());

            // Display results
            System.out.println("\nResults (fetched in " + duration.toMillis() + "ms):\n");

            for (String productId : products) {
                System.out.println("Product: " + productId);
                List<ProductPrice> prices = pricesByProduct.get(productId);

                // Find best price
                Optional<ProductPrice> bestPrice = prices.stream()
                    .filter(ProductPrice::inStock)
                    .min(Comparator.comparing(ProductPrice::price));

                for (ProductPrice price : prices) {
                    String marker = bestPrice.isPresent() && price.equals(bestPrice.get()) ? " ← BEST" : "";
                    String stock = price.inStock() ? "In Stock" : "Out of Stock";
                    System.out.println(String.format("  %-10s: $%6.2f (%s)%s",
                        price.vendor(), price.price(), stock, marker));
                }
                System.out.println();
            }

            System.out.println("Total time: " + duration.toMillis() + "ms");
            System.out.println("With traditional threads, this would take much longer!");
            System.out.println("Virtual threads make this simple, synchronous-looking code scale massively.");
        }

        System.out.println();
    }
}
