package examples.queues;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Queue methods come in two families: throw, or return a special value.
 * ArrayDeque is the default implementation for both a queue and a stack.
 *
 * Run: java -cp out examples.queues.QueueDequeDemo
 */
public class QueueDequeDemo {

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. A queue: add at the tail, take from the head. ArrayDeque does
        //    this with a circular array. LinkedList can too, but allocates
        //    a node per element.
        // ------------------------------------------------------------------
        System.out.println("[1] ArrayDeque as a queue");
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("a");
        queue.offer("b");
        queue.offer("c");
        System.out.println("    after offer a,b,c = " + queue);
        System.out.println("    poll()            = " + queue.poll() + ", remaining = " + queue);
        System.out.println("    peek()            = " + queue.peek() + ", remaining = " + queue);

        // ------------------------------------------------------------------
        // 2. Empty queue: poll/peek return null. remove/element throw.
        //    That is the whole difference between the two method families.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Throw versus special value");
        Queue<String> empty = new ArrayDeque<>();
        System.out.println("    poll()  on empty = " + empty.poll());
        System.out.println("    peek()  on empty = " + empty.peek());
        try {
            empty.remove();
        } catch (NoSuchElementException e) {
            System.out.println("    remove() on empty -> " + e.getClass().getSimpleName());
        }
        try {
            empty.element();
        } catch (NoSuchElementException e) {
            System.out.println("    element() on empty -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 3. A deque is both ends. push/pop at the head is a stack.
        //    ArrayDeque is the replacement for java.util.Stack.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] ArrayDeque as a stack");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");
        System.out.println("    after three push  = " + stack);
        System.out.println("    pop()             = " + stack.pop());
        System.out.println("    pop()             = " + stack.pop());
        System.out.println("    remaining         = " + stack);

        // ------------------------------------------------------------------
        // 4. LinkedList implements List and Deque. Using it as a queue
        //    works, but you pay a node allocation and you expose List
        //    methods (get, set, add at index) that a queue should not have.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] LinkedList as a queue");
        Queue<String> linked = new LinkedList<>();
        linked.offer("a");
        linked.offer("b");
        System.out.println("    LinkedList queue poll = " + linked.poll() + ", remaining = " + linked);
        System.out.println("    runtime class         = " + linked.getClass().getSimpleName());
    }
}
