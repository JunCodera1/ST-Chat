package me.chatapp.stchat.util;

public class VoiceRecorder {
    private long startTime;
    private long endTime;
    private String recordingPath;

    public void startRecording() throws Exception {
        startTime = System.currentTimeMillis();
        recordingPath = "voice_" + startTime + ".wav";

        // TODO: Implement actual voice recording using JavaFX Media API
        // For now, this is a placeholder
        System.out.println("Starting voice recording to: " + recordingPath);
    }

    public String stopRecording() throws Exception {
        endTime = System.currentTimeMillis();

        // TODO: Implement actual voice recording stop
        System.out.println("Stopping voice recording. Duration: " + getRecordingDuration() + "ms");

        return recordingPath;
    }

    public long getRecordingDuration() {
        return endTime - startTime;
    }
}
