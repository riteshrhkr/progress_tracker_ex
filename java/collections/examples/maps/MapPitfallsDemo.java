package examples.maps;

import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * IdentityHashMap uses ==, TreeMap requires a comparator or Comparable,
 * HashMap treeifies colliding bins without needing Comparable, and
 * Map.copyOf still exposes mutable values.
 *
 * Mutable keys: java/oops/examples/contracts/EqualsHashCodeDemo.java section [2].
 *
 * Run: java -cp out examples.maps.MapPitfallsDemo
 */
public class MapPitfallsDemo {

    /**
     * Every instance lands in the same HashMap bucket. Not Comparable.
     * HashMap still accepts these: the tree uses identity as a tie-break.
     * TreeMap does not — it has no hash, only compareTo.
     */
    static final class Colliding {
        final int id;

        Colliding(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "C" + id;
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. IdentityHashMap uses == and System.identityHashCode, not equals.
        //    Two equal Strings that are not the same object are two keys.
        // ------------------------------------------------------------------
        System.out.println("[1] IdentityHashMap uses ==");
        String a = new String("x");
        String b = new String("x");
        Map<String, Integer> identity = new IdentityHashMap<>();
        identity.put(a, 1);
        identity.put(b, 2);
        System.out.println("    a.equals(b) = " + a.equals(b) + ", a == b = " + (a == b));
        System.out.println("    IdentityHashMap size = " + identity.size() + "  " + identity);
        Map<String, Integer> ordinary = new HashMap<>();
        ordinary.put(a, 1);
        ordinary.put(b, 2);
        System.out.println("    HashMap size         = " + ordinary.size() + "  " + ordinary);

        // ------------------------------------------------------------------
        // 2. TreeMap with a key that is not Comparable, and no comparator,
        //    throws when it first needs to compare. Mixing types does too.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] TreeMap keys must be comparable");
        Map<Object, String> tree = new TreeMap<>();
        tree.put(1, "one");
        try {
            tree.put("two", "two");                 // Integer.compareTo(String)
        } catch (ClassCastException e) {
            System.out.println("    put(1) then put(\"two\") -> " + e);
        }

        // ------------------------------------------------------------------
        // 3. HashMap can treeify a colliding bin without Comparable.
        //    It compares hashes first, then compareTo if the class implements
        //    Comparable, otherwise it falls back to identity. TreeMap has
        //    only compareTo, so the same key type is rejected.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] HashMap treeifies without Comparable; TreeMap does not");
        Map<Colliding, Integer> colliding = new HashMap<>(64);
        for (int i = 0; i < 16; i++) {
            colliding.put(new Colliding(i), i);
        }
        System.out.println("    HashMap 16 keys, same hashCode, not Comparable, size = " + colliding.size());
        Map<Colliding, Integer> treeOfColliding = new TreeMap<>(Comparator.comparingInt(c -> c.id));
        treeOfColliding.put(new Colliding(1), 1);
        treeOfColliding.put(new Colliding(2), 2);
        System.out.println("    TreeMap with an explicit comparator accepts them, size = " + treeOfColliding.size());
        Map<Colliding, Integer> natural = new TreeMap<>();
        try {
            // TreeMap checks that even the first key can compare with itself.
            natural.put(new Colliding(1), 1);
        } catch (ClassCastException e) {
            System.out.println("    TreeMap first put, no comparator -> " + e.getClass().getSimpleName());
            System.out.println("    message: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // 4. An unmodifiable wrapper / Map.copyOf still exposes mutable
        //    values. The map structure cannot change; the objects can.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Unmodifiable map, mutable values");
        java.util.List<String> team = new java.util.ArrayList<>();
        team.add("rita");
        Map<String, java.util.List<String>> inner = new HashMap<>();
        inner.put("eng", team);
        Map<String, java.util.List<String>> frozen = Map.copyOf(inner);
        team.add("sam");
        System.out.println("    Map.copyOf still sees later adds to the list: " + frozen);
        try {
            frozen.put("hr", java.util.List.of("lee"));
        } catch (UnsupportedOperationException e) {
            System.out.println("    frozen.put(...) -> " + e.getClass().getSimpleName());
        }
    }
}
