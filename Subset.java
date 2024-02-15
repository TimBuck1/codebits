import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class CustomObjectExample {
    public static void main(String[] args) {
        // Sample class for demonstration
        class CustomObject {
            private Long value;

            public CustomObject(Long value) {
                this.value = value;
            }

            public Long getValue() {
                return value;
            }

            @Override
            public String toString() {
                return "CustomObject{" +
                        "value=" + value +
                        '}';
            }
        }

        // Creating a set of CustomObjects
        Set<CustomObject> customObjects = new TreeSet<>(new TreeSetComparator());

        // Adding objects with Long.MAX_VALUE
        customObjects.add(new CustomObject(42L));
        customObjects.add(new CustomObject(Long.MAX_VALUE));
        customObjects.add(new CustomObject(123L));
        customObjects.add(new CustomObject(Long.MAX_VALUE));

        // Printing the set
        System.out.println("Set of CustomObjects: " + customObjects);
    }

    // Custom comparator for sorting based on Long values
    static class TreeSetComparator implements Comparator<CustomObject> {
        @Override
        public int compare(CustomObject o1, CustomObject o2) {
            if (o1.getValue().equals(Long.MAX_VALUE)) {
                return 1; // o1 comes after o2
            } else if (o2.getValue().equals(Long.MAX_VALUE)) {
                return -1; // o1 comes before o2
            } else {
                return o1.getValue().compareTo(o2.getValue());
            }
        }

        @Override
        public boolean equals(Object obj) {
            // Always treat objects as distinct
            return false;
        }
    }
}
