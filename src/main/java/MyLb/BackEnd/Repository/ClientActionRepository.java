// src/main/java/MyLb/BackEnd/Repository/ClientActionRepository.java

package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Estnum.ActionType;
import MyLb.BackEnd.Model.Entities.ClientAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientActionRepository extends JpaRepository<ClientAction, Long> {

    /**
     * Récupère toutes les actions effectuées par un client spécifique, triées par date.
     */
    List<ClientAction> findByClientClientIdOrderByActionDateDesc(Long clientId);

    /**
     * Récupère les actions d'un type spécifique pour un client.
     */
    List<ClientAction> findByClientClientIdAndActionTypeOrderByActionDateDesc(Long clientId, ActionType actionType);
}