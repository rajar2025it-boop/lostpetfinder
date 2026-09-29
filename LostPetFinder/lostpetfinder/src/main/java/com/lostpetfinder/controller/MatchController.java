package com.lostpetfinder.controller;

import com.lostpetfinder.dto.MatchResponse;
import com.lostpetfinder.service.MatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService service;

    public MatchController(MatchService service) { this.service = service; }

    @GetMapping("/lost/{lostId}")
    public List<MatchResponse> forLost(@PathVariable Long lostId) { return service.suggestForLost(lostId); }

    @GetMapping("/found/{foundId}")
    public List<MatchResponse> forFound(@PathVariable Long foundId) { return service.suggestForFound(foundId); }
}
