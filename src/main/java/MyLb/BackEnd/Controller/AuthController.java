package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.dto.ClientUpdateRequest;
import MyLb.BackEnd.dto.LoginRequest;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class AuthController {

    private final ClientService clientService;
    private final EmailService emailService;

    @Autowired
    public AuthController(ClientService clientService, EmailService emailService) {
        this.clientService = clientService;
        this.emailService = emailService;
    }

    //-------------------------------------------------------------
    // 1. LOGIN
    //-------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Long clientId = clientService.authenticate(email, password);

        if (clientId != null) {
            // Authentification réussie et établissement de la session
            session.setAttribute("USER_ID", clientId);
            session.setMaxInactiveInterval(30 * 60);

            // Appel du service Email en arrière-plan (non bloquant)
            try {
                Optional<Client> clientOpt = clientService.getClientById(clientId);
                if (clientOpt.isPresent()) {
                    Client client = clientOpt.get();
                    emailService.sendLoginAlertEmail(client.getEmail(), client.getFirstName());
                }
            } catch (Exception e) {
                System.err.println("Erreur (asynchrone) lors de l'envoi de l'alerte de sécurité: " + e.getMessage());
            }

            // Réponse immédiate
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    //-------------------------------------------------------------
    // 2. GET AUTHENTICATED USER (CORRECTION DU 405)
    //-------------------------------------------------------------
    /**
     * Permet au front-end de récupérer les informations de l'utilisateur connecté via un appel GET.
     * Cette méthode est essentielle pour que le composant Verify.tsx puisse afficher le nom d'utilisateur.
     * @param session La session HTTP courante.
     * @return ClientData (Map ou DTO) contenant le prénom, ou 401 si non authentifié.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            // L'utilisateur n'est pas dans la session (non connecté ou session expirée)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("message", "Utilisateur non authentifié.")
            );
        }

        Optional<Client> clientOpt = clientService.getClientById(userId);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            // Retourne seulement les données nécessaires (firstName) pour correspondre à l'interface Front-end
            return ResponseEntity.ok(
                    Map.of(
                            "firstName", client.getFirstName()
                            // Vous pouvez ajouter d'autres champs nécessaires ici, ex: "email", client.getEmail()
                    )
            );
        } else {
            // Cas rare où l'ID de session n'existe plus dans la base de données
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Collections.singletonMap("message", "Client introuvable.")
            );
        }
    }


    //-------------------------------------------------------------
    // 3. UPDATE CLIENT INFO
    //-------------------------------------------------------------
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
            // Sécurité : ne renvoie jamais le mot de passe
            updatedClient.setPassword(null);
            return ResponseEntity.ok(updatedClient);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}