package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.SelfDetail;
import MyLb.BackEnd.Repository.SelfDetailRepository;
import MyLb.BackEnd.Service.SelfDetailService;
import org.springframework.beans.factory.annotation.Autowired; // ⬅️ NOUVEL IMPORT NÉCESSAIRE
import org.springframework.stereotype.Service;
// import lombok.RequiredArgsConstructor; // ⬅️ LOMBOK SUPPRIMÉ
import java.util.Optional;

@Service
// @RequiredArgsConstructor est supprimé
public class SelfDetailServiceImpl implements SelfDetailService {

    private final SelfDetailRepository selfDetailRepository;

    // CONSTRUCTEUR MANUEL POUR L'INJECTION DE DÉPENDANCE
    @Autowired
    public SelfDetailServiceImpl(SelfDetailRepository selfDetailRepository) {
        this.selfDetailRepository = selfDetailRepository;
    }
    @Override // ⬅️ The @Override annotation is crucial for verification
    public SelfDetail saveSelfDetail(SelfDetail selfDetail) {
        // Use the repository to save the entity to the database
        return selfDetailRepository.save(selfDetail);
    }
    @Override
    public SelfDetail saveOrUpdateDetails(SelfDetail details) {
        // En cas de création, l'ID est null; en cas de mise à jour, l'ID est présent
        return selfDetailRepository.save(details);
    }

    @Override
    public SelfDetail getDetailsById(Long selfDetailId) {
        return selfDetailRepository.findById(selfDetailId)
                .orElseThrow(() -> new RuntimeException("SelfDetail non trouvé avec l'ID: " + selfDetailId));
    }

    @Override
    public Optional<SelfDetail> getDetailsByCinNumber(String cinNumber) {
        return selfDetailRepository.findByCinNumber(cinNumber);
    }

    @Override
    public void deleteDetails(Long selfDetailId) {
        selfDetailRepository.deleteById(selfDetailId);
    }
}