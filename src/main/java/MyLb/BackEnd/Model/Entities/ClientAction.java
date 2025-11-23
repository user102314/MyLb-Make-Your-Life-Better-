package MyLb.BackEnd.Model.Entities;

import MyLb.BackEnd.Model.Estnum.ActionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore; // IMPORTANT pour le JSON

@Entity
@Table(name = "client_actions")
public class ClientAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActionType actionType;

    @Column(nullable = false)
    private LocalDateTime actionDate;

    // Lien vers le client (relation Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)

    // 1. CORRECTION du mapping :
    // On enlève 'referencedColumnName' pour laisser Hibernate pointer l'ID principal par défaut (qui est clientId).
    @JoinColumn(name = "client_id", nullable = false)

    // 2. CORRECTION de la sérialisation :
    // On ignore le Client lors de la sérialisation de ClientAction pour casser la boucle infinie JSON
    @JsonIgnore
    private Client client;

    @Column(length = 255)
    private String details;

    // Constructeur par défaut requis par JPA
    public ClientAction() {
        // Initialiser la date à la création
        this.actionDate = LocalDateTime.now();
    }

    // Constructeur utile pour la création d'actions
    public ClientAction(ActionType actionType, Client client, String details) {
        this();
        this.actionType = actionType;
        this.client = client;
        this.details = details;
    }

    // --- Getters et Setters ---

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}