package MyLb.BackEnd.dto;

public class CardResponse {
    private Long id;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private Double sold;
    private Long idClient;
    private Boolean isActive;
    private String cardType;
    private Double dailyLimit;

    public CardResponse() {}

    public CardResponse(Long id, String cardNumber, String cardHolderName,
                        String expiryDate, Double sold, Long idClient,
                        Boolean isActive, String cardType, Double dailyLimit) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.sold = sold;
        this.idClient = idClient;
        this.isActive = isActive;
        this.cardType = cardType;
        this.dailyLimit = dailyLimit;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public Double getSold() { return sold; }
    public void setSold(Double sold) { this.sold = sold; }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public Double getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Double dailyLimit) { this.dailyLimit = dailyLimit; }
}