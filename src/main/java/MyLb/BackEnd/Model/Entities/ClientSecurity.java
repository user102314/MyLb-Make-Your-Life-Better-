package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "client_security")
public class ClientSecurity {

    @Id
    @Column(name = "client_id")
    private Long id; // ID partagé (Shared Primary Key)

    // Relation où ClientSecurity possède la clé étrangère (join column)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 🚨 CRUCIAL: Utilise la PK du Client comme PK de cette entité
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "google_auth_secret", length = 32)
    private String googleAuthSecret;

    @Column(name = "is_2fa_enabled", nullable = false)
    private boolean is2FaEnabled = false;

    // Constructeur vide requis par JPA
    public ClientSecurity() {}

    // Constructeur pour lier immédiatement au client (utilisé dans Client.java)
    public ClientSecurity(Client client) {
        this.client = client;
        // NOTE: L'ID est défini par @MapsId/Cascade, pas ici.
    }

    // --- Getters et Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getGoogleAuthSecret() { return googleAuthSecret; }
    public void setGoogleAuthSecret(String googleAuthSecret) { this.googleAuthSecret = googleAuthSecret; }

    public boolean isIs2FaEnabled() { return is2FaEnabled; }
    public void setIs2FaEnabled(boolean is2FaEnabled) { this.is2FaEnabled = is2FaEnabled; }
}