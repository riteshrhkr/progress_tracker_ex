package examples.java8;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Real-World Scenario: Method References - All 4 Types
 *
 * This example demonstrates all four types of method references with
 * practical, real-world use cases and comparisons with lambda expressions.
 *
 * Key Concepts Demonstrated:
 * 1. Static method references (ClassName::staticMethod)
 * 2. Bound instance method references (object::instanceMethod)
 * 3. Unbound instance method references (ClassName::instanceMethod)
 * 4. Constructor references (ClassName::new)
 * 5. When to use method references vs lambdas
 * 6. Common patterns and best practices
 */
public class MethodReferencesDemo {

    public static void main(String[] args) {
        System.out.println("=== Method References: All 4 Types ===\n");

        // Example 1: Static method references
        example1_StaticMethodReferences();

        // Example 2: Bound instance method references
        example2_BoundInstanceReferences();

        // Example 3: Unbound instance method references
        example3_UnboundInstanceReferences();

        // Example 4: Constructor references
        example4_ConstructorReferences();

        // Example 5: Real-world use cases
        example5_RealWorldUseCases();

        // Example 6: Comparator with method references
        example6_ComparatorChaining();

        // Example 7: When NOT to use method references
        example7_WhenNotToUse();
    }

    /**
     * Example 1: Static Method References
     * Pattern: ClassName::staticMethodName
     */
    private static void example1_StaticMethodReferences() {
        System.out.println("--- Example 1: Static Method References ---");

        List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");

        // === BEFORE: Lambda ===
        List<Integer> parsedLambda = numbers.stream()
            .map(s -> Integer.parseInt(s))
            .collect(Collectors.toList());
        System.out.println("Parsed (lambda): " + parsedLambda);

        // === AFTER: Method Reference ===
        List<Integer> parsedMethodRef = numbers.stream()
            .map(Integer::parseInt)  // Static method reference
            .collect(Collectors.toList());
        System.out.println("Parsed (method ref): " + parsedMethodRef);

        // More examples of static method references
        List<Double> values = Arrays.asList(16.0, 25.0, 36.0);

        // Math.sqrt - static method
        List<Double> squareRoots = values.stream()
            .map(Math::sqrt)  // Method reference to Math.sqrt
            .collect(Collectors.toList());
        System.out.println("Square roots: " + squareRoots);

        // Comparing with static comparator
        List<Integer> nums = Arrays.asList(5, 2, 8, 1, 9);
        nums.sort(Integer::compare);  // Static method reference
        System.out.println("Sorted: " + nums);

        System.out.println();
    }

    /**
     * Example 2: Bound Instance Method References
     * Pattern: object::instanceMethodName
     * The method is called on a specific object instance
     */
    private static void example2_BoundInstanceReferences() {
        System.out.println("--- Example 2: Bound Instance Method References ---");

        // Create a specific object to bind to
        String prefix = "Hello, ";

        // === BEFORE: Lambda ===
        Function<String, String> greeterLambda = name -> prefix.concat(name);
        System.out.println("Lambda: " + greeterLambda.apply("World"));

        // === AFTER: Bound Method Reference ===
        Function<String, String> greeterMethodRef = prefix::concat;  // Bound to 'prefix' object
        System.out.println("Method ref: " + greeterMethodRef.apply("World"));

        // Real-world example: Using PrintStream instance
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        // Lambda
        names.forEach(name -> System.out.println(name));

        // Method reference (bound to System.out instance)
        names.forEach(System.out::println);  // Bound to System.out

        // Custom object example
        StringBuilder builder = new StringBuilder();
        List<String> words = Arrays.asList("Java", " ", "Method", " ", "References");

        // Bound to specific StringBuilder instance
        words.forEach(builder::append);  // builder.append(word) for each
        System.out.println("Built string: " + builder.toString());

        // Validator example
        EmailValidator validator = new EmailValidator("@company.com");
        List<String> emails = Arrays.asList(
            "alice@company.com",
            "bob@other.com",
            "charlie@company.com"
        );

        List<String> companyEmails = emails.stream()
            .filter(validator::isCompanyEmail)  // Bound to validator instance
            .collect(Collectors.toList());
        System.out.println("Company emails: " + companyEmails);

        System.out.println();
    }

