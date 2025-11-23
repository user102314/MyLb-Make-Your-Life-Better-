package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Model.Entities.CompanyValidation;
import MyLb.BackEnd.Model.Entities.EtatFinance;
import MyLb.BackEnd.Model.Estnum.CompanyStatus;
import MyLb.BackEnd.Repository.CompanyRepository;
import MyLb.BackEnd.Repository.CompanyValidationRepository;
import MyLb.BackEnd.Repository.EtatFinanceRepository;
import MyLb.BackEnd.Service.AdminCompanyService;
import MyLb.BackEnd.dto.CompanyDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCompanyServiceImpl implements AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyValidationRepository validationRepository;
    private final EtatFinanceRepository financeRepository;

    public AdminCompanyServiceImpl(
            CompanyRepository companyRepository,
            CompanyValidationRepository validationRepository,
            EtatFinanceRepository financeRepository)
    {
        this.companyRepository = companyRepository;
        this.validationRepository = validationRepository;
        this.financeRepository = financeRepository;
    }

    @Override
    public CompanyDetailsResponse getCompanyDetails(Long companyId) {
        System.out.println("🔍 Recherche de la société ID: " + companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Société non trouvée avec l'ID: " + companyId));

        System.out.println("✅ Société trouvée: " + company.getCompanyName());

        CompanyValidation validation = validationRepository.findById(companyId).orElse(null);
        System.out.println("📄 Validation trouvée: " + (validation != null ? "OUI" : "NON"));
        if (validation != null) {
            System.out.println("   - Nom légal: " + validation.getNomLegalComplet());
        }

        EtatFinance finance = financeRepository.findById(companyId).orElse(null);
        System.out.println("💰 Finance trouvée: " + (finance != null ? "OUI" : "NON"));
        if (finance != null) {
            System.out.println("   - Actif Total: " + finance.getActifTotal());
        }

        return new CompanyDetailsResponse(company, validation, finance);
    }

    @Override
    public List<CompanyDetailsResponse> getPendingCompanyDetails() {
        List<Company> pendingCompanies = companyRepository.findByStatus(CompanyStatus.PENDING);

        return pendingCompanies.stream()
                .map(company -> {
                    Long companyId = company.getCompanyId();
                    CompanyValidation validation = validationRepository.findById(companyId).orElse(null);
                    EtatFinance finance = financeRepository.findById(companyId).orElse(null);
                    return new CompanyDetailsResponse(company, validation, finance);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyDetailsResponse> getAllCompanyDetails() {
        System.out.println("🔍 Récupération de TOUTES les sociétés...");

        List<Company> companies = companyRepository.findAll();
        System.out.println("✅ Nombre de sociétés trouvées: " + companies.size());

        return companies.stream()
                .map(company -> {
                    Long companyId = company.getCompanyId();
                    System.out.println("   📌 Traitement société ID: " + companyId + " - " + company.getCompanyName());

                    CompanyValidation validation = validationRepository.findById(companyId).orElse(null);
                    EtatFinance finance = financeRepository.findById(companyId).orElse(null);

                    System.out.println("      Validation: " + (validation != null ? "✓" : "✗"));
                    System.out.println("      Finance: " + (finance != null ? "✓" : "✗"));

                    return new CompanyDetailsResponse(company, validation, finance);
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ NOUVEAU : Met à jour le statut d'une société (ACCEPTED, REJECTED, PENDING).
     */
    @Override
    public void updateCompanyStatus(Long companyId, String status) {
        System.out.println("🔄 Mise à jour du statut pour société ID: " + companyId + " -> " + status);

        // 1. Trouver la société
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Société non trouvée avec l'ID: " + companyId));

        // 2. Mettre à jour le statut
        company.setStatus(status);

        // 3. Sauvegarder
        companyRepository.save(company);

        System.out.println("✅ Statut mis à jour avec succès pour: " + company.getCompanyName());
    }
}