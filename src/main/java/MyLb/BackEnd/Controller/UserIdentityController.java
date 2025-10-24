package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.UserIdentity;
import MyLb.BackEnd.Service.UserIdentityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/kyc")
// 🚨 Nécessaire pour les cookies de session
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class UserIdentityController {

    @Autowired
    private UserIdentityService userIdentityService;

    @PostMapping("/upload-documents")
    public ResponseEntity<Map<String, Object>> uploadKycDocuments(
            @RequestParam("cinRecto") MultipartFile cinRecto,
            @RequestParam("cinVerso") MultipartFile cinVerso,
            @RequestParam("selfie") MultipartFile selfie,
            HttpSession session) {

        // 1. Récupération de l'ID utilisateur de la session
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Utilisateur non authentifié. Veuillez vous connecter."
            ));
        }

        if (cinRecto.isEmpty() || cinVerso.isEmpty() || selfie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Tous les 3 documents (Recto, Verso, Selfie) sont requis."
            ));
        }

        try {
            UserIdentity savedIdentity = userIdentityService.registerKycDocuments(
                    userId, cinRecto, cinVerso, selfie
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Documents d'identité soumis avec succès pour vérification.",
                    "etat", savedIdentity.getEtat().toString(),
                    "idv", savedIdentity.getIdv()
            ));

        } catch (IOException e) {
            // Erreur de lecture du fichier
            System.err.println("Erreur de lecture du fichier lors de l'upload KYC: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Échec de la lecture des données du fichier. " + e.getMessage()
            ));
        } catch (Exception e) {
            // 🚨 Gère l'erreur BLOB Data Too Long si elle n'est pas corrigée dans la DB
            System.err.println("Erreur BDD/Service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal Server Error: Taille de données (image) trop grande pour la base de données. Contactez l'administrateur."
            ));
        }
    }
}