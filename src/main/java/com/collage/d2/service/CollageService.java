package com.collage.d2.service;

import com.collage.d2.dto.TeamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollageService {

    private static final int REQUIRED_TEAM_SIZE = 5;

    private final ImageLoader imageLoader;
    private final CollageComposer collageComposer;

    public byte[] generate(List<TeamRequest> teams) {
        log.info("Received {} team(s) in request", teams == null ? 0 : teams.size());

        List<BufferedImage> direImages = resolveTeam(teams, "Dire");
        List<BufferedImage> radiantImages = resolveTeam(teams, "Radiant");
        BufferedImage mask = imageLoader.loadMask();

        return collageComposer.compose(direImages, radiantImages, mask);
    }

    public String generateInBase64(List<TeamRequest> teams) {
        byte[] result = generate(teams);
        log.info("Collage generated successfully, size={} bytes", result.length);
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * Resolves a team row by team name.
     * Falls back to default row when:
     * - teams list is null or empty
     * - no entry matches the team name
     * - matched entry has null/blank team field
     * - matched entry has null or empty characters list
     */
    private List<BufferedImage> resolveTeam(List<TeamRequest> teams, String teamName) {
        Optional<TeamRequest> teamOpt = findTeam(teams, teamName);

        if (teamOpt.isEmpty()) {
            log.warn("Team '{}' not found or has no valid 'team' field — filling all {} slots with default", teamName, REQUIRED_TEAM_SIZE);
            return defaultRow();
        }

        List<String> characters = teamOpt.get().getCharacters();

        if (characters == null || characters.isEmpty()) {
            log.warn("Team '{}' has null or empty 'characters' list — filling all {} slots with default", teamName, REQUIRED_TEAM_SIZE);
            return defaultRow();
        }

        log.info("Resolving team '{}' with {} character(s): {}", teamName, characters.size(), characters);
        return loadWithFallback(teamName, characters);
    }

    /**
     * Finds a TeamRequest by team name.
     * Entries with null/blank team field are ignored.
     */
    private Optional<TeamRequest> findTeam(List<TeamRequest> teams, String teamName) {
        if (teams == null || teams.isEmpty()) {
            return Optional.empty();
        }
        return teams.stream()
                .filter(t -> t.getTeam() != null && !t.getTeam().isBlank())
                .filter(t -> teamName.equalsIgnoreCase(t.getTeam()))
                .findFirst();
    }

    /**
     * Loads images for given character names.
     * - Stops after REQUIRED_TEAM_SIZE slots are filled.
     * - Missing/unknown characters fall back to default image (via ImageLoader).
     * - Pads with default if fewer than REQUIRED_TEAM_SIZE characters provided.
     */
    private List<BufferedImage> loadWithFallback(String teamName, List<String> characters) {
        List<BufferedImage> images = new ArrayList<>();

        for (String name : characters) {
            if (images.size() == REQUIRED_TEAM_SIZE) {
                log.warn("Team '{}' exceeds {} characters — ignoring the rest", teamName, REQUIRED_TEAM_SIZE);
                break;
            }
            images.add(imageLoader.loadCharacter(name));
        }

        int missing = REQUIRED_TEAM_SIZE - images.size();
        if (missing > 0) {
            log.warn("Team '{}' has only {} slot(s) filled — padding {} slot(s) with default", teamName, images.size(), missing);
            BufferedImage defaultImage = imageLoader.loadDefault();
            for (int i = 0; i < missing; i++) {
                images.add(defaultImage);
            }
        }

        return images;
    }

    private List<BufferedImage> defaultRow() {
        BufferedImage defaultImage = imageLoader.loadDefault();
        List<BufferedImage> images = new ArrayList<>();
        for (int i = 0; i < REQUIRED_TEAM_SIZE; i++) {
            images.add(defaultImage);
        }
        return images;
    }
}