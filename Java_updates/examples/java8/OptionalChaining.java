package examples.java8;

import java.util.*;
import java.util.function.Supplier;

/**
 * Real-World Scenario: REST API Response Handling with Optional
 *
 * This example demonstrates how to use Optional in a realistic service layer
 * that interacts with external APIs and databases.
 *
 * Scenario: E-commerce application fetching user profile data from multiple sources
 * - Primary API (user service)
 * - Secondary API (legacy system fallback)
 * - Cache layer
 * - Database
 *
 * Key Concepts Demonstrated:
 * 1. Optional chaining with map() and flatMap()
 * 2. Combining multiple Optional sources
 * 3. Proper error handling with orElseThrow()
 * 4. Lazy evaluation with orElseGet()
 * 5. Filtering with Optional.filter()
 * 6. Real-world service layer patterns
 */
public class OptionalChaining {

    public static void main(String[] args) {
        UserService userService = new UserService();

        System.out.println("=== Optional Chaining Examples ===\n");

        // Example 1: Basic chaining - get user's primary email
        example1_BasicChaining(userService);

        // Example 2: Nested Optional handling with flatMap
        example2_NestedOptionals(userService);

        // Example 3: Combining multiple Optional sources (fallback chain)
        example3_MultipleSources(userService);

        // Example 4: Filtering and conditional logic
        example4_FilteringAndConditionals(userService);

        // Example 5: Error handling and custom exceptions
        example5_ErrorHandling(userService);

        // Example 6: Lazy evaluation vs eager evaluation
        example6_LazyVsEager(userService);
    }

    /**
     * Example 1: Basic Optional chaining
     * Pattern: map() for simple transformations
     */
    private static void example1_BasicChaining(UserService userService) {
        System.out.println("--- Example 1: Basic Chaining ---");

        Long userId = 1L;

        // Get user's email with default fallback
        String email = userService.findUserById(userId)
            .map(User::getEmail)  // Extract email if user exists
            .orElse("no-reply@company.com");  // Default if user not found

        System.out.println("User email: " + email);

        // Get uppercase city name from user's address
        String city = userService.findUserById(userId)
            .map(User::getAddress)  // User -> Address (can be null)
            .map(Address::getCity)  // Address -> city string
            .map(String::toUpperCase)  // Transform to uppercase
            .orElse("UNKNOWN");

        System.out.println("User city: " + city);
        System.out.println();
    }

    /**
     * Example 2: Handling nested Optionals with flatMap()
     * Problem: map() would give us Optional<Optional<T>>
     * Solution: flatMap() flattens the result
     */
    private static void example2_NestedOptionals(UserService userService) {
        System.out.println("--- Example 2: Nested Optionals (flatMap) ---");

        Long userId = 2L;

        // WRONG WAY - using map() with Optional-returning method
        // This would give us Optional<Optional<String>>
        // Optional<Optional<String>> wrongWay = userService.findUserById(userId)
        //     .map(user -> user.getPrimaryPhone());  // getPrimaryPhone() returns Optional<String>

        // RIGHT WAY - using flatMap() to flatten nested Optional
        String phone = userService.findUserById(userId)
            .flatMap(User::getPrimaryPhone)  // flatMap flattens Optional<Optional<String>> to Optional<String>
            .orElse("No phone available");

        System.out.println("User phone: " + phone);

        // Complex chaining: Get user's preferred contact method
        String contact = userService.findUserById(userId)
            .flatMap(User::getPrimaryPhone)  // Try primary phone
            .or(() -> userService.findUserById(userId)  // If not available, try backup
                .map(User::getEmail))  // Use email as fallback
            .orElse("No contact information");

        System.out.println("Contact info: " + contact);
        System.out.println();
    }

    /**
     * Example 3: Combining multiple data sources with fallback chain
     * Pattern: Primary source -> Secondary source -> Cache -> Default
     */
    private static void example3_MultipleSources(UserService userService) {
        System.out.println("--- Example 3: Multiple Sources (Fallback Chain) ---");

        Long userId = 3L;

        // Try multiple sources in order: API -> Cache -> Database -> Default
        User user = userService.findUserFromPrimaryAPI(userId)
            .or(() -> {
                System.out.println("  Primary API failed, trying cache...");
                return userService.findUserFromCache(userId);
            })
            .or(() -> {
                System.out.println("  Cache miss, trying database...");
                return userService.findUserFromDatabase(userId);
            })
            .orElseGet(() -> {
                System.out.println("  All sources failed, creating guest user...");
                return User.createGuestUser();
            });

        System.out.println("Resolved user: " + user.getName());
        System.out.println();
    }

