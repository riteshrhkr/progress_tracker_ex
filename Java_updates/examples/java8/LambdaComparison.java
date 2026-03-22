package examples.java8;

import java.util.*;
import java.util.function.*;
import java.io.*;

/**
 * Real-World Scenario: Refactoring Legacy Code to Use Lambdas
 *
 * This example demonstrates the transformation from verbose anonymous classes
 * to concise lambda expressions in realistic scenarios.
 *
 * Key Concepts Demonstrated:
 * 1. Before/After refactoring comparisons
 * 2. Lambda syntax variations
 * 3. Functional interfaces (built-in and custom)
 * 4. Strategy pattern with lambdas
 * 5. Event handling with lambdas
 * 6. Exception handling in lambdas
 * 7. Variable capture and effectively final
 * 8. Performance comparison
 */
public class LambdaComparison {

    public static void main(String[] args) {
        System.out.println("=== Lambda Expressions: Before & After ===\n");

        // Example 1: Sorting with Comparator
        example1_Sorting();

        // Example 2: Filtering and processing collections
        example2_FilteringCollections();

        // Example 3: Custom functional interfaces
        example3_CustomFunctionalInterfaces();

        // Example 4: Strategy pattern refactoring
        example4_StrategyPattern();

        // Example 5: Event handling / Callbacks
        example5_EventHandling();

        // Example 6: Variable capture (closure)
        example6_VariableCapture();

        // Example 7: Exception handling in lambdas
        example7_ExceptionHandling();

        // Example 8: Function composition
        example8_FunctionComposition();
    }

    /**
     * Example 1: Sorting - Before and After
     */
    private static void example1_Sorting() {
        System.out.println("--- Example 1: Sorting (Before & After) ---");

        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "David");

