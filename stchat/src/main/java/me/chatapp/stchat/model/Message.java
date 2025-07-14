package me.chatapp.stchat.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.chatapp.stchat.util.LocalDateTimeFlexibleDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("id")
    private int id;

    @JsonIgnore
    private String sender;

    @JsonProperty("content")
    private String content;

    @JsonProperty("messageType")
    private MessageType type;

    @JsonProperty("senderId")
    private int senderId;

    @JsonProperty("conversationId")
    private int conversationId;

    @JsonProperty("isPinned")
    private boolean isPinned;

    @JsonProperty("receiverId")
    private int receiverId;

    @JsonProperty("channelId")
    private int channelId;

    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeFlexibleDeserializer.class)
    private LocalDateTime createdAt;

    // Additional properties for ChatPanel compatibility
    @JsonIgnore
    private AttachmentMessage attachment;

    @JsonIgnore
    private boolean hasAttachment = false;

    @JsonIgnore
    private boolean isEdited = false;

    @JsonIgnore
    private LocalDateTime editedAt;

    @JsonIgnore
    private String originalContent;

    // Default constructor for Jackson
    public Message() {
        this.isPinned = false;
        this.hasAttachment = false;
        this.isEdited = false;
    }

    public Message(String sender, String content, MessageType type) {
        this();
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public Message(int id, String sender, String content, MessageType type,
                   int conversationId, boolean isPinned,
                   int receiverId, int channelId) {
        this();
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.conversationId = conversationId;
        this.isPinned = isPinned;
        this.receiverId = receiverId;
        this.channelId = channelId;
        this.createdAt = LocalDateTime.now();
    }

    // Existing getters and setters
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
        // Track editing
        if (this.content != null && !this.content.equals(content)) {
            this.originalContent = this.content;
            this.isEdited = true;
            this.editedAt = LocalDateTime.now();
        }
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
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

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // NEW: Attachment support methods
    public AttachmentMessage getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentMessage attachment) {
        this.attachment = attachment;
        this.hasAttachment = (attachment != null);
        if (hasAttachment && (type == MessageType.TEXT || type == MessageType.USER)) {
            this.type = MessageType.FILE;
        }
    }

    public boolean hasAttachment() {
        return hasAttachment || attachment != null;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    // NEW: Edit tracking methods
    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    // NEW: Utility methods for ChatPanel
    @JsonProperty("createdAt")
    public String getCreatedAtFormatted() {
        if (createdAt == null) {
            return "Unknown time";
        }

        // Format for display in chat
        LocalDateTime now = LocalDateTime.now();
        if (createdAt.toLocalDate().equals(now.toLocalDate())) {
            // Today - show time only
            return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (createdAt.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            // Yesterday
            return "Yesterday " + createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            // Other days
            return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
        }
    }

    // For API serialization
    @JsonIgnore
    public String getCreatedAtForAPI() {
        return (createdAt != null)
                ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "Unknown";
    }

    // NEW: Message type checking methods
    public boolean isSystemMessage() {
        return type == MessageType.SYSTEM;
    }

    public boolean isTextMessage() {
        return type == MessageType.TEXT || type == MessageType.USER || type == MessageType.BOT;
    }

    public boolean isFileMessage() {
        return type == MessageType.FILE || hasAttachment();
    }

    public boolean isImageMessage() {
        return type == MessageType.IMAGE ||
                (hasAttachment() && attachment != null && attachment.isImage());
    }

    public boolean isAudioMessage() {
        return type == MessageType.AUDIO ||
                (hasAttachment() && attachment != null && attachment.isAudio());
    }

    public boolean isVideoMessage() {
        return type == MessageType.VIDEO ||
                (hasAttachment() && attachment != null && attachment.isVideo());
    }

    // NEW: User checking methods
    public boolean isFromCurrentUser(String currentUsername) {
        return sender != null && sender.equals(currentUsername);
    }

    public boolean isFromUser(int userId) {
        return senderId == userId;
    }

    // NEW: Content validation
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }

    public boolean isEmpty() {
        return !hasContent() && !hasAttachment();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s",
                createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("HH:mm")) : "??:??",
                sender,
                hasAttachment() ? "[Attachment: " + attachment.getFileName() + "]" : content);
    }

    // NEW: Clone method for editing
    public Message clone() {
        Message cloned = new Message();
        cloned.id = this.id;
        cloned.sender = this.sender;
        cloned.content = this.content;
        cloned.type = this.type;
        cloned.senderId = this.senderId;
        cloned.conversationId = this.conversationId;
        cloned.isPinned = this.isPinned;
        cloned.receiverId = this.receiverId;
        cloned.channelId = this.channelId;
        cloned.createdAt = this.createdAt;
        cloned.attachment = this.attachment;
        cloned.hasAttachment = this.hasAttachment;
        cloned.isEdited = this.isEdited;
        cloned.editedAt = this.editedAt;
        cloned.originalContent = this.originalContent;
        return cloned;
    }

    public enum MessageType {
        TEXT, IMAGE, FILE, LINK, SYSTEM, USER, BOT, AUDIO, VIDEO, STICKER, POLL, EVENT;

        @JsonValue
        public String toUpperCase() {
            return this.name();
        }

        // NEW: Utility methods for message type checking
        public boolean isMediaType() {
            return this == IMAGE || this == AUDIO || this == VIDEO;
        }

        public boolean isFileType() {
            return this == FILE || this == IMAGE || this == AUDIO || this == VIDEO;
        }

        public boolean isInteractiveType() {
            return this == POLL || this == EVENT || this == STICKER;
        }
    }
}