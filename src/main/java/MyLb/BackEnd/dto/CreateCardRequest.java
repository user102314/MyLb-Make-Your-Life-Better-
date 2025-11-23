package MyLb.BackEnd.dto;

import jakarta.validation.constraints.*;

public class CreateCardRequest {

    @NotBlank(message = "Le numéro de carte est obligatoire")
    @Size(min = 16, max = 16, message = "Le numéro de carte doit contenir 16 chiffres")
    @Pattern(regexp = "\\d+", message = "Le numéro de carte ne doit contenir que des chiffres")
    private String cardNumber;

    @NotBlank(message = "Le nom du titulaire est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String cardHolderName;

    @NotBlank(message = "La date d'expiration est obligatoire")
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = "Format de date invalide (MM/YY)")
    private String expiryDate;

    @NotBlank(message = "Le CVV est obligatoire")
    @Size(min = 3, max = 3, message = "Le CVV doit contenir 3 chiffres")
    @Pattern(regexp = "\\d+", message = "Le CVV ne doit contenir que des chiffres")
    private String cvv;

    @NotNull(message = "L'ID client est obligatoire")
    @Min(value = 1, message = "L'ID client doit être positif")
    private Long idClient;

    @NotBlank(message = "Le type de carte est obligatoire")
    @Pattern(regexp = "VISA|MASTERCARD|AMEX", message = "Type de carte invalide")
    private String cardType;

    // Constructeurs
    public CreateCardRequest() {}

    public CreateCardRequest(String cardNumber, String cardHolderName, String expiryDate,
                             String cvv, Long idClient, String cardType) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.idClient = idClient;
        this.cardType = cardType;
    }

    // Getters et Setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}