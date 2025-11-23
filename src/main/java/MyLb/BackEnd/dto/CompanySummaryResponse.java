// Fichier: MyLb/BackEnd/dto/CompanySummaryResponse.java
package MyLb.BackEnd.dto;

import java.time.LocalDate;

public record CompanySummaryResponse(
        Long companyId, // Utile pour les actions futures (ex: modifier les finances)
        String companyName,
        String status, // L'état de l'entreprise
        LocalDate dateInscri // Date d'inscription
) {}