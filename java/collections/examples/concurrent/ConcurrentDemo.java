package examples.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ArrayList and HashMap are not safe to share between threads.
 * Synchronized wrappers lock each call but the iterator is still fail-fast.
 * ConcurrentHashMap and CopyOnWriteArrayList are built for sharing.
 *
 * Run: java -cp out examples.concurrent.ConcurrentDemo
 */
public class ConcurrentDemo {

    public static void main(String[] args) throws InterruptedException {
        // ------------------------------------------------------------------
        // 1. Collections.synchronizedList locks add/remove/get. for-each
        //    still uses a fail-fast iterator. Mutating during iteration
        //    throws, even on the same thread, even with the wrapper.
        // ------------------------------------------------------------------
        System.out.println("[1] Synchronized wrapper, fail-fast iterator");
        List<String> synced = Collections.synchronizedList(new ArrayList<>(List.of("a", "b")));
        try {
            for (String s : synced) {
                synced.add("x");                    // individual add is locked; the iterator is not consistent
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("    for-each plus add on synchronizedList -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 2. CopyOnWriteArrayList snapshots on iterator(). Adds after that
        //    are invisible to the iterator. No ConcurrentModificationException.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] CopyOnWriteArrayList iterator is a snapshot");
        CopyOnWriteArrayList<String> cow = new CopyOnWriteArrayList<>(List.of("a"));
        Iterator<String> snap = cow.iterator();
        cow.add("b");
        System.out.println("    list after add(\"b\")     = " + cow);
        System.out.print("    iterator started before  = ");
        snap.forEachRemaining(s -> System.out.print(s + " "));
        System.out.println();

        // ------------------------------------------------------------------
        // 3. Two threads inserting disjoint keys into a HashMap. The map
        //    is not thread-safe: size is often less than 20_000. The same
        //    work on ConcurrentHashMap keeps every entry.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] HashMap versus ConcurrentHashMap under two writers");
        Map<Integer, Integer> unsafe = new HashMap<>();
        int unsafeSize = fillInTwoThreads(unsafe);
        System.out.println("    HashMap size after 2 x 10_000 puts = " + unsafeSize + " (expected 20000)");

        Map<Integer, Integer> safe = new ConcurrentHashMap<>();
        int safeSize = fillInTwoThreads(safe);
        System.out.println("    ConcurrentHashMap size             = " + safeSize);

        // ------------------------------------------------------------------
        // 4. ConcurrentHashMap forbids null keys and null values. HashMap
        //    allows one null key and any number of null values.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] ConcurrentHashMap rejects null");
        try {
            new ConcurrentHashMap<String, Integer>().put(null, 1);
        } catch (NullPointerException e) {
            System.out.println("    put(null, 1) -> " + e.getClass().getSimpleName());
        }
        try {
            new ConcurrentHashMap<String, Integer>().put("a", null);
        } catch (NullPointerException e) {
            System.out.println("    put(\"a\", null) -> " + e.getClass().getSimpleName());
        }

        HashMap<String, Integer> hash = new HashMap<>();
        hash.put(null, 1);
        hash.put("a", null);
        System.out.println("    HashMap allows both: " + hash);
    }

    /**
     * Thread A writes 0..9999, thread B writes 10000..19999.
     * A thread-safe map ends at 20000. HashMap often does not.
     */
    static int fillInTwoThreads(Map<Integer, Integer> map) throws InterruptedException {
        Thread a = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                map.put(i, i);
            }
        });
        Thread b = new Thread(() -> {
            for (int i = 10_000; i < 20_000; i++) {
                map.put(i, i);
            }
        });
        a.start();
        b.start();
        a.join();
        b.join();
        return map.size();
    }
}
