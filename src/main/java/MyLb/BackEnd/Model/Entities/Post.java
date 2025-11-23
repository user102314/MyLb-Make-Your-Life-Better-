// src/main/java/com/mylb/backend/model/Post.java

package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPost;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String nomUser;

    @Column(nullable = false)
    private LocalDateTime date;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    // 🚨 Stockage des données binaires de l'image (BLOB)
    @Lob
    private byte[] photoPost;

    // Constructeurs
    public Post() {
        this.date = LocalDateTime.now();
    }

    public Post(Long clientId, String nomUser, String contenu, byte[] photoPost) {
        this.clientId = clientId;
        this.nomUser = nomUser;
        this.contenu = contenu;
        this.photoPost = photoPost;
        this.date = LocalDateTime.now();
    }

    // --- Getters et Setters ---

    public Long getIdPost() { return idPost; }
    public void setIdPost(Long idPost) { this.idPost = idPost; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getNomUser() { return nomUser; }
    public void setNomUser(String nomUser) { this.nomUser = nomUser; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public byte[] getPhotoPost() { return photoPost; }
    public void setPhotoPost(byte[] photoPost) { this.photoPost = photoPost; }
}