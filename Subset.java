import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MyService {

    private AtomicBoolean methodCalled = new AtomicBoolean(false);

    public String restrictedMethod() {
        if (methodCalled.compareAndSet(false, true)) {
            // Your logic for the method goes here
            return "Method called successfully.";
        } else {
            return "Method has already been called and cannot be called again.";
        }
    }
}
