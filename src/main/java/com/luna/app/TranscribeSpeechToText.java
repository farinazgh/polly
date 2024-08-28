package com.luna.app;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.Media;
import software.amazon.awssdk.services.transcribe.model.StartTranscriptionJobRequest;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJob;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;
import software.amazon.awssdk.services.transcribe.model.TranscribeException;

public class TranscribeSpeechToText {

    public static void main(String[] args) {

        String jobName = "luna-transcription-job";
        String mediaUri = "https://luna-synthia.s3.eu-west-1.amazonaws.com/French.mp3";
        String languageCode = "fr-FR"; // French

        Region region = Region.EU_WEST_1;
        TranscribeClient transcribeClient = TranscribeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        startTranscriptionJob(transcribeClient, jobName, mediaUri, languageCode);
        transcribeClient.close();
    }

    public static void startTranscriptionJob(TranscribeClient transcribeClient, String jobName, String mediaUri, String languageCode) {

        try {
            Media media = Media.builder()
                    .mediaFileUri(mediaUri)
                    .build();

            StartTranscriptionJobRequest transcriptionJobRequest = StartTranscriptionJobRequest.builder()
                    .transcriptionJobName(jobName)
                    .languageCode(languageCode)
                    .media(media)
                    .build();

            transcribeClient.startTranscriptionJob(transcriptionJobRequest);

            // Wait until the job finishes
            TranscriptionJob transcriptionJob;
            do {
                transcriptionJob = transcribeClient.getTranscriptionJob(r -> r.transcriptionJobName(jobName)).transcriptionJob();
                System.out.println("Current status: " + transcriptionJob.transcriptionJobStatus());
                Thread.sleep(5000); // Wait 5 seconds before checking again
            } while (transcriptionJob.transcriptionJobStatus() == TranscriptionJobStatus.IN_PROGRESS);

            if (transcriptionJob.transcriptionJobStatus() == TranscriptionJobStatus.COMPLETED) {
                System.out.println("Transcription completed. You can download the transcript at: "
                        + transcriptionJob.transcript().transcriptFileUri());
            } else {
                System.out.println("Transcription failed with status: " + transcriptionJob.transcriptionJobStatus());
            }

        } catch (TranscribeException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
