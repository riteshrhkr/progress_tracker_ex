package examples.contracts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * What actually breaks when equals and hashCode are wrong:
 * duplicate set entries, lost map values, and asymmetric comparisons.
 *
 * Run: java -cp out examples.contracts.EqualsHashCodeDemo
 */
public class EqualsHashCodeDemo {

    /**
     * equals overridden, hashCode forgotten. Nothing throws at runtime.
     * javac -Xlint:all does warn about exactly this.
     */
    static class BadUser {
        final String email;

        BadUser(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BadUser other && email.equals(other.email);
        }
        // no hashCode, so two equal objects inherit two different identity hashes
    }

    /** Both methods, built from the same single field. This is the correct shape. */
    static class GoodUser {
        final String email;

        GoodUser(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;                          // cheap fast path
            if (!(o instanceof GoodUser other)) return false;    // also covers null
            return email.equals(other.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }
    }

    /** Correct equals/hashCode pair, but built on a field that can change. */
    static class MutableKey {
        String id;      // not final: that is the whole problem

        MutableKey(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey other && Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "MutableKey[" + id + "]";
        }
    }

    /**
     * The classic asymmetry: this type accepts a String, but String will never
     * accept this type back, so x.equals(y) and y.equals(x) disagree.
     */
    static final class CaseInsensitiveWord {
        private final String value;

        CaseInsensitiveWord(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CaseInsensitiveWord other) {
                return value.equalsIgnoreCase(other.value);
            }
            if (o instanceof String s) {
                return value.equalsIgnoreCase(s);      // the mistake
            }
            return false;
        }

        @Override
        public int hashCode() {
            return value.toLowerCase().hashCode();
        }
    }

    /** instanceof-based equals: accepts any Point, including subclasses. */
    static class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Point p && p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /** Adds a field to the comparison, which is what breaks symmetry with Point. */
    static class ColourPoint extends Point {
        final String colour;

        ColourPoint(int x, int y, String colour) {
            super(x, y);
            this.colour = colour;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ColourPoint cp && super.equals(cp) && colour.equals(cp.colour);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, colour);
        }
    }

    /** The compiler generates equals, hashCode and toString from the components. */
    record RecordUser(String email) {
    }

    public static void main(String[] args) {
        // ------------------------------------------------------------------
        // 1. Hash containers find by bucket first and compare second. A wrong
        //    hash files the object in one bucket and looks for it in another.
        // ------------------------------------------------------------------
        System.out.println("[1] equals without hashCode");
        Set<BadUser> bad = new HashSet<>();
        bad.add(new BadUser("a@x.com"));
        bad.add(new BadUser("a@x.com"));        // equal to the first, but a different bucket
        System.out.println("    two equal BadUser objects -> HashSet size = " + bad.size());
        System.out.println("    contains(new BadUser(\"a@x.com\")) = " + bad.contains(new BadUser("a@x.com")));

        Set<GoodUser> good = new HashSet<>();
        good.add(new GoodUser("a@x.com"));
        good.add(new GoodUser("a@x.com"));
        System.out.println("    two equal GoodUser objects -> HashSet size = " + good.size());

        // ------------------------------------------------------------------
        // 2. The entry is still in the table, and no lookup can reach it: the
        //    old hash leads to a bucket whose contents no longer match.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[2] Mutating a key after it is in the map");
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey("k1");
        map.put(key, "value");
        System.out.println("    before mutation: get(key) = " + map.get(key));

        key.id = "k2";                          // the hash changes, the bucket does not
        System.out.println("    after key.id = \"k2\": get(key) = " + map.get(key));
        System.out.println("    get(new MutableKey(\"k2\")) = " + map.get(new MutableKey("k2")));
        System.out.println("    map still reports size = " + map.size() + " and contents " + map);

        // ------------------------------------------------------------------
        // 3. HashMap compares query-against-stored, so which side you are on
        //    decides the answer. That is why symmetry is in the contract.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[3] Asymmetric equals");
        CaseInsensitiveWord word = new CaseInsensitiveWord("Yes");
        String plain = "yes";
        System.out.println("    word.equals(plain) = " + word.equals(plain));
        System.out.println("    plain.equals(word) = " + plain.equals(word));

        Set<Object> mixed = new HashSet<>();
        mixed.add(word);
        System.out.println("    set built from the word contains the String? " + mixed.contains(plain));

        // ------------------------------------------------------------------
        // 4. The same asymmetry, reached through inheritance instead of a
        //    second type. The parent accepts the child; the child rejects the parent.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[4] Subclass with an extra field, instanceof-style equals");
        Point p = new Point(1, 2);
        ColourPoint cp = new ColourPoint(1, 2, "red");
        System.out.println("    p.equals(cp) = " + p.equals(cp));
        System.out.println("    cp.equals(p) = " + cp.equals(p));

        Set<Point> points = new HashSet<>();
        points.add(p);
        points.add(cp);
        System.out.println("    HashSet<Point> holding both = size " + points.size());

        // ------------------------------------------------------------------
        // 5. A record removes the whole category of bug, as long as the
        //    components themselves are immutable.
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("[5] A record writes both methods for you");
        Set<RecordUser> records = new HashSet<>();
        records.add(new RecordUser("a@x.com"));
        records.add(new RecordUser("a@x.com"));
        System.out.println("    HashSet<RecordUser> size = " + records.size());
        System.out.println("    toString = " + new RecordUser("a@x.com"));
        System.out.println("    equal records share a hashCode: "
                + (new RecordUser("a@x.com").hashCode() == new RecordUser("a@x.com").hashCode()));
    }
}
