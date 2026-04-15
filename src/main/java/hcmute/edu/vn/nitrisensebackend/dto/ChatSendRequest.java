package hcmute.edu.vn.nitrisensebackend.dto;

public class ChatSendRequest {
    private Long userId;
    private String message;

    // --- Getters and Setters ---
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}