package com.example.apimonitor.Controller;


import com.example.apimonitor.Service.CheckService;
import com.example.apimonitor.Dtos.Checkers.CheckRequest;
import com.example.apimonitor.Dtos.Checkers.CheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/check")
@RequiredArgsConstructor
public class ApicheckController {

    private final CheckService checkService;

    @PostMapping
    public ResponseEntity<CheckResponse> apichecker(@Valid @RequestBody CheckRequest request){

        CheckResponse response = checkService.checkApi(request);
        return ResponseEntity.ok(response);
    }


}
