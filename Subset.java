Enter file contents here
  public class SubsetGenerator {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5};
        generateSubsets(numbers);
    }

    private static void generateSubsets(int[] numbers) {
        int n = numbers.length;

        // The total number of subsets is 2^n
        int totalSubsets = 1 << n;

        for (int subsetMask = 0; subsetMask < totalSubsets; subsetMask++) {
            System.out.print("Subset " + (subsetMask + 1) + ": {");

            // Iterate through each bit in the subsetMask
            for (int i = 0; i < n; i++) {
                if ((subsetMask & (1 << i)) != 0) {
                    // If the i-th bit is set, include the corresponding number in the subset
                    System.out.print(numbers[i] + " ");
                }
            }

            System.out.println("}");
        }
    }
}
import java.util.*;

public class RandomMapEntryPicker {
    public static void main(String[] args) {
        Map<String, String> animalCategoryMap = new HashMap<>();
        animalCategoryMap.put("Cat", "Mammal");
        animalCategoryMap.put("Dog", "Mammal");
        animalCategoryMap.put("Fish", "Aquatic");
        animalCategoryMap.put("Bird", "Avian");

        Map.Entry<String, String> randomEntry = getRandomEntry(animalCategoryMap);

        if (randomEntry != null) {
            System.out.println("Random Entry: " + randomEntry.getKey() + " -> " + randomEntry.getValue());
        } else {
            System.out.println("The map is empty");
        }
    }

    private static <K, V> Map.Entry<K, V> getRandomEntry(Map<K, V> map) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());
        if (!entryList.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(entryList.size());
            return entryList.get(randomIndex);
        }
        return null;
    }
}
