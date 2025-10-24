package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Investor;
import java.util.Optional;

public interface InvestorService {

    // Création d'un profil Investor lié à un Client existant
    Investor createInvestorProfile(Long clientId, Investor investorDetails);

    // Lire (Find by Client ID, qui est aussi la PK de Investor)
    Optional<Investor> getInvestorByClientId(Long clientId);

    // Mettre à jour (Modifier le grade ou d'autres détails)
    Investor updateInvestorGrade(Long clientId, String newGrade);

    // Suppression
    void deleteInvestorProfile(Long clientId);

    // Logique métier spécifique
    String calculateGrade(Long clientId);
}