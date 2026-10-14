package examples.design;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Two ways inheritance breaks, and the same two problems solved by holding
 * an object instead of extending its class.
 *
 * Run: java -cp out examples.design.CompositionDemo
 */
public class CompositionDemo {

    // ---------- problem 1: you inherit the whole API, not the part you wanted ----------

    /**
     * A stack has one rule: only the top element is reachable. Extending
     * ArrayList hands every caller the tools to break that rule.
     */
    static class InheritedStack<E> extends ArrayList<E> {
        void push(E item) {
            add(item);
        }

        E pop() {
            return remove(size() - 1);
        }
        // plus add(int, E), remove(int), set(int, E), clear(), sort(), ... all inherited
    }

    /** Same feature, and no way in except push and pop. */
    static final class ComposedStack<E> {
        private final List<E> items = new ArrayList<>();   // an implementation detail, swappable

        void push(E item) {
            items.add(item);
        }

        E pop() {
            if (items.isEmpty()) {
                throw new IllegalStateException("stack is empty");
            }
            return items.remove(items.size() - 1);
        }

        int size() {
            return items.size();
        }
    }

    // ---------- problem 2: you depend on how the superclass calls itself ----------

    /**
     * Looks correct and is not. AbstractCollection.addAll is implemented by
     * calling add in a loop, so every element is counted twice: once by the
     * addAll override, once by the add override the inherited addAll invokes.
     * One method of a class calling another overridable method of the same
     * class is called self-use, and it is almost never documented.
     */
    static class CountingHashSet<E> extends HashSet<E> {
        private int added;

        @Override
        public boolean add(E e) {
            added++;
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            added += c.size();
            return super.addAll(c);     // internally calls this.add() c.size() more times
        }

        int added() {
            return added;
        }
    }

    /** The wrapped set never calls back into this class, so there is nothing to double count. */
    static final class CountingSet<E> {
        private final Set<E> delegate = new HashSet<>();
        private int added;

        boolean add(E e) {
            added++;
            return delegate.add(e);
        }

        boolean addAll(Collection<? extends E> c) {
            boolean changed = false;
            for (E e : c) {
                changed |= add(e);      // our own add, deliberately and visibly
            }
            return changed;
        }

        int added() {
            return added;
        }

        int size() {
            return delegate.size();
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Nothing throws, which is the problem. The type says "stack" and
        //    behaves like a list.
        // ------------------------------------------------------------------
        System.out.println("[1] Inheriting ArrayList leaks operations a stack must not allow");
        InheritedStack<String> inherited = new InheritedStack<>();
        inherited.push("a");
        inherited.push("b");
        System.out.println("    after two pushes : " + inherited);

        inherited.add(0, "sneaked-in");     // inserting underneath the stack
        inherited.remove(1);                // deleting from the middle
        inherited.set(0, "rewritten");      // rewriting an element in place
        System.out.println("    after add(0,..), remove(1), set(0,..) : " + inherited);
        System.out.println("    pop() : " + inherited.pop()
                + "   the caller edited the stack without ever calling push or pop");

        // ------------------------------------------------------------------
        // 2. The composed version exposes exactly two operations.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] The composed stack has no way to be corrupted");
        ComposedStack<String> composed = new ComposedStack<>();
        composed.push("a");
        composed.push("b");
        System.out.println("    pop() : " + composed.pop() + ", size now " + composed.size());

        composed.pop();
        try {
            composed.pop();                 // it can also enforce its own emptiness rule
        } catch (IllegalStateException e) {
            System.out.println("    pop() on empty threw: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // 3. Three elements added, counter says six.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Counting by extending HashSet counts twice");
        CountingHashSet<String> countingBySubclass = new CountingHashSet<>();
        countingBySubclass.addAll(List.of("x", "y", "z"));
        System.out.println("    added 3 elements, counter says " + countingBySubclass.added());

        // ------------------------------------------------------------------
        // 4. Same feature, correct count, and immune to how HashSet is written.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Counting by wrapping counts once");
        CountingSet<String> countingByWrapping = new CountingSet<>();
        countingByWrapping.addAll(List.of("x", "y", "z"));
        System.out.println("    added 3 elements, counter says " + countingByWrapping.added()
                + ", set size " + countingByWrapping.size());
    }
}
