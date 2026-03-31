# Postgram — Projet Services Web

Application de réseau social construite en architecture microservices avec Spring Boot, dans le cadre du cours de Services Web.

## Objectifs pédagogiques

Ce projet applique les concepts vus en cours :

- Architecture **microservices** avec Spring Boot
- **API Gateway** et routing avec Spring Cloud Gateway
- **Authentification** et autorisation avec Spring Security + JWT
- **Communication asynchrone** via Apache Kafka (events)
- **Cache distribué** avec Redis
- **Stockage objet** avec MinIO (compatible S3)
- Conteneurisation avec **Docker Compose**

---

## Architecture

```
Client
  │
  ▼
API Gateway  (routing · rate limiting)
  │
  ├── auth          login · register · JWT
  ├── content       posts · comments · likes
  ├── social        follow · profil
  ├── feed          news feed · ranking · cache Redis
  ├── notification  like · follow · comment
  ├── messaging     messages temps réel · WebSocket
  └── media         upload · stockage MinIO

Infrastructure
  ├── PostgreSQL            base de données partagée
  ├── Redis                 cache du feed
  ├── Kafka (KRaft)         event bus
  └── MinIO                 fichiers média
```

### Événements Kafka

| Événement | Émetteur | Consommateurs |
|---|---|---|
| `PostPublished` | content-service | feed-service, notification-service |
| `PostLiked` | content-service | feed-service, notification-service |
| `PostDeleted` | content-service | feed-service |
| `CommentAdded` | content-service | notification-service |
| `UserFollowed` | social-service | feed-service, notification-service |
| `UserUnfollowed` | social-service | feed-service |

---

## Stack technique

| Technologie              | Usage |
|--------------------------|---|
| Java 21                  | Langage |
| Spring Boot 4.0.5        | Framework applicatif |
| Spring Cloud Gateway     | API Gateway |
| Spring Security + JWT    | Authentification |
| Spring Data JPA          | Accès base de données |
| Spring Kafka             | Producer / Consumer Kafka |
| Spring Data Redis        | Cache |
| Spring WebSocket         | Messaging temps réel |
| PostgreSQL 16            | Base de données |
| Redis 7                  | Cache du feed |
| Apache Kafka 3.9 (KRaft) | Event bus |
| MinIO                    | Stockage médias |
| Docker Compose           | Orchestration locale |
| Maven                    | Build tool |

---

## Lancer le projet

### Prérequis

- Java 21
- Maven 3.9+
- Docker Desktop

### 1. Démarrer l'infrastructure

```bash
cp .env.example .env        # copier et ajuster les variables
docker compose up -d        # démarre Postgres, Redis, Kafka, MinIO
docker compose ps           # vérifier que tout tourne
```

### 2. Compiler le projet

```bash
mvn clean install
```

### 3. Démarrer les services

Depuis IntelliJ, lancer chaque `*Application.java` dans cet ordre :

1. `auth-service`
2. `content-service`
3. `social-service`
4. `feed-service`
5. `notification-service`
6. `messaging-service`
7. `media-service`
8. `gateway`

### Ports exposés

| Service | Port |
|---|---|
| API Gateway | 8080 |
| auth-service | 8081 |
| content-service | 8082 |
| social-service | 8083 |
| feed-service | 8084 |
| notification-service | 8085 |
| messaging-service | 8086 |
| media-service | 8087 |
| PostgreSQL | 5432 |
| Redis | 6379 |
| Kafka | 9092 |
| MinIO API | 9000 |
| MinIO Console | 9001 |

---

## Variables d'environnement

Créer un fichier `.env` à la racine (voir `.env.example`) :

```env
# Postgres
POSTGRES_DB=social_db
POSTGRES_USER=social_user
POSTGRES_PASSWORD=changeme_postgres

# Redis
REDIS_PASSWORD=changeme_redis

# MinIO
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=changeme_minio
```

> Le fichier `.env` est dans le `.gitignore` et ne doit jamais être versionné.