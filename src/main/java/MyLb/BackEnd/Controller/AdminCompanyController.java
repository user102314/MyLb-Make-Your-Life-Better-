package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.AdminCompanyService;
import MyLb.BackEnd.dto.CompanyDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    public AdminCompanyController(AdminCompanyService adminCompanyService) {
        this.adminCompanyService = adminCompanyService;
    }

    /**
     * Endpoint pour afficher les détails de TOUTES les sociétés.
     * GET /api/admin/companies
     */
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDetailsResponse>> getAllCompaniesDetails() {
        List<CompanyDetailsResponse> details = adminCompanyService.getAllCompanyDetails();
        return ResponseEntity.ok(details);
    }

    /**
     * Endpoint pour afficher les détails d'une seule société par ID.
     * GET /api/admin/companies/{companyId}
     */
    @GetMapping("/companies/{companyId}")
    public ResponseEntity<CompanyDetailsResponse> getCompanyDetails(@PathVariable Long companyId) {
        CompanyDetailsResponse details = adminCompanyService.getCompanyDetails(companyId);
        return ResponseEntity.ok(details);
    }

    /**
     * ✅ NOUVEAU : Endpoint pour les demandes d'identité en attente (PENDING)
     * GET /api/admin/identity-application
     */
    @GetMapping("/identity-application")
    public ResponseEntity<List<CompanyDetailsResponse>> getPendingApplications() {
        List<CompanyDetailsResponse> pendingCompanies = adminCompanyService.getPendingCompanyDetails();
        return ResponseEntity.ok(pendingCompanies);
    }

    /**
     * ✅ NOUVEAU : Endpoint pour approuver ou rejeter une société
     * PUT /api/admin/companies/{companyId}/status
     */
    @PutMapping("/companies/{companyId}/status")
    public ResponseEntity<String> updateCompanyStatus(
            @PathVariable Long companyId,
            @RequestBody StatusUpdateRequest request) {

        // Appeler le service pour mettre à jour le statut
        adminCompanyService.updateCompanyStatus(companyId, request.getStatus());

        return ResponseEntity.ok("Statut mis à jour avec succès");
    }

    /**
     * Classe interne pour la requête de mise à jour du statut
     */
    public static class StatusUpdateRequest {
        private String status; // "ACCEPTED", "REJECTED", ou "PENDING"

        public StatusUpdateRequest() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}