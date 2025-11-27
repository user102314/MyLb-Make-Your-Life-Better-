package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.dto.ClientUpdateRequest;
import MyLb.BackEnd.dto.LoginRequest;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.EmailService;
import MyLb.BackEnd.Service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class AuthController {
    private final ClientService clientService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    public AuthController(ClientService clientService, EmailService emailService, NotificationService notificationService) {
        this.clientService = clientService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Long clientId = clientService.authenticate(email, password);

        if (clientId != null) {
            // 1. Récupérer l'entité Client pour obtenir le rôle et le nom
            Optional<Client> clientOpt = clientService.getClientById(clientId);
            if (clientOpt.isEmpty()) {
                // L'utilisateur est authentifié mais l'entité n'existe pas (Erreur critique)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Collections.singletonMap("message", "Erreur serveur interne: Client introuvable.")
                );
            }
            Client client = clientOpt.get();
            String role = client.getRole(); // ⬅️ Récupération du rôle

            // ... (Logique SecurityContextHolder et Session inchangée) ...
            User principal = new User(String.valueOf(clientId), "", List.of());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("USER_ID", clientId);
            session.setMaxInactiveInterval(30 * 60);

            // 🔔 AJOUT: Créer une notification de bienvenue pour la connexion
            try {
                notificationService.creerNotification(
                        "Connexion réussie",
                        "Bonjour " + client.getFirstName() + ", vous vous êtes connecté avec succès à votre compte MyLB Capital. Bienvenue !",
                        clientId
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de la création de la notification de connexion: " + e.getMessage());
                // Ne pas bloquer la connexion si la notification échoue
            }

            try {
                emailService.sendLoginAlertEmail(client.getEmail(), client.getFirstName());
            } catch (Exception e) {
                System.err.println("Erreur (asynchrone) lors de l'envoi de l'alerte de sécurité: " + e.getMessage());
            }

            // 2. Retourner le rôle (et d'autres infos utiles comme le nom d'utilisateur)
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "role", role, // ⬅️ Rôle inclus dans la réponse
                            "firstName", client.getFirstName()
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("success", false)
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("message", "Utilisateur non authentifié.")
            );
        }
        Optional<Client> clientOpt = clientService.getClientById(userId);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            return ResponseEntity.ok(
                    Map.of(
                            "firstName", client.getFirstName()
                    )
            );
        } else {
            // Cas rare où l'ID de session n'existe plus dans la base de données
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("message", "Client introuvable.")
            );
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateClientInfo(
            @RequestBody ClientUpdateRequest updateRequest,
            HttpSession session)
    {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        try {
            Client updatedClient = clientService.updateClientProfile(userId, updateRequest);
            updatedClient.setPassword(null);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}