package examples.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Comparable is the type's own order. Comparator is an external order.
 * thenComparing breaks ties. nullsFirst says where null sits. List.sort
 * is stable: equal elements keep their original relative order.
 *
 * Run: java -cp out examples.utilities.ComparatorDemo
 */
public class ComparatorDemo {

    record Person(String name, int age) implements Comparable<Person> {
        @Override
        public int compareTo(Person o) {
            return name.compareTo(o.name);          // the type's natural order is by name
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Comparable: the objects know how to order themselves.
        //    Collections.sort / List.sort uses that if you pass no comparator.
        // ------------------------------------------------------------------
        System.out.println("[1] Comparable natural order");
        List<Person> people = new ArrayList<>(List.of(
                new Person("sam", 40),
                new Person("rita", 20),
                new Person("lee", 20)
        ));
        Collections.sort(people);
        System.out.println("    sort by Comparable (name) = " + people);

        // ------------------------------------------------------------------
        // 2. Comparator.comparing + thenComparing: age first, then name.
        //    The original list is unchanged until you sort it.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] thenComparing");
        people = new ArrayList<>(List.of(
                new Person("sam", 40),
                new Person("rita", 20),
                new Person("lee", 20)
        ));
        people.sort(Comparator.comparingInt(Person::age).thenComparing(Person::name));
        System.out.println("    sort by age then name     = " + people);

        // ------------------------------------------------------------------
        // 3. nullsFirst / nullsLast wrap another comparator. Without them,
        //    comparing a null element throws.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] nullsFirst");
        List<String> names = new ArrayList<>();
        names.add("rita");
        names.add(null);
        names.add("sam");
        names.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("    nullsFirst = " + names);
        List<String> withNull = new ArrayList<>();
        withNull.add("rita");
        withNull.add(null);
        try {
            withNull.sort(Comparator.naturalOrder());
        } catch (NullPointerException e) {
            System.out.println("    naturalOrder on [rita, null] -> " + e.getClass().getSimpleName());
        }

        // ------------------------------------------------------------------
        // 4. Stable sort: two equal keys keep the order they had. TimSort
        //    (what List.sort uses for objects) is stable.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Stable sort");
        record Row(String city, String name) {}
        List<Row> rows = new ArrayList<>(List.of(
                new Row("delhi", "rita"),
                new Row("pune", "sam"),
                new Row("delhi", "lee")
        ));
        rows.sort(Comparator.comparing(Row::city));
        System.out.println("    sort by city only = " + rows);
        System.out.println("    rita stayed before lee (both delhi) because the sort is stable");
    }
}
