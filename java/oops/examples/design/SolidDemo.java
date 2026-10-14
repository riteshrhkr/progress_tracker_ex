package examples.design;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Four of the five SOLID principles, each shown as a small failure and its fix.
 * (Single responsibility is a structural point, so it is described in the
 * document rather than demonstrated with output.)
 *
 * Run: java -cp out examples.design.SolidDemo
 */
public class SolidDemo {

    // ================= Open/closed =================
    // Adding a shape must not mean editing the code that measures shapes.

    /** Closed for extension: every new shape forces an edit to this method. */
    static double areaByType(Object shape) {
        if (shape instanceof double[] circle) {
            return Math.PI * circle[0] * circle[0];
        }
        if (shape instanceof int[] rect) {
            return rect[0] * (double) rect[1];
        }
        throw new IllegalArgumentException("unknown shape: " + shape.getClass());
    }

    interface Shape {
        double area();
    }

    record Circle(double radius) implements Shape {
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    record Rect(double w, double h) implements Shape {
        public double area() {
            return w * h;
        }
    }

    /** Open for extension: a new shape is a new class, and this method never changes again. */
    static double totalArea(List<Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }

    // ================= Liskov substitution =================
    // A subtype must keep the promises its supertype made.

    static class Rectangle {
        protected int width;
        protected int height;

        /** The promise: setting the width leaves the height alone. */
        void setWidth(int width) {
            this.width = width;
        }

        void setHeight(int height) {
            this.height = height;
        }

        int area() {
            return width * height;
        }
    }

    /**
     * "A square is a rectangle" is true in geometry and false for mutable objects,
     * because a square cannot keep the promise above. Note the real culprit is
     * mutability: immutable Rectangle and Square value types substitute fine.
     */
    static class Square extends Rectangle {
        @Override
        void setWidth(int width) {
            this.width = width;
            this.height = width;      // breaks Rectangle's promise
        }

        @Override
        void setHeight(int height) {
            this.width = height;
            this.height = height;
        }
    }

    /** Written against Rectangle, with a completely reasonable expectation. */
    static String resizeAndCheck(Rectangle r) {
        r.setWidth(5);
        r.setHeight(4);
        return r.getClass().getSimpleName() + ": expected area 20, got " + r.area();
    }

    // ================= Interface segregation =================
    // Do not force a class to implement methods it cannot support.

    interface Store {
        String read(String key);

        void write(String key, String value);
    }

    /** A read-only store has to sabotage half of the interface to satisfy it. */
    static class ReadOnlyStore implements Store {
        private final Map<String, String> data = Map.of("a", "1");

        public String read(String key) {
            return data.get(key);
        }

        public void write(String key, String value) {
            throw new UnsupportedOperationException("read-only");   // the smell
        }
    }

    interface ReadableStore {
        String read(String key);
    }

    interface WritableStore extends ReadableStore {
        void write(String key, String value);
    }

    /** Now the failure is at compile time: there is simply no write() to call. */
    static class SplitReadOnlyStore implements ReadableStore {
        private final Map<String, String> data = Map.of("a", "1");

        public String read(String key) {
            return data.get(key);
        }
    }

    // ================= Dependency inversion =================
    // Depend on an interface you own, not on a concrete thing you cannot replace.

    interface Clock {
        long millis();
    }

    interface AuditLog {
        void record(String line);
    }

    static class OrderService {
        private final Clock clock;      // not System.currentTimeMillis()
        private final AuditLog log;     // not a concrete logger

        /** Constructor injection also makes the full dependency list visible. */
        OrderService(Clock clock, AuditLog log) {
            this.clock = clock;
            this.log = log;
        }

        void place(String order) {
            log.record("t=" + clock.millis() + " placed " + order);
        }
    }

    /** A test double, with no mocking framework needed. */
    static class RecordingLog implements AuditLog {
        final List<String> lines = new ArrayList<>();

        public void record(String line) {
            lines.add(line);
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // Open/closed: the type switch has to be edited for every new shape,
        // the polymorphic version never does.
        // ------------------------------------------------------------------
        System.out.println("[1] Open/closed");
        System.out.printf("    type switch, circle r=1 : %.4f%n", areaByType(new double[]{1}));
        try {
            areaByType("triangle");
        } catch (IllegalArgumentException e) {
            System.out.println("    a new shape breaks it: " + e.getMessage());
        }
        System.out.printf("    polymorphic total       : %.4f%n", totalArea(List.of(new Circle(1), new Rect(2, 3))));

        // ------------------------------------------------------------------
        // Liskov: identical calling code, two different answers, and the one
        // that is wrong compiles perfectly.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Liskov substitution");
        System.out.println("    " + resizeAndCheck(new Rectangle()));
        System.out.println("    " + resizeAndCheck(new Square()));

        // ------------------------------------------------------------------
        // Interface segregation: splitting moves the error from runtime to
        // compile time, which is always the better place for it.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Interface segregation");
        Store fat = new ReadOnlyStore();
        System.out.println("    read(\"a\") = " + fat.read("a"));
        try {
            fat.write("b", "2");
        } catch (UnsupportedOperationException e) {
            System.out.println("    write on a read-only store throws at runtime: " + e.getMessage());
        }
        ReadableStore narrow = new SplitReadOnlyStore();
        System.out.println("    split interfaces: the compiler no longer offers write(), read(\"a\") = " + narrow.read("a"));

        // ------------------------------------------------------------------
        // Dependency inversion: time is pinned to a fixed value and the log is
        // captured, so this assertion is possible at all.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Dependency inversion");
        RecordingLog log = new RecordingLog();
        OrderService service = new OrderService(() -> 1_700_000_000_000L, log);
        service.place("order-1");
        System.out.println("    captured log line: " + log.lines);
    }
}
