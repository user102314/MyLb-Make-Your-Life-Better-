package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Post;
import MyLb.BackEnd.Repository.PostRepository;
import MyLb.BackEnd.Service.PostService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // N'oubliez pas l'importation!

import org.springframework.data.domain.Sort;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post savePost(Post post) {
        if (post.getDate() == null) {
            post.setDate(java.time.LocalDateTime.now());
        }
        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        // Retourne tous les posts, triés par date décroissante
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @Override
    public List<Post> getPostsByUserId(Long clientId) {
        return postRepository.findByClientIdOrderByDateDesc(clientId);
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Vérification de sécurité: seul l'auteur peut supprimer le post
        if (!post.getClientId().equals(userId)) {
            throw new RuntimeException("Access Denied: User ID " + userId + " is not the author of post ID " + postId);
        }

        postRepository.delete(post);
    }
// DANS VOTRE CLASSE D'IMPLÉMENTATION DU SERVICE (ex: PostServiceImpl.java)


    @Override
    public Post createPost(Long userId, String nomUser, String contenu, byte[] photoBytes) {
        Post post = new Post();
        post.setClientId(userId);
        post.setNomUser(nomUser);
        post.setContenu(contenu);

        // CORRECTION : Utiliser LocalDateTime.now() au lieu de new Date()
        post.setDate(LocalDateTime.now());

        post.setPhotoPost(photoBytes);

        return savePost(post);
    }
    // ⬅️ AJOUTÉ : Implémentation de la méthode de mise à jour sécurisée
    @Override
    public Post updatePost(Long postId, Long userId, String contenu, byte[] photoBytes) {
        // 1. Trouver le Post ou lancer une exception
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // 2. Vérification de sécurité: seul l'auteur peut modifier le post
        if (!existingPost.getClientId().equals(userId)) {
            throw new RuntimeException("Access Denied: User ID " + userId + " is not the author of post ID " + postId);
        }

        // 3. Mettre à jour les champs
        existingPost.setContenu(contenu);

        // La photo est mise à jour si un nouveau fichier est fourni (sinon elle garde l'ancienne valeur)
        // Note: Si photoBytes est null (pas de nouveau fichier), l'ancienne photo est conservée.
        // Si vous voulez une logique pour supprimer une ancienne photo sans en mettre une nouvelle,
        // cette logique doit être gérée ici.
        if (photoBytes != null) {
            existingPost.setPhotoPost(photoBytes);
        }

        // 4. Sauvegarder et retourner le Post mis à jour
        return postRepository.save(existingPost);
    }
}