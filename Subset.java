import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DataObject {
    private String value;
    private Integer quantity;
    private Integer rate;
    private List<DataObject> children;

    public DataObject(String value, Integer quantity, Integer rate) {
        this.value = value;
        this.quantity = quantity;
        this.rate = rate;
        this.children = new ArrayList<>();
    }

    public String getValue() {
        return value;
    }

    public List<DataObject> getChildren() {
        return children;
    }

    public void addChild(DataObject child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "value='" + value + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", children=" + children +
                '}';
    }
}

public class ParentChildExample {
    public static void main(String[] args) {
        List<Object[]> rawData = List.of(
                new Object[]{10000, 4, 4},
                new Object[]{1000, 20, 1},
                new Object[]{10000, 50, 5},
                new Object[]{1000, 30, 2},
                new Object[]{1000, null, 3},
                new Object[]{10000, 60, 6},
                new Object[]{10000, null, 7},
                new Object[]{null, null, 15}
        );

        List<DataObject> result = organizeData(rawData);

        for (DataObject dataObject : result) {
            System.out.println(dataObject);
        }
    }

    private static List<DataObject> organizeData(List<Object[]> rawData) {
        List<DataObject> result = new ArrayList<>();
        Map<String, DataObject> valueToParentMap = new HashMap<>();

        for (Object[] row : rawData) {
            String value = (row[0] != null) ? String.valueOf(row[0]) : null;
            Integer quantity = (row.length > 1 && row[1] != null) ? (Integer) row[1] : null;
            Integer rate = (row.length > 2) ? (Integer) row[2] : null;

            if (quantity == null && rate == null) {
                // This row has only the first and third fields populated, so it's a new parent
                DataObject parent = new DataObject(value, null, rate);
                valueToParentMap.put(value, parent);
                result.add(parent);
            } else if (value != null) {
                // This row has the first field populated, it's a child of the corresponding parent
                DataObject parent = valueToParentMap.get(value);

                if (parent != null) {
                    DataObject child = new DataObject(value, quantity, rate);
                    parent.addChild(child);
                }
            }
        }

        return result;
    }
}
