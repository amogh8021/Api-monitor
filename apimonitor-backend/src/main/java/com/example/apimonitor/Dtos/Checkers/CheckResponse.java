package com.example.apimonitor.Dtos.Checkers;

public record CheckResponse(
        String url ,
        Integer statusCode,
        long responseTimems,
        boolean success,
        String failureType

){
}
