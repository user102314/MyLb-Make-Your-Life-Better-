package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_client", nullable = false, unique = true)
    private Long idClient;

    @Column(name = "sold", nullable = false)
    private Double sold = 0.0;

    // Constructeurs
    public Wallet() {}

    public Wallet(Long idClient) {
        this.idClient = idClient;
        this.sold = 0.0;
    }

    public Wallet(Long idClient, Double sold) {
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

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", sold=" + sold +
                '}';
    }
}