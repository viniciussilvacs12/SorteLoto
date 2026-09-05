package br.com.smartloto.dto;
public record ProfileResponse(Long id, String name, String email, String role, long savedGames, long favorites) {}
