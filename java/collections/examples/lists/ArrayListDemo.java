package examples.lists;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;

/**
 * ArrayList is a resizable array: capacity, growth, and O(1) index access.
 * LinkedList is a chain of nodes: get(i) walks i steps.
 *
 * Run: java -cp out examples.lists.ArrayListDemo
 */
public class ArrayListDemo {

    public static void main(String[] args) throws Exception {
        Field elementData = ArrayList.class.getDeclaredField("elementData");
        elementData.setAccessible(true);

        // ------------------------------------------------------------------
        // 1. A new ArrayList has no backing array slots yet. The first add
        //    allocates the default of 10. After that it grows by about 50%.
        // ------------------------------------------------------------------
        System.out.println("[1] Capacity growth");
        ArrayList<Integer> list = new ArrayList<>();
        System.out.println("    new ArrayList<>() capacity = " + capacity(elementData, list));
        list.add(0);
        System.out.println("    after first add            = " + capacity(elementData, list));
        int last = capacity(elementData, list);
        for (int i = 1; i <= 20; i++) {
            list.add(i);
            int now = capacity(elementData, list);
            if (now != last) {
                System.out.println("    size " + list.size() + " grew capacity " + last + " -> " + now);
                last = now;
            }
        }

        // ------------------------------------------------------------------
        // 2. ensureCapacity / the sized constructor skip those copies when
        //    you already know how many elements you will add.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Sized constructor");
        ArrayList<Integer> sized = new ArrayList<>(20);
        System.out.println("    new ArrayList<>(20) capacity = " + capacity(elementData, sized));

        // ------------------------------------------------------------------
        // 3. add(index, e) and remove(index) shift the tail. That is why
        //    inserting at 0 on a large ArrayList is expensive, even though
        //    get(i) is a single array access.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Index add and remove shift the tail");
        ArrayList<String> letters = new ArrayList<>(List.of("b", "c", "d"));
        letters.add(0, "a");
        System.out.println("    add(0, \"a\")  = " + letters);
        letters.remove(1);
        System.out.println("    remove(1)    = " + letters + "   (removed \"b\", c and d shifted left)");

        // ------------------------------------------------------------------
        // 4. RandomAccess is a marker: get(i) is cheap. ArrayList has it.
        //    LinkedList does not — get(i) walks from the nearer end.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] RandomAccess and get(i)");
        List<Integer> array = new ArrayList<>();
        List<Integer> linked = new LinkedList<>();
        for (int i = 0; i < 50_000; i++) {
            array.add(i);
            linked.add(i);
        }
        System.out.println("    ArrayList  instanceof RandomAccess = " + (array instanceof RandomAccess));
        System.out.println("    LinkedList instanceof RandomAccess = " + (linked instanceof RandomAccess));

        int mid = 25_000;
        int fromArray = 0;
        int fromLinked = 0;
        for (int w = 0; w < 50; w++) {              // warm the JIT; first-call timings are noise
            fromArray = array.get(mid);
            fromLinked = linked.get(mid);
        }
        long t0 = System.nanoTime();
        fromArray = array.get(mid);
        long arrayNs = System.nanoTime() - t0;

        t0 = System.nanoTime();
        fromLinked = linked.get(mid);
        long linkedNs = System.nanoTime() - t0;

        System.out.println("    get(" + mid + ") ArrayList  = " + fromArray + " in " + arrayNs + " ns");
        System.out.println("    get(" + mid + ") LinkedList = " + fromLinked + " in " + linkedNs + " ns");
        System.out.println("    LinkedList/ArrayList ratio  = " + (linkedNs / Math.max(arrayNs, 1)) + "x");
    }

    static int capacity(Field elementData, ArrayList<?> list) throws IllegalAccessException {
        return ((Object[]) elementData.get(list)).length;
    }
}
