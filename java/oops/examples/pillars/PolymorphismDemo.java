package examples.pillars;

import java.util.List;

/**
 * Overriding is decided while the program runs, using the real object.
 * Overloading is decided while the code compiles, using the declared types.
 *
 * Run: java -cp out examples.pillars.PolymorphismDemo
 */
public class PolymorphismDemo {

    interface Shape {
        double area();
    }

    record Circle(double radius) implements Shape {
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    record Square(double side) implements Shape {
        public double area() {
            return side * side;
        }
    }

    // ------------------------------------------------------------------
    // Overloading: three methods, one name. The compiler picks one and
    // bakes the choice into the class file. Nothing is decided at runtime.
    // ------------------------------------------------------------------

    static String pick(Object o) {
        return "pick(Object)";
    }

    static String pick(String s) {
        return "pick(String)";
    }

    static String pick(Integer i) {
        return "pick(Integer)";
    }

    // ------------------------------------------------------------------
    // Overload matching runs in three phases and stops at the first hit:
    // widening primitive conversion, then boxing, then varargs.
    // ------------------------------------------------------------------

    static String priority(long l) {
        return "priority(long)";        // phase 1: int widens to long
    }

    static String priority(Integer i) {
        return "priority(Integer)";     // phase 2: int boxes to Integer
    }

    static String priority(int... ints) {
        return "priority(int...)";      // phase 3: last resort
    }

    /** An override may return a subtype of what the parent declared: a covariant return. */
    static class Parent {
        Object create() {
            return "from Parent";
        }
    }

    static class Child extends Parent {
        @Override
        String create() {               // String is a subtype of Object, so this is legal
            return "from Child";
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. The loop names no concrete type. Adding a Triangle would need
        //    no change here at all. That is the whole payoff of overriding.
        // ------------------------------------------------------------------
        System.out.println("[1] Overriding, the object decides");
        List<Shape> shapes = List.of(new Circle(1), new Square(2));
        for (Shape shape : shapes) {
            System.out.printf("    %-20s area = %.4f%n", shape.getClass().getSimpleName(), shape.area());
        }

        // ------------------------------------------------------------------
        // 2. The same object, reached through two variable types, selects two
        //    different overloads. If you expected pick(String) both times, you
        //    were expecting overload resolution to happen at runtime.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Overloading, the declared type decides");
        String text = "hello";
        Object textAsObject = text;     // same object, wider declared type
        System.out.println("    pick(text)         -> " + pick(text));
        System.out.println("    pick(textAsObject) -> " + pick(textAsObject) + "   same object, different variable type");

        // ------------------------------------------------------------------
        // 3. An unqualified pick(null) does NOT compile: String and Integer are
        //    equally specific and neither is a subtype of the other. The cast
        //    tells the compiler which one you meant.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] A null argument goes to the most specific match");
        System.out.println("    pick((String) null) -> " + pick((String) null));

        // ------------------------------------------------------------------
        // 4. This fixed ordering is why adding an overload does not silently
        //    change which method existing code calls.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] int argument: widening wins over boxing, boxing wins over varargs");
        int n = 7;
        System.out.println("    priority(7) -> " + priority(n));

        // ------------------------------------------------------------------
        // 5. Through Parent the return type is the declared Object and you must
        //    cast. Through Child the compiler already knows it is a String.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] Covariant return");
        Parent parent = new Child();
        Object viaParentType = parent.create();
        System.out.println("    declared return type is Object, actual value: " + viaParentType);

        String viaChildType = new Child().create();     // no cast needed
        System.out.println("    through Child the return type is String: " + viaChildType.toUpperCase());
    }
}
