package examples.java8;

import java.util.*;
import java.util.stream.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Real-World Scenario: Employee Data Processing with Streams API
 *
 * This example demonstrates comprehensive use of the Streams API in a realistic
 * HR/Payroll system context. Common interview scenario: "Process employee data
 * to generate reports and perform business analytics."
 *
 * Key Concepts Demonstrated:
 * 1. Filtering and mapping operations
 * 2. Collectors: groupingBy, partitioningBy, joining, summarizing
 * 3. FlatMap for nested data structures
 * 4. Reduction operations (custom and built-in)
 * 5. Parallel streams with performance comparison
 * 6. Combining multiple stream operations
 * 7. Method references vs lambdas
 * 8. Common mistakes and their corrections
 */
public class StreamsRealWorld {

    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();
        List<Employee> employees = service.getAllEmployees();

        System.out.println("=== Streams API Real-World Examples ===\n");

        // Example 1: Basic filtering and mapping
        example1_FilterAndMap(employees);

        // Example 2: Grouping operations
        example2_GroupingOperations(employees);

        // Example 3: Partitioning and statistics
        example3_PartitioningAndStats(employees);

        // Example 4: FlatMap for nested structures
        example4_FlatMapOperations(employees);

        // Example 5: Complex reduction operations
        example5_ReductionOperations(employees);

        // Example 6: Combining multiple operations
        example6_ComplexQueries(employees);

        // Example 7: Parallel streams performance
        example7_ParallelStreams();

