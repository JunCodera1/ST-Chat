package me.chatapp.stchat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    @JsonProperty("id")
    private int id;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private MessageType type;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("conversationId")
    private int conversationId;

    @JsonProperty("isPinned")
    private boolean isPinned;

    @JsonProperty("receiverId")
    private String receiverId;

    @JsonProperty("channelId")
    private String channelId;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public Message(String sender, String content, MessageType type, LocalDateTime timestamp) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.isPinned = false;
    }

    public Message(int id, String sender, String content, MessageType type,
                   LocalDateTime timestamp, int conversationId, boolean isPinned,
                   String receiverId, String channelId) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.conversationId = conversationId;
        this.isPinned = isPinned;
        this.receiverId = receiverId;
        this.channelId = channelId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getFormattedTime() {
        return timestamp.format(TIME_FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getFormattedTime(), sender, content);
    }

    public enum MessageType {
        TEXT,           // Tin nhắn text thông thường
        IMAGE,          // Tin nhắn hình ảnh
        FILE,           // Tin nhắn file đính kèm
        LINK,           // Tin nhắn chứa link
        SYSTEM,         // Tin nhắn hệ thống (user join/leave, etc.)

        // Nếu bạn muốn giữ phân loại theo người gửi
        USER,           // Tin nhắn từ user thường
        BOT,            // Tin nhắn từ bot

        // Hoặc có thể thêm các loại khác
        AUDIO,          // Tin nhắn âm thanh
        VIDEO,          // Tin nhắn video
        STICKER,        // Sticker/emoji
        POLL,           // Bình chọn
        EVENT           // Sự kiện (meeting, reminder, etc.)
    }

}