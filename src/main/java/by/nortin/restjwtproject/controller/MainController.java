package by.nortin.restjwtproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainController {

    @GetMapping("/unsecurity")
    public String getUnsecurityPage() {
        return "unsecurity page";
    }

    @GetMapping("/security")
    public String getSecurityPage() {
        return "security page";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin page";
    }
}
