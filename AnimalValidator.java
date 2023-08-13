package dependency;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnimalValidator {
    private static final Map<String, Set<String>> ANIMAL_DEPENDENCIES = new HashMap<>();

    static {
        ANIMAL_DEPENDENCIES.put("Leopard", Set.of("Tiger", "Lion"));
        ANIMAL_DEPENDENCIES.put("Tiger", Set.of());
        ANIMAL_DEPENDENCIES.put("Lion", Set.of());
        // ... add more dependencies as needed
    }

    public static boolean isValid(Set<String> selectedAnimals) {
        Set<String> animalsWithDependencies = new HashSet<>();
        Set<String> animalsWithNoDependencies = new HashSet<>();

        for (String selected : selectedAnimals) {
            if (ANIMAL_DEPENDENCIES.containsKey(selected)) {
                animalsWithDependencies.add(selected);
                animalsWithDependencies.addAll(ANIMAL_DEPENDENCIES.get(selected));
            } else {
                animalsWithNoDependencies.add(selected);
            }
        }

        // Check if animals with dependencies have only one dependency selected
        for (String animal : animalsWithDependencies) {
            Set<String> dependencies = ANIMAL_DEPENDENCIES.get(animal);
            if (dependencies.size() > 0) {
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
        Set<String> selectedAnimals = Set.of("Leopard", "Lion");

        if (isValid(selectedAnimals)) {
            System.out.println("Selection is valid.");
        } else {
            System.out.println("Invalid selection.");
        }
    }
}
