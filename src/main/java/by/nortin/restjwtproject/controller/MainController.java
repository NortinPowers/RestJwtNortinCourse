package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.Constants.SECURITY_SWAGGER;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
@SecurityRequirement(name = SECURITY_SWAGGER)
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
