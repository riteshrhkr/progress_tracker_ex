package examples.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Collection hierarchy in code: List is a Collection, Map is not,
 * equals is type-sensitive, and toArray has a typed form you should use.
 *
 * Run: java -cp out examples.overview.HierarchyDemo
 */
public class HierarchyDemo {

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. A List is a Collection. A Map is not. You cannot pass a Map
        //    where a Collection is required — they do not share that type.
        // ------------------------------------------------------------------
        System.out.println("[1] Collection versus Map");
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        Collection<String> asCollection = list;     // legal: List extends Collection
        System.out.println("    List assigned to Collection: " + asCollection);

        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        // Collection<String> nope = map;           // does not compile — Map is not a Collection
        System.out.println("    Map is a Map, size = " + map.size() + ", keySet is a Collection: " + map.keySet());
        System.out.println("    map instanceof Collection = " + (map instanceof Collection));

        // ------------------------------------------------------------------
        // 2. Two lists with the same elements in the same order are equal,
        //    even if the implementations differ. A List is never equal to a Set.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Collection equals");
        List<String> arrayList = new ArrayList<>(List.of("a", "b"));
        List<String> another = new ArrayList<>(List.of("a", "b"));
        Set<String> set = new HashSet<>(List.of("a", "b"));
        System.out.println("    two ArrayLists with [a, b] equal? " + arrayList.equals(another));
        System.out.println("    List.equals(Set) with the same elements? " + arrayList.equals(set));
        System.out.println("    Set.equals(List) with the same elements? " + set.equals(arrayList));

        // ------------------------------------------------------------------
        // 3. toArray() returns Object[]. The typed overload fills a T[].
        //    Passing a zero-length array is the usual form; the JDK allocates
        //    a correctly sized array of the right runtime type.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] toArray");
        Object[] objects = list.toArray();
        String[] strings = list.toArray(new String[0]);
        System.out.println("    toArray() component type     = " + objects.getClass().getComponentType().getSimpleName());
        System.out.println("    toArray(new String[0]) type  = " + strings.getClass().getComponentType().getSimpleName());
        System.out.println("    contents                     = " + java.util.Arrays.toString(strings));

        // ------------------------------------------------------------------
        // 4. The declared type is the contract. The constructor is where you
        //    pick the implementation. Callers should not have to know ArrayList.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Program to the interface");
        Collection<String> names = newNames();
        names.add("rita");
        System.out.println("    runtime class = " + names.getClass().getSimpleName() + ", contents = " + names);
    }

    /** Return type is Collection, so the caller cannot depend on ArrayList methods. */
    static Collection<String> newNames() {
        return new ArrayList<>();
    }
}
