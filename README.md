# E-Learning Center

Nowoczesna platforma e-learningowa umożliwiająca tworzenie, zarządzanie i udostępnianie kursów online z systemem quizów i zarządzania materiałami.

## Spis treści

- [Opis projektu](#opis-projektu)
- [Funkcjonalności](#funkcjonalności)
- [Architektura](#architektura)
- [Wymagania systemowe](#wymagania-systemowe)
- [Instalacja i uruchomienie](#instalacja-i-uruchomienie)
- [Konfiguracja](#konfiguracja)
- [Struktura projektu](#struktura-projektu)
- [API Documentation](#api-documentation)
- [Użyte technologie](#użyte-technologie)

## Opis projektu

E-Learning Center to kompleksowa platforma edukacyjna typu LMS (Learning Management System) zbudowana w architekturze mikroserwisowej. System umożliwia instruktorom tworzenie i zarządzanie kursami, a uczniom zapisywanie się na kursy, przeglądanie materiałów oraz rozwiązywanie quizów.

### Główne możliwości:

- **Dla instruktorów:**
  - Tworzenie i edycja kursów z wielopoziomową strukturą (kursy → sekcje → lekcje)
  - System quizów z pytaniami jednokrotnego i wielokrotnego wyboru
  - Zarządzanie materiałami dydaktycznymi (pliki i linki)
  - Drag & drop do zmiany kolejności sekcji i lekcji
  - Zarządzanie cenami i publikacją kursów

- **Dla studentów:**
  - Przeglądanie i zapisywanie się na kursy
  - Dostęp do materiałów kursowych
  - Rozwiązywanie quizów z automatycznym sprawdzaniem
  - System postępu i punktacji
  - Zarządzanie saldem konta

## Funkcjonalności

### System użytkowników
- Rejestracja i logowanie z weryfikacją email
- Role użytkowników (Student, Instructor, Admin)
- Resetowanie hasła
- Zarządzanie profilem

### Zarządzanie kursami
- Hierarchiczna struktura: Kurs → Sekcje → Lekcje
- Drag & drop do zmiany kolejności elementów
- Wsparcie dla różnych poziomów trudności
- Kategoryzacja kursów
- System publikacji (draft/published)

### System quizów
- Quizy na poziomie kursu, sekcji i lekcji
- Pytania jednokrotnego i wielokrotnego wyboru
- Automatyczne sprawdzanie odpowiedzi
- Minimalny próg zaliczenia (passing score)
- Historia prób i wyników

### Materiały dydaktyczne
- Przesyłanie plików (PDF, dokumenty, obrazy)
- Dodawanie linków zewnętrznych
- Zarządzanie materiałami w ramach lekcji

### System płatności i zapisów
- System sald użytkowników
- Zapisywanie na kursy płatne i bezpłatne
- Historia transakcji

## Architektura

### Backend (Spring Boot)
```
backend/
├── domain/              # Logika biznesowa (DDD)
│   ├── course/         # Agregaty Course, Section, Lesson
│   ├── quiz/           # Quiz i Question
│   ├── user/           # User management
│   └── enrollment/     # System zapisów
├── application/        # Use cases i handlers
│   ├── command/        # Command handlers (CQRS)
│   └── query/          # Query handlers (CQRS)
├── infrastructure/     # Implementacje techniczne
│   ├── persistence/    # Repositories (JPA)
│   ├── security/       # Spring Security
│   ├── config/         # Konfiguracja
│   └── messaging/      # RabbitMQ
└── interfaces/         # REST API
    └── rest/           # Controllers
```

**Wzorce projektowe:**
- Domain-Driven Design (DDD)
- CQRS (Command Query Responsibility Segregation)
- Repository Pattern
- Aggregate Root
- Value Objects

### Frontend (React + TypeScript)
```
frontend/
├── src/
│   ├── components/     # Komponenty reużywalne
│   ├── pages/         # Widoki aplikacji
│   ├── services/      # API clients
│   ├── contexts/      # Context API (auth, state)
│   ├── hooks/         # Custom hooks
│   └── layouts/       # Layout components
```

## Wymagania systemowe

### Wymagane oprogramowanie

- **Docker** (wersja 20.10 lub nowsza)
- **Docker Compose** (wersja 2.0 lub nowsza)
- **Git**

### Opcjonalne (do development bez Docker)

- **Backend:**
  - Java 21 (JDK)
  - Maven 3.9+
  - PostgreSQL 16
  - RabbitMQ 3.x

- **Frontend:**
  - Node.js 20+
  - npm lub yarn
  - 
## Instalacja i uruchomienie

### Opcja 1: Docker Compose

1. **Sklonuj repozytorium:**
```bash
git clone <repository-url>
cd ElearningCenter
```

2. **Skonfiguruj zmienne środowiskowe:**
```bash
cp .env.example .env
# Edytuj .env jeśli potrzeba dostosować konfigurację
```

3. **Uruchom aplikację:**
```bash
make up-build
```

4. **Sprawdź status kontenerów:**
```bash
docker-compose ps
```

5. **Aplikacja będzie dostępna pod adresami:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8081
   - RabbitMQ Management: http://localhost:15672 (guest/guest)

6. **Zatrzymanie aplikacji:**
```bash
docker-compose down
```

### Opcja 2: Uruchomienie lokalne (Development)

#### Backend

1. **Uruchom PostgreSQL i RabbitMQ:**
```bash
docker-compose up -d postgres rabbitmq
```

2. **Przejdź do katalogu backend:**
```bash
cd backend
```

3. **Uruchom aplikację:**
```bash
./mvnw spring-boot:run
```

Backend będzie dostępny na: http://localhost:8080

#### Frontend

1. **Zainstaluj zależności:**
```bash
cd frontend
npm install
```

2. **Uruchom serwer deweloperski:**
```bash
npm run dev
```

Frontend będzie dostępny na: http://localhost:5173

## Konfiguracja

### Zmienne środowiskowe (.env)

```bash
# Database
POSTGRES_DB=elearning_db
POSTGRES_USER=elearning_user
POSTGRES_PASSWORD=elearning_pass
POSTGRES_PORT=5432

# Backend
BACKEND_PORT=8080
SPRING_JPA_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8081

# Frontend
FRONTEND_PORT=3000
VITE_API_BASE_URL=http://localhost:8080/api

# Email (Mailtrap dla testów)
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=587
MAIL_USERNAME=your-mailtrap-username
MAIL_PASSWORD=your-mailtrap-password

# RabbitMQ
RABBITMQ_USER=elearning_user
RABBITMQ_PASSWORD=elearning_pass
RABBITMQ_PORT=5672
```

### Baza danych

Aplikacja automatycznie tworzy schemat bazy danych przy pierwszym uruchomieniu dzięki `spring.jpa.hibernate.ddl-auto=update`.

**Ważne relacje w bazie:**
- `courses` - główna tabela kursów
- `sections` - sekcje w ramach kursu
- `lessons` - lekcje w sekcjach
- `quizzes` - quizy (przypisane do kursu/sekcji/lekcji)
- `questions` - pytania quizowe
- `users` - użytkownicy systemu
- `enrollments` - zapisy na kursy

### Porty

| Serwis | Port  | Opis |
|--------|-------|------|
| Frontend | 3000  | Aplikacja React (produkcja) |
| Backend | 8080  | Spring Boot API |
| PostgreSQL | 5432  | Baza danych |
| RabbitMQ | 5672  | AMQP |
| RabbitMQ Management | 15672 | Panel zarządzania RabbitMQ |
| Swagger UI | 8081  | Dokumentacja API |

## Struktura projektu

```
ElearningCenter/
├── backend/                  # Aplikacja Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/pl/dominik/elearningcenter/
│   │   │   │   ├── domain/              # Logika biznesowa
│   │   │   │   ├── application/         # Use cases
│   │   │   │   ├── infrastructure/      # Implementacje
│   │   │   │   └── interfaces/          # API endpoints
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                 # Aplikacja React
│   ├── src/
│   │   ├── components/      # Komponenty UI
│   │   ├── pages/          # Strony aplikacji
│   │   ├── services/       # API clients
│   │   ├── contexts/       # React Context
│   │   └── App.tsx
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml     
├── .env                     
├── .env.example           
└── README.md               
```

## API Documentation

### Swagger UI

Pełna dokumentacja API dostępna jest pod adresem:
```
http://localhost:8081
```

### Główne endpointy

#### Autoryzacja
- `POST /api/users/register` - Rejestracja użytkownika
- `POST /api/users/login` - Logowanie
- `POST /api/users/verify-email` - Weryfikacja email
- `POST /api/users/request-password-reset` - Reset hasła

#### Kursy
- `GET /api/courses` - Lista kursów
- `GET /api/courses/{id}` - Szczegóły kursu
- `POST /api/courses` - Tworzenie kursu (instruktor)
- `PUT /api/courses/{id}` - Aktualizacja kursu
- `GET /api/courses/instructor/{instructorId}` - Kursy instruktora

#### Sekcje i lekcje
- `POST /api/courses/{courseId}/sections` - Dodanie sekcji
- `POST /api/sections/{sectionId}/lessons` - Dodanie lekcji
- `PUT /api/sections/{sectionId}/reorder` - Zmiana kolejności

#### Quizy
- `POST /api/quizzes` - Tworzenie quizu
- `GET /api/quizzes/course/{courseId}` - Quizy kursu
- `POST /api/quizzes/{quizId}/questions` - Dodanie pytania
- `POST /api/quizzes/{quizId}/attempt` - Rozpoczęcie quizu
- `POST /api/quiz-attempts/{attemptId}/submit` - Wysłanie odpowiedzi

#### Zapisy
- `POST /api/enrollments` - Zapis na kurs
- `GET /api/enrollments/my-courses` - Moje kursy

## Użyte technologie

### Backend
- **Framework:** Spring Boot 3.5.6
- **Java:** 21
- **Baza danych:** PostgreSQL 16
- **ORM:** Hibernate/JPA
- **Security:** Spring Security
- **Message Broker:** RabbitMQ
- **API Docs:** SpringDoc OpenAPI 2.8.14
- **Build Tool:** Maven
- **Email:** Spring Mail (SMTP)

### Frontend
- **Framework:** React 19.1.1
- **Language:** TypeScript 5.9
- **Build Tool:** Vite 5.0
- **Routing:** React Router 6.30
- **Form Handling:** React Hook Form 7.65
- **Validation:** Zod 4.1
- **HTTP Client:** Axios 1.12
- **UI/UX:** Tailwind CSS 3.4
- **Drag & Drop:** dnd-kit 6.3
- **Icons:** Lucide React
- **Notifications:** React Hot Toast

### DevOps
- **Containerization:** Docker
- **Orchestration:** Docker Compose
- **Database:** PostgreSQL (containerized)
- **Message Queue:** RabbitMQ (containerized)

### Architektura i wzorce
- Domain-Driven Design (DDD)
- CQRS (Command Query Responsibility Segregation)
- Repository Pattern
- RESTful API
- JWT Authentication
- Role-Based Access Control (RBAC)

## Autor

Dominik Kępczyk

