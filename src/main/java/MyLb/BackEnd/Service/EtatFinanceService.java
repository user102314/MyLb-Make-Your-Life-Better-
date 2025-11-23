package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.EtatFinance;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface EtatFinanceService {


    EtatFinance saveFinancialData(Long companyId, EtatFinance financialData, MultipartFile rapport) throws IOException;


    EtatFinance getFinancialData(Long companyId);
}