    /**
     * Example 4: Filtering and conditional logic
     * Pattern: Use filter() to add conditions to Optional chain
     */
    private static void example4_FilteringAndConditionals(UserService userService) {
        System.out.println("--- Example 4: Filtering and Conditionals ---");

        Long userId = 1L;

        // Get email only if user is verified and active
        String verifiedEmail = userService.findUserById(userId)
            .filter(User::isVerified)  // Only proceed if verified
            .filter(User::isActive)    // Only proceed if active
            .map(User::getEmail)
            .orElse("User not eligible");

        System.out.println("Verified email: " + verifiedEmail);

        // Get premium users only
        Optional<User> premiumUser = userService.findUserById(userId)
            .filter(user -> user.getAccountType().equals("PREMIUM"));

        premiumUser.ifPresentOrElse(
            user -> System.out.println("Premium user: " + user.getName()),
            () -> System.out.println("User is not premium")
        );

        System.out.println();
    }

    /**
     * Example 5: Proper error handling with meaningful exceptions
     * Pattern: Use orElseThrow() with custom exceptions
     */
    private static void example5_ErrorHandling(UserService userService) {
        System.out.println("--- Example 5: Error Handling ---");

        Long userId = 999L;  // Non-existent user

        try {
            // Throw custom exception if user not found
            User user = userService.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

            System.out.println("Found user: " + user.getName());

        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Chained error handling: Get email or throw specific exception
        try {
            String email = userService.findUserById(userId)
                .filter(User::isActive)
                .map(User::getEmail)
                .orElseThrow(() -> new IllegalStateException("Active user email not available"));

            System.out.println("Email: " + email);

        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * Example 6: Lazy evaluation (orElseGet) vs Eager evaluation (orElse)
     * Important: orElse() ALWAYS executes, orElseGet() only executes if empty
     */
    private static void example6_LazyVsEager(UserService userService) {
        System.out.println("--- Example 6: Lazy vs Eager Evaluation ---");

        Long userId = 1L;

        System.out.println("Using orElse() - EAGER (always executes):");
        String eager = userService.findUserById(userId)
            .map(User::getEmail)
            .orElse(expensiveDefaultEmail());  // Called even though user exists!
        System.out.println("Result: " + eager);

        System.out.println("\nUsing orElseGet() - LAZY (only if needed):");
        String lazy = userService.findUserById(userId)
            .map(User::getEmail)
            .orElseGet(() -> expensiveDefaultEmail());  // NOT called because user exists
        System.out.println("Result: " + lazy);

        System.out.println();
    }

    // Simulates expensive operation (API call, database query, etc.)
    private static String expensiveDefaultEmail() {
        System.out.println("  [EXPENSIVE] Computing default email...");
        // Simulate slow operation
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        return "default@company.com";
    }
}

// =============================================================================
// DOMAIN MODELS
// =============================================================================

class User {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Address address;
    private boolean verified;
    private boolean active;
    private String accountType;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.verified = true;
        this.active = true;
        this.accountType = "PREMIUM";
    }

    public static User createGuestUser() {
        User guest = new User(0L, "Guest", "guest@company.com");
        guest.accountType = "GUEST";
        return guest;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Address getAddress() { return address; }
    public boolean isVerified() { return verified; }
    public boolean isActive() { return active; }
    public String getAccountType() { return accountType; }

    // Returns Optional because phone might not be set
    public Optional<String> getPrimaryPhone() {
        return Optional.ofNullable(phoneNumber);
    }

    // Setters for demo
    public void setAddress(Address address) { this.address = address; }
    public void setPhoneNumber(String phone) { this.phoneNumber = phone; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setActive(boolean active) { this.active = active; }
}

class Address {
    private String street;
    private String city;
    private String zipCode;

    public Address(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
}

// =============================================================================
// SERVICE LAYER (Simulates real-world data access)
// =============================================================================

class UserService {
    // Simulated database
    private Map<Long, User> database = new HashMap<>();
    private Map<Long, User> cache = new HashMap<>();

    public UserService() {
        // Setup test data
        User user1 = new User(1L, "Alice", "alice@example.com");
        user1.setAddress(new Address("123 Main St", "Springfield", "12345"));
        user1.setPhoneNumber("555-1234");
        database.put(1L, user1);

        User user2 = new User(2L, "Bob", "bob@example.com");
        user2.setAddress(new Address("456 Oak Ave", "Shelbyville", "67890"));
        // Bob has no phone number
        database.put(2L, user2);

        User user3 = new User(3L, "Charlie", "charlie@example.com");
        cache.put(3L, user3);  // Only in cache
    }

    /**
     * Primary method: Find user by ID
     * Returns Optional to signal that user might not exist
     */
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Simulates primary API call (might fail)
     */
    public Optional<User> findUserFromPrimaryAPI(Long id) {
        // Simulate API failure for user 3
        if (id == 3L) {
            return Optional.empty();
        }
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Simulates cache lookup
     */
    public Optional<User> findUserFromCache(Long id) {
        return Optional.ofNullable(cache.get(id));
    }

    /**
     * Simulates database fallback
     */
    public Optional<User> findUserFromDatabase(Long id) {
        return Optional.ofNullable(database.get(id));
    }
}

// =============================================================================
// CUSTOM EXCEPTIONS
// =============================================================================

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
