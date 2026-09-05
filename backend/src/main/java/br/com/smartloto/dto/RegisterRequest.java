package br.com.smartloto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message="Informe seu nome.")
        @Size(max=120, message="O nome deve ter no máximo 120 caracteres.")
        String name,

        @NotBlank(message="Informe o e-mail.")
        @Email(message="Informe um e-mail válido.")
        String email,

        @NotBlank(message="Informe a senha.")
        @Size(min=8, max=100, message="A senha deve ter entre 8 e 100 caracteres.")
        String password
) {}
