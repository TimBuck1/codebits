import java.math.BigDecimal;
import java.util.TreeSet;

class MyObject implements Comparable<MyObject> {
    private BigDecimal field1;
    private BigDecimal field2;

    public MyObject(BigDecimal field1, BigDecimal field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public int compareTo(MyObject other) {
        // Compare field1 first
        int field1Comparison = this.field1.compareTo(other.field1);

        if (field1Comparison != 0) {
            return field1Comparison;
        }

        // If field1 is equal, compare field2
        int field2Comparison = this.field2.compareTo(other.field2);

        // Ensure that even if field2 is equal, the result is non-zero
        return field2Comparison == 0 ? 1 : field2Comparison;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "field1=" + field1 +
                ", field2=" + field2 +
                '}';
    }
}

public class TreeSetExample {
    public static void main(String[] args) {
        TreeSet<MyObject> myObjects = new TreeSet<>();

        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("20")));
        myObjects.add(new MyObject(new BigDecimal("5"), new BigDecimal("30")));
        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("10")));

        System.out.println("Sorted Set: " + myObjects);
    }
}
import java.math.BigDecimal;
import java.util.TreeSet;

class MyObject implements Comparable<MyObject> {
    private BigDecimal field1;
    private BigDecimal field2;

    public MyObject(BigDecimal field1, BigDecimal field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public int compareTo(MyObject other) {
        // Compare field1 first
        int field1Comparison = this.field1.compareTo(other.field1);

        if (field1Comparison != 0) {
            return field1Comparison;
        }

        // If field1 is equal, compare field2
        int field2Comparison = this.field2.compareTo(other.field2);

        // Ensure that even if field2 is equal, the result is non-zero
        return field2Comparison == 0 ? 1 : field2Comparison;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "field1=" + field1 +
                ", field2=" + field2 +
                '}';
    }
}

public class TreeSetExample {
    public static void main(String[] args) {
        TreeSet<MyObject> myObjects = new TreeSet<>();

        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("20")));
        myObjects.add(new MyObject(new BigDecimal("5"), new BigDecimal("30")));
        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("10")));

        System.out.println("Sorted Set: " + myObjects);
    }
}
import java.math.BigDecimal;
import java.util.TreeSet;

class MyObject implements Comparable<MyObject> {
    private BigDecimal field1;
    private BigDecimal field2;

    public MyObject(BigDecimal field1, BigDecimal field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public int compareTo(MyObject other) {
        // Compare field1 first
        int field1Comparison = this.field1.compareTo(other.field1);

        if (field1Comparison != 0) {
            return field1Comparison;
        }

        // If field1 is equal, compare field2
        int field2Comparison = this.field2.compareTo(other.field2);

        // Ensure that even if field2 is equal, the result is non-zero
        return field2Comparison == 0 ? 1 : field2Comparison;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "field1=" + field1 +
                ", field2=" + field2 +
                '}';
    }
}

public class TreeSetExample {
    public static void main(String[] args) {
        TreeSet<MyObject> myObjects = new TreeSet<>();

        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("20")));
        myObjects.add(new MyObject(new BigDecimal("5"), new BigDecimal("30")));
        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("10")));

        System.out.println("Sorted Set: " + myObjects);
    }
}

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeSet;

class MyObject {
    private BigDecimal field1;
    private BigDecimal field2;
    private String type;

    public MyObject(BigDecimal field1, BigDecimal field2, String type) {
        this.field1 = field1;
        this.field2 = field2;
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "field1=" + field1 +
                ", field2=" + field2 +
                ", type='" + type + '\'' +
                '}';
    }
}

public class TreeSetExample {
    public static void main(String[] args) {
        TreeSet<MyObject> myObjects = new TreeSet<>(new MyObjectComparator());

        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("20"), "ALPHA"));
        myObjects.add(new MyObject(new BigDecimal("5"), new BigDecimal("30"), "BETA"));
        myObjects.add(new MyObject(new BigDecimal("10"), new BigDecimal("10"), "ALPHA"));

        System.out.println("Sorted Set: " + myObjects);
    }
}

class MyObjectComparator implements Comparator<MyObject> {
    @Override
    public int compare(MyObject obj1, MyObject obj2) {
        if ("ALPHA".equals(obj1.getType()) && "ALPHA".equals(obj2.getType())) {
            // Custom comparison logic for ALPHA type
            int field1Comparison = obj1.getField1().compareTo(obj2.getField1());
            if (field1Comparison != 0) {
                return field1Comparison;
            }

            return obj1.getField2().compareTo(obj2.getField2());
        }

        // For non-ALPHA types or mixed types, use default comparison
        return obj1.getType().compareTo(obj2.getType());
    }
}
