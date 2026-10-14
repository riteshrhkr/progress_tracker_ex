package examples.pillars;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation is not "private field plus getter and setter".
 * It is the class refusing to let anyone put it in an invalid state.
 *
 * Run: java -cp out examples.pillars.EncapsulationDemo
 */
public class EncapsulationDemo {

    /** No guard anywhere, so "balance is never negative" is not a rule, just a hope. */
    static class LeakyAccount {
        double balance;
        List<String> history = new ArrayList<>();
    }

    /** Same data, but this class owns the rules about it. */
    static final class Account {

        // Money as whole cents. See section 5 for why this is not a double.
        private long balanceCents;
        private final List<String> history = new ArrayList<>();

        Account(long openingCents) {
            // Guarding in the constructor too. A rule enforced only by the methods
            // is a rule an object can be born without.
            if (openingCents < 0) {
                throw new IllegalArgumentException("opening balance cannot be negative");
            }
            this.balanceCents = openingCents;
            history.add("open " + openingCents);
        }

        /** The operation callers are allowed to perform. Note there is no setBalance(). */
        void withdraw(long cents) {
            if (cents <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            if (cents > balanceCents) {
                throw new IllegalStateException("insufficient funds: have " + balanceCents + ", need " + cents);
            }
            balanceCents -= cents;
            history.add("withdraw " + cents);
        }

        /** Reading is harmless; a long is copied, so the caller gets a value, not a handle. */
        long balanceCents() {
            return balanceCents;
        }

        /** Safe: an unmodifiable snapshot. The caller cannot reach the real list. */
        List<String> history() {
            return List.copyOf(history);
        }

        /** Unsafe, and the point of section 3: this hands out the internal list itself. */
        List<String> historyLeak() {
            return history;
        }
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Without a guard there is nothing to stop an impossible value.
        // ------------------------------------------------------------------
        System.out.println("[1] No guard means no invariant");
        LeakyAccount leaky = new LeakyAccount();
        leaky.balance = -500;
        System.out.println("    leaky.balance = " + leaky.balance);

        // ------------------------------------------------------------------
        // 2. The guarded version rejects every route into a bad state.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] The guarded version refuses bad state");
        Account account = new Account(10_000);
        account.withdraw(2_500);
        System.out.println("    balance after withdraw(2500) = " + account.balanceCents());

        try {
            account.withdraw(1_000_000);          // blocked by the method guard
        } catch (IllegalStateException e) {
            System.out.println("    withdraw(1000000) rejected: " + e.getMessage());
        }
        try {
            new Account(-1);                      // blocked by the constructor guard
        } catch (IllegalArgumentException e) {
            System.out.println("    new Account(-1) rejected: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // 3. 'private' protects the reference, not the object it points at.
        //    One leaky getter and the audit history can be rewritten.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] A getter that returns the internal list undoes all of it");
        account.historyLeak().clear();
        account.historyLeak().add("forged entry");
        System.out.println("    history after tampering through the leaky getter: " + account.history());

        // ------------------------------------------------------------------
        // 4. The copy has no path back to the field.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] The copy cannot be used to reach back in");
        try {
            account.history().add("another forged entry");
        } catch (UnsupportedOperationException e) {
            System.out.println("    List.copyOf(...).add(...) threw " + e.getClass().getSimpleName());
        }
        System.out.println("    history is still: " + account.history());

        // ------------------------------------------------------------------
        // 5. Binary floating point cannot represent most decimal fractions,
        //    so money is stored in whole minor units instead.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] Why money is stored as long cents, not double");
        System.out.println("    0.1 + 0.2 = " + (0.1 + 0.2));
    }
}
