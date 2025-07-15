package me.chatapp.stchat.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoiceRecorder {
    private long startTime;
    private long endTime;
    private String recordingPath;
    private TargetDataLine targetLine;
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private File audioFile;
    private boolean isRecording = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Audio format configuration
    private static final float SAMPLE_RATE = 16000.0f;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 1; // Mono
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    public void startRecording() throws Exception {
        if (isRecording) {
            throw new IllegalStateException("Recording is already in progress");
        }

        startTime = System.currentTimeMillis();
        recordingPath = "voice_" + startTime + ".wav";
        audioFile = new File(recordingPath);

        // Configure audio format
        AudioFormat audioFormat = new AudioFormat(
                SAMPLE_RATE,
                SAMPLE_SIZE_IN_BITS,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
        );

        // Get microphone line info
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        // Check if microphone is available
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Microphone not available");
        }

        // Get and open the microphone line
        targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open(audioFormat);
        targetLine.start();

        isRecording = true;
        System.out.println("Starting voice recording to: " + recordingPath);

        // Start recording in a separate thread
        CompletableFuture.runAsync(() -> {
            try {
                AudioInputStream audioInputStream = new AudioInputStream(targetLine);
                AudioSystem.write(audioInputStream, fileType, audioFile);
            } catch (IOException e) {
                System.err.println("Error during recording: " + e.getMessage());
                e.printStackTrace();
            }
        }, executorService);
    }

    public String stopRecording() throws Exception {
        if (!isRecording) {
            throw new IllegalStateException("No recording in progress");
        }

        endTime = System.currentTimeMillis();
        isRecording = false;

        // Stop and close the microphone line
        if (targetLine != null) {
            targetLine.stop();
            targetLine.close();
        }

        System.out.println("Stopping voice recording. Duration: " + getRecordingDuration() + "ms");
        System.out.println("Recording saved to: " + recordingPath);

        return recordingPath;
    }

    public long getRecordingDuration() {
        if (startTime == 0) {
            return 0;
        }
        return (endTime == 0 ? System.currentTimeMillis() : endTime) - startTime;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getRecordingPath() {
        return recordingPath;
    }

    public File getAudioFile() {
        return audioFile;
    }

    // Method to check if microphone is available
    public static boolean isMicrophoneAvailable() {
        AudioFormat audioFormat = new AudioFormat(
                SAMPLE_RATE,
                SAMPLE_SIZE_IN_BITS,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
        );

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        return AudioSystem.isLineSupported(info);
    }

    // Method to get available audio input devices
    public static Mixer.Info[] getAvailableInputDevices() {
        return AudioSystem.getMixerInfo();
    }

    // Clean up resources
    public void cleanup() {
        if (isRecording) {
            try {
                stopRecording();
            } catch (Exception e) {
                System.err.println("Error stopping recording during cleanup: " + e.getMessage());
            }
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}