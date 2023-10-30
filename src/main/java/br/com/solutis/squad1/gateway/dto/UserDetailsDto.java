package br.com.solutis.squad1.gateway.dto;

public record UserDetailsDto(
        Long id,
        String username,
        String role,
        String authorities
) {
}
