package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.ClientSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

// Le ClientSecurity est identifié par l'ID du client (Long)
public interface ClientSecurityRepository extends JpaRepository<ClientSecurity, Long> {

    // Pas besoin de méthodes personnalisées si vous utilisez findById(clientId)
}