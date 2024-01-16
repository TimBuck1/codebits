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
