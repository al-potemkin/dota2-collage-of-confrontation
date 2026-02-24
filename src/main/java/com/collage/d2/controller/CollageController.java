package com.collage.d2.controller;

import com.collage.d2.dto.TeamRequest;
import com.collage.d2.service.CollageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collage")
public class CollageController {

    private final CollageService collageService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "image/png")
    public byte[] generateCollage(@RequestBody @Valid List<TeamRequest> teams) {
        log.info("Received collage request for {} teams", teams.size());

        if (teams.size() != 2) {
            throw new IllegalArgumentException("Exactly 2 teams are required, got: " + teams.size());
        }

        byte[] result = collageService.generate(teams);
        log.info("Collage generated successfully, size={} bytes", result.length);
        return result;
    }
}