package examples.overview;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * for-each is an iterator you cannot call remove on. iterator.remove is the
 * supported way. Changing the list's structure while an iterator is live
 * throws ConcurrentModificationException.
 *
 * Run: java -cp out examples.overview.IteratorDemo
 */
public class IteratorDemo {

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. The enhanced for-loop hides the iterator. You have no variable
        //    to call remove on. Removing through the list itself throws
        //    ConcurrentModificationException on the next next().
        // ------------------------------------------------------------------
        System.out.println("[1] for-each cannot remove safely");
        List<String> letters = new ArrayList<>(List.of("a", "b", "c", "d"));
        try {
            for (String s : letters) {
                if (s.equals("b")) {
                    letters.remove(s);          // structural change while the hidden iterator is live
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("    letters.remove inside for-each -> " + e.getClass().getSimpleName());
        }
        System.out.println("    list after the failed attempt = " + letters);

        // ------------------------------------------------------------------
        // 2. Iterator.remove is the method that is allowed to change the
        //    collection while you walk it. It updates the iterator's idea
        //    of the structure, so the next call to next() is consistent.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] iterator.remove is the supported path");
        letters = new ArrayList<>(List.of("a", "b", "c", "d"));
        Iterator<String> it = letters.iterator();
        while (it.hasNext()) {
            if (it.next().equals("b")) {
                it.remove();
            }
        }
        System.out.println("    after iterator.remove(\"b\") = " + letters);

        // ------------------------------------------------------------------
        // 3. removeIf does the same job without you writing the loop.
        //    It is still fail-fast if some other code mutates the list
        //    while it runs; it is not a concurrent collection.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] removeIf");
        letters = new ArrayList<>(List.of("a", "b", "c", "d"));
        letters.removeIf(s -> s.equals("c"));
        System.out.println("    after removeIf(c) = " + letters);

        // ------------------------------------------------------------------
        // 4. ListIterator walks both ways and can set the current element
        //    or add at the cursor. Plain Iterator cannot.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] ListIterator");
        letters = new ArrayList<>(List.of("a", "c"));
        ListIterator<String> li = letters.listIterator();
        li.next();                  // cursor is after "a"
        li.add("b");                // insert between a and c
        System.out.println("    after add(\"b\") at cursor = " + letters);
        li.previous();              // back onto "b"
        li.set("B");                // replace the last element returned
        System.out.println("    after set(\"B\")           = " + letters);
    }
}
