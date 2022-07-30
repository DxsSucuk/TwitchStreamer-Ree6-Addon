package de.presti.ree6.twitchredemption.utils;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TTSUtil {

    public static byte[] createTTS(String text) {
        String apiKey = "";
        try {
            apiKey =  Files.readString(Path.of("twitchRedemption/","voicerss.creds"));
        } catch (Exception ignore) {
            return new byte[512];
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.voicerss.org/?key=" + apiKey + "&hl=en-us&src=" + URLEncoder.encode(text, StandardCharsets.UTF_8)))
                .header("content-type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("src=Hello%2C%20world!&hl=en-us&r=0&c=mp3&f=8khz_8bit_mono"))
                .build();
        try (InputStream inputStream = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream()).body()) {
            return inputStream.readAllBytes();
        } catch (Exception ignore) {}

        return new byte[512];
    }
}
