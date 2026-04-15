# État d'avancement du projet Postgram

Ce document résume l'analyse de l'état actuel du projet Postgram, une architecture microservices pour un réseau social.

## 🏗️ Ce qui est fait (Infrastructure & Socle)

- **Structure du projet** : Maven multi-module fonctionnel (Java 17 / Spring Boot 3.2.5).
- **Orchestration Docker** : Fichier `docker-compose.yml` complet (PostgreSQL, Redis, Kafka KRaft, MinIO).
- **Configuration** : Système de variables d'environnement via `.env` et `run.sh` pour le lancement local ordonné (Discovery -> Services -> Gateway).
- **Shared Module** : Définition des événements Kafka communs et DTOs partagés.
- **Discovery Service** : Eureka Server opérationnel sur le port 8761.
- **API Gateway** : Routage fonctionnel sur le port 8080 avec sécurité JWT.

## ✅ Services implémentés

### 1. Auth Service (`auth-service`)

- **Authentification** : Serveur d'autorisation OAuth2/OIDC complet.
- **Gestion des utilisateurs** : Inscription, stockage en base (PostgreSQL).
- **Sécurité** : Gestion des JWT (avec claims personnalisés `user_id`), support PKCE.

### 2. Content Service (`content-service`)

- **Posts** : API CRUD complète (création, lecture, mise à jour, suppression).
- **Interactions** : Gestion des likes (avec protection contre les doublons) et des commentaires.
- **Événements** : Publication d'événements Kafka (`PostPublished`, `PostLiked`, `CommentAdded`).
- **Validation** : Validé via script de test automatisé.

### 3. Notification Service (`notification-service`)

- **Consommation** : Consumer Kafka prêt à recevoir les événements.
- **Persistance** : Stockage des notifications en base PostgreSQL.

### 4. Messaging Service (`messaging-service`)

- **Temps réel** : Support des WebSockets pour le chat.
- **Gestion** : Création de conversations et envoi de messages.

## 🚧 Ce qui est en cours / Partiellement fait

- **Social Service (`social-service`)** :
  - La logique de "Follow/Unfollow" est implémentée.
  - Reste à implémenter : Gestion complète des profils utilisateurs.

## ❌ Ce qu'il reste à faire (Priorités)

### 1. Feed Service (`feed-service`) - **Priorité Haute**

- Doit consommer les événements de `content-service` et `social-service`.
- Doit intégrer **Redis** pour la mise en cache des flux personnalisés.

### 2. Media Service (`media-service`) - **Priorité Moyenne**

- Doit intégrer **MinIO** pour le stockage des images/vidéos.
- API d'upload et de récupération de médias.

---

_Dernière mise à jour le 15 avril 2026._
