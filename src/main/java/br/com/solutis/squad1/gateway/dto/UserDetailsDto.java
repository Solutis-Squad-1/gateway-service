package br.com.solutis.squad1.gateway.dto;

/**
 * DTO para representar os dados do usuário logado.
 */
public record UserDetailsDto(
        String username,
        String authorities
) {
}
