package com.project.FinnC.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
                        @NotBlank(message = "Email é obrigatório")
                        @Email(message = "Email inválido")
                        String email,
                        @NotBlank(message = "nome é obrigatório")
                        String name,
                        @NotBlank(message = "Senha é obrigatória")
                        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
                        String password
) {
}
