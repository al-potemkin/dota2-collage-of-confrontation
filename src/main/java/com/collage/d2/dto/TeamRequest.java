package com.collage.d2.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeamRequest {

    private String team;
    private List<String> characters;
}