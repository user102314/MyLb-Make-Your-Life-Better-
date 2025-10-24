"# MyLb-Make-Your-Life-Better-" 

Absolument. Voici un fichier README.md complet pour votre projet backend Spring Boot, couvrant les fonctionnalités que nous avons développées (Authentification par Session, KYC avec BLOB, et Suivi de Vérification).

🚀 Backend Spring Boot - MyLB Application
Ce projet est le composant backend de l'application MyLB, construit avec Spring Boot et Java 17. Il gère l'authentification des utilisateurs, la soumission et le stockage des documents d'identité (KYC), et le suivi de la progression de la vérification grâce à un système d'étapes (CheckVerification).

🛠️ Technologies Utilisées
Langage : Java 17+

Framework : Spring Boot 3.x

Base de données : MySQL (Recommandé pour le type MEDIUMBLOB) ou PostgreSQL

ORM : Spring Data JPA / Hibernate

Sécurité/Session : Gestion de session via HttpSession (pour l'authentification et l'état des requêtes)

⚙️ Configuration Requise
1. Base de Données
Ce projet nécessite une base de données configurée.

Configuration BLOB (Cruciale pour KYC) :

Étant donné que les images sont stockées en base de données, assurez-vous que les colonnes de votre table user_identity sont de type MEDIUMBLOB pour supporter des fichiers jusqu'à 16 Mo (contrairement au BLOB standard, limité à 64 Ko).

MySQL: Dans application.properties, assurez-vous d'avoir :

Properties

spring.jpa.hibernate.ddl-auto=update
# Ou exécutez manuellement la requête suivante :
# ALTER TABLE user_identity MODIFY COLUMN photocin_recto MEDIUMBLOB;
# (et pour les deux autres colonnes photo)
2. Variables d'Environnement
Configurez votre fichier application.properties ou application.yml pour la connexion DB et l'email :

Properties

# Configuration de la base de données (exemple MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/votre_base_de_donnees
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update

# Configuration CORS (Assurez-vous que l'URL de votre front-end est correcte)
frontend.url=http://localhost:8081

# Configuration Email (pour l'envoi du code de vérification)
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=votre_email@example.com
spring.mail.password=votre_mot_de_passe_smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


Note sur l'Authentification (CORS & Session)
Ce backend utilise l'authentification basée sur la session HTTP (HttpSession).

Pour garantir que votre front-end (tournant sur http://localhost:8081) puisse s'authentifier, il est crucial que :

Côté Frontend : Tous les appels fetch ou Axios incluent l'option credentials: 'include'.

Côté Backend : Tous les contrôleurs sensibles ont l'annotation :

Java

@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
Ceci permet à Spring de créer le cookie JSESSIONID après le login et au navigateur de le renvoyer automatiquement avec les requêtes suivantes.

📦 Démarrage
Clonez ce référentiel.

Configurez votre base de données et les propriétés dans application.properties.

Exécutez l'application via votre IDE (IntelliJ, Eclipse) ou via la ligne de commande :

Bash

./mvnw spring-boot:run
# L'application sera disponible sur http://localhost:9090 (par défaut)
