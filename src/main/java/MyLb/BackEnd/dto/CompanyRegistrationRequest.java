package MyLb.BackEnd.dto;

import jakarta.validation.constraints.NotBlank;

public class CompanyRegistrationRequest {

    @NotBlank(message = "Le nom de la société est obligatoire")
    private String companyName;

    private String cinNumber; // Optionnel

    public CompanyRegistrationRequest() {}

    public CompanyRegistrationRequest(String companyName, String cinNumber) {
        this.companyName = companyName;
        this.cinNumber = cinNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCinNumber() {
        return cinNumber;
    }

    public void setCinNumber(String cinNumber) {
        this.cinNumber = cinNumber;
    }
}