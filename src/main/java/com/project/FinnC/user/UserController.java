package com.project.FinnC.user;

import com.project.FinnC.infra_security.MessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<UserDTO> getUserName(@AuthenticationPrincipal User user){ //With @AuthenticationPrincipal i can get the logged-in user
        return ResponseEntity.ok(
                new UserDTO(user.getName(), user.getEmail(), user.getImgUrl())
        );
    }

    @PutMapping("/name")
    public ResponseEntity<UserNameDto> changeName(
            @RequestBody UserNameDto dto,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(userService.changeUserName(dto, user));
    }

    @PutMapping("/email")
    public ResponseEntity<EmailResponseDto> changeEmail(
            @RequestBody UserEmailDto dto,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(userService.changeUserEmail(dto, user));
    }

    @PutMapping("/password")
    public ResponseEntity<MessageResponseDto> changePassword(
            @RequestBody UserPasswordDto dto,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(userService.changeUserPassword(dto, user));
    }

    @PutMapping("/photo")
    public ResponseEntity<UserPhotoDto> changePhoto(
            @RequestBody UserPhotoDto dto,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(userService.changeUserPhoto(dto, user));
    }
}
