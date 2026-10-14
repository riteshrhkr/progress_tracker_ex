package examples.maps;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * How HashMap looks up a key, and the four methods that replace the
 * contains-then-put pattern: getOrDefault, putIfAbsent, computeIfAbsent, merge.
 *
 * Mutable-key damage is in java/oops/examples/contracts/EqualsHashCodeDemo.java
 *
 * Run: java -cp out examples.maps.HashMapDemo
 */
public class HashMapDemo {

    static final class User {
        final String email;

        User(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof User other && email.equals(other.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }

        @Override
        public String toString() {
            return email;
        }
    }

    public static void main(String[] args) throws Exception {
        // ------------------------------------------------------------------
        // 1. get uses hashCode to pick a bucket, then equals to find the key.
        //    A new User with the same email hits the same entry. HashMap
        //    also allows one null key.
        // ------------------------------------------------------------------
        System.out.println("[1] get, put, null key");
        Map<User, Integer> logins = new HashMap<>();
        logins.put(new User("a@x.com"), 3);
        logins.put(null, 0);
        System.out.println("    get(new User(\"a@x.com\")) = " + logins.get(new User("a@x.com")));
        System.out.println("    get(null)                 = " + logins.get(null));
        System.out.println("    get(missing)              = " + logins.get(new User("nope@x.com")));

        // ------------------------------------------------------------------
        // 2. getOrDefault returns the fallback without inserting it.
        //    putIfAbsent inserts only when the key is missing (or mapped to null).
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] getOrDefault and putIfAbsent");
        Map<String, Integer> counts = new HashMap<>();
        System.out.println("    getOrDefault(\"a\", 0) = " + counts.getOrDefault("a", 0) + ", map = " + counts);
        counts.putIfAbsent("a", 1);
        counts.putIfAbsent("a", 99);                // ignored, a is already present
        System.out.println("    after putIfAbsent 1 then 99, map = " + counts);

        // ------------------------------------------------------------------
        // 3. computeIfAbsent runs the function only when the key is missing,
        //    then stores the result. Use it to build nested maps or lists.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] computeIfAbsent");
        Map<String, java.util.List<String>> byCity = new HashMap<>();
        byCity.computeIfAbsent("delhi", city -> new java.util.ArrayList<>()).add("rita");
        byCity.computeIfAbsent("delhi", city -> new java.util.ArrayList<>()).add("sam");
        byCity.computeIfAbsent("pune", city -> new java.util.ArrayList<>()).add("lee");
        System.out.println("    grouped = " + byCity);

        // ------------------------------------------------------------------
        // 4. If the mapping function mutates the same map, HashMap notices
        //    and throws. Do the extra put outside the lambda.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] computeIfAbsent must not mutate the map");
        Map<String, Integer> map = new HashMap<>();
        try {
            map.computeIfAbsent("a", k -> {
                map.put("b", 1);                    // structural change during compute
                return 1;
            });
        } catch (ConcurrentModificationException e) {
            System.out.println("    mutating inside computeIfAbsent -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 5. merge(key, value, remappingFn) inserts value if absent, otherwise
        //    combines old and new. Natural for "add this count to the total".
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] merge");
        Map<String, Integer> totals = new HashMap<>();
        totals.merge("err", 1, Integer::sum);
        totals.merge("err", 1, Integer::sum);
        totals.merge("ok", 1, Integer::sum);
        System.out.println("    after three merge calls = " + totals);

        // ------------------------------------------------------------------
        // 6. The table starts null. First put allocates 16 slots. At 13
        //    entries (load factor 0.75 × 16 = 12, then one more) it doubles.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[6] Table length");
        java.lang.reflect.Field table = HashMap.class.getDeclaredField("table");
        table.setAccessible(true);
        HashMap<Integer, Integer> sized = new HashMap<>();
        System.out.println("    new HashMap<>() table = " + table.get(sized));
        sized.put(0, 0);
        System.out.println("    after 1 put, length   = " + ((Object[]) table.get(sized)).length);
        int lastLen = ((Object[]) table.get(sized)).length;
        for (int i = 1; i <= 20; i++) {
            sized.put(i, i);
            int len = ((Object[]) table.get(sized)).length;
            if (len != lastLen) {
                System.out.println("    size " + sized.size() + " grew table " + lastLen + " -> " + len);
                lastLen = len;
            }
        }
    }
}
