import java.math.BigDecimal;
import java.util.*;

public class CustomObjectExample {
    public static void main(String[] args) {
        // Sample class for demonstration
        class CustomObject {
            private BigDecimal amount;
            private BigDecimal rate;

            public CustomObject(BigDecimal amount, BigDecimal rate) {
                this.amount = amount;
                this.rate = rate;
            }

            public BigDecimal getAmount() {
                return amount;
            }

            public BigDecimal getRate() {
                return rate;
            }

            @Override
            public String toString() {
                return "CustomObject{" +
                        "amount=" + amount +
                        ", rate=" + rate +
                        '}';
            }
        }

        // Creating a list of CustomObjects
        List<CustomObject> customObjects = Arrays.asList(
                new CustomObject(null, new BigDecimal("5.0")),
                new CustomObject(new BigDecimal("1000.0"), new BigDecimal("4.0")),
                new CustomObject(new BigDecimal("20.0"), new BigDecimal("1.0")),
                new CustomObject(new BigDecimal("30.0"), new BigDecimal("2.0")),
                new CustomObject(null, new BigDecimal("4.0"))
        );

        // Custom comparator for sorting based on rate and then amount
        Comparator<CustomObject> customComparator = Comparator
                .comparing(CustomObject::getRate, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(CustomObject::getAmount, Comparator.nullsFirst(Comparator.naturalOrder()));

        // Creating a custom TreeSet with a custom comparator and custom equality check
        TreeSet<CustomObject> customObjectTreeSet = new TreeSet<>(new TreeSetComparator<>(customComparator));

        // Adding all elements from the list to the TreeSet
        customObjectTreeSet.addAll(customObjects);

        // Printing the sorted list and TreeSet
        System.out.println("Sorted List: " + customObjects);
        System.out.println("TreeSet: " + customObjectTreeSet);
    }

    // Custom TreeSetComparator to handle equality for objects with null fields
    static class TreeSetComparator<T> implements Comparator<T> {
        private final Comparator<T> comparator;

        TreeSetComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(T o1, T o2) {
            int result = comparator.compare(o1, o2);
            if (result == 0 && !Objects.equals(o1, o2)) {
                // Custom equality check for null fields
                return 1;
            }
            return result;
        }
    }
}
