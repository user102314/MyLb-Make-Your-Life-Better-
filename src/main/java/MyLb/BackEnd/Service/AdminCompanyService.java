// package MyLb.BackEnd.Service;
// Nom de fichier : AdminCompanyService.java

package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.CompanyDetailsResponse;
import java.util.List;

public interface AdminCompanyService {

    /**
     * Récupère les détails complets d'une seule société par son ID.
     */
    CompanyDetailsResponse getCompanyDetails(Long companyId);
    public List<CompanyDetailsResponse> getPendingCompanyDetails();
    List<CompanyDetailsResponse> getAllCompanyDetails();
    void updateCompanyStatus(Long companyId, String status);

}