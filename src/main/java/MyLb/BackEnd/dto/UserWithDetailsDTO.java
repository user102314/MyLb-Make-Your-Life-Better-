package MyLb.BackEnd.dto;

import java.time.LocalDate;

public class UserWithDetailsDTO {
    private Long clientId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String role;
    private Boolean isVerified;
    private String usagePurpose;
    private String cinNumber;
    private String phoneNumber;
    private Integer age;

    // Constructeurs, Getters et Setters
    public UserWithDetailsDTO() {}

    // Constructor avec tous les champs
    public UserWithDetailsDTO(Long clientId, String firstName, String lastName,
                              String email, LocalDate birthDate, String role,
                              Boolean isVerified, String usagePurpose,
                              String cinNumber, String phoneNumber, Integer age) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.role = role;
        this.isVerified = isVerified;
        this.usagePurpose = usagePurpose;
        this.cinNumber = cinNumber;
        this.phoneNumber = phoneNumber;
        this.age = age;
    }

    // Getters et Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getUsagePurpose() { return usagePurpose; }
    public void setUsagePurpose(String usagePurpose) { this.usagePurpose = usagePurpose; }

    public String getCinNumber() { return cinNumber; }
    public void setCinNumber(String cinNumber) { this.cinNumber = cinNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}