package MyLb.BackEnd.dto;

import jakarta.validation.constraints.*;

public class CreateStockRequest {
    @NotBlank(message = "Le nom du stock est obligatoire")
    private String nomStock;

    @NotNull(message = "Le stock disponible est obligatoire")
    @Min(value = 0, message = "Le stock disponible doit être positif ou nul")
    private Integer stockDisponible;

    @NotNull(message = "Le stock restant est obligatoire")
    @Min(value = 0, message = "Le stock restant doit être positif ou nul")
    private Integer stockReste;

    @NotNull(message = "Le prix du stock est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private Double prixStock;

    private String etat; // Optionnel, défaut: "Actif"

    @NotNull(message = "L'ID de la company est obligatoire")
    private Long idComponey;

    @NotNull(message = "L'ID du propriétaire est obligatoire")
    private Long ownerId;

    // Constructeurs
    public CreateStockRequest() {}

    public CreateStockRequest(String nomStock, Integer stockDisponible, Integer stockReste,
                              Double prixStock, String etat, Long idComponey, Long ownerId) {
        this.nomStock = nomStock;
        this.stockDisponible = stockDisponible;
        this.stockReste = stockReste;
        this.prixStock = prixStock;
        this.etat = etat;
        this.idComponey = idComponey;
        this.ownerId = ownerId;
    }

    // Getters et Setters
    public String getNomStock() {
        return nomStock;
    }

    public void setNomStock(String nomStock) {
        this.nomStock = nomStock;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockReste() {
        return stockReste;
    }

    public void setStockReste(Integer stockReste) {
        this.stockReste = stockReste;
    }

    public Double getPrixStock() {
        return prixStock;
    }

    public void setPrixStock(Double prixStock) {
        this.prixStock = prixStock;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Long getIdComponey() {
        return idComponey;
    }

    public void setIdComponey(Long idComponey) {
        this.idComponey = idComponey;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}