        // Example 8: Common mistakes and corrections
        example8_CommonMistakes(employees);
    }

    /**
     * Example 1: Basic filtering and mapping
     * Pattern: filter() + map() + collect()
     */
    private static void example1_FilterAndMap(List<Employee> employees) {
        System.out.println("--- Example 1: Basic Filtering and Mapping ---");

        // Get names of all engineers earning > $80,000
        List<String> highEarningEngineers = employees.stream()
            .filter(e -> e.getDepartment().equals("Engineering"))
            .filter(e -> e.getSalary() > 80000)
            .map(Employee::getName)
            .sorted()
            .collect(Collectors.toList());

        System.out.println("High-earning engineers: " + highEarningEngineers);

        // Get unique departments
        Set<String> departments = employees.stream()
            .map(Employee::getDepartment)
            .collect(Collectors.toSet());

        System.out.println("Departments: " + departments);

        // Get emails joined with comma
        String emailList = employees.stream()
            .map(Employee::getEmail)
            .collect(Collectors.joining(", "));

        System.out.println("All emails: " + emailList);
        System.out.println();
    }

    /**
     * Example 2: Grouping operations
     * Pattern: groupingBy() collector
     */
    private static void example2_GroupingOperations(List<Employee> employees) {
        System.out.println("--- Example 2: Grouping Operations ---");

        // Group employees by department
        Map<String, List<Employee>> byDepartment = employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment));

        System.out.println("Employees per department:");
        byDepartment.forEach((dept, emps) ->
            System.out.println("  " + dept + ": " + emps.size() + " employees")
        );

        // Group by department and count
        Map<String, Long> countByDepartment = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.counting()
            ));

        System.out.println("\nEmployee count by department: " + countByDepartment);

        // Group by department and calculate average salary
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getSalary)
            ));

        System.out.println("\nAverage salary by department:");
        avgSalaryByDept.forEach((dept, avg) ->
            System.out.printf("  %s: $%.2f%n", dept, avg)
        );

        // Group by department and get highest earner in each
        Map<String, Optional<Employee>> topEarnerByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.maxBy(Comparator.comparing(Employee::getSalary))
            ));

        System.out.println("\nTop earner by department:");
        topEarnerByDept.forEach((dept, emp) ->
            emp.ifPresent(e -> System.out.printf("  %s: %s ($%.2f)%n",
                dept, e.getName(), e.getSalary()))
        );

        // Multi-level grouping: department -> seniority level
        Map<String, Map<String, List<Employee>>> multiLevel = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.groupingBy(e -> e.getYearsOfService() > 5 ? "Senior" : "Junior")
            ));

        System.out.println("\nMulti-level grouping (Dept -> Seniority):");
        multiLevel.forEach((dept, seniorityMap) -> {
            System.out.println("  " + dept + ":");
            seniorityMap.forEach((level, emps) ->
                System.out.println("    " + level + ": " + emps.size())
            );
        });

        System.out.println();
    }

    /**
     * Example 3: Partitioning and statistics
     * Pattern: partitioningBy() and summarizingDouble()
     */
    private static void example3_PartitioningAndStats(List<Employee> employees) {
        System.out.println("--- Example 3: Partitioning and Statistics ---");

        // Partition into high earners (> $70k) and others
        Map<Boolean, List<Employee>> partitioned = employees.stream()
            .collect(Collectors.partitioningBy(e -> e.getSalary() > 70000));

        System.out.println("High earners (>$70k): " +
            partitioned.get(true).size());
        System.out.println("Others: " +
            partitioned.get(false).size());

        // Partition and get names
        Map<Boolean, List<String>> namesByEarning = employees.stream()
            .collect(Collectors.partitioningBy(
                e -> e.getSalary() > 70000,
                Collectors.mapping(Employee::getName, Collectors.toList())
            ));

        System.out.println("High earner names: " + namesByEarning.get(true));

        // Get comprehensive salary statistics
        DoubleSummaryStatistics salaryStats = employees.stream()
            .collect(Collectors.summarizingDouble(Employee::getSalary));

        System.out.println("\nSalary Statistics:");
        System.out.printf("  Count: %d%n", salaryStats.getCount());
        System.out.printf("  Min: $%.2f%n", salaryStats.getMin());
        System.out.printf("  Max: $%.2f%n", salaryStats.getMax());
        System.out.printf("  Average: $%.2f%n", salaryStats.getAverage());
        System.out.printf("  Total: $%.2f%n", salaryStats.getSum());

        // Statistics per department
        Map<String, DoubleSummaryStatistics> statsByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summarizingDouble(Employee::getSalary)
            ));

        System.out.println("\nSalary statistics by department:");
        statsByDept.forEach((dept, stats) ->
            System.out.printf("  %s: Avg=$%.2f, Count=%d%n",
                dept, stats.getAverage(), stats.getCount())
        );

        System.out.println();
    }

    /**
     * Example 4: FlatMap for nested structures
     * Pattern: flatMap() to flatten collections
     */
    private static void example4_FlatMapOperations(List<Employee> employees) {
        System.out.println("--- Example 4: FlatMap Operations ---");

        // Get all skills across all employees (each employee has multiple skills)
        List<String> allSkills = employees.stream()
            .flatMap(e -> e.getSkills().stream())  // Flatten List<List<String>> to List<String>
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        System.out.println("All unique skills: " + allSkills);

        // Count occurrences of each skill
        Map<String, Long> skillCount = employees.stream()
            .flatMap(e -> e.getSkills().stream())
            .collect(Collectors.groupingBy(
                skill -> skill,
                Collectors.counting()
            ));

        System.out.println("\nSkill distribution:");
        skillCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry ->
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " employees")
            );

        // Find employees with specific skill
        String targetSkill = "Java";
        List<String> employeesWithJava = employees.stream()
            .filter(e -> e.getSkills().contains(targetSkill))
            .map(Employee::getName)
            .collect(Collectors.toList());

        System.out.println("\nEmployees with " + targetSkill + " skill: " +
            employeesWithJava);

        // Get all unique project names across all employees
        Set<String> allProjects = employees.stream()
            .flatMap(e -> e.getProjects().stream())
            .collect(Collectors.toSet());

        System.out.println("\nAll projects: " + allProjects);

        System.out.println();
    }

    /**
     * Example 5: Reduction operations
     * Pattern: reduce() for custom aggregations
     */
    private static void example5_ReductionOperations(List<Employee> employees) {
        System.out.println("--- Example 5: Reduction Operations ---");

        // Total salary (using reduce)
        double totalSalary = employees.stream()
            .map(Employee::getSalary)
            .reduce(0.0, Double::sum);

        System.out.printf("Total salary (reduce): $%.2f%n", totalSalary);

        // Total salary (using specialized stream)
        double totalSalary2 = employees.stream()
            .mapToDouble(Employee::getSalary)
            .sum();

        System.out.printf("Total salary (sum): $%.2f%n", totalSalary2);

        // Highest salary
        Optional<Double> maxSalary = employees.stream()
            .map(Employee::getSalary)
            .reduce(Double::max);

        maxSalary.ifPresent(max ->
            System.out.printf("Highest salary: $%.2f%n", max)
        );

        // Custom reduction: Build a summary string
        String summary = employees.stream()
            .map(e -> e.getName() + " (" + e.getDepartment() + ")")
            .reduce((a, b) -> a + ", " + b)
            .orElse("No employees");

        System.out.println("\nEmployee summary: " + summary);

        // Complex reduction: Calculate total compensation including bonuses
        double totalCompensation = employees.stream()
            .mapToDouble(e -> e.getSalary() + e.getBonus())
            .sum();

        System.out.printf("Total compensation (salary + bonus): $%.2f%n",
            totalCompensation);

        // Count employees by custom criteria
        long seniorCount = employees.stream()
            .filter(e -> e.getYearsOfService() > 5)
            .count();

        System.out.println("\nSenior employees (>5 years): " + seniorCount);

        // Custom collector: concatenate with custom delimiter
        String departments = employees.stream()
            .map(Employee::getDepartment)
            .distinct()
            .collect(Collectors.joining(" | ", "[", "]"));

        System.out.println("Departments: " + departments);

        System.out.println();
    }

    /**
     * Example 6: Complex queries combining multiple operations
     * Pattern: Real-world business logic
     */
    private static void example6_ComplexQueries(List<Employee> employees) {
        System.out.println("--- Example 6: Complex Business Queries ---");

        // Query 1: Top 3 highest paid engineers
        System.out.println("Top 3 highest-paid engineers:");
        employees.stream()
            .filter(e -> e.getDepartment().equals("Engineering"))
            .sorted(Comparator.comparing(Employee::getSalary).reversed())
            .limit(3)
            .forEach(e -> System.out.printf("  %s: $%.2f%n",
                e.getName(), e.getSalary()));

        // Query 2: Average salary of senior employees per department
        Map<String, Double> seniorAvgSalary = employees.stream()
            .filter(e -> e.getYearsOfService() > 5)
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getSalary)
            ));

        System.out.println("\nAverage salary of senior employees by dept:");
        seniorAvgSalary.forEach((dept, avg) ->
            System.out.printf("  %s: $%.2f%n", dept, avg)
        );

        // Query 3: Employees eligible for promotion (>3 years, high performer)
        List<Employee> promotionCandidates = employees.stream()
            .filter(e -> e.getYearsOfService() > 3)
            .filter(e -> e.getPerformanceRating() >= 4.0)
            .sorted(Comparator.comparing(Employee::getPerformanceRating).reversed())
            .collect(Collectors.toList());

        System.out.println("\nPromotion candidates (>3 years, rating >=4.0):");
        promotionCandidates.forEach(e ->
            System.out.printf("  %s (%s): %.1f rating, %d years%n",
                e.getName(), e.getDepartment(),
                e.getPerformanceRating(), e.getYearsOfService())
        );

        // Query 4: Department with highest average performance
        Optional<Map.Entry<String, Double>> topDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getPerformanceRating)
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue());

        topDept.ifPresent(entry ->
            System.out.printf("%nTop performing department: %s (avg rating: %.2f)%n",
                entry.getKey(), entry.getValue())
        );

        // Query 5: Skill gap analysis - skills with <3 employees
        System.out.println("\nSkills with shortage (<3 employees):");
        employees.stream()
            .flatMap(e -> e.getSkills().stream())
            .collect(Collectors.groupingBy(
                skill -> skill,
                Collectors.counting()
            ))
            .entrySet().stream()
            .filter(entry -> entry.getValue() < 3)
            .forEach(entry ->
                System.out.println("  " + entry.getKey() + ": " +
                    entry.getValue() + " employees")
            );

        System.out.println();
    }

    /**
     * Example 7: Parallel streams - when and how to use
     * Pattern: Performance comparison
     */
    private static void example7_ParallelStreams() {
        System.out.println("--- Example 7: Parallel Streams ---");

        // Create large dataset
        List<Integer> largeData = IntStream.rangeClosed(1, 10_000_000)
            .boxed()
            .collect(Collectors.toList());

        // Sequential processing
        long startSeq = System.currentTimeMillis();
        long sumSeq = largeData.stream()
            .filter(n -> n % 2 == 0)
            .mapToLong(n -> n * n)
            .sum();
        long timeSeq = System.currentTimeMillis() - startSeq;

        System.out.printf("Sequential: sum=%d, time=%dms%n", sumSeq, timeSeq);

        // Parallel processing
        long startPar = System.currentTimeMillis();
        long sumPar = largeData.parallelStream()
            .filter(n -> n % 2 == 0)
            .mapToLong(n -> n * n)
            .sum();
        long timePar = System.currentTimeMillis() - startPar;

        System.out.printf("Parallel: sum=%d, time=%dms%n", sumPar, timePar);
        System.out.printf("Speedup: %.2fx%n", (double) timeSeq / timePar);

        // NOTE: Parallel streams are beneficial for:
        // - Large datasets (>10k elements)
        // - Expensive per-element operations
        // - Stateless operations
        // - Operations where order doesn't matter

        System.out.println("\nParallel stream guidelines:");
        System.out.println("  ✓ Use for large datasets with expensive operations");
        System.out.println("  ✓ Ensure thread-safety (no shared mutable state)");
        System.out.println("  ✗ Avoid for small collections");
        System.out.println("  ✗ Avoid when order matters (unless using forEachOrdered)");
        System.out.println("  ✗ Measure performance - parallel isn't always faster!");

        System.out.println();
    }

    /**
     * Example 8: Common mistakes and how to fix them
     * Pattern: Anti-patterns and corrections
     */
    private static void example8_CommonMistakes(List<Employee> employees) {
        System.out.println("--- Example 8: Common Mistakes and Fixes ---");

        // MISTAKE 1: Reusing streams
        System.out.println("Mistake 1: Reusing streams");
        try {
            Stream<Employee> stream = employees.stream();
            long count = stream.count();
            // This will throw IllegalStateException
            // stream.forEach(System.out::println);  // ❌ ERROR!
            System.out.println("  ✗ Cannot reuse stream after count()");
        } catch (IllegalStateException e) {
            System.out.println("  ✗ IllegalStateException: " + e.getMessage());
        }
        System.out.println("  ✓ FIX: Create new stream for each operation");

        // MISTAKE 2: Side effects in operations
        System.out.println("\nMistake 2: Side effects");
        List<String> names = new ArrayList<>();
        // BAD - side effect
        // employees.stream().forEach(e -> names.add(e.getName()));  // ❌

        // GOOD - functional approach
        names = employees.stream()
            .map(Employee::getName)
            .collect(Collectors.toList());  // ✓
        System.out.println("  ✓ FIX: Use collect() instead of side effects");

        // MISTAKE 3: Wrong identity in reduce
        System.out.println("\nMistake 3: Wrong identity value");
        List<Integer> numbers = Arrays.asList(2, 3, 4);
        // WRONG - using 0 as identity for multiplication
        int wrongProduct = numbers.stream().reduce(0, (a, b) -> a * b);  // Always 0!
        System.out.println("  ✗ Product with identity 0: " + wrongProduct);

        // CORRECT - using 1 as identity for multiplication
        int correctProduct = numbers.stream().reduce(1, (a, b) -> a * b);
        System.out.println("  ✓ Product with identity 1: " + correctProduct);

        // MISTAKE 4: Using map instead of flatMap
        System.out.println("\nMistake 4: map() vs flatMap()");
        // WRONG - creates Stream<Stream<String>>
        // employees.stream().map(e -> e.getSkills().stream())  // ❌

        // CORRECT - flattens to Stream<String>
        long skillCount = employees.stream()
            .flatMap(e -> e.getSkills().stream())
            .distinct()
            .count();
        System.out.println("  ✓ Unique skills count: " + skillCount);

        // MISTAKE 5: Assuming parallel is always faster
        System.out.println("\nMistake 5: Blindly using parallel streams");
        List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);

        long start = System.nanoTime();
        int sum1 = small.stream().mapToInt(i -> i).sum();
        long time1 = System.nanoTime() - start;

        start = System.nanoTime();
        int sum2 = small.parallelStream().mapToInt(i -> i).sum();
        long time2 = System.nanoTime() - start;

        System.out.printf("  Sequential: %dns%n", time1);
        System.out.printf("  Parallel: %dns%n", time2);
        System.out.println("  ✓ For small collections, sequential is often faster");

        System.out.println();
    }
}

