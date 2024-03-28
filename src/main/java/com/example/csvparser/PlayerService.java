package com.example.csvparser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.StringJoiner;

@Service
@EnableScheduling
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    public PlayerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public ResponseEntity<String> getPlayerInfo() {
        try {
            StringJoiner csvContent = new StringJoiner("\n");
            csvContent.add("id,first name,last name,nickname");

            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/players.csv"))) {
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        int playerId = Integer.parseInt(parts[0]);
                        String nickname = parts[1];

                        Optional<Player> playerOptional = playerRepository.findById(playerId);
                        if (playerOptional.isPresent()) {
                            Player player = playerOptional.get();
                            csvContent.add(playerId + "," + player.getFirstName() + "," + player.getLastName() + "," + nickname);
                        } else {
                            updatePlayerDataFromAPI(playerId, nickname);

                            Optional<Player> updatedPlayerOptional = playerRepository.findById(playerId);
                            if (updatedPlayerOptional.isPresent()) {
                                Player updatedPlayer = updatedPlayerOptional.get();
                                csvContent.add(playerId + "," + updatedPlayer.getFirstName() + "," + updatedPlayer.getLastName() + "," + nickname);
                            }
                            notifyPlayerUpdate(updatedPlayerOptional.get());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
            }

            return ResponseEntity.ok(csvContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }

    @Scheduled(fixedRate = 900000) // 15 minutes = 15 * 60 * 1000 milliseconds
    public void updatePlayerData() {
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/players.csv"))) {
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        int playerId = Integer.parseInt(parts[0]);
                        String nickname = parts[1];
                        updatePlayerDataFromAPI(playerId, nickname);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyPlayerUpdate(Player player) {
        messagingTemplate.convertAndSend("/topic/playerUpdate", player);
    }

    private void updatePlayerDataFromAPI(int playerId, String nickname) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.balldontlie.io/v1/players/" + playerId))
                    .GET()
                    .setHeader("Authorization", "9fac760a-5d40-4d4f-8795-8e031c4f79fd")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                String firstName = "";
                String lastName = "";
                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    firstName = responseBody.contains("first_name") ? responseBody.split("\"first_name\":\"")[1].split("\"")[0] : "";
                    lastName = responseBody.contains("last_name") ? responseBody.split("\"last_name\":\"")[1].split("\"")[0] : "";
                }

                Player player = new Player();
                player.setId(playerId);
                player.setFirstName(firstName);
                player.setLastName(lastName);
                player.setNickname(nickname);
                playerRepository.save(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
