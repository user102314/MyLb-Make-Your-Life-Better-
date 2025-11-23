package MyLb.BackEnd.Controller;

// src/main/java/com/mylb/backend/controller/PasswordController.java


import MyLb.BackEnd.dto.PasswordChangeRequest;
import MyLb.BackEnd.Service.ClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class PasswordController {

    private final ClientService clientService;

    public PasswordController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Endpoint pour modifier le mot de passe d'un utilisateur connecté.
     * Requête: PUT /api/password/change
     * Corps: { "currentPassword": "...", "newPassword": "..." }
     */
    @PutMapping("/change")
    public ResponseEntity<?> changePassword(
            @RequestBody PasswordChangeRequest request,
            HttpSession session)
    {
        // 1. Récupérer l'ID de l'utilisateur connecté depuis la session
        Long userId = (Long) session.getAttribute("USER_ID");

        // 🚨 NOTE: En utilisant Spring Security, on utiliserait:
        // Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        if (userId == null) {
            // Non authentifié
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Utilisateur non authentifié.");
        }

        try {
            // 2. Appeler le service pour effectuer la vérification et la mise à jour
            boolean success = clientService.changePassword(userId, request);

            if (success) {
                return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
            } else {
                // Ancien mot de passe incorrect
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("L'ancien mot de passe est incorrect.");
            }
        } catch (RuntimeException e) {
            // Erreur si l'utilisateur n'est pas trouvé (peu probable si l'ID vient de la session)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}