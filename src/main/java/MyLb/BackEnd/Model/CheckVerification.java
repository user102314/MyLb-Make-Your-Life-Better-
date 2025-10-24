package MyLb.BackEnd.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_check_verification") // Nouvelle table
public class CheckVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idverification; // L'ID reste le même

    // Clé étrangère vers la table Client (User)
    @Column(name = "iduser", unique = true, nullable = false)
    private Long iduser;

    // Étapes de vérification
    @Column(name = "etat1_email")
    private boolean etat1 = false; // Ex: Vérification Email

    @Column(name = "etat2_kyc_submit")
    private boolean etat2 = false; // Ex: Soumission Documents KYC

    @Column(name = "etat3_kyc_valid")
    private boolean etat3 = false; // Ex: Validation KYC par Support

    @Column(name = "etat4_face_rec")
    private boolean etat4 = false; // Ex: Connexion Faciale

    // --- Constructeur par défaut ---
    public CheckVerification() {}

    // --- Constructeur avec iduser (pour la création) ---
    public CheckVerification(Long iduser) {
        this.iduser = iduser;
    }

    // --- Getters et Setters (Complets) ---
    public Long getIdverification() {
        return idverification;
    }

    public void setIdverification(Long idverification) {
        this.idverification = idverification;
    }

    public Long getIduser() {
        return iduser;
    }

    public void setIduser(Long iduser) {
        this.iduser = iduser;
    }

    public boolean isEtat1() {
        return etat1;
    }

    public void setEtat1(boolean etat1) {
        this.etat1 = etat1;
    }

    public boolean isEtat2() {
        return etat2;
    }

    public void setEtat2(boolean etat2) {
        this.etat2 = etat2;
    }

    public boolean isEtat3() {
        return etat3;
    }

    public void setEtat3(boolean etat3) {
        this.etat3 = etat3;
    }

    public boolean isEtat4() {
        return etat4;
    }

    public void setEtat4(boolean etat4) {
        this.etat4 = etat4;
    }
}