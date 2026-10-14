package examples.pillars;

/**
 * Methods are chosen by the object. Fields and static methods are chosen by
 * the declared type of the variable. Mixing those two rules up is the most
 * common inheritance surprise in Java.
 *
 * Run: java -cp out examples.pillars.InheritanceDemo
 */
public class InheritanceDemo {

    static class Animal {
        // Dog declares a field with this same name. Both exist in a Dog object.
        String name = "animal";

        static String kind() {
            return "Animal.kind()";
        }

        String speak() {
            return "...";
        }

        /**
         * One line of code in the parent, reading a field and calling a method.
         * The two resolve by different rules, which is what section 3 shows.
         */
        void intro() {
            System.out.println("    name field says '" + name + "', speak() says '" + speak() + "'");
        }
    }

    static class Dog extends Animal {
        /** Hides Animal.name rather than replacing it. Never do this in real code. */
        String name = "dog";

        /** Hides Animal.kind(). Static methods cannot be overridden. */
        static String kind() {
            return "Dog.kind()";
        }

        @Override
        String speak() {
            return "woof";
        }

        /** super.name reaches the field this class hid. */
        String hiddenParentName() {
            return super.name;
        }
    }

    /** A subclass object contains its superclass part, so that part is built first. */
    static class Vehicle {
        private final int wheels;

        Vehicle(int wheels) {
            this.wheels = wheels;
            System.out.println("    Vehicle(" + wheels + ")");
        }

        int wheels() {
            return wheels;
        }
    }

    static class Car extends Vehicle {
        private final String model;

        Car(String model) {
            super(4);            // must run before anything in this constructor body
            this.model = model;
            System.out.println("    Car(" + model + ")");
        }

        @Override
        public String toString() {
            return model + " with " + wheels() + " wheels";
        }
    }

    /**
     * Calls a static method through an instance reference on purpose, to prove
     * the compiler resolves it from the declared type and never dispatches.
     * javac warns about this style, which is why the warning is suppressed here.
     */
    @SuppressWarnings("static")
    static void staticsAreNotDispatched(Animal declaredAsAnimal) {
        System.out.println("    variable is declared Animal, holds a Dog, .kind() gives " + declaredAsAnimal.kind());
    }

    public static void main(String[] args) {
        Dog dog = new Dog();
        Animal asAnimal = dog;      // ONE object, referred to through two different types

        // ------------------------------------------------------------------
        // 1. Field access uses the variable's type. Method calls use the object.
        // ------------------------------------------------------------------
        System.out.println("[1] Same object, two variable types");
        System.out.println("    asAnimal.name  = " + asAnimal.name + "   (field, picked from the variable type)");
        System.out.println("    dog.name       = " + dog.name + "      (field, picked from the variable type)");
        System.out.println("    asAnimal.speak() = " + asAnimal.speak() + "   (method, picked from the object)");

        // ------------------------------------------------------------------
        // 2. Both 'name' fields are present in the same object at the same time.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Both fields exist at once, the subclass one hides the other");
        System.out.println("    dog.hiddenParentName() = " + dog.hiddenParentName());
        System.out.println("    ((Animal) dog).name    = " + ((Animal) dog).name);

        // ------------------------------------------------------------------
        // 3. The clearest case: inherited code reading the parent field but
        //    calling the child method.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] A base-class method sees the base field but the overridden method");
        asAnimal.intro();

        // ------------------------------------------------------------------
        // 4. Statics belong to the class, so there is no runtime lookup at all.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Static methods are hidden, not overridden");
        System.out.println("    Animal.kind() = " + Animal.kind());
        System.out.println("    Dog.kind()    = " + Dog.kind());
        staticsAreNotDispatched(dog);

        // ------------------------------------------------------------------
        // 5. Constructors run parent first, all the way down the chain.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] Constructors run from the top of the chain down");
        Car car = new Car("hatchback");
        System.out.println("    " + car);
    }
}
