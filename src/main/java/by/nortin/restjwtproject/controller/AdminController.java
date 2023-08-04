package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.Constants.USER;
import static by.nortin.restjwtproject.utils.ResponseUtils.CHANGE_ROLE_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getSuccessResponse;

import by.nortin.restjwtproject.dto.BaseResponse;
import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.service.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/set/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> setAdmin(@PathVariable("id") Long id) {
        adminService.setAdmin(id);
        return ResponseEntity.ok(getSuccessResponse(CHANGE_ROLE_MESSAGE, USER));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}
