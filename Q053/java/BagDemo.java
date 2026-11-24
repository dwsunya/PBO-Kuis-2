// BagDemo.java - Q053: a generic multiset Bag<T> for the sorting table.
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BagDemo {

    /** A multiset: each distinct element with the number of times it was added. */
    static final class Bag<T> {
        private final Map<T, Integer> counts = new LinkedHashMap<>();   // keeps first-added order
        private int size = 0;

        void add(T item) {
            add(item, 1);
        }

        void add(T item, int n) {
            // TODO(Q053): if (n <= 0) throw new IllegalArgumentException(...);
            //   counts.merge(item, n, Integer::sum); size += n;
            if (n <= 0) throw new IllegalArgumentException("n must be positive: " + n); // If n is less than or equal to 0, then reject it and provide a message stating that the value of n must be positive.
            counts.merge(item, n, Integer::sum); // Perform an item merge; if 'n' already exists, add the new 'n' to the existing 'n'.
            size += n; // add n to size
        }

        /** Object, not T, like Collection.contains: asking about a stranger is not an error. */
        int count(Object item) {
            // TODO(Q053): return counts.getOrDefault(item, 0);
            return counts.getOrDefault(item, 0); // return the result from getOrDeault(item, 0)
        }

        boolean remove(T item) {
            // TODO(Q053): if absent return false; if count == 1 remove the key, else decrement; size--;
            Integer count = counts.get(item); // Create a count variable of type Integer, as it can hold a null value.
            if (count == null) return false; // If count is null, return false.
            if (count == 1) { // if count is 1
                counts.remove(item); // remove item
            } else { // if count is a value other than 1
                counts.put(item, count - 1); // decrement count by 1
            }
            size--; // decrement size
            return true; // return true
        }

        int size() {
            return size;
        }

        int distinct() {
            // TODO(Q053): return counts.size();
            return counts.size(); // return size
        }

        /** The element added most often; on a tie, the one added first. */
        Optional<T> mostCommon() {
            // TODO(Q053): walk entries in insertion order, keep the first strictly larger count.
            T best = null; // create new variable named best
            int bestCount = 0; // create new variable named bestCount
            for (Map.Entry<T, Integer> e : counts.entrySet()) { // loop trough entrySet
                if (e.getValue() > bestCount) { // if the value of e is bigger than bestCount
                    best = e.getKey(); // assign best with the key of e
                    bestCount = e.getValue(); // assign bestCount with the value of e
                }
            }
            return Optional.ofNullable(best); // return best
        }

        /** A read-only view: callers can look but cannot corrupt size. */
        Map<T, Integer> asMap() {
            // TODO(Q053): return Collections.unmodifiableMap(counts);
            return Collections.unmodifiableMap(counts);
        }

        /** A generic method: the union of two bags of any subtype of T. */
        static <T> Bag<T> union(Bag<? extends T> a, Bag<? extends T> b) {
            // TODO(Q053): result = new Bag<>(); a.counts.forEach(result::add); b.counts.forEach(result::add);
            Bag<T> result = new Bag<>(); // create result variable with Bag type
            a.counts.forEach(result::add); 
            b.counts.forEach(result::add);
            return result;
        }

        @Override
        public String toString() {
            return counts + " size=" + size;
        }
    }

    public static void main(String[] args) {
        Bag<String> garments = new Bag<>();
        for (String g : "shirt towel shirt sock sock shirt sheet sock".split(" ")) {
            garments.add(g);
        }
        garments.add("towel", 3);
        System.out.println("garments:        " + garments);
        System.out.println("count(shirt)=" + garments.count("shirt") + " count(jacket)=" + garments.count("jacket")
                + " count(42)=" + garments.count(42));
        System.out.println("most common:     " + garments.mostCommon().orElse("(empty)"));
        System.out.println("remove(sheet)=" + garments.remove("sheet") + " remove(sheet)=" + garments.remove("sheet"));
        System.out.println("after removals:  " + garments + " distinct=" + garments.distinct());

        Bag<Integer> sockSizes = new Bag<>();
        sockSizes.add(39, 2);
        sockSizes.add(42, 2);
        sockSizes.add(40);
        int total = 0;
        for (Map.Entry<Integer, Integer> e : sockSizes.asMap().entrySet()) {
            total += e.getKey() * e.getValue();     // no cast: the key is already an Integer
        }
        System.out.println("sock sizes:      " + sockSizes + " sum of sizes=" + total
                + " most common=" + sockSizes.mostCommon().orElse(-1));

        Bag<Object> lostAndFound = Bag.union(garments, sockSizes);
        System.out.println("union:           " + lostAndFound);

        try {
            garments.asMap().put("scarf", 99);
        } catch (UnsupportedOperationException e) {
            System.out.println("asMap().put refused: UnsupportedOperationException");
        }
        try {
            garments.add("scarf", 0);
        } catch (IllegalArgumentException e) {
            System.out.println("add(scarf, 0) refused: " + e.getMessage());
        }
        System.out.println("empty bag most common present? " + new Bag<String>().mostCommon().isPresent());
    }
}
