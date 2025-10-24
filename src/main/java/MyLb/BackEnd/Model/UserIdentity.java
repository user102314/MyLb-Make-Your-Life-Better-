package MyLb.BackEnd.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_identity")
public class UserIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idv;

    @Column(name = "iduser", unique = true, nullable = false)
    private Long iduser;

    // 🚨 MODIFICATION CRITIQUE: Utilisation de @Lob pour stocker de grands objets binaires
    // 🚨 MODIFICATION CRITIQUE: Forcer le type MEDIUMBLOB (pour MySQL)
    @Lob
    @Column(name = "photocin_recto", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] photocinRecto;

    @Lob
    @Column(name = "photocin_verso", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] photocinVerso;

    @Lob
    @Column(name = "photocomplet_selfie", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] photocompletSelfie;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private ValidationStatus etat = ValidationStatus.PENDING;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    public enum ValidationStatus {
        PENDING, VALIDATED, REJECTED
    }

    // --- Getters et Setters (Mis à jour pour byte[]) ---

    public Long getIdv() { return idv; }
    public void setIdv(Long idv) { this.idv = idv; }

    public Long getIduser() { return iduser; }
    public void setIduser(Long iduser) { this.iduser = iduser; }

    public byte[] getPhotocinRecto() { return photocinRecto; }
    public void setPhotocinRecto(byte[] photocinRecto) { this.photocinRecto = photocinRecto; }

    public byte[] getPhotocinVerso() { return photocinVerso; }
    public void setPhotocinVerso(byte[] photocinVerso) { this.photocinVerso = photocinVerso; }

    public byte[] getPhotocompletSelfie() { return photocompletSelfie; }
    public void setPhotocompletSelfie(byte[] photocompletSelfie) { this.photocompletSelfie = photocompletSelfie; }

    public ValidationStatus getEtat() { return etat; }
    public void setEtat(ValidationStatus etat) { this.etat = etat; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}