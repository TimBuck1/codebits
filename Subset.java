import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class YourObject {
    private Long id;
    private BigDecimal upperBand;

    // Constructors, getters, setters, etc.

    public static void main(String[] args) {
        List<YourObject> yourObjects = getListOfYourObjects(); // Replace with your actual list

        Map<Long, List<YourObject>> groupedAndSortedMap = yourObjects.stream()
                .collect(Collectors.groupingBy(
                        YourObject::getId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(YourObject::getUpperBand)),
                                optional -> optional.map(Collections::singletonList).orElse(Collections.emptyList())
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(entry -> entry.getValue().get(0).getUpperBand())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
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