        // === BEFORE: Anonymous class ===
        List<String> namesCopy1 = new ArrayList<>(names);
        Collections.sort(namesCopy1, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareToIgnoreCase(b);
            }
        });
        System.out.println("Before (anonymous class): " + namesCopy1);

        // === AFTER: Lambda expression ===
        List<String> namesCopy2 = new ArrayList<>(names);
        Collections.sort(namesCopy2, (a, b) -> a.compareToIgnoreCase(b));
        System.out.println("After (lambda):           " + namesCopy2);

        // === EVEN BETTER: Method reference ===
        List<String> namesCopy3 = new ArrayList<>(names);
        namesCopy3.sort(String::compareToIgnoreCase);
        System.out.println("Best (method reference):  " + namesCopy3);

        // Complex sorting - multiple criteria
        List<Employee> employees = createSampleEmployees();

        // Before: Verbose anonymous class
        Collections.sort(employees, new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                int deptCompare = e1.getDepartment().compareTo(e2.getDepartment());
                if (deptCompare != 0) return deptCompare;
                return Double.compare(e2.getSalary(), e1.getSalary());  // Descending salary
            }
        });

        // After: Concise lambda with Comparator chaining
        employees.sort(
            Comparator.comparing(Employee::getDepartment)
                     .thenComparing(Comparator.comparingDouble(Employee::getSalary).reversed())
        );

        System.out.println("Sorted employees: " + employees.get(0).getName() + " (first)");
        System.out.println();
    }

    /**
     * Example 2: Filtering Collections - Before and After
     */
    private static void example2_FilteringCollections() {
        System.out.println("--- Example 2: Filtering Collections ---");

        List<Employee> employees = createSampleEmployees();

        // === BEFORE: Manual iteration and filtering ===
        System.out.println("BEFORE (imperative style):");
        List<String> highEarners = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp.getSalary() > 70000) {
                highEarners.add(emp.getName());
            }
        }
        System.out.println("High earners: " + highEarners);

        // === AFTER: Stream with lambda ===
        System.out.println("\nAFTER (declarative with lambda):");
        List<String> highEarnersLambda = employees.stream()
            .filter(emp -> emp.getSalary() > 70000)
            .map(Employee::getName)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        System.out.println("High earners: " + highEarnersLambda);

        // Even more concise
        List<String> highEarnersBest = employees.stream()
            .filter(emp -> emp.getSalary() > 70000)
            .map(Employee::getName)
            .toList();  // Java 16+ or use Collectors.toList()
        System.out.println("High earners (best): " + highEarnersBest);

        System.out.println();
    }

    /**
     * Example 3: Custom Functional Interfaces
     */
    private static void example3_CustomFunctionalInterfaces() {
        System.out.println("--- Example 3: Custom Functional Interfaces ---");

        // === BEFORE: Interface + Anonymous class ===
        System.out.println("BEFORE:");
        StringProcessor upperCase = new StringProcessor() {
            @Override
            public String process(String input) {
                return input.toUpperCase();
            }
        };
        System.out.println(upperCase.process("hello"));

        // === AFTER: Lambda ===
        System.out.println("\nAFTER:");
        StringProcessor lowerCase = input -> input.toLowerCase();
        System.out.println(lowerCase.process("WORLD"));

        // Using custom functional interface with validation
        Validator<String> emailValidator = email -> email.contains("@") && email.contains(".");
        System.out.println("\nEmail validation:");
        System.out.println("  test@example.com: " + emailValidator.validate("test@example.com"));
        System.out.println("  invalid-email: " + emailValidator.validate("invalid-email"));

        // BiFunction for more complex operations
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;

        System.out.println("\nCalculator:");
        System.out.println("  5 + 3 = " + add.calculate(5, 3));
        System.out.println("  5 * 3 = " + multiply.calculate(5, 3));

        System.out.println();
    }

    /**
     * Example 4: Strategy Pattern - Refactoring with Lambdas
     */
    private static void example4_StrategyPattern() {
        System.out.println("--- Example 4: Strategy Pattern with Lambdas ---");

        List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);

        // === BEFORE: Strategy pattern with classes ===
        System.out.println("BEFORE (traditional strategy classes):");

        DiscountStrategy noDiscount = new NoDiscountStrategy();
        DiscountStrategy tenPercent = new TenPercentDiscount();
        DiscountStrategy twentyPercent = new TwentyPercentDiscount();

        System.out.println("No discount on $100: $" + noDiscount.applyDiscount(100.0));
        System.out.println("10% discount on $100: $" + tenPercent.applyDiscount(100.0));

        // === AFTER: Strategy as lambda ===
        System.out.println("\nAFTER (lambda strategies):");

        DiscountStrategy noDiscountLambda = price -> price;
        DiscountStrategy tenPercentLambda = price -> price * 0.9;
        DiscountStrategy twentyPercentLambda = price -> price * 0.8;

        System.out.println("No discount on $100: $" + noDiscountLambda.applyDiscount(100.0));
        System.out.println("10% discount on $100: $" + tenPercentLambda.applyDiscount(100.0));

        // Dynamic strategy selection
        DiscountStrategy dynamicDiscount = getDynamicDiscount("GOLD");
        System.out.println("GOLD member discount on $100: $" + dynamicDiscount.applyDiscount(100.0));

        // Using with business logic
        PriceCalculator calculator = new PriceCalculator();
        System.out.println("\nBusiness logic:");
        System.out.println("Regular customer: $" +
            calculator.calculateFinalPrice(100.0, price -> price));
        System.out.println("Premium customer: $" +
            calculator.calculateFinalPrice(100.0, price -> price * 0.85));

        System.out.println();
    }

    /**
     * Example 5: Event Handling and Callbacks
     */
    private static void example5_EventHandling() {
        System.out.println("--- Example 5: Event Handling with Lambdas ---");

        // === BEFORE: Anonymous listener ===
        System.out.println("BEFORE (anonymous class):");
        Button button1 = new Button("Submit");
        button1.setOnClick(new ClickListener() {
            @Override
            public void onClick(String buttonName) {
                System.out.println("  Button clicked: " + buttonName);
            }
        });
        button1.click();

        // === AFTER: Lambda listener ===
        System.out.println("\nAFTER (lambda):");
        Button button2 = new Button("Save");
        button2.setOnClick(buttonName -> System.out.println("  Button clicked: " + buttonName));
        button2.click();

        // Multiple event handlers
        Button button3 = new Button("Delete");
        button3.setOnClick(name -> {
            System.out.println("  Confirming deletion...");
            System.out.println("  Deleting item from: " + name);
        });
        button3.click();

        // Callback pattern
        System.out.println("\nCallback pattern:");
        DataService service = new DataService();

        // Before: Anonymous class
        service.fetchDataOldStyle("user123", new DataCallback() {
            @Override
            public void onSuccess(String data) {
                System.out.println("  Success: " + data);
            }

            @Override
            public void onError(String error) {
                System.out.println("  Error: " + error);
            }
        });

        // After: Lambda (requires separate calls or wrapper)
        service.fetchData("user456",
            data -> System.out.println("  Success: " + data),
            error -> System.out.println("  Error: " + error)
        );

        System.out.println();
    }

    /**
     * Example 6: Variable Capture (Closure)
     */
    private static void example6_VariableCapture() {
        System.out.println("--- Example 6: Variable Capture ---");

        String prefix = "Hello, ";  // Effectively final
        int multiplier = 10;        // Effectively final

        // Lambda captures variables from enclosing scope
        Function<String, String> greeter = name -> prefix + name;
        Function<Integer, Integer> scaler = x -> x * multiplier;

        System.out.println(greeter.apply("World"));      // Hello, World
        System.out.println(scaler.apply(5));             // 50

        // prefix = "Hi, ";  // ❌ Would cause compilation error

        // Instance variable capture
        Counter counter = new Counter();
        Runnable incrementTask = () -> counter.increment();

        incrementTask.run();
        incrementTask.run();
        incrementTask.run();
        System.out.println("Counter value: " + counter.getValue());

        // Effectively final with collections
        List<String> captured = new ArrayList<>();
        captured.add("initial");

        Consumer<String> addToCaptured = item -> captured.add(item);  // OK - not reassigning
        addToCaptured.accept("added via lambda");

        System.out.println("Captured list: " + captured);

        System.out.println();
    }

    /**
     * Example 7: Exception Handling in Lambdas
     */
    private static void example7_ExceptionHandling() {
        System.out.println("--- Example 7: Exception Handling ---");

        List<String> files = Arrays.asList("file1.txt", "file2.txt", "file3.txt");

        // === PROBLEM: Checked exceptions in lambdas ===
        // This won't compile:
        // files.forEach(file -> readFile(file));  // readFile throws IOException

        // === SOLUTION 1: Wrap in try-catch (verbose) ===
        System.out.println("Solution 1: Inline try-catch");
        files.forEach(file -> {
            try {
                readFile(file);
                System.out.println("  Read: " + file);
            } catch (IOException e) {
                System.out.println("  Error reading: " + file);
            }
        });

        // === SOLUTION 2: Helper method (cleaner) ===
        System.out.println("\nSolution 2: Helper method");
        files.forEach(file -> readFileSafely(file));

        // === SOLUTION 3: Wrapper functional interface ===
        System.out.println("\nSolution 3: Wrapper functional interface");
        files.forEach(unchecked(file -> {
            try {
                readFile(file);
                System.out.println("  Processed: " + file);
            } catch (IOException e) {
                System.out.println("  Error processing: " + file);
            }
        }));

        System.out.println();
    }

    /**
     * Example 8: Function Composition
     */
    private static void example8_FunctionComposition() {
        System.out.println("--- Example 8: Function Composition ---");

        // Basic functions
        Function<Integer, Integer> multiplyByTwo = x -> x * 2;
        Function<Integer, Integer> addThree = x -> x + 3;

        // Compose: andThen - f.andThen(g) means g(f(x))
        Function<Integer, Integer> multiplyThenAdd = multiplyByTwo.andThen(addThree);
        System.out.println("(5 * 2) + 3 = " + multiplyThenAdd.apply(5));  // 13

        // Compose: compose - f.compose(g) means f(g(x))
        Function<Integer, Integer> addThenMultiply = multiplyByTwo.compose(addThree);
        System.out.println("(5 + 3) * 2 = " + addThenMultiply.apply(5));  // 16

        // Predicate composition
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> greaterThanTen = n -> n > 10;

        Predicate<Integer> evenAndGreaterThanTen = isEven.and(greaterThanTen);
        Predicate<Integer> evenOrGreaterThanTen = isEven.or(greaterThanTen);
        Predicate<Integer> notEven = isEven.negate();

        System.out.println("\nPredicate composition:");
        System.out.println("  12 is even AND > 10: " + evenAndGreaterThanTen.test(12));
        System.out.println("  8 is even AND > 10: " + evenAndGreaterThanTen.test(8));
        System.out.println("  15 is even OR > 10: " + evenOrGreaterThanTen.test(15));
        System.out.println("  5 is not even: " + notEven.test(5));

        // Real-world: Data processing pipeline
        Function<String, String> trim = String::trim;
        Function<String, String> toLowerCase = String::toLowerCase;
        Function<String, String> removeSpaces = s -> s.replaceAll("\\s+", "");

        Function<String, String> pipeline = trim
            .andThen(toLowerCase)
            .andThen(removeSpaces);

        String input = "  Hello   World  ";
        System.out.println("\nPipeline: '" + input + "' -> '" + pipeline.apply(input) + "'");

        System.out.println();
    }

    // =======================================================================
    // HELPER METHODS
    // =======================================================================

    private static List<Employee> createSampleEmployees() {
        return Arrays.asList(
            new Employee("Alice", "Engineering", 95000),
            new Employee("Bob", "Engineering", 87000),
            new Employee("Carol", "Sales", 65000),
            new Employee("David", "Sales", 72000),
            new Employee("Eve", "Marketing", 68000)
        );
    }

    private static DiscountStrategy getDynamicDiscount(String membershipLevel) {
        return switch (membershipLevel) {
            case "GOLD" -> price -> price * 0.8;
            case "SILVER" -> price -> price * 0.9;
            default -> price -> price;
        };
    }

    private static void readFile(String filename) throws IOException {
        // Simulated file reading that throws checked exception
        if (filename.contains("2")) {
            throw new IOException("Simulated error");
        }
    }

    private static void readFileSafely(String filename) {
        try {
            readFile(filename);
            System.out.println("  Read: " + filename);
        } catch (IOException e) {
            System.out.println("  Error reading: " + filename);
        }
    }

    /**
     * Wrapper to convert checked exceptions to unchecked
     */
    @FunctionalInterface
    interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    private static <T> Consumer<T> unchecked(ThrowingConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

// =============================================================================
// DOMAIN MODELS
// =============================================================================

class Employee {
    private String name;
    private String department;
    private double salary;

    public Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return name + " (" + department + ", $" + salary + ")";
    }
}

