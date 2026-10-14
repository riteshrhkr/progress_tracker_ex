package examples.coordination;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Shows three coordination tools:
 * synchronized for mutual exclusion, wait/notify for a condition,
 * and ReentrantLock when the code needs timed lock attempts.
 *
 * Run: java -cp out examples.coordination.LockCoordinationDemo
 */
public class LockCoordinationDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[1] wait() releases the monitor until data is ready");
        OneItemBox box = new OneItemBox();
        Thread consumer = new Thread(() -> System.out.println("    consumed = " + box.take()), "consumer");
        consumer.start();
        Thread.sleep(50);
        box.put("message");
        consumer.join();

        System.out.println();
        System.out.println("[2] notifyAll() wakes waiters, while loop re-checks the condition");
        System.out.println("    box is empty after take = " + box.isEmpty());

        System.out.println();
        System.out.println("[3] ReentrantLock can try instead of waiting forever");
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            Thread contender = new Thread(() -> {
                boolean acquired = lock.tryLock();
                System.out.println("    contender acquired lock = " + acquired);
                if (acquired) {
                    lock.unlock();
                }
            }, "contender");
            contender.start();
            contender.join();
        } finally {
            lock.unlock();
        }
    }

    static class OneItemBox {
        private final Queue<String> queue = new ArrayDeque<>();

        synchronized void put(String value) {
            queue.add(value);
            notifyAll();
        }

        synchronized String take() {
            while (queue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting", e);
                }
            }
            return queue.remove();
        }

        synchronized boolean isEmpty() {
            return queue.isEmpty();
        }
    }
}
