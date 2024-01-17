import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/example")
    public String exampleWithPathVariable(
            @RequestParam(name = "id", required = false, defaultValue = "defaultId") String id) {
        // Your logic here using the "id" variable
        return "Received id: " + id;
    }
}
