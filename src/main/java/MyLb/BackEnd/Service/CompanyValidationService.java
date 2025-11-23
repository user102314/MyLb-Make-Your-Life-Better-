package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.CompanyValidation;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface CompanyValidationService {


    CompanyValidation saveValidationData(Long companyId, CompanyValidation validationData,
                                         MultipartFile certificatImmatriculation,
                                         MultipartFile pieceIdentiteRepresentantLegal,
                                         MultipartFile statutsSociete,
                                         MultipartFile justificatifDomiciliationCommerciale) throws IOException;


    CompanyValidation getValidationData(Long companyId);

    boolean isLegallyComplete(Long companyId);
    CompanyValidation saveValidation(CompanyValidation validation);
}