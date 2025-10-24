package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.CompanyApplication;
import java.util.List;

public interface CompanyApplicationService {

    // Soumettre une nouvelle application (Création)
    CompanyApplication submitApplication(Long ownerId, CompanyApplication applicationDetails);

    // Lire
    CompanyApplication getApplicationById(Long applicationId);
    List<CompanyApplication> getApplicationsByStatus(String status);

    // Mettre à jour (Modification) : Mettre à jour le statut ou le commentaire admin
    CompanyApplication updateApplicationStatus(Long applicationId, String newStatus, String adminComment);

    // Suppression
    void deleteApplication(Long applicationId);
}