// =============================================================================
// CUSTOM FUNCTIONAL INTERFACES
// =============================================================================

@FunctionalInterface
interface StringProcessor {
    String process(String input);
}

@FunctionalInterface
interface Validator<T> {
    boolean validate(T value);
}

@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// =============================================================================
// STRATEGY PATTERN - BEFORE (Traditional Classes)
// =============================================================================

interface DiscountStrategy {
    double applyDiscount(double price);
}

class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double applyDiscount(double price) {
        return price;
    }
}

class TenPercentDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double price) {
        return price * 0.9;
    }
}

class TwentyPercentDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double price) {
        return price * 0.8;
    }
}

class PriceCalculator {
    public double calculateFinalPrice(double basePrice, DiscountStrategy strategy) {
        double discountedPrice = strategy.applyDiscount(basePrice);
        double tax = discountedPrice * 0.1;  // 10% tax
        return discountedPrice + tax;
    }
}

// =============================================================================
// EVENT HANDLING
// =============================================================================

interface ClickListener {
    void onClick(String buttonName);
}

class Button {
    private String name;
    private ClickListener listener;

    public Button(String name) {
        this.name = name;
    }

    public void setOnClick(ClickListener listener) {
        this.listener = listener;
    }

    public void click() {
        if (listener != null) {
            listener.onClick(name);
        }
    }
}

// =============================================================================
// CALLBACK PATTERN
// =============================================================================

interface DataCallback {
    void onSuccess(String data);
    void onError(String error);
}

class DataService {
    // Old style with single callback interface
    public void fetchDataOldStyle(String userId, DataCallback callback) {
        if (userId.contains("123")) {
            callback.onError("User not found");
        } else {
            callback.onSuccess("Data for " + userId);
        }
    }

    // New style with separate lambda callbacks
    public void fetchData(String userId,
                         Consumer<String> onSuccess,
                         Consumer<String> onError) {
        if (userId.contains("123")) {
            onError.accept("User not found");
        } else {
            onSuccess.accept("Data for " + userId);
        }
    }
}

// =============================================================================
// HELPER CLASSES
// =============================================================================

class Counter {
    private int value = 0;

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }
}
