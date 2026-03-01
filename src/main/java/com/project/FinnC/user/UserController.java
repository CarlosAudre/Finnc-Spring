package com.project.FinnC.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class UserController {
    
    @GetMapping
    public ResponseEntity<UserDTO> getUserName(@AuthenticationPrincipal User user){ //With @AuthenticationPrincipal i can get the logged-in user
        return ResponseEntity.ok(
                new UserDTO(user.getName())
        );
    }
}
