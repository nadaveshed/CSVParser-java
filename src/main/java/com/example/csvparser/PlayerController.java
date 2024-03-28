package com.example.csvparser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/player")
    public ResponseEntity<String> getPlayerInfo() {
        return playerService.getPlayerInfo();
    }
}
