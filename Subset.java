import java.math.BigDecimal;

public class MyObject implements Comparable<MyObject> {

    private BigDecimal field1;
    private BigDecimal field2;

    // Constructors, getters, setters, etc.

    @Override
    public int compareTo(MyObject other) {
        // Compare field1 first
        int field1Comparison = this.field1.compareTo(other.field1);

        if (field1Comparison != 0) {
            return field1Comparison;
        }

        // If field1 is equal, compare field2
        return this.field2.compareTo(other.field2);
    }

    // Other methods and fields
}
