package examples.basics;

/**
 * Class vs object, reference variables, identity vs equality,
 * default field values, and what "Java passes everything by value" means.
 *
 * Run: java -cp out examples.basics.ClassObjectDemo
 */
public class ClassObjectDemo {

    /**
     * No constructor and no initializers, so the JVM zeroes every field
     * when it allocates the object. Used by section 1.
     */
    static class Defaults {
        int i;
        long l;
        double d;
        boolean b;
        char c;
        String s;
        int[] arr;
    }

    /** A plain data holder. Deliberately does NOT override equals, so section 3 can show the default. */
    static class Cake {
        String flavour;
        int layers;
        boolean baked;

        Cake(String flavour, int layers) {
            this.flavour = flavour;
            this.layers = layers;
        }

        @Override
        public String toString() {
            return "Cake[" + flavour + ", " + layers + " layers, baked=" + baked + "]";
        }
    }

    static class Box {
        int value;
    }

    /** Follows the copied address to the shared object, so the caller sees the change. */
    static void mutate(Box box) {
        box.value = 42;
    }

    /** Overwrites only this method's own copy of the address. The caller's variable is untouched. */
    static void reassign(Box box) {
        box = new Box();
        box.value = 99;
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Fields are never "undefined". Every field starts at its zero value.
        // ------------------------------------------------------------------
        System.out.println("[1] Default field values");
        Defaults defaults = new Defaults();
        System.out.println("    int     = " + defaults.i);
        System.out.println("    long    = " + defaults.l);
        System.out.println("    double  = " + defaults.d);
        System.out.println("    boolean = " + defaults.b);
        // char defaults to '\u0000', which prints as an invisible character, so show its number
        System.out.println("    char    = (numeric value) " + (int) defaults.c);
        System.out.println("    String  = " + defaults.s);
        System.out.println("    int[]   = " + defaults.arr);

        // ------------------------------------------------------------------
        // 2. A variable holds an address, not an object. Two variables can hold
        //    the same address, so writing through one is visible through the other.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Two objects, three variables");
        Cake a = new Cake("vanilla", 2);
        Cake b = new Cake("vanilla", 2);   // same contents as 'a', but a separate object
        Cake c = a;                        // NOT a new object: a second address for the first one

        System.out.println("    a == b : " + (a == b) + "   two separate objects");
        System.out.println("    a == c : " + (a == c) + "    one object, two variables holding its address");

        c.baked = true;                    // written through 'c'
        System.out.println("    a after 'c.baked = true' : " + a);   // read through 'a'

        // ------------------------------------------------------------------
        // 3. Object.equals is defined as 'this == obj'. Until a class overrides
        //    it, equals and == mean exactly the same thing.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] equals() with no override is still identity");
        System.out.println("    a.equals(b)      : " + a.equals(b));                 // Cake does not override
        System.out.println("    \"x\".equals(\"x\")  : " + "x".equals("x") + "   String overrides equals");

        // ------------------------------------------------------------------
        // 4. Arguments are always copied. For a reference type, the copied
        //    value is the address, which is why mutate() works and reassign() does not.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] References are passed by value");
        Box box = new Box();

        mutate(box);
        System.out.println("    after mutate(box)   : " + box.value + "   the object was changed");

        reassign(box);
        System.out.println("    after reassign(box) : " + box.value + "   the caller's variable never moved");

        // ------------------------------------------------------------------
        // 5. null is a valid reference value meaning "points at no object".
        //    The message names the variable only when compiled with -g.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] null is a reference pointing at nothing");
        Cake missing = null;
        try {
            System.out.println(missing.flavour);
        } catch (NullPointerException e) {
            System.out.println("    NullPointerException: " + e.getMessage());
        }
    }
}
