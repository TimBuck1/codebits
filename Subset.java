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
