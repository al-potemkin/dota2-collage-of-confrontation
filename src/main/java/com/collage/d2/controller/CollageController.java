package com.collage.d2.controller;

import com.collage.d2.dto.TeamRequest;
import com.collage.d2.service.CollageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collage")
public class CollageController {

    private final CollageService collageService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateCollage(@RequestBody List<TeamRequest> teams) {
        log.info("Received collage request for {} teams", teams.size());
        return collageService.generate(teams);
    }

    @PostMapping(path = "/base64", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateCollageInBase64(@RequestBody List<TeamRequest> teams) {
        log.info("Received collage request for {} teams in base64", teams.size());
        return collageService.generateInBase64(teams);
    }
}