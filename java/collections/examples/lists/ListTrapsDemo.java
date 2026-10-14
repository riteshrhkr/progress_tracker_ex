package examples.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

/**
 * Three traps that look like ordinary List calls: remove(int) versus
 * remove(Object), Arrays.asList as a fixed-size view, and subList as a view
 * of the original. LinkedList implementing both List and Deque is also here.
 *
 * Run: java -cp out examples.lists.ListTrapsDemo
 */
public class ListTrapsDemo {

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. List has two remove methods. remove(int) deletes by index.
        //    remove(Object) deletes the first equal element. Integer boxing
        //    makes list.remove(1) pick the index overload, not the value 1.
        // ------------------------------------------------------------------
        System.out.println("[1] remove(int) versus remove(Object)");
        List<Integer> numbers = new ArrayList<>(List.of(10, 20, 1, 30));
        System.out.println("    start                  = " + numbers);
        numbers.remove(1);
        System.out.println("    after remove(1)        = " + numbers + "   index 1 was 20, not the value 1");
        numbers.remove(Integer.valueOf(1));
        System.out.println("    after remove(Integer 1)= " + numbers);

        // ------------------------------------------------------------------
        // 2. Arrays.asList wraps the array. set writes through. add/remove
        //    throw because the array cannot change length.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Arrays.asList is a fixed-size view");
        String[] raw = {"a", "b", "c"};
        List<String> view = Arrays.asList(raw);
        view.set(1, "B");
        System.out.println("    after view.set(1, \"B\"), array = " + Arrays.toString(raw));
        try {
            view.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("    view.add(\"d\") -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 3. subList is a window onto the same storage. Mutating the window
        //    mutates the original. A structural change on the original
        //    invalidates the window.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] subList is a view");
        List<String> letters = new ArrayList<>(List.of("a", "b", "c", "d", "e"));
        List<String> middle = letters.subList(1, 4);     // b, c, d  — fromIndex inclusive, toIndex exclusive
        System.out.println("    letters = " + letters);
        System.out.println("    middle  = " + middle);
        middle.set(0, "B");
        System.out.println("    after middle.set(0, \"B\"), letters = " + letters);
        middle.clear();
        System.out.println("    after middle.clear(),      letters = " + letters);

        letters = new ArrayList<>(List.of("a", "b", "c", "d"));
        middle = letters.subList(1, 3);
        letters.add("e");                               // structural change on the parent
        try {
            System.out.println(middle.get(0));
        } catch (ConcurrentModificationException e) {
            System.out.println("    parent add then subList.get -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 4. LinkedList is both a List and a Deque. That is why add/remove
        //    at the ends are cheap and get(i) is not. Prefer ArrayDeque
        //    when you only need the ends, ArrayList when you need indexes.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] LinkedList is List and Deque");
        LinkedList<String> both = new LinkedList<>();
        both.addLast("a");
        both.addLast("b");
        both.addFirst("z");
        System.out.println("    after addFirst(z), addLast(a), addLast(b) = " + both);
        System.out.println("    get(2) = " + both.get(2) + "   (walked from the head)");
        System.out.println("    instanceof List  = " + (both instanceof List));
        System.out.println("    instanceof java.util.Deque = " + (both instanceof java.util.Deque));
    }
}
