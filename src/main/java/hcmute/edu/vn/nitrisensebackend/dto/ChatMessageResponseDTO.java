package hcmute.edu.vn.nitrisensebackend.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class ChatMessageResponseDTO {
    private Long id;
    private String sender;
    private String messageType;
    private String text;
    private JsonNode contextData;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public JsonNode getContextData() {
        return contextData;
    }

    public void setContextData(JsonNode contextData) {
        this.contextData = contextData;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}