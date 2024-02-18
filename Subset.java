import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class UpperBandMapExample {

    public static void main(String[] args) {
        Map<Long, List<YourObject>> originalMap = getOriginalMap(); // Replace with your actual map

        Map<BigDecimal, List<YourObject>> result = originalMap.values().stream()
                .filter(list -> list.size() == 1)
                .map(list -> list.get(0))
                .collect(Collectors.toMap(
                        obj -> obj.getUpperBand() != null ? obj.getUpperBand() : BigDecimal.ZERO, // Default to BigDecimal.ZERO if null
                        Collections::singletonList,
                        // No merge function needed, as lists with a size of 1 won't have duplicates
                        LinkedHashMap::new // Maintain insertion order
                ));

        // Print the result map
        result.forEach((upperBand, objectsList) ->
                System.out.println("UpperBand: " + upperBand + ", Objects: " + objectsList)
        );
    }

    // Other methods...

    // Replace this method with your actual data source
    private static Map<Long, List<YourObject>> getOriginalMap() {
        // Implementation to create and return a map of Long to List<YourObject>
        return Map.of(
                1L, List.of(new YourObject(BigDecimal.valueOf(10.5))),
                2L, List.of(new YourObject(BigDecimal.valueOf(15.2))),
                3L, List.of(new YourObject((BigDecimal) null)),
                4L, List.of(new YourObject(BigDecimal.valueOf(12.8))),
                5L, List.of(new YourObject((BigDecimal) null))
        );
    }

    static class YourObject {
        private final BigDecimal upperBand;

        public YourObject(BigDecimal upperBand) {
            this.upperBand = upperBand;
        }

        public BigDecimal getUpperBand() {
            return upperBand;
        }
    }
}
