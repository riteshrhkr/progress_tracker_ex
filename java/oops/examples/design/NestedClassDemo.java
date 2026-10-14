package examples.design;

import java.util.function.Supplier;

/**
 * Static nested, inner, local and anonymous classes, plus the one difference
 * that bites in production: an inner class instance keeps its outer instance alive.
 *
 * Run: java -cp out examples.design.NestedClassDemo
 */
public class NestedClassDemo {

    static class Outer {
        private final String label;

        Outer(String label) {
            this.label = label;
        }

        /**
         * Static nested: effectively a top-level class that lives inside another
         * for naming. It holds no reference to any Outer instance.
         */
        static class StaticNested {
            String describe() {
                return "StaticNested has no outer instance";
            }
        }

        /**
         * Inner (non-static): every instance silently holds a reference to an
         * Outer. That hidden field is why a cached inner object can keep a whole
         * outer object from being garbage collected.
         */
        class Inner {
            String describe() {
                return "Inner can read outer state: label=" + label;
            }

            /** Makes the normally invisible reference visible. */
            Outer outerInstance() {
                return Outer.this;
            }
        }

        /** Local class: declared inside a method, can capture effectively final locals. */
        Supplier<String> localClassCounter() {
            int start = 100;      // effectively final: assigned once, never reassigned

            class Counter implements Supplier<String> {
                // The captured value was copied at creation time, which is why
                // 'start' has to be effectively final: two copies must not drift apart.
                private int n = start;

                public String get() {
                    return "local class counter -> " + (++n);
                }
            }
            return new Counter();
        }

        /** Anonymous class: a new object, so 'this' means that object. */
        Supplier<String> anonymousThis() {
            return new Supplier<>() {
                public String get() {
                    return "anonymous class this = " + this.getClass().getSimpleName()
                            + " (empty name means anonymous), outer via Outer.this = " + Outer.this.label;
                }
            };
        }

        /** Lambda: no new scope for 'this', so it means the enclosing instance. */
        Supplier<String> lambdaThis() {
            return () -> "lambda this = " + this.getClass().getSimpleName() + ", label=" + this.label;
        }
    }

    public static void main(String[] args) {
        Outer outer = new Outer("outer-1");

        // ------------------------------------------------------------------
        // 1. A static nested class is constructed like any other class.
        // ------------------------------------------------------------------
        System.out.println("[1] Static nested needs no outer instance");
        System.out.println("    " + new Outer.StaticNested().describe());

        // ------------------------------------------------------------------
        // 2. An inner class needs an outer instance to be created at all,
        //    and then holds onto it for its whole life.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Inner needs one, and keeps it");
        Outer.Inner inner = outer.new Inner();      // note the unusual 'outer.new' syntax
        System.out.println("    " + inner.describe());
        System.out.println("    inner.outerInstance() == outer : " + (inner.outerInstance() == outer));

        // ------------------------------------------------------------------
        // 3. The local class kept the captured value and its own mutable state.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Local class capturing a local variable");
        Supplier<String> counter = outer.localClassCounter();
        System.out.println("    " + counter.get());
        System.out.println("    " + counter.get());

        // ------------------------------------------------------------------
        // 4. The difference that catches people out when converting an
        //    anonymous listener into a lambda.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] 'this' inside an anonymous class vs inside a lambda");
        System.out.println("    " + outer.anonymousThis().get());
        System.out.println("    " + outer.lambdaThis().get());

        // ------------------------------------------------------------------
        // 5. Each nested class becomes its own class file, named with '$'.
        //    These are the names you meet in stack traces.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] Generated class names show the relationship");
        System.out.println("    static nested : " + Outer.StaticNested.class.getName());
        System.out.println("    inner         : " + Outer.Inner.class.getName());
        System.out.println("    local         : " + counter.getClass().getName());
        System.out.println("    anonymous     : " + outer.anonymousThis().getClass().getName());

        // A lambda has no class file at all: the JVM spins its class at first use
        // through invokedynamic, so the name carries a per-run address. Trim it
        // off to keep this output stable.
        String lambdaName = outer.lambdaThis().getClass().getName();
        System.out.println("    lambda        : " + lambdaName.substring(0, lambdaName.indexOf('/')) + "/<varies per run>");
    }
}
