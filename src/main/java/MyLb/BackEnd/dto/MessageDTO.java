package MyLb.BackEnd.dto;

import java.time.LocalDateTime;

/**
 * DTO for transferring message data via WebSocket and REST APIs
 */
public class MessageDTO {

    private Long id;
    private Long sendFrom;
    private Long sendTo;
    private String message;
    private LocalDateTime date;
    private String senderName;
    private String recipientName;

    public MessageDTO() {
    }

    public MessageDTO(Long sendFrom, Long sendTo, String message) {
        this.sendFrom = sendFrom;
        this.sendTo = sendTo;
        this.message = message;
        this.date = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(Long sendFrom) {
        this.sendFrom = sendFrom;
    }

    public Long getSendTo() {
        return sendTo;
    }

    public void setSendTo(Long sendTo) {
        this.sendTo = sendTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
