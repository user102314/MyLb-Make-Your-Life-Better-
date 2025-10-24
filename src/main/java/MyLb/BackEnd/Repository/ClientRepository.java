package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    // Méthode spécifique pour l'authentification
    // Utilise la requête JPQL explicite pour contourner les problèmes de findBy...
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Client c WHERE c.email = :email AND c.password = :password")
    Optional<Client> findByEmailAndPassword(String email, String password);

    // Recherche par email (pour vérifier l'unicité lors de l'inscription)
    Optional<Client> findByEmail(String email);
}