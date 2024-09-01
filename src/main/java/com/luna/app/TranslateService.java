package com.luna.app;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TranslateService {

    private static final int MAX_CHUNK_SIZE = 8000; // Maximum size of each chunk in bytes

    public static void translateFileToFile(TranslateClient translateClient, String inputFilePath, String sourceLangCode, String targetLangCode, String outputFilePath) {
        StringBuilder textToTranslate = new StringBuilder();

        try {
            // Read the text from the input file
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    textToTranslate.append(line).append("\n"); // Append a newline after each line of text
                }
            }

            // Split the text into chunks
            List<String> chunks = splitText(textToTranslate.toString(), MAX_CHUNK_SIZE);

            // Translate each chunk and collect results
            StringBuilder translatedText = new StringBuilder();
            for (String chunk : chunks) {
                TranslateTextRequest request = TranslateTextRequest.builder()
                        .sourceLanguageCode(sourceLangCode)
                        .targetLanguageCode(targetLangCode)
                        .text(chunk)
                        .build();

                TranslateTextResponse response = translateClient.translateText(request);
                translatedText.append(response.translatedText()).append("\n"); // Append a newline after each translated chunk
            }

            // Write the translated text to the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                writer.write(translatedText.toString());
            }

        } catch (IOException e) {
            System.err.println("Error reading from or writing to file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error during translation: " + e.getMessage());
            System.exit(1);
        }
    }

    private static List<String> splitText(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + maxChunkSize, length);
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }

    public static void main(String[] args) {
        Region region = Region.EU_WEST_1;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        translateFileToFile(translateClient,
                "C:\\polaris\\scor\\activeMQTXT.txt"
                , "fr"
                , "en"
                , "C:\\polaris\\scor\\activeMQENG.txt");
    }
}