    /**
     * Example 3: Unbound Instance Method References
     * Pattern: ClassName::instanceMethodName
     * First parameter becomes the object on which the method is called
     */
    private static void example3_UnboundInstanceReferences() {
        System.out.println("--- Example 3: Unbound Instance Method References ---");

        List<String> words = Arrays.asList("java", "method", "reference");

        // === BEFORE: Lambda ===
        List<String> upperLambda = words.stream()
            .map(s -> s.toUpperCase())  // s.toUpperCase()
            .collect(Collectors.toList());
        System.out.println("Upper (lambda): " + upperLambda);

        // === AFTER: Unbound Method Reference ===
        List<String> upperMethodRef = words.stream()
            .map(String::toUpperCase)  // First param (the string) becomes 'this'
            .collect(Collectors.toList());
        System.out.println("Upper (method ref): " + upperMethodRef);

        // Key difference: The object is passed as the first parameter
        // String::toUpperCase means (String s) -> s.toUpperCase()

        // BiPredicate example
        // Lambda: (s1, s2) -> s1.startsWith(s2)
        BiPredicate<String, String> startsWithLambda = (s1, s2) -> s1.startsWith(s2);

        // Method reference: String::startsWith
        BiPredicate<String, String> startsWithMethodRef = String::startsWith;

        System.out.println("'Hello' starts with 'He': " +
            startsWithMethodRef.test("Hello", "He"));

        // More examples
        List<String> strings = Arrays.asList("  spaced  ", "  trimmed  ");

        // String::trim - unbound
        List<String> trimmed = strings.stream()
            .map(String::trim)
            .collect(Collectors.toList());
        System.out.println("Trimmed: " + trimmed);

        // String::length - unbound
        List<Integer> lengths = words.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println("Lengths: " + lengths);

        // Employee example - unbound instance method
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", 95000),
            new Employee("Bob", 87000),
            new Employee("Carol", 92000)
        );

        // Employee::getName - unbound
        List<String> empNames = employees.stream()
            .map(Employee::getName)  // Equivalent to: e -> e.getName()
            .collect(Collectors.toList());
        System.out.println("Employee names: " + empNames);

        // Employee::getSalary - unbound
        double totalSalary = employees.stream()
            .mapToDouble(Employee::getSalary)  // e -> e.getSalary()
            .sum();
        System.out.println("Total salary: $" + totalSalary);

