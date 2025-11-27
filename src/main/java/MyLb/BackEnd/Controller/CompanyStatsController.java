package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.CompanyService;
import MyLb.BackEnd.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/companies")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class CompanyStatsController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getCompanyStats() {
        try {
            System.out.println("🏢 [CompanyStatsController] Récupération des statistiques entreprises");

            List<Company> allCompanies = companyService.getAllCompanies();
            List<Map<String, Object>> companyStats = new ArrayList<>();

            for (Company company : allCompanies) {
                Map<String, Object> companyData = new HashMap<>();
                companyData.put("companyId", company.getCompanyId());
                companyData.put("companyName", company.getCompanyName());
                companyData.put("status", company.getStatus() != null ? company.getStatus() : "ACTIVE");
                companyData.put("ownerId", company.getOwnerID());

                // Récupérer les informations du propriétaire
                Optional<Client> ownerOpt = clientRepository.findById(company.getOwnerID());
                if (ownerOpt.isPresent()) {
                    Client owner = ownerOpt.get();
                    companyData.put("ownerName", owner.getFirstName() + " " + owner.getLastName());
                    companyData.put("ownerEmail", owner.getEmail());
                } else {
                    companyData.put("ownerName", "Inconnu");
                    companyData.put("ownerEmail", "N/A");
                }

                companyStats.add(companyData);
            }

            System.out.println("✅ [CompanyStatsController] " + companyStats.size() + " entreprises chargées");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "companies", companyStats
            ));

        } catch (Exception e) {
            System.err.println("❌ [CompanyStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération des statistiques entreprises",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getCompaniesSummary() {
        try {
            System.out.println("📈 [CompanyStatsController] Récupération du résumé entreprises");

            List<Company> allCompanies = companyService.getAllCompanies();

            if (allCompanies.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "summary", Map.of(
                                "totalCompanies", 0,
                                "uniqueOwners", 0,
                                "averageCompaniesPerOwner", 0,
                                "topOwners", List.of(),
                                "monthlyDistribution", Map.of()
                        )
                ));
            }

            // Statistiques par propriétaire
            Map<Long, Long> companiesPerOwner = allCompanies.stream()
                    .collect(Collectors.groupingBy(Company::getOwnerID, Collectors.counting()));

            // Top propriétaires
            List<Map<String, Object>> topOwners = companiesPerOwner.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(10)
                    .map(entry -> {
                        Map<String, Object> ownerData = new HashMap<>();
                        Optional<Client> ownerOpt = clientRepository.findById(entry.getKey());
                        if (ownerOpt.isPresent()) {
                            Client owner = ownerOpt.get();
                            ownerData.put("ownerName", owner.getFirstName() + " " + owner.getLastName());
                            ownerData.put("ownerEmail", owner.getEmail());
                        } else {
                            ownerData.put("ownerName", "Utilisateur " + entry.getKey());
                            ownerData.put("ownerEmail", "N/A");
                        }
                        ownerData.put("companiesCount", entry.getValue());
                        return ownerData;
                    })
                    .collect(Collectors.toList());

            // Distribution par statut au lieu de croissance mensuelle
            Map<String, Long> statusDistribution = allCompanies.stream()
                    .collect(Collectors.groupingBy(
                            company -> company.getStatus() != null ? company.getStatus() : "ACTIVE",
                            Collectors.counting()
                    ));

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCompanies", allCompanies.size());
            summary.put("uniqueOwners", companiesPerOwner.size());
            summary.put("averageCompaniesPerOwner", companiesPerOwner.size() > 0 ?
                    (double) allCompanies.size() / companiesPerOwner.size() : 0);
            summary.put("topOwners", topOwners);
            summary.put("statusDistribution", statusDistribution);

            System.out.println("✅ [CompanyStatsController] Résumé entreprises généré");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "summary", summary
            ));

        } catch (Exception e) {
            System.err.println("❌ [CompanyStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération du résumé entreprises",
                            "message", e.getMessage()
                    ));
        }
    }
}