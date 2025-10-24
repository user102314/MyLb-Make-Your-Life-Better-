package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.dto.ClientUpdateRequest;
import MyLb.BackEnd.dto.LoginRequest;
import MyLb.BackEnd.Service.ClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class AuthController {

    private final ClientService clientService;

    @Autowired
    public AuthController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Méthode de Connexion : Définit l'ID dans la session
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Long clientId = clientService.authenticate(email, password);

        if (clientId != null) {

            // 🚨 ACTION CRITIQUE: Stockage de l'ID client dans la session
            session.setAttribute("USER_ID", clientId);

            session.setMaxInactiveInterval(30 * 60);

            return ResponseEntity.ok(true);
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    // Méthode de Mise à Jour : Récupère l'ID de la session
    @PutMapping("/me")
    public ResponseEntity<?> updateClientInfo(
            @RequestBody ClientUpdateRequest updateRequest,
            HttpSession session)
    {
        // 1. Récupérer l'ID de session (cast direct pour la cohérence)
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