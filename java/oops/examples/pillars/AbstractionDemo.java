package examples.pillars;

/**
 * Interfaces, abstract classes, default methods and the diamond conflict,
 * plus a sealed hierarchy with an exhaustive switch (Java 17 / Java 21).
 *
 * Run: java -cp out examples.pillars.AbstractionDemo
 */
public class AbstractionDemo {

    // ---------- interface: a capability, with no instance state ----------

    interface Greeter {
        /** The single abstract method, which is what lets a lambda implement this interface. */
        String name();

        /** A default method: an interface method with a body, added in Java 8. */
        default String greet() {
            return prefix() + " " + name();
        }

        /** A private interface method, added in Java 9. A shared helper, not part of the API. */
        private String prefix() {
            return "hello,";
        }

        static Greeter of(String name) {
            return () -> name;          // lambda supplies name(); greet() comes for free
        }
    }

    // ---------- the diamond: two unrelated interfaces, same default signature ----------

    interface Walker {
        default String move() {
            return "walking";
        }
    }

    interface Swimmer {
        default String move() {
            return "swimming";
        }
    }

    /**
     * Java refuses to guess. Without this override the class does not compile.
     * X.super.method() is the syntax for "the default from that specific interface".
     */
    static class Duck implements Walker, Swimmer {
        @Override
        public String move() {
            return Walker.super.move() + " and " + Swimmer.super.move();
        }
    }

    // ---------- abstract class: shared state, plus a hole for subclasses ----------

    abstract static class Report {
        private final String title;     // state, which an interface could not hold

        protected Report(String title) {
            this.title = title;
        }

        /** Template method: final on purpose, so subclasses fill the hole rather than redraw the outline. */
        final String render() {
            return "    [" + title + "]\n" + body();
        }

        /** The one open step. */
        protected abstract String body();
    }

    static class SalesReport extends Report {
        SalesReport() {
            super("Sales");
        }

        @Override
        protected String body() {
            return "        units sold: 42";
        }
    }

    // ---------- sealed hierarchy: a deliberately closed set of subtypes ----------

    sealed interface Payment permits Card, Cash, Transfer {
    }

    record Card(String last4) implements Payment {
    }

    record Cash() implements Payment {
    }

    record Transfer(String iban) implements Payment {
    }

    /**
     * No default branch. Because the subtype list is closed, the compiler can
     * prove this switch is exhaustive. Delete a branch and compilation fails
     * with "the switch expression does not cover all possible input values".
     */
    static String describe(Payment payment) {
        return switch (payment) {
            case Card c -> "card ending " + c.last4();
            case Cash ignored -> "cash";
            case Transfer t -> "transfer to " + t.iban();
        };
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. One abstract method means a lambda is enough to implement it.
        // ------------------------------------------------------------------
        System.out.println("[1] Interface with a default method and a lambda implementation");
        Greeter greeter = Greeter.of("Ritesh");
        System.out.println("    " + greeter.greet());

        // ------------------------------------------------------------------
        // 2. Conflicting defaults must be resolved explicitly by the class.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Two inherited defaults, resolved explicitly");
        System.out.println("    duck.move() = " + new Duck().move());

        // ------------------------------------------------------------------
        // 3. The skeleton is fixed by the parent, one step is left to the child.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Abstract class supplying the skeleton");
        System.out.println(new SalesReport().render());

        // ------------------------------------------------------------------
        // 4. Adding a fourth payment type would turn every incomplete switch
        //    into a compile error instead of a runtime surprise.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Sealed interface plus exhaustive switch");
        for (Payment payment : new Payment[]{new Card("4242"), new Cash(), new Transfer("DE89...")}) {
            System.out.println("    " + describe(payment));
        }
    }
}
