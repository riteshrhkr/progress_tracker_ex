package examples.contracts;

import java.util.ArrayList;
import java.util.List;

/**
 * "final field" is not "immutable object". A class is only immutable if
 * nothing that escapes it can be used to change what is inside it.
 *
 * Run: java -cp out examples.contracts.ImmutabilityDemo
 */
public class ImmutabilityDemo {

    /** Looks immutable: final class, private final fields, no setters. Is not immutable. */
    static final class LeakyTrip {
        private final String name;
        private final List<String> stops;

        LeakyTrip(String name, List<String> stops) {
            this.name = name;
            this.stops = stops;      // leak IN: keeps the caller's list, which the caller still holds
        }

        List<String> stops() {
            return stops;            // leak OUT: hands the same list to anyone who asks
        }

        @Override
        public String toString() {
            return name + " " + stops;
        }
    }

    /** The same class with both boundaries closed. */
    static final class SafeTrip {
        private final String name;
        private final List<String> stops;

        SafeTrip(String name, List<String> stops) {
            this.name = name;
            this.stops = List.copyOf(stops);   // copy in: an unmodifiable snapshot
        }

        List<String> stops() {
            return stops;                       // already unmodifiable, so nothing to copy out
        }

        /** Change produces a new object instead of editing this one. */
        SafeTrip withExtraStop(String stop) {
            List<String> next = new ArrayList<>(stops);
            next.add(stop);
            return new SafeTrip(name, next);
        }

        @Override
        public String toString() {
            return name + " " + stops;
        }
    }

    /** A record is shallowly immutable: the reference is fixed, the object it points at is not. */
    record LeakyRoute(String name, List<String> stops) {
    }

    /** A compact constructor is where a record deepens its immutability. */
    record SafeRoute(String name, List<String> stops) {
        SafeRoute {
            // No parameter list and no explicit field assignment: you reassign the
            // parameter, and the compiler assigns the field from it afterwards.
            stops = List.copyOf(stops);
        }
    }

    public static void main(String[] args) {
        List<String> caller = new ArrayList<>(List.of("Pune", "Mumbai"));

        // ------------------------------------------------------------------
        // 1. The constructor stored the caller's list, and the caller kept it.
        // ------------------------------------------------------------------
        System.out.println("[1] The caller still holds the list you stored");
        LeakyTrip leaky = new LeakyTrip("weekend", caller);
        System.out.println("    before: " + leaky);
        caller.add("Goa");                       // changing the "immutable" object from outside
        System.out.println("    after caller.add(\"Goa\"): " + leaky);

        // ------------------------------------------------------------------
        // 2. Even a caller who never saw the original list can reach in.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] The getter hands out the same list");
        leaky.stops().clear();
        System.out.println("    after leaky.stops().clear(): " + leaky);

        // ------------------------------------------------------------------
        // 3. Copying at the boundary closes both routes at once.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Copying on the way in blocks both routes");
        List<String> caller2 = new ArrayList<>(List.of("Pune", "Mumbai"));
        SafeTrip safe = new SafeTrip("weekend", caller2);

        caller2.add("Goa");                      // no effect: the copy was taken at construction
        System.out.println("    after caller2.add(\"Goa\"): " + safe);

        try {
            safe.stops().add("Goa");             // no effect: the returned list is unmodifiable
        } catch (UnsupportedOperationException e) {
            System.out.println("    safe.stops().add(...) threw " + e.getClass().getSimpleName());
        }
        System.out.println("    withExtraStop returns a new object: " + safe.withExtraStop("Goa")
                + ", original still " + safe);

        // ------------------------------------------------------------------
        // 4. Both records are built from the same list. Only the one with a
        //    compact constructor survives the caller mutating it.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Records are shallowly immutable");
        List<String> caller3 = new ArrayList<>(List.of("Pune"));
        LeakyRoute leakyRoute = new LeakyRoute("r1", caller3);
        SafeRoute safeRoute = new SafeRoute("r1", caller3);
        System.out.println("    both built from the same list:");
        System.out.println("      " + leakyRoute);
        System.out.println("      " + safeRoute);

        caller3.add("Nashik");
        System.out.println("    after caller3.add(\"Nashik\"):");
        System.out.println("      " + leakyRoute + "   <- changed underneath the record");
        System.out.println("      " + safeRoute + "   <- unaffected");

        // ------------------------------------------------------------------
        // 5. final freezes the variable, not the object it points at.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] final stops reassignment, not mutation");
        final List<String> fixed = new ArrayList<>(List.of("a"));
        fixed.add("b");                          // legal: the list is not final, the variable is
        // fixed = new ArrayList<>();            // this would not compile
        System.out.println("    final List after add: " + fixed);
        System.out.println("    the variable cannot be pointed at a different list, the list itself is still editable");
    }
}
