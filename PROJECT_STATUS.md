# État d'avancement du projet Postgram

Ce document résume l'analyse de l'état actuel du projet Postgram, une architecture microservices pour un réseau social.

## 🏗️ Ce qui est fait (Infrastructure & Socle)

- **Structure du projet** : Maven multi-module fonctionnel avec 11 modules identifiés.
- **Orchestration Docker** : Fichier `docker-compose.yml` complet (PostgreSQL, Redis, Kafka KRaft, MinIO).
- **Configuration** : Système de variables d'environnement via `.env` et `run.sh` pour le lancement local.
- **Shared Module** : Définition des événements Kafka communs (`UserRegisteredEvent`, `PostLikedEvent`, etc.) et des DTOs partagés.
- **Discovery Service** : Service de découverte (Eureka) présent.
- **API Gateway** : Routage de base configuré avec `SecurityConfig`.

## ✅ Services implémentés

### 1. Auth Service (`auth-service`)
- **Authentification** : Serveur d'autorisation OAuth2/OIDC complet.
- **Gestion des utilisateurs** : Inscription, stockage en base (PostgreSQL).
- **Sécurité** : Gestion des JWT, PKCE supporté.
- **Événements** : Publication de `UserRegisteredEvent` via Kafka.

### 2. Notification Service (`notification-service`)
- **Consommation** : Consumer Kafka prêt à recevoir les événements.
- **Persistance** : Stockage des notifications en base PostgreSQL.
- **API** : Endpoints pour récupérer les notifications d'un utilisateur.

### 3. Messaging Service (`messaging-service`)
- **Temps réel** : Support des WebSockets pour le chat.
- **Gestion** : Création de conversations et envoi de messages.
- **Persistance** : Stockage des messages et conversations.

## 🚧 Ce qui est en cours / Partiellement fait

- **Social Service (`social-service`)** :
  - La logique de "Follow/Unfollow" est implémentée.
  - Reste à implémenter : Gestion complète des profils utilisateurs.
- **Tests d'intégration** :
  - Le script `auth_tester.py` permet de valider le flux OAuth2 PKCE.

## ❌ Ce qu'il reste à faire (Priorités)

### 1. Content Service (`content-service`) - **Priorité Haute**
- Actuellement juste un squelette.
- Doit gérer : Création de posts, commentaires, likes.
- Doit émettre les événements Kafka (`PostPublished`, `PostLiked`, `CommentAdded`).

### 2. Feed Service (`feed-service`) - **Priorité Haute**
- Actuellement juste un squelette.
- Doit consommer les événements de `content-service` et `social-service`.
- Doit intégrer **Redis** pour la mise en cache des flux personnalisés.
- Algorithme de ranking de base.

### 3. Media Service (`media-service`) - **Priorité Moyenne**
- Actuellement juste un squelette.
- Doit intégrer **MinIO** pour le stockage des images/vidéos.
- API d'upload et de récupération de médias.

### 4. Améliorations transverses
- **Gateway** : Ajouter du Rate Limiting et une meilleure gestion des erreurs globales.
- **Documentation API** : Intégrer Swagger/OpenAPI sur chaque service.
- **Tests** : Ajouter des tests unitaires et d'intégration (Testcontainers pour Kafka/Postgres).

---
*Analyse générée le 15 avril 2026.*
