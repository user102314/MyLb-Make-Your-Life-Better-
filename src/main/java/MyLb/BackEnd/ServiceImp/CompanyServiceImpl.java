package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Repository.CompanyRepository;
import MyLb.BackEnd.Service.CompanyService;
import MyLb.BackEnd.dto.CompanySummaryResponse; // ⬅️ Import du DTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors; // ⬅️ Import nécessaire pour le mappage Stream

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company createCompany(Company company) {
        // Logique métier : Initialisation de la date d'inscription et du statut
        company.setDateInscri(LocalDate.now().toString());
        if (company.getStatus() == null || company.getStatus().isEmpty()) {
            company.setStatus("PENDING");
        }
        return companyRepository.save(company);
    }

    @Override
    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Société non trouvée avec l'ID: " + companyId));
    }

    @Override
    public Company getCompanyByOwnerId(Long ownerId) {
        // NOTE : Cette méthode semble destinée à retourner UNE seule société.
        // Si un utilisateur peut avoir plusieurs sociétés, cette méthode est ambiguë.
        Company company = companyRepository.findByOwnerID(ownerId);
        if (company == null) {
            throw new EntityNotFoundException("Aucune société trouvée pour l'OwnerID: " + ownerId);
        }
        return company;
    }

    @Override
    public Company updateCompanyStatus(Long companyId, String newStatus) {
        Company company = getCompanyById(companyId);

        // Logique de validation du statut peut être ajoutée ici
        if (newStatus == null || newStatus.isEmpty()) {
            throw new IllegalArgumentException("Le nouveau statut ne peut pas être vide.");
        }

        company.setStatus(newStatus);
        return companyRepository.save(company);
    }

    @Override
    public boolean isOwner(Long userId, Long companyId) {
        return companyRepository.findById(companyId)
                // Vérifie si la société existe ET si son OwnerID correspond à l'userId fourni
                .map(company -> company.getOwnerID() != null && company.getOwnerID().equals(userId))
                .orElse(false);
    }

    // -------------------------------------------------------------
    // ⬇️ IMPLÉMENTATION DE LA NOUVELLE MÉTHODE DE SERVICE
    // -------------------------------------------------------------

    @Override
    public List<CompanySummaryResponse> getCompanySummariesByOwnerId(Long ownerId) {

        // 1. Récupérer TOUTES les entités Company pour cet OwnerID
        List<Company> companies = companyRepository.findAllByOwnerID(ownerId);

        // 2. Mapper chaque entité Company vers le DTO CompanySummaryResponse
        return companies.stream()
                .map(company -> new CompanySummaryResponse(
                        company.getCompanyId(),
                        company.getCompanyName(),
                        company.getStatus(),
                        // Assurez-vous que DateInscri est bien converti en LocalDate si nécessaire
                        // Si le champ dans Company est déjà un String, pas besoin de conversion ici
                        LocalDate.parse(company.getDateInscri())
                ))
                .collect(Collectors.toList());
    }
}