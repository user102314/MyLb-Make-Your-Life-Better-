package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Service.ClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client") // Base: /api/client
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Endpoint 1: GET /api/client/me
     * Récupère toutes les informations du client connecté, y compris l'image en Base64.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getConnectedClientInfo(HttpSession session) {

        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        Client client = clientService.getClientById(userId).orElse(null);

        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User data not found for ID: " + userId);
        }

        // --- Préparation de la réponse ---

        // 1. Conversion de l'image en Base64
        String base64Image = null;
        if (client.getProfileImage() != null && client.getProfileImage().length > 0) {
            base64Image = Base64.getEncoder().encodeToString(client.getProfileImage());
        }

        // 2. Nettoyage de l'objet Client avant de l'ajouter à la réponse
        client.setPassword(null);
        client.setProfileImage(null); // On nullifie le champ byte[] pour ne pas le sérialiser en plus

        // 3. Construction de la Map de réponse
        // Cette Map est essentielle pour envoyer l'objet Client propre ET l'image Base64
        Map<String, Object> response = new HashMap<>();
        response.put("client", client);
        response.put("profileImageBase64", base64Image);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 2: GET /api/client/name
     * Récupère le nom et prénom du client connecté.
     */
    @GetMapping("/name")
    public ResponseEntity<?> getConnectedClientName(HttpSession session) {

        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        Client client = clientService.getClientById(userId).orElse(null);

        if (client != null) {
            String fullName = client.getFirstName() + " " + client.getLastName();
            return ResponseEntity.ok(fullName);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User data not found.");
        }
    }
}