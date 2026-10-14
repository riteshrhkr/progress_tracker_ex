package examples.basics;

/**
 * Shows the smallest useful facts about Thread:
 * code runs on the current thread until a new Thread is started,
 * start() schedules work on a different thread,
 * run() is just an ordinary method call,
 * and join() waits for another thread to finish.
 *
 * Run: java -cp out examples.basics.ThreadBasicsDemo
 */
public class ThreadBasicsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[1] main thread");
        System.out.println("    current thread = " + Thread.currentThread().getName());

        System.out.println();
        System.out.println("[2] start() creates another path of execution");
        Thread worker = new Thread(() ->
                System.out.println("    worker body ran on = " + Thread.currentThread().getName()),
                "worker-1");
        worker.start();
        worker.join();
        System.out.println("    worker finished before this line");

        System.out.println();
        System.out.println("[3] run() does not start a new thread");
        Thread notStarted = new Thread(() ->
                System.out.println("    run() body ran on = " + Thread.currentThread().getName()),
                "worker-2");
        notStarted.run();

        System.out.println();
        System.out.println("[4] start() can be called only once");
        try {
            worker.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("    second start() -> " + e.getClass().getSimpleName());
        }
    }
}
