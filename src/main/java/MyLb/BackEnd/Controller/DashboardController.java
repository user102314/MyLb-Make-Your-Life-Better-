package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.CheckVerification;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Repository.ClientRepository;
import MyLb.BackEnd.Service.CheckVerificationService;
import MyLb.BackEnd.Service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class DashboardController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CheckVerificationService checkVerificationService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(@RequestParam(defaultValue = "month") String range) {
        try {
            System.out.println("📊 [DashboardController] Récupération des statistiques - Période: " + range);

            // Calculer la date de début basée sur la période
            LocalDateTime startDate = calculateStartDate(range);

            // Récupérer les données
            long totalUsers = clientRepository.count();
            long totalCompanies = companyService.getTotalCompanies();

            // Utilisateurs vérifiés complètement
            long fullyVerifiedUsers = checkVerificationService.getAllCheckVerifications()
                    .stream()
                    .filter(v -> v.isEtat1() && v.isEtat2() && v.isEtat3() && v.isEtat4())
                    .count();

            // Utilisateurs avec documents mais pas complètement vérifiés
            long pendingVerification = checkVerificationService.getAllCheckVerifications()
                    .stream()
                    .filter(v -> !(v.isEtat1() && v.isEtat2() && v.isEtat3() && v.isEtat4()))
                    .filter(v -> v.isEtat1() || v.isEtat2() || v.isEtat3() || v.isEtat4())
                    .count();

            // Statistiques de vérification détaillées
            List<CheckVerification> allVerifications = checkVerificationService.getAllCheckVerifications();
            long emailVerified = allVerifications.stream().filter(CheckVerification::isEtat1).count();
            long kycSubmitted = allVerifications.stream().filter(CheckVerification::isEtat2).count();
            long kycValidated = allVerifications.stream().filter(CheckVerification::isEtat3).count();
            long faceRecognition = allVerifications.stream().filter(CheckVerification::isEtat4).count();

            // Calculer la croissance (simplifié)
            double userGrowth = calculateUserGrowth(startDate);
            double companyGrowth = calculateCompanyGrowth(startDate);

            // Construire la réponse
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalCompanies", totalCompanies);
            stats.put("verifiedUsers", clientRepository.countByIsVerifiedTrue());
            stats.put("pendingVerification", pendingVerification);
            stats.put("fullyVerifiedUsers", fullyVerifiedUsers);
            stats.put("userGrowth", userGrowth);
            stats.put("companyGrowth", companyGrowth);

            Map<String, Object> verificationStats = new HashMap<>();
            verificationStats.put("emailVerified", emailVerified);
            verificationStats.put("kycSubmitted", kycSubmitted);
            verificationStats.put("kycValidated", kycValidated);
            verificationStats.put("faceRecognition", faceRecognition);
            stats.put("verificationStats", verificationStats);

            System.out.println("✅ [DashboardController] Statistiques générées: " + stats);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", stats
            ));

        } catch (Exception e) {
            System.err.println("❌ [DashboardController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération des statistiques",
                            "message", e.getMessage()
                    ));
        }
    }

    private LocalDateTime calculateStartDate(String range) {
        return switch (range.toLowerCase()) {
            case "today" -> LocalDate.now().atStartOfDay();
            case "week" -> LocalDate.now().minusWeeks(1).atStartOfDay();
            case "month" -> LocalDate.now().minusMonths(1).atStartOfDay();
            default -> LocalDate.now().minusYears(10).atStartOfDay(); // "all"
        };
    }

    private double calculateUserGrowth(LocalDateTime startDate) {
        // Implémentation simplifiée - à adapter selon votre modèle de données
        long currentUsers = clientRepository.count();
        long previousUsers = Math.max(1, currentUsers - 10); // Simulation
        return ((double) (currentUsers - previousUsers) / previousUsers) * 100;
    }

    private double calculateCompanyGrowth(LocalDateTime startDate) {
        // Implémentation simplifiée - à adapter selon votre modèle de données
        long currentCompanies = companyService.getTotalCompanies();
        long previousCompanies = Math.max(1, currentCompanies - 5); // Simulation
        return ((double) (currentCompanies - previousCompanies) / previousCompanies) * 100;
    }
}