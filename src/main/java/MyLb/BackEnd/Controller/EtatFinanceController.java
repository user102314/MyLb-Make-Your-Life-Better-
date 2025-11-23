package MyLb.BackEnd.Controller;

import MyLb.BackEnd.dto.EtatFinanceRequest;
import MyLb.BackEnd.Model.Entities.EtatFinance;
import MyLb.BackEnd.Service.EtatFinanceService;
import MyLb.BackEnd.Service.CompanyService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/company/finance")
public class EtatFinanceController {

    private final EtatFinanceService etatFinanceService;
    private final CompanyService companyService;

    @Autowired
    public EtatFinanceController(
            EtatFinanceService etatFinanceService,
            CompanyService companyService)
    {
        this.etatFinanceService = etatFinanceService;
        this.companyService = companyService;
    }

    /**
     * Endpoint pour soumettre les données financières et le rapport associé (Multipart).
     * @param request Le DTO JSON contenant les chiffres financiers (part 'financeData').
     * @param rapportFile Le fichier binaire du rapport financier (part 'rapportEtatFinancier').
     * @param session Session HTTP pour l'authentification.
     * @return L'entité EtatFinance sauvegardée.
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EtatFinance> submitEtatFinance(
            @Valid @RequestPart("financeData") EtatFinanceRequest request,
            @RequestPart("rapportEtatFinancier") MultipartFile rapportFile,
            HttpSession session) throws IOException
    {
        // --- 1. Vérification de l'Authentification et de l'Autorisation ---
        Long authenticatedUserId = (Long) session.getAttribute("USER_ID");
        Long companyId = request.getCompanyId();

        if (authenticatedUserId == null) {
            // Utilise 401 Unauthorized
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Accès refusé. Veuillez vous connecter.");
        }

        // Vérifie si l'utilisateur est le propriétaire (Business Logic Check)
        if (!companyService.isOwner(authenticatedUserId, companyId)) {
            // Utilise 403 Forbidden
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé. Vous n'êtes pas autorisé à modifier les finances de cette société.");
        }

        // Vérification de la présence du fichier (la contrainte @RequestPart est forte, mais on double-vérifie)
        if (rapportFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le rapport financier est obligatoire.");
        }

        // --- 2. Mappage des données numériques du DTO vers une Entité temporaire ---
        // Cette entité temporaire est utilisée pour passer les données chiffrées au service.
        EtatFinance finance = new EtatFinance();

        finance.setActifTotal(request.getActifTotal());
        finance.setActifImmobilise(request.getActifImmobilise());
        finance.setActifCirculant(request.getActifCirculant());
        finance.setPassifTotal(request.getPassifTotal());
        finance.setCapitauxPropres(request.getCapitauxPropres());
        finance.setDettes(request.getDettes());

        finance.setProduitsTotal(request.getProduitsTotal());
        finance.setChargesTotal(request.getChargesTotal());
        finance.setResultatNet(request.getResultatNet());
        finance.setChiffreAffaires(request.getChiffreAffaires());

        finance.setFluxOperationnels(request.getFluxOperationnels());
        finance.setFluxInvestissement(request.getFluxInvestissement());
        finance.setFluxFinancement(request.getFluxFinancement());
        finance.setVariationNetteTresorerie(request.getVariationNetteTresorerie());

        // --- 3. Sauvegarde via le Service ---
        // companyId est passé comme argument séparé, ce qui permet au service
        // d'affecter manuellement la clé primaire (@Id) à l'entité,
        // résolvant ainsi l'erreur 'Identifier must be manually assigned'.
        try {
            EtatFinance savedFinance = etatFinanceService.saveFinancialData(
                    companyId,          // 1. Clé primaire manuelle (pour le service)
                    finance,            // 2. Les données numériques
                    rapportFile         // 3. Le fichier
            );

            // --- 4. Retourner la réponse ---
            return new ResponseEntity<>(savedFinance, HttpStatus.CREATED);

        } catch (IOException e) {
            // Gère les erreurs de lecture de fichier
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du traitement du fichier rapport.", e);
        }
    }

    /*
     * NOTE IMPORTANTE: D'autres méthodes comme la récupération (GET) devraient être ici.
     * Exemple:
     * @GetMapping("/{companyId}")
     * public ResponseEntity<EtatFinance> getEtatFinance(@PathVariable Long companyId) {
     * // ... logique de vérification et appel au service ...
     * }
     */
}