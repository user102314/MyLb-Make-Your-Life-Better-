package MyLb.BackEnd.Model;

import jakarta.persistence.*;
import java.util.Date;
// Lombok imports have been removed

@Entity
@Table(name = "company_application")
public class CompanyApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_application_id")
    private Long companyApplicationId;

    // Clé étrangère vers OwnerPO (Relation One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private OwnerPO owner;

    private String companyName;

    @Temporal(TemporalType.DATE)
    private Date incorporationDate;

    private String legalDocumentsPath;
    private String status;
    private String adminComment;

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument (Remplaçant @NoArgsConstructor)
    // ----------------------------------------------------------------------
    public CompanyApplication() {
        // Constructeur par défaut requis par JPA
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments (Remplaçant @AllArgsConstructor)
    // ----------------------------------------------------------------------
    public CompanyApplication(Long companyApplicationId, OwnerPO owner, String companyName, Date incorporationDate, String legalDocumentsPath, String status, String adminComment) {
        this.companyApplicationId = companyApplicationId;
        this.owner = owner;
        this.companyName = companyName;
        this.incorporationDate = incorporationDate;
        this.legalDocumentsPath = legalDocumentsPath;
        this.status = status;
        this.adminComment = adminComment;
    }

    // ----------------------------------------------------------------------
    // 3. Getters et Setters (Remplaçant @Data)
    // ----------------------------------------------------------------------

    // Getters
    public Long getCompanyApplicationId() {
        return companyApplicationId;
    }

    public OwnerPO getOwner() {
        return owner;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Date getIncorporationDate() {
        return incorporationDate;
    }

    public String getLegalDocumentsPath() {
        return legalDocumentsPath;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminComment() {
        return adminComment;
    }

    // Setters
    public void setCompanyApplicationId(Long companyApplicationId) {
        this.companyApplicationId = companyApplicationId;
    }

    public void setOwner(OwnerPO owner) {
        this.owner = owner;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setIncorporationDate(Date incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public void setLegalDocumentsPath(String legalDocumentsPath) {
        this.legalDocumentsPath = legalDocumentsPath;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}