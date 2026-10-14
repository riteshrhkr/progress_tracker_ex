package examples.maps;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LinkedHashMap is a HashMap that also holds a doubly-linked list of entries.
 * That list is either insertion order (default) or access order (for LRU).
 *
 * Run: java -cp out examples.maps.LinkedHashMapDemo
 */
public class LinkedHashMapDemo {

    /** Access-order map that drops the eldest entry once size exceeds 3. */
    static final class LruCache<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;
        private final int max;

        LruCache(int max) {
            super(16, 0.75f, true);                 // true = access order
            this.max = max;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > max;
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Default LinkedHashMap iterates in insertion order. Re-putting
        //    an existing key updates the value and does not move the entry.
        // ------------------------------------------------------------------
        System.out.println("[1] Insertion order");
        Map<String, Integer> inserted = new LinkedHashMap<>();
        inserted.put("c", 1);
        inserted.put("a", 2);
        inserted.put("b", 3);
        inserted.put("a", 9);                       // value changes, position does not
        System.out.println("    iteration = " + inserted);

        // ------------------------------------------------------------------
        // 2. Access-order: get and put move the entry to the end. The
        //    iteration order is then "least recently used first".
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Access order");
        Map<String, Integer> access = new LinkedHashMap<>(16, 0.75f, true);
        access.put("c", 1);
        access.put("a", 2);
        access.put("b", 3);
        System.out.println("    after puts           = " + access);
        access.get("c");                            // c is now most recently used
        System.out.println("    after get(\"c\")       = " + access);

        // ------------------------------------------------------------------
        // 3. removeEldestEntry turns that into a bounded LRU cache.
        //    After the 4th distinct put, the least recently used key is dropped.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] LRU via removeEldestEntry");
        Map<String, Integer> lru = new LruCache<>(3);
        lru.put("a", 1);
        lru.put("b", 2);
        lru.put("c", 3);
        System.out.println("    after a,b,c          = " + lru);
        lru.get("a");                               // a is now newest; b is eldest
        lru.put("d", 4);                            // size would be 4, so b is dropped
        System.out.println("    get(a) then put(d)   = " + lru);
    }
}
