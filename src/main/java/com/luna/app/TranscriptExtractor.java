package com.luna.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TranscriptExtractor {

    public static String extractTranscript(String jsonFilePath) {
        StringBuilder fullTranscript = new StringBuilder();

        try {
            // Load the JSON file from the local file system
            File jsonFile = new File(jsonFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Navigate to the "transcripts" node
            JsonNode transcriptsNode = rootNode.path("results").path("transcripts");

            if (transcriptsNode.isArray()) {
                // Loop through each transcript in the array and append it to the full transcript
                for (JsonNode transcriptNode : transcriptsNode) {
                    String transcriptText = transcriptNode.path("transcript").asText();
                    fullTranscript.append(transcriptText).append(" ");  // Append a space or newline between transcripts
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading or parsing JSON file: " + e.getMessage());
        }
        return fullTranscript.toString().trim();  // Return the full transcript as a single string
    }

    public static void writeTranscriptToFile(String transcript, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(transcript);
        } catch (IOException e) {
            System.err.println("Error writing transcript to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String text = extractTranscript("C:/Users/farin/IdeaProjects/polly/src/main/resources/french-meeting.json");
        writeTranscriptToFile(text, "C:/polaris/scor/french-meeting.txt");
    }
}
