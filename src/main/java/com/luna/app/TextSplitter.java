package com.luna.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class TextSplitter {
    private static final int MAX_CHUNK_SIZE = 3000;


    public static List<String> textFileToChunks(String filePath ) {

        try {
            String text = textFileToString(filePath);
            return splitText(text);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String textFileToString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    public static List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + MAX_CHUNK_SIZE, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }
}
