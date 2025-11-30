package MyLb.BackEnd.Repository;

// src/main/java/com/mylb/backend/repository/PostRepository.java


import MyLb.BackEnd.Model.Entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByDateDesc();

    /**
     * Récupère tous les posts créés par un utilisateur spécifique, triés par date.
     */
    List<Post> findByClientIdOrderByDateDesc(Long clientId);
}