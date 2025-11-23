package MyLb.BackEnd.Model.Entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe représentant la clé primaire composite de OwnerPO
 * Permet à un client d'être propriétaire de plusieurs companies
 */
public class OwnerPOId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long clientId;
    private Long companyId;

    // ============================================================
    // Constructeurs
    // ============================================================

    /**
     * Constructeur par défaut requis
     */
    public OwnerPOId() {
    }

    /**
     * Constructeur avec paramètres
     */
    public OwnerPOId(Long clientId, Long companyId) {
        this.clientId = clientId;
        this.companyId = companyId;
    }

    // ============================================================
    // Getters et Setters
    // ============================================================

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    // ============================================================
    // equals() et hashCode() - OBLIGATOIRES pour les clés composites
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnerPOId that = (OwnerPOId) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, companyId);
    }

    // ============================================================
    // toString()
    // ============================================================

    @Override
    public String toString() {
        return "OwnerPOId{" +
                "clientId=" + clientId +
                ", companyId=" + companyId +
                '}';
    }
}