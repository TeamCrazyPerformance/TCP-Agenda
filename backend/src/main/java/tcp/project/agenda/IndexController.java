package tcp.project.agenda;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class IndexController {

    @GetMapping("/health-check")
    private ResponseEntity<String> index() {
        return ResponseEntity.ok(LocalDateTime.now().toString());
    }
}
