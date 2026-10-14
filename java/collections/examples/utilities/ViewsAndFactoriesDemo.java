package examples.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Views versus copies, factory methods, and SequencedCollection.
 * Arrays.asList and subList are also views — see ListTrapsDemo for those.
 *
 * Run: java -cp out examples.utilities.ViewsAndFactoriesDemo
 */
public class ViewsAndFactoriesDemo {

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Collections.unmodifiableList wraps the original. Later adds
        //    to the original show up in the wrapper. The wrapper itself
        //    rejects add/remove.
        // ------------------------------------------------------------------
        System.out.println("[1] Unmodifiable view still tracks the original");
        List<String> live = new ArrayList<>();
        live.add("a");
        List<String> view = Collections.unmodifiableList(live);
        live.add("b");
        System.out.println("    view after live.add(\"b\") = " + view);
        try {
            view.add("c");
        } catch (UnsupportedOperationException e) {
            System.out.println("    view.add(\"c\") -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 2. List.copyOf / List.of take a snapshot. Later adds to the
        //    original do not show up. Null is rejected.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] List.copyOf is a snapshot, List.of rejects null");
        List<String> snapshot = List.copyOf(live);
        live.add("c");
        System.out.println("    live after add(\"c\")      = " + live);
        System.out.println("    snapshot still             = " + snapshot);
        try {
            List.of("a", null);
        } catch (NullPointerException e) {
            System.out.println("    List.of(\"a\", null) -> " + e.getClass().getSimpleName());
        }
        try {
            Set.of("a", "a");
        } catch (IllegalArgumentException e) {
            System.out.println("    Set.of(\"a\", \"a\")   -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // 3. Three ways to get a List from values. asList is fixed-size
        //    (set works). List.of is unmodifiable (set throws). A real
        //    ArrayList is fully mutable.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] asList versus List.of versus new ArrayList");
        List<String> asList = Arrays.asList("a", "b");
        asList.set(0, "A");
        System.out.println("    Arrays.asList after set(0,\"A\") = " + asList);
        List<String> of = List.of("a", "b");
        try {
            of.set(0, "A");
        } catch (UnsupportedOperationException e) {
            System.out.println("    List.of set(0,\"A\")           -> " + e.getClass().getSimpleName());
        }
        List<String> mutable = new ArrayList<>(List.of("a", "b"));
        mutable.add("c");
        System.out.println("    new ArrayList<>(List.of) add   = " + mutable);

        // ------------------------------------------------------------------
        // 4. Java 21 SequencedCollection: getFirst, getLast, reversed.
        //    reversed is a view — mutating it mutates the original.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] SequencedCollection (Java 21)");
        List<String> seq = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("    getFirst = " + seq.getFirst() + ", getLast = " + seq.getLast());
        List<String> reversed = seq.reversed();
        System.out.println("    reversed view = " + reversed);
        reversed.set(0, "C");                       // index 0 of reversed is the last of seq
        System.out.println("    reversed.set(0,\"C\"), original = " + seq);

        // ------------------------------------------------------------------
        // 5. Map.of rejects null keys, null values, and duplicate keys.
        //    More than 10 pairs needs Map.ofEntries.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] Map.of and Map.ofEntries");
        Map<String, Integer> small = Map.of("a", 1, "b", 2);
        System.out.println("    Map.of = " + small);
        Map<String, Integer> entries = Map.ofEntries(
                Map.entry("a", 1),
                Map.entry("b", 2),
                Map.entry("c", 3)
        );
        System.out.println("    Map.ofEntries = " + entries);
        try {
            Map.of("a", 1, "a", 2);
        } catch (IllegalArgumentException e) {
            System.out.println("    Map.of duplicate key -> " + e.getClass().getSimpleName());
        }
    }
}
