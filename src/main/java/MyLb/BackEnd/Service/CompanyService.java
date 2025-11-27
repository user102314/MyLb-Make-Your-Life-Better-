package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.dto.CompanySummaryResponse;

import java.util.List;

public interface CompanyService {

    Company createCompany(Company company);

    Company getCompanyById(Long companyId);

    Company getCompanyByOwnerId(Long ownerId);
    List<Company> getAllCompanies();

    Long getTotalCompanies();
    Company updateCompanyStatus(Long companyId, String newStatus);
    List<CompanySummaryResponse> getCompanySummariesByOwnerId(Long ownerId);
    public boolean isOwner(Long userId, Long companyId);
}
