package map;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GroupListToMap {

    public static void main(String[] args) {
        List<YourObject> objects = getYourObjects(); // Replace with your actual list of objects

        Map<Long, List<YourObject>> maxUpperBandByGroup = objects.stream()
                .collect(Collectors.groupingBy(
                        YourObject::getGroupId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(YourObject::getCost, Comparator.nullsFirst(Comparator.naturalOrder())))
                                        .collect(Collectors.toList())
                        )
                ));

        // Print the result map
        maxUpperBandByGroup.forEach((groupId, objectsWithSortedUpperBand) ->
                System.out.println("Group: " + groupId + ", Sorted cost Objects: " + objectsWithSortedUpperBand)
        );
    }

    // Other methods...

    // Replace this method with your actual list of objects
    private static List<YourObject> getYourObjects() {
        // Implementation to create and return a list of YourObject instances
        return Arrays.asList(
                new YourObject(1L, null),
                new YourObject(1L, BigDecimal.valueOf(15.5)),
                new YourObject(1L, BigDecimal.valueOf(15.2)),
                new YourObject(2L, BigDecimal.valueOf(12.8)),
                new YourObject(2L, null),
                new YourObject(3L, BigDecimal.valueOf(8.0)),
                new YourObject(3L, BigDecimal.valueOf(9.5)),
                new YourObject(3L, null)
        );
    }

    static class YourObject {
        private final Long groupId;
        private final BigDecimal cost;

        public YourObject(Long groupId, BigDecimal upperBand) {
            this.groupId = groupId;
            this.cost = upperBand;
        }

        public Long getGroupId() {
            return groupId;
        }

        public BigDecimal getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return "YourObject{" +
                    "groupId=" + groupId +
                    ", cost=" + cost +
                    '}';
        }
    }
}
