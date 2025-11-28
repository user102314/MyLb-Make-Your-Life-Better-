package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "send_from", nullable = false)
    private Long sendFrom;

    @Column(name = "send_to", nullable = false)
    private Long sendTo;

    @Column(name = "message_content", nullable = false, length = 2000)
    private String message;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_from", referencedColumnName = "client_id", insertable = false, updatable = false)
    private Client sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_to", referencedColumnName = "client_id", insertable = false, updatable = false)
    private Client recipient;

    public Message() {
        this.date = LocalDateTime.now();
    }

    public Message(Long sendFrom, Long sendTo, String message) {
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

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public Client getRecipient() {
        return recipient;
    }

    public void setRecipient(Client recipient) {
        this.recipient = recipient;
    }
}
