package examples.queues;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * PriorityQueue is a binary heap. poll() gives the next smallest (or whatever
 * the comparator says). Iteration walks the heap array, which is not sorted.
 *
 * Run: java -cp out examples.queues.PriorityQueueDemo
 */
public class PriorityQueueDemo {

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. poll returns elements in comparator order. for-each does not.
        //    Printing the queue also uses iteration, so do not trust that.
        // ------------------------------------------------------------------
        System.out.println("[1] poll order versus iteration order");
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int n : new int[] {10, 3, 5, 1, 9, 2, 8, 4}) {
            heap.offer(n);
        }
        System.out.println("    toString / iteration = " + heap);
        System.out.print("    poll until empty     = ");
        while (!heap.isEmpty()) {
            System.out.print(heap.poll());
            if (!heap.isEmpty()) {
                System.out.print(", ");
            }
        }
        System.out.println();

        // ------------------------------------------------------------------
        // 2. A comparator reverses the order. null is never allowed —
        //    the heap has no way to compare it.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Comparator and null");
        PriorityQueue<Integer> largestFirst = new PriorityQueue<>(Comparator.reverseOrder());
        largestFirst.offer(5);
        largestFirst.offer(1);
        largestFirst.offer(3);
        System.out.println("    reverseOrder poll = " + largestFirst.poll()
                + ", " + largestFirst.poll() + ", " + largestFirst.poll());
        try {
            new PriorityQueue<String>().offer(null);
        } catch (NullPointerException e) {
            System.out.println("    offer(null) -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 3. contains and remove(Object) scan the heap array, O(n).
        //    Only poll/peek/offer are the cheap heap operations.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] remove(Object) is a linear scan");
        PriorityQueue<Integer> q = new PriorityQueue<>();
        q.offer(1);
        q.offer(9);
        q.offer(3);
        System.out.println("    before remove(9) = " + q);
        boolean removed = q.remove(9);
        System.out.println("    remove(9)        = " + removed);
        System.out.print("    poll remaining   = ");
        while (!q.isEmpty()) {
            System.out.print(q.poll());
            if (!q.isEmpty()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
}
