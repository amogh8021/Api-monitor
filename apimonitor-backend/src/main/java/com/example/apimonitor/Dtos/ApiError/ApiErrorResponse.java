package com.example.apimonitor.Dtos.ApiError;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ApiErrorResponse( int status ,
                                String msg ,
                                Instant timestamp,
                                String path) {

}