// =============================================================================
// DOMAIN MODELS
// =============================================================================

class Employee {
    private Long id;
    private String name;
    private String email;
    private String department;
    private double salary;
    private double bonus;
    private int yearsOfService;
    private double performanceRating;
    private List<String> skills;
    private List<String> projects;

    public Employee(Long id, String name, String email, String department,
                   double salary, int yearsOfService, double performanceRating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.yearsOfService = yearsOfService;
        this.performanceRating = performanceRating;
        this.bonus = salary * 0.1;  // 10% bonus
        this.skills = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public double getBonus() { return bonus; }
    public int getYearsOfService() { return yearsOfService; }
    public double getPerformanceRating() { return performanceRating; }
    public List<String> getSkills() { return skills; }
    public List<String> getProjects() { return projects; }

    // Setters
    public void setSkills(List<String> skills) { this.skills = skills; }
    public void setProjects(List<String> projects) { this.projects = projects; }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f", name, department, salary);
    }
}

// =============================================================================
// SERVICE LAYER
// =============================================================================

class EmployeeService {
    /**
     * Generate sample employee data for examples
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = Arrays.asList(
            createEmployee(1L, "Alice Johnson", "alice@company.com",
                "Engineering", 95000, 6, 4.5,
                Arrays.asList("Java", "Python", "AWS"),
                Arrays.asList("Project A", "Project B")),

            createEmployee(2L, "Bob Smith", "bob@company.com",
                "Engineering", 87000, 4, 4.2,
                Arrays.asList("Java", "Spring", "Docker"),
                Arrays.asList("Project A", "Project C")),

            createEmployee(3L, "Carol Davis", "carol@company.com",
                "Engineering", 92000, 5, 4.7,
                Arrays.asList("Python", "Machine Learning", "TensorFlow"),
                Arrays.asList("Project D")),

            createEmployee(4L, "David Brown", "david@company.com",
                "Sales", 65000, 3, 3.8,
                Arrays.asList("Salesforce", "Communication"),
                Arrays.asList("Project E")),

            createEmployee(5L, "Eve Wilson", "eve@company.com",
                "Sales", 72000, 7, 4.3,
                Arrays.asList("Salesforce", "Negotiation", "CRM"),
                Arrays.asList("Project E", "Project F")),

            createEmployee(6L, "Frank Miller", "frank@company.com",
                "Marketing", 68000, 2, 3.5,
                Arrays.asList("SEO", "Content Marketing"),
                Arrays.asList("Project G")),

            createEmployee(7L, "Grace Lee", "grace@company.com",
                "Marketing", 71000, 4, 4.0,
                Arrays.asList("Social Media", "Analytics", "SEO"),
                Arrays.asList("Project G", "Project H")),

            createEmployee(8L, "Henry Taylor", "henry@company.com",
                "Engineering", 105000, 8, 4.8,
                Arrays.asList("Java", "Kubernetes", "Microservices", "AWS"),
                Arrays.asList("Project A", "Project B", "Project C")),

            createEmployee(9L, "Iris Anderson", "iris@company.com",
                "HR", 62000, 3, 3.9,
                Arrays.asList("Recruitment", "Employee Relations"),
                Arrays.asList("Project I")),

            createEmployee(10L, "Jack Thomas", "jack@company.com",
                "Engineering", 78000, 2, 3.7,
                Arrays.asList("JavaScript", "React", "Node.js"),
                Arrays.asList("Project C")),

            createEmployee(11L, "Karen White", "karen@company.com",
                "Sales", 70000, 5, 4.1,
                Arrays.asList("B2B Sales", "Presentation"),
                Arrays.asList("Project E")),

            createEmployee(12L, "Leo Harris", "leo@company.com",
                "Engineering", 98000, 6, 4.6,
                Arrays.asList("Python", "Django", "PostgreSQL"),
                Arrays.asList("Project D", "Project B"))
        );

        return employees;
    }

    private Employee createEmployee(Long id, String name, String email,
                                    String dept, double salary, int years,
                                    double rating, List<String> skills,
                                    List<String> projects) {
        Employee emp = new Employee(id, name, email, dept, salary, years, rating);
        emp.setSkills(skills);
        emp.setProjects(projects);
        return emp;
    }
}
