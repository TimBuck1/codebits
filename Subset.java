import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YourObject {
    private String id;
    private BigDecimal upperBand;

    // Constructors, getters, setters, etc.

    public static void main(String[] args) {
        List<YourObject> yourObjects = getListOfYourObjects(); // Replace with your actual list

        Map<String, List<YourObject>> groupedAndSortedMap = yourObjects.stream()
                .collect(Collectors.groupingBy(
                        YourObject::getId,
                        Collectors.mapping(
                                YourObject::getUpperBand,
                                Collectors.maxBy(Comparator.naturalOrder())
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> yourObjects.stream()
                                .filter(obj -> obj.getId().equals(entry.getKey()))
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Now 'groupedAndSortedMap' contains the grouped and sorted result based on the conditions
    }

    // Other methods...

    // Replace this method with your actual data source
    private static List<YourObject> getListOfYourObjects() {
        // Implementation to create and return a list of YourObject instances
    }
}
