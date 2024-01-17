import java.time.LocalDate;
import java.util.Random;

public class RandomLocalDateGenerator {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        int yearRange = 5; // Change this to your desired year range

        LocalDate randomDate = getRandomLocalDateAfterToday(today, yearRange);
        System.out.println("Random LocalDate: " + randomDate);
    }

    private static LocalDate getRandomLocalDateAfterToday(LocalDate today, int yearRange) {
        Random random = new Random();
        int randomYear = today.getYear() + random.nextInt(yearRange + 1);
        int randomMonth = random.nextInt(12) + 1; // Month ranges from 1 to 12
        int randomDay = random.nextInt(today.lengthOfMonth()) + 1; // Day ranges from 1 to the length of the month

        return LocalDate.of(randomYear, randomMonth, randomDay);
    }
}
