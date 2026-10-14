package examples.basics;

/**
 * The exact order in which static blocks, instance initializers, field
 * initializers and constructors run, and why calling an overridable method
 * from a constructor is a bug waiting to happen.
 *
 * Run: java -cp out examples.basics.ConstructorOrderDemo
 */
public class ConstructorOrderDemo {

    static class Base {

        /** Runs only once, when the class is first used. Not once per object. */
        static {
            System.out.println("    [static]   Base static block");
        }

        /** Instance initializer: runs for every object, just before the constructor body. */
        {
            System.out.println("    [instance] Base instance initializer");
        }

        Base() {
            System.out.println("    [ctor]     Base constructor starts");

            // The bug. describe() is overridable, so the subclass version runs here,
            // and at this point the subclass has not initialized a single field yet.
            describe();

            System.out.println("    [ctor]     Base constructor ends");
        }

        void describe() {
            System.out.println("               Base.describe()");
        }
    }

    static class Derived extends Base {

        static {
            System.out.println("    [static]   Derived static block");
        }

        // Both are final and both are assigned in the constructor below,
        // yet describe() can still observe them as null and 0.
        private final String label;
        private final int size;

        {
            System.out.println("    [instance] Derived instance initializer");
        }

        Derived(String label, int size) {
            super();                 // implicit anyway; written out to make the ordering visible
            this.label = label;
            this.size = size;
            System.out.println("    [ctor]     Derived constructor ends");
        }

        @Override
        void describe() {
            System.out.println("               Derived.describe() label=" + label + " size=" + size);
        }
    }

    public static void main(String[] args) {
        // Watch the interleaving: the whole Base part is built before Derived
        // assigns anything, which is why describe() sees defaults.
        System.out.println("[1] First instance");
        Derived first = new Derived("alpha", 10);

        // The same call, once construction has finished, sees the real values.
        System.out.println();
        System.out.println("[2] Calling describe() after construction finished");
        first.describe();

        // Static blocks are absent this time: they ran once, on first use of the class.
        System.out.println();
        System.out.println("[3] Second instance, static blocks do not run again");
        new Derived("beta", 20);
    }
}
