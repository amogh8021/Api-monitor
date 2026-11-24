package com.example.apimonitor.Dtos.Checkers;

import jakarta.validation.constraints.NotBlank;

public record CheckRequest(
        @NotBlank(message = "url can't be empty") String url) {

}
