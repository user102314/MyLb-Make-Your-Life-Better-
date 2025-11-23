package MyLb.BackEnd.Controller;

import MyLb.BackEnd.dto.CompanyRegistrationRequest;
import MyLb.BackEnd.dto.CompanySummaryResponse;
import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Model.Entities.OwnerPO;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.CompanyService;
import MyLb.BackEnd.Repository.OwnerPORepository;
import MyLb.BackEnd.Repository.ClientRepository;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class CompanyController {

    private final CompanyService companyService;
    private final OwnerPORepository ownerPORepository;
    private final ClientRepository clientRepository;

    @Autowired
    public CompanyController(CompanyService companyService,
                             OwnerPORepository ownerPORepository,
                             ClientRepository clientRepository) {
        this.companyService = companyService;
        this.ownerPORepository = ownerPORepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Créer une nouvelle company ET créer une nouvelle entrée dans owner_po
     * POST /api/companies/add
     */
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<?> addCompany(
            @Valid @RequestBody CompanyRegistrationRequest request,
            HttpSession session)
    {
        System.out.println("📥 [CompanyController] POST /api/companies/add - Création de company");

        try {
            // 1. Récupérer l'ID utilisateur depuis la session
            Long authenticatedOwnerId = (Long) session.getAttribute("USER_ID");

            if (authenticatedOwnerId == null) {
                System.err.println("❌ [CompanyController] Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Utilisateur non authentifié. ID client introuvable dans la session."));
            }

            System.out.println("   ✅ Utilisateur authentifié - ID: " + authenticatedOwnerId);

            // 2. Vérifier que le client existe
            Client client = clientRepository.findById(authenticatedOwnerId)
                    .orElseThrow(() -> {
                        System.err.println("❌ [CompanyController] Client introuvable avec l'ID: " + authenticatedOwnerId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Client introuvable avec l'ID: " + authenticatedOwnerId);
                    });

            System.out.println("   ✅ Client trouvé: " + client.getFirstName() + " " + client.getLastName());

            // 3. Créer la company
            Company newCompany = new Company();
            newCompany.setOwnerID(authenticatedOwnerId);
            newCompany.setCompanyName(request.getCompanyName());

            Company savedCompany = companyService.createCompany(newCompany);

            System.out.println("   ✅ Company créée - ID: " + savedCompany.getCompanyId() +
                    " | Nom: " + savedCompany.getCompanyName());

            // 4. Créer une NOUVELLE entrée OwnerPO pour cette company
            try {
                // Vérifier si cette combinaison existe déjà (sécurité)
                boolean exists = ownerPORepository.existsByClientIdAndCompanyId(
                        authenticatedOwnerId,
                        savedCompany.getCompanyId()
                );

                if (exists) {
                    System.out.println("   ⚠️ OwnerPO existe déjà pour Client " + authenticatedOwnerId +
                            " et Company " + savedCompany.getCompanyId());
                } else {
                    // Créer un nouveau OwnerPO avec la clé composite
                    OwnerPO ownerPO = new OwnerPO();
                    ownerPO.setClientId(authenticatedOwnerId);
                    ownerPO.setCompanyId(savedCompany.getCompanyId());
                    ownerPO.setRole("OWNER"); // Rôle par défaut

                    // NE PAS définir client et company ici car on utilise insertable=false, updatable=false
                    // ownerPO.setClient(client);
                    // ownerPO.setCompany(savedCompany);

                    // Définir le CIN si fourni
                    if (request.getCinNumber() != null && !request.getCinNumber().isEmpty()) {
                        ownerPO.setCinNumber(request.getCinNumber());
                    }

                    ownerPORepository.save(ownerPO);

                    System.out.println("   ✅ OwnerPO créé - Client ID: " + authenticatedOwnerId +
                            " | Company ID: " + savedCompany.getCompanyId());
                }

                // Afficher le nombre total de companies du client
                long totalCompanies = ownerPORepository.countCompaniesByClientId(authenticatedOwnerId);
                System.out.println("   📊 Nombre total de companies du client: " + totalCompanies);

            } catch (Exception e) {
                System.err.println("❌ [CompanyController] Erreur lors de la création de OwnerPO: " + e.getMessage());
                e.printStackTrace();
                // Ne pas lancer d'exception, la company est déjà créée
                System.out.println("   ⚠️ Company créée mais OwnerPO non créé. Vous pouvez le créer manuellement.");
            }

            System.out.println("🎉 [CompanyController] Company créée avec succès!");
            return new ResponseEntity<>(savedCompany, HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            System.err.println("❌ [CompanyController] Erreur ResponseStatusException: " + e.getReason());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            System.err.println("❌ [CompanyController] Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors de la création de la company",
                            "message", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
        }
    }

    /**
     * Récupérer toutes les companies d'un propriétaire
     * GET /api/companies/my-list
     */
    @GetMapping("/my-list")
    public ResponseEntity<?> getAllCompaniesForOwner(HttpSession session)
    {
        System.out.println("📥 [CompanyController] GET /api/companies/my-list");

        try {
            Long authenticatedOwnerId = (Long) session.getAttribute("USER_ID");

            if (authenticatedOwnerId == null) {
                System.err.println("❌ [CompanyController] Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Accès refusé. Veuillez vous connecter."));
            }

            System.out.println("   ✅ Utilisateur authentifié - ID: " + authenticatedOwnerId);

            List<CompanySummaryResponse> companySummaries =
                    companyService.getCompanySummariesByOwnerId(authenticatedOwnerId);

            System.out.println("   ✅ " + companySummaries.size() + " compan(ies) trouvée(s)");

            return ResponseEntity.ok(companySummaries);

        } catch (Exception e) {
            System.err.println("❌ [CompanyController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors de la récupération des companies",
                            "message", e.getMessage()
                    ));
        }
    }
}