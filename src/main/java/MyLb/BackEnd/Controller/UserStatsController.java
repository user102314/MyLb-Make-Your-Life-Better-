package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.CheckVerification;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Repository.ClientRepository;
import MyLb.BackEnd.Repository.CompanyRepository;
import MyLb.BackEnd.Service.CheckVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/statistics") // Route modifiée pour éviter les conflits
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class UserStatsController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CheckVerificationService checkVerificationService;

    @GetMapping("/users/verification-stats")
    public ResponseEntity<?> getUserVerificationStats() {
        try {
            System.out.println("👥 [UserStatsController] Récupération des statistiques utilisateurs");

            List<Client> allUsers = clientRepository.findAll();
            List<Map<String, Object>> userStats = new ArrayList<>();

            for (Client user : allUsers) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("clientId", user.getClientId());
                userData.put("firstName", user.getFirstName());
                userData.put("lastName", user.getLastName());
                userData.put("email", user.getEmail());
                userData.put("isVerified", user.getIsVerified() != null ? user.getIsVerified() : false);

                if (user.getCreatedAt() != null) {
                    userData.put("createdAt", user.getCreatedAt().toString());
                } else {
                    userData.put("createdAt", "Non disponible");
                }

                // Compter le nombre d'entreprises
                Long companiesCount = companyRepository.countByOwnerID(user.getClientId());
                userData.put("companiesCount", companiesCount != null ? companiesCount : 0);

                // Statut de vérification
                Optional<CheckVerification> verificationOpt = checkVerificationService.getVerificationByIduser(user.getClientId());

                boolean hasIdentityDocuments = false;
                boolean isFullyVerified = false;
                Map<String, Boolean> verificationStatus = new HashMap<>();

                if (verificationOpt.isPresent()) {
                    CheckVerification verification = verificationOpt.get();
                    verificationStatus.put("emailVerified", verification.isEtat1());
                    verificationStatus.put("kycSubmitted", verification.isEtat2());
                    verificationStatus.put("kycValidated", verification.isEtat3());
                    verificationStatus.put("faceRecognition", verification.isEtat4());

                    hasIdentityDocuments = verification.isEtat2();
                    isFullyVerified = verification.isEtat1() && verification.isEtat2() &&
                            verification.isEtat3() && verification.isEtat4();
                }

                userData.put("hasIdentityDocuments", hasIdentityDocuments);
                userData.put("isFullyVerified", isFullyVerified);
                userData.put("verificationStatus", verificationStatus);

                userStats.add(userData);
            }

            System.out.println("✅ [UserStatsController] " + userStats.size() + " utilisateurs chargés");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "users", userStats
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération des statistiques utilisateurs",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/users/complete-list")
    public ResponseEntity<?> getUsersCompleteDetails() {
        try {
            System.out.println("📋 [UserStatsController] Récupération des détails complets utilisateurs");

            List<Client> allUsers = clientRepository.findAll();
            List<Map<String, Object>> usersWithDetails = new ArrayList<>();

            for (Client user : allUsers) {
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("clientId", user.getClientId());
                userDetails.put("firstName", user.getFirstName());
                userDetails.put("lastName", user.getLastName());
                userDetails.put("email", user.getEmail());
                userDetails.put("role", user.getRole());
                userDetails.put("isVerified", user.getIsVerified() != null ? user.getIsVerified() : false);
                userDetails.put("phoneNumber", user.getPhoneNumber());
                userDetails.put("cinNumber", user.getCinNumber());
                userDetails.put("age", user.getAge());
                userDetails.put("usagePurpose", user.getUsagePurpose());

                // Statut de vérification
                Optional<CheckVerification> verificationOpt = checkVerificationService.getVerificationByIduser(user.getClientId());

                boolean hasIdentityDocuments = false;
                boolean isFullyVerified = false;

                if (verificationOpt.isPresent()) {
                    CheckVerification verification = verificationOpt.get();
                    hasIdentityDocuments = verification.isEtat2();
                    isFullyVerified = verification.isEtat1() && verification.isEtat2() &&
                            verification.isEtat3() && verification.isEtat4();
                }

                userDetails.put("hasIdentityDocuments", hasIdentityDocuments);
                userDetails.put("isFullyVerified", isFullyVerified);

                usersWithDetails.add(userDetails);
            }

            System.out.println("✅ [UserStatsController] Détails de " + usersWithDetails.size() + " utilisateurs chargés");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "users", usersWithDetails
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération des détails utilisateurs",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/users/{userId}/details")
    public ResponseEntity<?> getUserCompleteDetails(@PathVariable Long userId) {
        try {
            System.out.println("🔍 [UserStatsController] Détails complets pour l'utilisateur: " + userId);

            Client user = clientRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("clientId", user.getClientId());
            userDetails.put("firstName", user.getFirstName());
            userDetails.put("lastName", user.getLastName());
            userDetails.put("email", user.getEmail());
            userDetails.put("birthDate", user.getBirthDate());
            userDetails.put("role", user.getRole());
            userDetails.put("isVerified", user.getIsVerified() != null ? user.getIsVerified() : false);
            userDetails.put("phoneNumber", user.getPhoneNumber());
            userDetails.put("cinNumber", user.getCinNumber());
            userDetails.put("age", user.getAge());
            userDetails.put("usagePurpose", user.getUsagePurpose());

            // Documents d'identité
            userDetails.put("photocinRecto", user.getPhotocinRecto());
            userDetails.put("photocinVerso", user.getPhotocinVerso());
            userDetails.put("photocompletSelfie", user.getPhotocompletSelfie());
            userDetails.put("identityStatus", user.getIdentityStatus());

            // Statut de vérification complet
            Optional<CheckVerification> verificationOpt = checkVerificationService.getVerificationByIduser(userId);

            if (verificationOpt.isPresent()) {
                CheckVerification verification = verificationOpt.get();
                userDetails.put("emailVerified", verification.isEtat1());
                userDetails.put("kycSubmitted", verification.isEtat2());
                userDetails.put("kycValidated", verification.isEtat3());
                userDetails.put("faceRecognition", verification.isEtat4());
                userDetails.put("fullyVerified", verification.isEtat1() && verification.isEtat2() &&
                        verification.isEtat3() && verification.isEtat4());
            } else {
                userDetails.put("emailVerified", false);
                userDetails.put("kycSubmitted", false);
                userDetails.put("kycValidated", false);
                userDetails.put("faceRecognition", false);
                userDetails.put("fullyVerified", false);
            }

            System.out.println("✅ [UserStatsController] Détails complets chargés pour l'utilisateur: " + userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "user", userDetails
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération des détails utilisateur",
                            "message", e.getMessage()
                    ));
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String newRole = request.get("role");
            System.out.println("🔄 [UserStatsController] Mise à jour du rôle pour l'utilisateur " + userId + " -> " + newRole);

            Client user = clientRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            user.setRole(newRole);
            clientRepository.save(user);

            System.out.println("✅ [UserStatsController] Rôle mis à jour avec succès");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Rôle utilisateur mis à jour avec succès"
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la mise à jour du rôle",
                            "message", e.getMessage()
                    ));
        }
    }

    @PutMapping("/users/{userId}/verification")
    public ResponseEntity<?> updateUserVerification(@PathVariable Long userId, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean isVerified = request.get("isVerified");
            System.out.println("🔄 [UserStatsController] Mise à jour vérification pour l'utilisateur " + userId + " -> " + isVerified);

            Client user = clientRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            user.setIsVerified(isVerified);
            clientRepository.save(user);

            System.out.println("✅ [UserStatsController] Statut de vérification mis à jour avec succès");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Statut de vérification mis à jour avec succès"
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la mise à jour de la vérification",
                            "message", e.getMessage()
                    ));
        }
    }

    @PutMapping("/users/{userId}/identity-status")
    public ResponseEntity<?> updateIdentityStatus(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            System.out.println("🔄 [UserStatsController] Mise à jour statut identité pour l'utilisateur " + userId + " -> " + status);

            Client user = clientRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            user.setIdentityStatus(status);
            clientRepository.save(user);

            System.out.println("✅ [UserStatsController] Statut identité mis à jour avec succès");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Statut des documents d'identité mis à jour avec succès"
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la mise à jour du statut identité",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/users/verification/status")
    public ResponseEntity<?> getGlobalVerificationStatus() {
        try {
            System.out.println("📊 [UserStatsController] Récupération du statut global de vérification");

            var allVerifications = checkVerificationService.getAllCheckVerifications();

            long totalUsers = allVerifications.size();
            long step1Completed = allVerifications.stream().filter(v -> v.isEtat1()).count();
            long step2Completed = allVerifications.stream().filter(v -> v.isEtat2()).count();
            long step3Completed = allVerifications.stream().filter(v -> v.isEtat3()).count();
            long step4Completed = allVerifications.stream().filter(v -> v.isEtat4()).count();
            long fullyVerified = allVerifications.stream()
                    .filter(v -> v.isEtat1() && v.isEtat2() && v.isEtat3() && v.isEtat4())
                    .count();

            Map<String, Object> status = Map.of(
                    "totalUsers", totalUsers,
                    "step1Completed", step1Completed,
                    "step2Completed", step2Completed,
                    "step3Completed", step3Completed,
                    "step4Completed", step4Completed,
                    "fullyVerified", fullyVerified,
                    "completionRate", totalUsers > 0 ? (double) fullyVerified / totalUsers * 100 : 0
            );

            System.out.println("✅ [UserStatsController] Statut global de vérification récupéré");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "verificationStatus", status
            ));

        } catch (Exception e) {
            System.err.println("❌ [UserStatsController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération du statut de vérification",
                            "message", e.getMessage()
                    ));
        }
    }
}