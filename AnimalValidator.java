package dependency;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnimalValidator {
    private static final Map<String, Set<String>> ANIMAL_DEPENDENCIES = new HashMap<>();

    static {
        // Map to store animal dependencies
        ANIMAL_DEPENDENCIES.put("Leopard", Set.of("Tiger", "Lion"));
        ANIMAL_DEPENDENCIES.put("Cat", Set.of("Dog"));
    }
    // Method to validate the selected animals
    public static boolean isValid(Set<String> selectedAnimals) {
        // Set to store animals with dependencies
        Set<String> animalsWithDependencies = new HashSet<>();
        // Set to store animals with no dependencies
        Set<String> animalsWithNoDependencies = new HashSet<>();
        // Iterate through selected animals
        for (String selected : selectedAnimals) {
            // If the selected animal has dependencies
            if (ANIMAL_DEPENDENCIES.containsKey(selected)) {
                animalsWithDependencies.add(selected);
                animalsWithDependencies.addAll(ANIMAL_DEPENDENCIES.get(selected));
            } else {
                // If the selected animal has no dependencies
                animalsWithNoDependencies.add(selected);
            }
        }

        // Check if animals with dependencies have only one dependency selected
        for (String animal : animalsWithDependencies) {
            Set<String> dependencies = ANIMAL_DEPENDENCIES.get(animal);
            if (dependencies!=null && dependencies.size() > 0) {
                long selectedDependencies = selectedAnimals.stream()
                        .filter(dependencies::contains)
                        .count();

                if (selectedDependencies != 1) {
                    return false;
                }
            }
        }

        return animalsWithNoDependencies.size() <= 1;
    }

    public static void main(String[] args) {
       // Set<String> selectedAnimals = Set.of("Leopard", "Tiger", "Lion");
       // Set<String> selectedAnimals = Set.of( "Lion");
      // Set<String> selectedAnimals = Set.of( "Tiger");
      // Set<String> selectedAnimals = Set.of("Leopard", "Tiger");
     //  Set<String> selectedAnimals = Set.of("Leopard", "Lion");
      // Set<String> selectedAnimals = Set.of("Leopard", "Lion","Elephant");
      // Set<String> selectedAnimals = Set.of("Leopard");
       //Set<String> selectedAnimals = Set.of("Cat");
      //   Set<String> selectedAnimals = Set.of("Cat", "Dog");
        Set<String> selectedAnimals = Set.of();
        if (selectedAnimals.size()!=0 && isValid(selectedAnimals)) {
            System.out.println("Selection is valid.");
        } else {
            System.out.println("Invalid selection.");
        }
    }
}
