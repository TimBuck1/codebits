import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RandomBigDecimalGenerator {
    public static void main(String[] args) {
        BigDecimal minValue = new BigDecimal("10.0");
        BigDecimal maxValue = new BigDecimal("20.0");

        BigDecimal randomBigDecimal = getRandomBigDecimalInRange(minValue, maxValue);
        System.out.println("Random BigDecimal: " + randomBigDecimal);
    }

    private static BigDecimal getRandomBigDecimalInRange(BigDecimal minValue, BigDecimal maxValue) {
        if (minValue.compareTo(maxValue) >= 0) {
            throw new IllegalArgumentException("Invalid range: minValue must be less than maxValue");
        }

        Random random = new Random();
        BigDecimal randomBigDecimal = maxValue.subtract(minValue).multiply(new BigDecimal(random.nextDouble()))
                .add(minValue);

        // Optionally, you can round the result based on your requirements
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }
}
