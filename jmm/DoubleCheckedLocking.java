package jmm;

/**
 * A classic demonstration of the Double-Checked Locking (DCL) singleton pattern.
 * This pattern relies on the volatile keyword to prevent instruction reordering
 * that could lead to another thread seeing a partially initialized object.
 */
public class DoubleCheckedLocking {

    // 'volatile' is CRITICAL here to ensure visibility and prevent reordering.
    // Without volatile, the compiler or CPU might reorder the assignment of the 
    // instance reference and the actual initialization of the object.
    private static volatile DoubleCheckedLocking instance;

    private DoubleCheckedLocking() {
        // Private constructor to prevent instantiation
        // Assume some heavy initialization takes place here.
    }

    public static DoubleCheckedLocking getInstance() {
        // First check (without locking) for performance. If the instance
        // is already initialized, we bypass the expensive synchronized block.
        if (instance == null) {
            
            // Lock the class object. Only one thread can enter this block at a time.
            synchronized (DoubleCheckedLocking.class) {
                
                // Second check (with locking). Another thread might have initialized
                // the instance while we were waiting for the lock.
                if (instance == null) {
                    // Under the hood, this is a 3-step process:
                    // 1. Allocate memory for the object
                    // 2. Initialize the object (call constructor)
                    // 3. Point the 'instance' reference to the allocated memory
                    // 'volatile' ensures that steps 2 and 3 are not reordered!
                    instance = new DoubleCheckedLocking();
                }
            }
        }
        return instance;
    }
}
