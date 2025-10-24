package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.CompanyApplication;
import MyLb.BackEnd.Model.OwnerPO;
import MyLb.BackEnd.Repository.CompanyApplicationRepository;
import MyLb.BackEnd.Service.CompanyApplicationService;
import MyLb.BackEnd.Service.OwnerPOService;
import org.springframework.beans.factory.annotation.Autowired; // NOUVEL IMPORT NÉCESSAIRE
import org.springframework.stereotype.Service;
// import lombok.RequiredArgsConstructor; // LOMBOK SUPPRIMÉ
import java.util.List;
import java.util.Optional;

@Service
// @RequiredArgsConstructor est supprimé
public class CompanyApplicationServiceImpl implements CompanyApplicationService {

    private final CompanyApplicationRepository applicationRepository;
    private final OwnerPOService ownerPOService; // Pour lier à l'Owner existant

    // CONSTRUCTEUR MANUEL POUR L'INJECTION DE DÉPENDANCE
    @Autowired
    public CompanyApplicationServiceImpl(CompanyApplicationRepository applicationRepository, OwnerPOService ownerPOService) {
        this.applicationRepository = applicationRepository;
        this.ownerPOService = ownerPOService;
    }

    // --- Méthodes de l'interface (Non modifiées) ---

    @Override
    public CompanyApplication submitApplication(Long ownerId, CompanyApplication applicationDetails) {
        // 1. Vérifier si l'OwnerPO existe
        OwnerPO owner = ownerPOService.getOwnerPOByClientId(ownerId)
                .orElseThrow(() -> new RuntimeException("OwnerPO non trouvé avec l'ID: " + ownerId));

        // 2. Préparer l'application
        applicationDetails.setOwner(owner);
        applicationDetails.setStatus("PENDING"); // Statut initial

        return applicationRepository.save(applicationDetails);
    }

    @Override
    public CompanyApplication getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application non trouvée avec l'ID: " + applicationId));
    }

    @Override
    public List<CompanyApplication> getApplicationsByStatus(String status) {
        return applicationRepository.findByStatus(status);
    }

    @Override
    public CompanyApplication updateApplicationStatus(Long applicationId, String newStatus, String adminComment) {
        CompanyApplication existingApplication = getApplicationById(applicationId);

        existingApplication.setStatus(newStatus);
        existingApplication.setAdminComment(adminComment);

        // Logique métier : Si approuvé, mettre à jour le statut "isVerified" du Client
        // if (newStatus.equals("APPROVED")) { /* Logique de vérification Client ici */ }

        return applicationRepository.save(existingApplication);
    }

    @Override
    public void deleteApplication(Long applicationId) {
        applicationRepository.deleteById(applicationId);
    }
}