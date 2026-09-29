package com.lostpetfinder.dto;

import java.util.List;

public record MatchResponse(ReportResponse report, int score, List<String> matchedOn) {}
