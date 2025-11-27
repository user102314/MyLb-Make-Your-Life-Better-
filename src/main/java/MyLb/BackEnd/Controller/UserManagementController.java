package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.SelfDetail;
import MyLb.BackEnd.Model.Entities.UserIdentity;
import MyLb.BackEnd.Model.Entities.CheckVerification;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.SelfDetailService;
import MyLb.BackEnd.Service.UserIdentityService;
import MyLb.BackEnd.Service.CheckVerificationService;
import MyLb.BackEnd.dto.PasswordChangeRequest;
import MyLb.BackEnd.dto.UserRoleUpdateRequest;
import MyLb.BackEnd.dto.UserVerificationRequest;
import MyLb.BackEnd.dto.UserWithDetailsDTO;
import MyLb.BackEnd.dto.UserCompleteDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class UserManagementController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private SelfDetailService selfDetailService;

    @Autowired
    private UserIdentityService userIdentityService;

    @Autowired
    private CheckVerificationService checkVerificationService;

    /**
     * GET all users with their self details in one request
     */
    @GetMapping
    public ResponseEntity<?> getAllUsersWithDetails() {
        try {
            List<UserWithDetailsDTO> users = clientService.getAllUsersWithDetails();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            response.put("totalUsers", users.size());
            response.put("message", "Liste des utilisateurs récupérée avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la récupération des utilisateurs: " + e.getMessage()
            ));
        }
    }

    /**
     * GET all users with complete details (including verification status)
     */
    @GetMapping("/complete-details")
    public ResponseEntity<?> getAllUsersCompleteDetails() {
        try {
            List<UserWithDetailsDTO> users = clientService.getAllUsersWithDetails();

            List<Map<String, Object>> completeUsers = users.stream()
                    .map(user -> {
                        Map<String, Object> userMap = new HashMap<>();

                        // Informations de base
                        userMap.put("clientId", user.getClientId());
                        userMap.put("firstName", user.getFirstName());
                        userMap.put("lastName", user.getLastName());
                        userMap.put("email", user.getEmail());
                        userMap.put("role", user.getRole());
                        userMap.put("isVerified", user.getIsVerified());

                        // SelfDetail
                        userMap.put("usagePurpose", user.getUsagePurpose());
                        userMap.put("cinNumber", user.getCinNumber());
                        userMap.put("phoneNumber", user.getPhoneNumber());
                        userMap.put("age", user.getAge());

                        // Vérifications
                        boolean hasIdentity = userIdentityService.existsByUserId(user.getClientId());
                        boolean isFullyVerified = checkVerificationService.isUserFullyVerified(user.getClientId());

                        userMap.put("hasIdentityDocuments", hasIdentity);
                        userMap.put("isFullyVerified", isFullyVerified);

                        return userMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", completeUsers);
            response.put("totalUsers", completeUsers.size());
            response.put("message", "Liste complète des utilisateurs récupérée avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la récupération des utilisateurs: " + e.getMessage()
            ));
        }
    }

    /**
     * GET user by ID with basic details
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            Client client = clientService.getClientById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", createUserResponse(client));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la récupération de l'utilisateur: " + e.getMessage()
            ));
        }
    }

    /**
     * GET user complete details by ID (Client + SelfDetail + UserIdentity + CheckVerification)
     */
    @GetMapping("/{userId}/complete-details")
    public ResponseEntity<?> getUserCompleteDetails(@PathVariable Long userId) {
        try {
            // Récupérer le client
            Client client = clientService.getClientById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            // Récupérer les informations supplémentaires
            SelfDetail selfDetail = client.getSelfDetail();
            Optional<UserIdentity> userIdentity = userIdentityService.getUserIdentityByUserId(userId);
            Optional<CheckVerification> checkVerification = checkVerificationService.getVerificationByIduser(userId);

            // Construire la réponse complète
            UserCompleteDetailsDTO completeDetails = new UserCompleteDetailsDTO();

            // Informations Client
            completeDetails.setClientId(client.getClientId());
            completeDetails.setFirstName(client.getFirstName());
            completeDetails.setLastName(client.getLastName());
            completeDetails.setEmail(client.getEmail());
            completeDetails.setBirthDate(client.getBirthDate());
            completeDetails.setRole(client.getRole());
            completeDetails.setIsVerified(client.getIsVerified());

            // Informations SelfDetail
            if (selfDetail != null) {
                completeDetails.setUsagePurpose(selfDetail.getUsagePurpose());
                completeDetails.setCinNumber(selfDetail.getCinNumber());
                completeDetails.setPhoneNumber(selfDetail.getPhoneNumber());
                completeDetails.setAge(selfDetail.getAge());
            }

            // Informations UserIdentity
            if (userIdentity.isPresent()) {
                UserIdentity identity = userIdentity.get();
                completeDetails.setIdentityId(identity.getIdv());
                completeDetails.setPhotocinRecto(identity.getPhotocinRecto());
                completeDetails.setPhotocinVerso(identity.getPhotocinVerso());
                completeDetails.setPhotocompletSelfie(identity.getPhotocompletSelfie());
                completeDetails.setIdentityStatus(identity.getEtat());
                completeDetails.setIdentityUploadDate(identity.getUploadDate());
            }

            // Informations CheckVerification
            if (checkVerification.isPresent()) {
                CheckVerification verification = checkVerification.get();
                completeDetails.setVerificationId(verification.getIdverification());
                completeDetails.setEmailVerified(verification.isEtat1());
                completeDetails.setKycSubmitted(verification.isEtat2());
                completeDetails.setKycValidated(verification.isEtat3());
                completeDetails.setFaceRecognition(verification.isEtat4());
                completeDetails.setFullyVerified(
                        verification.isEtat1() &&
                                verification.isEtat2() &&
                                verification.isEtat3() &&
                                verification.isEtat4()
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", completeDetails);
            response.put("hasIdentity", userIdentity.isPresent());
            response.put("hasVerification", checkVerification.isPresent());
            response.put("message", "Détails complets de l'utilisateur récupérés avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la récupération des détails complets: " + e.getMessage()
            ));
        }
    }

    /**
     * GET UserIdentity images only (for display purposes)
     */
    @GetMapping("/{userId}/identity-images")
    public ResponseEntity<?> getUserIdentityImages(@PathVariable Long userId) {
        try {
            Optional<UserIdentity> userIdentity = userIdentityService.getUserIdentityByUserId(userId);

            if (userIdentity.isPresent()) {
                UserIdentity identity = userIdentity.get();
                Map<String, Object> images = new HashMap<>();
                images.put("photocinRecto", identity.getPhotocinRecto());
                images.put("photocinVerso", identity.getPhotocinVerso());
                images.put("photocompletSelfie", identity.getPhotocompletSelfie());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("images", images);
                response.put("identityId", identity.getIdv());
                response.put("status", identity.getEtat());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Aucun document d'identité trouvé pour cet utilisateur"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la récupération des images: " + e.getMessage()
            ));
        }
    }

    /**
     * CHANGE user password
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changeUserPassword(
            @PathVariable Long userId,
            @RequestBody PasswordChangeRequest request) {
        try {
            boolean passwordChanged = clientService.changePassword(userId, request);

            if (passwordChanged) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Mot de passe modifié avec succès");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Ancien mot de passe incorrect"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors du changement de mot de passe: " + e.getMessage()
            ));
        }
    }

    /**
     * UPDATE user verification status
     */
    @PutMapping("/{userId}/verification")
    public ResponseEntity<?> updateUserVerification(
            @PathVariable Long userId,
            @RequestBody UserVerificationRequest request) {
        try {
            Client updatedClient = clientService.updateUserVerification(userId, request.getIsVerified());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Statut de vérification mis à jour avec succès");
            response.put("user", createUserResponse(updatedClient));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la mise à jour du statut de vérification: " + e.getMessage()
            ));
        }
    }

    /**
     * UPDATE user role
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UserRoleUpdateRequest request) {
        try {
            Client updatedClient = clientService.updateUserRole(userId, request.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rôle utilisateur mis à jour avec succès");
            response.put("user", createUserResponse(updatedClient));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la mise à jour du rôle: " + e.getMessage()
            ));
        }
    }

    /**
     * UPDATE UserIdentity validation status
     */
    @PutMapping("/{userId}/identity-status")
    public ResponseEntity<?> updateUserIdentityStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            UserIdentity.ValidationStatus validationStatus = UserIdentity.ValidationStatus.valueOf(status.toUpperCase());

            UserIdentity updatedIdentity = userIdentityService.updateValidationStatus(userId, validationStatus);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Statut de validation des documents mis à jour avec succès");
            response.put("status", updatedIdentity.getEtat());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la mise à jour du statut de validation: " + e.getMessage()
            ));
        }
    }

    /**
     * UPDATE CheckVerification status for a specific step
     */
    @PutMapping("/{userId}/verification-step/{step}")
    public ResponseEntity<?> updateVerificationStep(
            @PathVariable Long userId,
            @PathVariable int step,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean status = request.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le statut est requis"));
            }

            if (step < 1 || step > 4) {
                return ResponseEntity.badRequest().body(createErrorResponse("L'étape doit être entre 1 et 4"));
            }

            CheckVerification updatedVerification = checkVerificationService.updateVerificationStatus(userId, step, status);

            if (updatedVerification != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Étape de vérification " + step + " mise à jour avec succès");
                response.put("step", step);
                response.put("status", status);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Enregistrement de vérification non trouvé pour cet utilisateur"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la mise à jour de l'étape de vérification: " + e.getMessage()
            ));
        }
    }

    /**
     * DELETE user (client and self detail)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            // First delete self detail if exists
            selfDetailService.deleteDetailsByClientId(userId);

            // Delete user identity if exists
            Optional<UserIdentity> userIdentity = userIdentityService.getUserIdentityByUserId(userId);
            if (userIdentity.isPresent()) {
                userIdentityService.deleteUserIdentity(userIdentity.get().getIdv());
            }

            // Delete check verification if exists
            Optional<CheckVerification> checkVerification = checkVerificationService.getVerificationByIduser(userId);
            if (checkVerification.isPresent()) {
                checkVerificationService.deleteCheckVerification(checkVerification.get().getIdverification());
            }

            // Then delete client
            clientService.deleteClient(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Utilisateur supprimé avec succès");
            response.put("deletedUserId", userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Erreur lors de la suppression de l'utilisateur: " + e.getMessage()
            ));
        }
    }

    // Méthodes utilitaires
    private Map<String, Object> createUserResponse(Client client) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("clientId", client.getClientId());
        userResponse.put("firstName", client.getFirstName());
        userResponse.put("lastName", client.getLastName());
        userResponse.put("email", client.getEmail());
        userResponse.put("birthDate", client.getBirthDate());
        userResponse.put("role", client.getRole());
        userResponse.put("isVerified", client.getIsVerified());

        // Include self detail information
        SelfDetail selfDetail = client.getSelfDetail();
        if (selfDetail != null) {
            Map<String, Object> selfDetailResponse = new HashMap<>();
            selfDetailResponse.put("selfDetailId", selfDetail.getSelfDetailId());
            selfDetailResponse.put("usagePurpose", selfDetail.getUsagePurpose());
            selfDetailResponse.put("cinNumber", selfDetail.getCinNumber());
            selfDetailResponse.put("phoneNumber", selfDetail.getPhoneNumber());
            selfDetailResponse.put("age", selfDetail.getAge());
            userResponse.put("selfDetail", selfDetailResponse);
        }

        return userResponse;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }
}