        System.out.println();
    }

    /**
     * Example 4: Constructor References
     * Pattern: ClassName::new
     */
    private static void example4_ConstructorReferences() {
        System.out.println("--- Example 4: Constructor References ---");

        // === No-arg constructor ===

        // Lambda
        Supplier<List<String>> listFactoryLambda = () -> new ArrayList<>();
        List<String> list1 = listFactoryLambda.get();

        // Constructor reference
        Supplier<List<String>> listFactoryMethodRef = ArrayList::new;
        List<String> list2 = listFactoryMethodRef.get();
        System.out.println("Created list: " + list2);

        // === Constructor with parameters ===

        // Lambda
        Function<String, Employee> employeeFactoryLambda = name -> new Employee(name, 50000);

        // Constructor reference (uses constructor with 2 params)
        BiFunction<String, Double, Employee> employeeFactory = Employee::new;
        Employee emp = employeeFactory.apply("David", 85000.0);
        System.out.println("Created employee: " + emp.getName());

        // === Array constructor ===

        // Lambda
        IntFunction<String[]> arrayFactoryLambda = size -> new String[size];
        String[] arr1 = arrayFactoryLambda.apply(5);

        // Constructor reference for arrays
        IntFunction<String[]> arrayFactoryMethodRef = String[]::new;
        String[] arr2 = arrayFactoryMethodRef.apply(5);
        System.out.println("Created array of size: " + arr2.length);

        // === Real-world: Stream to collection ===
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        // Using constructor reference for collection creation
        Set<String> nameSet = names.stream()
            .collect(Collectors.toCollection(HashSet::new));  // Constructor ref
        System.out.println("Name set: " + nameSet);

        // Converting to custom objects
        List<String> namesList = Arrays.asList("Alice", "Bob", "Carol");
        List<Employee> empList = namesList.stream()
            .map(name -> new Employee(name, 60000))  // Lambda
            .collect(Collectors.toList());

        // With method reference (requires factory method)
        List<Employee> empList2 = namesList.stream()
            .map(Employee::createWithDefaultSalary)  // Static factory method
            .collect(Collectors.toList());
        System.out.println("Created employees: " +
            empList2.stream().map(Employee::getName).collect(Collectors.toList()));

        System.out.println();
    }

    /**
     * Example 5: Real-World Use Cases
     */
    private static void example5_RealWorldUseCases() {
        System.out.println("--- Example 5: Real-World Use Cases ---");

        List<Employee> employees = Arrays.asList(
            new Employee("Alice", 95000),
            new Employee("Bob", 87000),
            new Employee("Carol", 92000),
            new Employee("David", 78000),
            new Employee("Eve", 105000)
        );

        // Use case 1: Extract and transform data
        List<String> upperNames = employees.stream()
            .map(Employee::getName)       // Unbound instance reference
            .map(String::toUpperCase)     // Unbound instance reference
            .collect(Collectors.toList());
        System.out.println("Names: " + upperNames);

        // Use case 2: Filtering with extracted predicate
        long highEarnerCount = employees.stream()
            .filter(Employee::isHighEarner)  // Unbound instance reference to predicate method
            .count();
        System.out.println("High earners (>$90k): " + highEarnerCount);

        // Use case 3: Grouping
        Map<Boolean, List<Employee>> grouped = employees.stream()
            .collect(Collectors.groupingBy(Employee::isHighEarner));
        System.out.println("High earners count: " + grouped.get(true).size());
        System.out.println("Regular earners count: " + grouped.get(false).size());

        // Use case 4: Finding max with comparator
        Optional<Employee> highestPaid = employees.stream()
            .max(Comparator.comparingDouble(Employee::getSalary));
        highestPaid.ifPresent(e -> System.out.println("Highest paid: " +
            e.getName() + " ($" + e.getSalary() + ")"));

        // Use case 5: Custom processing with instance method
        DataProcessor processor = new DataProcessor();
        List<String> processed = employees.stream()
            .map(Employee::getName)
            .map(processor::processData)  // Bound instance reference
            .collect(Collectors.toList());
        System.out.println("Processed: " + processed);

        System.out.println();
    }

    /**
     * Example 6: Comparator with Method References
     */
    private static void example6_ComparatorChaining() {
        System.out.println("--- Example 6: Comparator Chaining ---");

        List<Employee> employees = Arrays.asList(
            new Employee("Charlie", 95000),
            new Employee("Alice", 95000),
            new Employee("Bob", 87000),
            new Employee("Alice", 92000)
        );

        // Complex sorting with method references
        employees.sort(
            Comparator.comparing(Employee::getName)          // Primary: by name
                     .thenComparingDouble(Employee::getSalary)  // Secondary: by salary
        );

        System.out.println("Sorted employees:");
        employees.forEach(e -> System.out.println("  " + e.getName() + ": $" + e.getSalary()));

        // Reverse order
        List<Employee> employees2 = Arrays.asList(
            new Employee("Alice", 95000),
            new Employee("Bob", 87000),
            new Employee("Carol", 92000)
        );

        employees2.sort(Comparator.comparingDouble(Employee::getSalary).reversed());
        System.out.println("\nBy salary (descending):");
        employees2.forEach(e -> System.out.println("  " + e.getName() + ": $" + e.getSalary()));

        System.out.println();
    }

    /**
     * Example 7: When NOT to Use Method References
     */
    private static void example7_WhenNotToUse() {
        System.out.println("--- Example 7: When NOT to Use Method References ---");

        List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");

        // Case 1: Additional logic needed - use lambda
        System.out.println("Case 1: Additional logic");
        List<Integer> doubled = numbers.stream()
            .map(s -> Integer.parseInt(s) * 2)  // Can't use method ref - need multiplication
            .collect(Collectors.toList());
        System.out.println("Doubled: " + doubled);

        // Case 2: Simple inline logic - lambda is clearer
        System.out.println("\nCase 2: Simple condition");
        List<String> longStrings = Arrays.asList("a", "abc", "abcdef").stream()
            .filter(s -> s.length() > 3)  // Lambda is clearer than creating helper method
            .collect(Collectors.toList());
        System.out.println("Long strings: " + longStrings);

        // Case 3: Parameter order manipulation - use lambda
        System.out.println("\nCase 3: Parameter manipulation");
        BiFunction<String, String, String> customConcat =
            (a, b) -> b + " " + a;  // Can't use String::concat - order is reversed
        System.out.println("Custom concat: " + customConcat.apply("World", "Hello"));

        System.out.println();
    }
}

// =============================================================================
// SUPPORTING CLASSES
// =============================================================================

class Employee {
    private String name;
    private double salary;

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() { return name; }
    public double getSalary() { return salary; }

    public boolean isHighEarner() {
        return salary > 90000;
    }

    public static Employee createWithDefaultSalary(String name) {
        return new Employee(name, 60000);
    }

    @Override
    public String toString() {
        return name + " ($" + salary + ")";
    }
}

class EmailValidator {
    private String domain;

    public EmailValidator(String domain) {
        this.domain = domain;
    }

    public boolean isCompanyEmail(String email) {
        return email.endsWith(domain);
    }
}

class DataProcessor {
    public String processData(String input) {
        return "[Processed: " + input + "]";
    }
}
