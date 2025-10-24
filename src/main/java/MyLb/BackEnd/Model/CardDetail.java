package MyLb.BackEnd.Model;

import jakarta.persistence.*;
// Les imports de Lombok sont supprimés
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;


@Entity
@Table(name = "card_detail")
public class CardDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_detail_id")
    private Long cardDetailId;

    // Clé étrangère vers Investor (relation One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Investor investor;

    private String cardNumber;
    private String expiryDate;

    // CVV ne doit jamais être stocké en clair, même pour le développement
    // C'est un champ sensible !
    @Transient // Indique que ce champ n'est PAS PERSISTÉ dans la BDD
    private String cvv;

    private String address1;
    private String address2;

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument (@NoArgsConstructor)
    // ----------------------------------------------------------------------
    public CardDetail() {
        // Constructeur par défaut requis par JPA
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments (@AllArgsConstructor)
    //    (Exclut généralement les champs @GeneratedValue comme 'cardDetailId'
    //     et le champ @Transient 'cvv' si vous ne voulez pas le passer au constructeur)
    // ----------------------------------------------------------------------
    public CardDetail(Long cardDetailId, Investor investor, String cardNumber, String expiryDate, String cvv, String address1, String address2) {
        this.cardDetailId = cardDetailId;
        this.investor = investor;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.address1 = address1;
        this.address2 = address2;
    }


    // ----------------------------------------------------------------------
    // 3. Getters et Setters (@Data)
    // ----------------------------------------------------------------------

    // Getters
    public Long getCardDetailId() {
        return cardDetailId;
    }

    public Investor getInvestor() {
        return investor;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    // Setters
    public void setCardDetailId(Long cardDetailId) {
        this.cardDetailId = cardDetailId;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}