import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateConversionExample {

    public static void main(String[] args) {
        String dateString = "2024-01-17T18:30:00.000Z";

        // Parse the input string into Instant
        Instant instant = Instant.parse(dateString);

        // Convert Instant to LocalDate
        LocalDate localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate();

        // Print the result
        System.out.println(localDate); // Outputs: 2024-01-18
    }
}
