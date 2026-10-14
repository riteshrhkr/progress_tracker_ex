package examples.virtualthreads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Shows the shape of Java 21 virtual threads and CompletableFuture.
 * Virtual threads are useful for blocking I/O-style work. CompletableFuture
 * is useful when composing separate asynchronous results.
 *
 * Run: java -cp out examples.virtualthreads.VirtualThreadDemo
 */
public class VirtualThreadDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("[1] Virtual thread");
        Thread virtual = Thread.ofVirtual()
                .name("order-loader")
                .start(() -> System.out.println("    isVirtual = " + Thread.currentThread().isVirtual()));
        virtual.join();

        System.out.println();
        System.out.println("[2] One virtual thread per task executor");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var first = executor.submit(() -> blockingStyleCall("customer"));
            var second = executor.submit(() -> blockingStyleCall("orders"));
            System.out.println("    result = " + first.get() + " + " + second.get());
        }

        System.out.println();
        System.out.println("[3] CompletableFuture composes results");
        CompletableFuture<String> customer = CompletableFuture.supplyAsync(() -> "customer-42");
        CompletableFuture<String> orders = CompletableFuture.supplyAsync(() -> "3 orders");
        System.out.println("    combined = " + customer.thenCombine(orders, (c, o) -> c + " has " + o).get());

        System.out.println();
        System.out.println("[4] CompletableFuture wraps failures");
        try {
            CompletableFuture.supplyAsync(() -> 10 / 0).get();
        } catch (ExecutionException e) {
            System.out.println("    cause = " + e.getCause().getClass().getSimpleName());
        }
    }

    private static String blockingStyleCall(String value) throws InterruptedException {
        Thread.sleep(20);
        return value;
    }
}
