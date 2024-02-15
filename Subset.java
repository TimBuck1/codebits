import java.util.Comparator;
import java.util.Objects;
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
            // Custom comparison logic
            return Long.compare(o1.getValue(), o2.getValue());
        }

        @Override
        public boolean equals(Object obj) {
            // Custom equality check to treat Long.MAX_VALUE as distinct
            return this == obj || obj != null && getClass() == obj.getClass();
        }
    }
}
