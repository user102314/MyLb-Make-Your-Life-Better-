package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Post;
import MyLb.BackEnd.Service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ------------------------------------------------------------------
    // 1. CRÉER UN NOUVEAU POST (Méthode manquante - AJOUTÉE)
    // ------------------------------------------------------------------
    @PostMapping // Gère POST /api/posts
    public ResponseEntity<Post> createPost(
            // Récupère les données du formulaire (FormData) envoyé par le front-end
            @RequestParam("contenu") String contenu,
            @RequestParam("nomUser") String nomUser,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            HttpSession session) // Récupère l'ID utilisateur de la session
    {
        // 1. Récupérer l'ID de l'utilisateur de la session
        Long authenticatedUserId = (Long) session.getAttribute("USER_ID");

        if (authenticatedUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 si non connecté
        }

        try {
            // 2. Traitement de l'image (conversion en tableau de bytes)
            byte[] photoBytes = (photo != null && !photo.isEmpty()) ? photo.getBytes() : null;

            // 3. Appel au service de création
            Post newPost = postService.createPost(
                    authenticatedUserId,
                    nomUser,
                    contenu,
                    photoBytes
            );

            return new ResponseEntity<>(newPost, HttpStatus.CREATED); // 201 Created
        } catch (IOException e) {
            // Erreur lors de la lecture du fichier
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Autres erreurs (service, base de données, etc.)
            System.err.println("Erreur lors de la création du post: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des posts: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // ------------------------------------------------------------------
    // 2. AFFICHER LES POSTS DE L'UTILISATEUR CONNECTÉ
    // ------------------------------------------------------------------
    @GetMapping("/my-posts") // Gère GET /api/posts/my-posts
    public ResponseEntity<List<Post>> getMyPosts(HttpSession session) {

        Long authenticatedUserId = (Long) session.getAttribute("USER_ID");

        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié.");
        }

        List<Post> posts = postService.getPostsByUserId(authenticatedUserId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    // ------------------------------------------------------------------
    // 3. MODIFIER UN POST EXISTANT
    // ------------------------------------------------------------------
    @PutMapping("/{postId}") // Gère PUT /api/posts/{postId}
    public ResponseEntity<Post> editPost(
            @PathVariable Long postId,
            @RequestParam("contenu") String contenu,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            HttpSession session)
    {
        Long authenticatedUserId = (Long) session.getAttribute("USER_ID");
        if (authenticatedUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            byte[] photoBytes = (photo != null && !photo.isEmpty()) ? photo.getBytes() : null;

            // Le service vérifie la propriété
            Post updatedPost = postService.updatePost(postId, authenticatedUserId, contenu, photoBytes);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            // 403 Forbidden si l'utilisateur n'est pas le propriétaire
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    // ------------------------------------------------------------------
    // 4. SUPPRIMER UN POST
    // ------------------------------------------------------------------
    @DeleteMapping("/{postId}") // Gère DELETE /api/posts/{postId}
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            HttpSession session) {

        Long authenticatedUserId = (Long) session.getAttribute("USER_ID");

        if (authenticatedUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            // Le service vérifie la propriété
            postService.deletePost(postId, authenticatedUserId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}