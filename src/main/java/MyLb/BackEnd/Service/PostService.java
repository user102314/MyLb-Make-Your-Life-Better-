package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.Post;
import java.util.List;

public interface PostService {

    Post savePost(Post post);

    List<Post> getAllPosts();

    List<Post> getPostsByUserId(Long clientId);

    // --------------------------------------------------
    // ➡️ AJOUTÉE : Signature de la méthode de création sécurisée
    // --------------------------------------------------
    Post createPost(Long userId, String nomUser, String contenu, byte[] photoBytes);


    // Supprime un post après vérification de l'auteur
    void deletePost(Long postId, Long userId);

    // Signature de la méthode de mise à jour sécurisée
    Post updatePost(Long postId, Long userId, String contenu, byte[] photoBytes);
}