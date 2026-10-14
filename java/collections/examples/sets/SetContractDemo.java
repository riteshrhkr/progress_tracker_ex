package examples.sets;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Uniqueness is defined by equals (HashSet) or by the comparator (TreeSet).
 * Those two definitions can disagree. EnumSet is a bit vector, not a hash table.
 *
 * The equals-without-hashCode failure lives in the OOP notes:
 * java/oops/examples/contracts/EqualsHashCodeDemo.java
 *
 * Run: java -cp out examples.sets.SetContractDemo
 */
public class SetContractDemo {

    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    /**
     * Two users with the same email are equal. Used by HashSet to drop
     * the second add, and by TreeSet only if the comparator agrees.
     */
    static final class User {
        final String email;
        final int age;

        User(String email, int age) {
            this.email = email;
            this.age = age;
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
            return email + "/" + age;
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. HashSet.add returns false when the element is already present
        //    under equals. Size stays 1. The stored object is the first one.
        // ------------------------------------------------------------------
        System.out.println("[1] HashSet uniqueness is equals");
        Set<User> hash = new HashSet<>();
        User first = new User("a@x.com", 20);
        User second = new User("a@x.com", 40);     // equal to first, different age
        System.out.println("    add first  (20) -> " + hash.add(first));
        System.out.println("    add second (40) -> " + hash.add(second) + "   false because equals");
        System.out.println("    set = " + hash + ", size = " + hash.size());

        // ------------------------------------------------------------------
        // 2. LinkedHashSet is a HashSet that also remembers insertion order.
        //    HashSet does not promise an order, even when it looks stable.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] LinkedHashSet keeps insertion order");
        Set<String> linked = new LinkedHashSet<>();
        linked.add("c");
        linked.add("a");
        linked.add("b");
        linked.add("a");                            // duplicate, ignored
        System.out.println("    LinkedHashSet iterated = " + linked);

        // ------------------------------------------------------------------
        // 3. TreeSet decides uniqueness with the comparator, not equals.
        //    Sorted by email, the two Users collide and size is 1.
        //    Sorted by age, they are different and size is 2 — even though
        //    equals still says they are the same user.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] TreeSet uniqueness is the comparator");
        Set<User> byEmail = new TreeSet<>(Comparator.comparing(u -> u.email));
        byEmail.add(first);
        byEmail.add(second);
        System.out.println("    TreeSet by email size = " + byEmail.size() + "  " + byEmail);

        Set<User> byAge = new TreeSet<>(Comparator.comparingInt(u -> u.age));
        byAge.add(first);
        byAge.add(second);
        System.out.println("    TreeSet by age   size = " + byAge.size() + "  " + byAge);
        System.out.println("    equals(first, second) = " + first.equals(second)
                + "   comparator-by-age disagrees with equals");

        // ------------------------------------------------------------------
        // 4. EnumSet is a bit vector over the enum's constants. Add is a
        //    bit flip. Iteration is the enum's declaration order.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] EnumSet");
        EnumSet<Day> weekend = EnumSet.of(Day.SUN, Day.SAT);
        weekend.add(Day.SAT);                       // already present
        System.out.println("    EnumSet.of(SUN, SAT) iterated = " + weekend);
        System.out.println("    contains SAT = " + weekend.contains(Day.SAT));
        System.out.println("    runtime class = " + weekend.getClass().getSimpleName());
    }
}
