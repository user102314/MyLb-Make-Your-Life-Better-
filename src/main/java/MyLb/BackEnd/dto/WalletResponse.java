package MyLb.BackEnd.dto;

/**
 * DTO pour la réponse du wallet
 */
public class WalletResponse {
    private Long id;
    private Long idClient;
    private Double sold;

    // Constructeurs
    public WalletResponse() {}

    public WalletResponse(Long id, Long idClient, Double sold) {
        this.id = id;
        this.idClient = idClient;
        this.sold = sold;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public Double getSold() {
        return sold;
    }

    public void setSold(Double sold) {
        this.sold = sold;
    }
}