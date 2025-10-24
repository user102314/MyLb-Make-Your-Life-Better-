package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.SelfDetail;

import java.util.Optional;

public interface SelfDetailService {

    // Création/Mise à jour (Save)
    SelfDetail saveOrUpdateDetails(SelfDetail details);

    // Lecture (Find by SelfDetail ID)
    SelfDetail getDetailsById(Long selfDetailId);

    // Logique métier : Trouver par numéro CIN
    Optional<SelfDetail> getDetailsByCinNumber(String cinNumber);

    SelfDetail saveSelfDetail(SelfDetail selfDetail);
    // Suppression
    void deleteDetails(Long selfDetailId);
}