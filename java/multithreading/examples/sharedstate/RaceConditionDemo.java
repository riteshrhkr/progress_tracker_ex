package examples.sharedstate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Two threads updating the same state must coordinate.
 * This demo compares an unprotected int with synchronized and AtomicInteger.
 *
 * Run: java -cp out examples.sharedstate.RaceConditionDemo
 */
public class RaceConditionDemo {

    private static final int INCREMENTS_PER_THREAD = 100_000;

    private int unsafeCount;
    private int synchronizedCount;
    private final AtomicInteger atomicCount = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        RaceConditionDemo demo = new RaceConditionDemo();

        System.out.println("[1] Lost update");
        demo.runTwoThreads(demo::incrementUnsafely);
        System.out.println("    unsafe count       = " + demo.unsafeCount);
        System.out.println("    expected count     = " + (INCREMENTS_PER_THREAD * 2));

        System.out.println();
        System.out.println("[2] Synchronized update");
        demo.runTwoThreads(demo::incrementWithSynchronized);
        System.out.println("    synchronized count = " + demo.synchronizedCount);

        System.out.println();
        System.out.println("[3] AtomicInteger update");
        demo.runTwoThreads(demo.atomicCount::incrementAndGet);
        System.out.println("    atomic count       = " + demo.atomicCount.get());
    }

    private void incrementUnsafely() {
        unsafeCount++;
    }

    private synchronized void incrementWithSynchronized() {
        synchronizedCount++;
    }

    private void runTwoThreads(Runnable task) throws InterruptedException {
        Thread a = new Thread(() -> repeat(task), "counter-a");
        Thread b = new Thread(() -> repeat(task), "counter-b");
        a.start();
        b.start();
        a.join();
        b.join();
    }

    private static void repeat(Runnable task) {
        for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
            task.run();
        }
    }
}
