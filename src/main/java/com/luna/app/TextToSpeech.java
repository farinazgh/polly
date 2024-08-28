package com.luna.app;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class TextToSpeech {

    public static void main(String args[]) {
        int counter = 0;
        List<String> chunks = TextSplitter.textFileToChunks("C:\\Users\\farin\\IdeaProjects\\polly\\src\\main\\resources\\micro.txt");
        PollyClient polly = PollyClient.builder().region(Region.EU_WEST_1).build();

        for (String chuck : chunks) {
            talkPolly(polly, chuck, String.valueOf(counter) + ".mp3");
            counter++;
        }

        polly.close();
    }

    public static void talkPolly(PollyClient polly, String SAMPLE, String OUTPUT_FILE) {
        try {
            DescribeVoicesRequest describeVoiceRequest = DescribeVoicesRequest.builder().engine("neural").build();

            DescribeVoicesResponse describeVoicesResult = polly.describeVoices(describeVoiceRequest);
            Voice voice = describeVoicesResult
                    .voices()
                    .stream()
                    .filter(v -> v.name().equals("Joanna"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Voice not found"));
            InputStream stream = synthesize(polly, SAMPLE, voice, OutputFormat.MP3);

            try (FileOutputStream out = new FileOutputStream(OUTPUT_FILE)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Audio written to " + OUTPUT_FILE);


        } catch (PollyException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static InputStream synthesize(PollyClient polly, String text, Voice voice, OutputFormat format) throws IOException {
        SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder().text(text).voiceId(voice.id()).outputFormat(format).build();

        return polly.synthesizeSpeech(synthReq);
    }
}

