package examples.executors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Shows why production code usually submits tasks to an ExecutorService
 * instead of manually creating a new platform thread for every piece of work.
 *
 * Run: java -cp out examples.executors.ExecutorDemo
 */
public class ExecutorDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("[1] Fixed thread pool reuses workers");
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            List<Callable<String>> tasks = List.of(
                    () -> work("task-a"),
                    () -> work("task-b"),
                    () -> work("task-c"));

            for (Future<String> future : pool.invokeAll(tasks)) {
                System.out.println("    " + future.get());
            }
        }

        System.out.println();
        System.out.println("[2] Future.get() reports task failure");
        try (ExecutorService pool = Executors.newSingleThreadExecutor()) {
            Future<Integer> failed = pool.submit(() -> 10 / 0);
            try {
                failed.get();
            } catch (ExecutionException e) {
                System.out.println("    cause = " + e.getCause().getClass().getSimpleName());
            }
        }
    }

    private static String work(String name) {
        return name + " ran on " + Thread.currentThread().getName();
    }
}
