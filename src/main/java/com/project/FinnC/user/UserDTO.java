package com.project.FinnC.user;

import java.time.LocalDateTime;

public record UserDTO(String name, String email, String imgUrl, LocalDateTime createdAt) {
}
