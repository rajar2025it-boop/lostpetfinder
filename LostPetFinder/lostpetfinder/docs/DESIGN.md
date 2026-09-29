# LostPetFinder: Design Document

## 1. MVP (what to build, in priority order)

| # | Feature (from the question) | Endpoint | Must / Bonus |
|---|---|---|---|
| 0 | Register user (needed so reports have an owner) | `POST /api/users` | Must |
| 1 | Report a lost pet (species, breed, colour, last-seen location) | `POST /api/lost-reports` | Must |
| 2 | Report a found animal (same attributes) | `POST /api/found-reports` | Must |
| 3 | Suggest possible matches (species + locality minimum) | `GET /api/matches/lost/{id}`, `GET /api/matches/found/{id}` | Must |
| 4 | Mark a report resolved | `PUT /api/lost-reports/{id}/resolve`, `PUT /api/found-reports/{id}/resolve` | Must |
| 5 | Search reports by locality | `GET /api/lost-reports?locality=`, `GET /api/found-reports?locality=` | Must |
| 6 | Dashboard counts | `GET /api/dashboard` | Bonus ("Go Further") |
| 7 | Filter by species/status | query params on the list endpoints | Bonus |

### Business rules (enforced in the SERVICE layer)
1. A resolved report never appears in active match suggestions: candidates are fetched with `status = ACTIVE`, and asking for suggestions *for* a resolved report is rejected with **409** and a clear message.
2. Matching needs species AND locality (case-insensitive) at minimum. Colour (+2) and breed (+1) only rank results.
3. You cannot resolve a report twice (409).
4. Linking a lost and found report on resolve requires the same species, and the found report must still be active.
5. Referenced user must exist (404); email must be unique (409).

## 2. Layered architecture

```mermaid
flowchart TB
    Client["Client: Postman / Swagger UI"] --> C
    subgraph App["Spring Boot Application"]
      C["Controller layer<br/>REST endpoints, @Valid, HTTP status codes"]
      S["Service layer<br/>business rules, @Transactional, matching logic"]
      R["Repository layer<br/>Spring Data JPA: JpaRepository"]
      E["Entity layer<br/>JPA entities and enums"]
      D["DTO and Mapper<br/>request and response records"]
      X["GlobalExceptionHandler<br/>@RestControllerAdvice"]
    end
    C --> S --> R --> E
    C -.uses.-> D
    S -.uses.-> D
    C -.errors.-> X
    S -.errors.-> X
    R --> DB[("MySQL: lostpetfinder")]
```

Package layout: `controller`, `service`, `repository`, `entity`, `enums`, `dto`, `mapper`, `exception`.
Rule of thumb: controllers never touch repositories; entities are never returned to clients (DTOs only).

## 3. Class diagram

```mermaid
classDiagram
    class User {
      +Long id
      +String name
      +String email
      +String phone
      +LocalDateTime createdAt
    }
    class LostPetReport {
      +Long id
      +String petName
      +Species species
      +String breed
      +String colour
      +String locality
      +String lastSeenLocation
      +LocalDate lastSeenDate
      +String description
      +ReportStatus status
      +LocalDateTime createdAt
      +LocalDateTime resolvedAt
    }
    class FoundAnimalReport {
      +Long id
      +Species species
      +String breed
      +String colour
      +String locality
      +String foundLocation
      +LocalDate foundDate
      +String description
      +ReportStatus status
      +LocalDateTime createdAt
      +LocalDateTime resolvedAt
    }
    class Species { <<enumeration>> DOG CAT BIRD RABBIT OTHER }
    class ReportStatus { <<enumeration>> ACTIVE RESOLVED }

    class LostPetReportService {
      +create(LostPetRequest) ReportResponse
      +search(locality, species, status) List
      +resolve(id, foundReportId) ReportResponse
    }
    class FoundAnimalReportService {
      +create(FoundAnimalRequest) ReportResponse
      +search(locality, species, status) List
      +resolve(id) ReportResponse
    }
    class MatchService {
      +suggestForLost(id) List
      +suggestForFound(id) List
    }
    class UserService { +create(UserRequest) UserResponse }

    User "1" --> "0..*" LostPetReport : reports
    User "1" --> "0..*" FoundAnimalReport : reports
    LostPetReport ..> Species
    LostPetReport ..> ReportStatus
    FoundAnimalReport ..> Species
    FoundAnimalReport ..> ReportStatus
    LostPetReport .. FoundAnimalReport : matched on species and locality (computed, not stored)
    MatchService ..> LostPetReportService
    MatchService ..> FoundAnimalReportService
    LostPetReportService ..> UserService
    FoundAnimalReportService ..> UserService
```

## 4. Database schema (ER diagram)

```mermaid
erDiagram
    USERS ||--o{ LOST_PET_REPORTS : "files"
    USERS ||--o{ FOUND_ANIMAL_REPORTS : "files"
    USERS {
      bigint id PK
      varchar name
      varchar email UK
      varchar phone
      datetime created_at
    }
    LOST_PET_REPORTS {
      bigint id PK
      bigint user_id FK
      varchar pet_name
      varchar species
      varchar breed
      varchar colour
      varchar locality
      varchar last_seen_location
      date last_seen_date
      varchar description
      varchar status
      datetime created_at
      datetime resolved_at
    }
    FOUND_ANIMAL_REPORTS {
      bigint id PK
      bigint user_id FK
      varchar species
      varchar breed
      varchar colour
      varchar locality
      varchar found_location
      date found_date
      varchar description
      varchar status
      datetime created_at
      datetime resolved_at
    }
```

Design decisions worth saying in the viva:
- **Owner relationship**: `User` owns reports (`@ManyToOne` on the report, `@OneToMany(mappedBy)` on the user). The FK lives on the "many" side.
- **Matches are computed, not stored**: a match is a query on (species, locality, ACTIVE), so no join table and no stale data. A `Match` table would be a possible extension.
- **Composite index** `(species, locality, status)` on both report tables makes match queries fast.

## 5. UML use case diagram

```mermaid
flowchart LR
    U(("User<br/>owner or rescuer"))
    subgraph LostPetFinder
      UC0([Register])
      UC1([Report lost pet])
      UC2([Report found animal])
      UC3([View match suggestions])
      UC4([Mark report resolved])
      UC5([Search by locality])
      UC6([View dashboard])
    end
    U --> UC0 & UC1 & UC2 & UC3 & UC4 & UC5 & UC6
    UC3 -. "include: only ACTIVE reports" .-> UC5
```

## 6. Sequence diagram: suggest matches

```mermaid
sequenceDiagram
    actor U as User
    participant C as MatchController
    participant S as MatchService
    participant LR as LostPetReportService
    participant FR as FoundAnimalReportRepository
    U->>C: GET /api/matches/lost/1
    C->>S: suggestForLost(1)
    S->>LR: find(1)
    LR-->>S: LostPetReport
    alt report is RESOLVED
        S-->>C: throw BusinessRuleException
        C-->>U: 409 Conflict + message
    else report is ACTIVE
        S->>FR: findBySpeciesAndLocalityIgnoreCaseAndStatus(species, locality, ACTIVE)
        FR-->>S: candidate found reports
        S->>S: score by colour and breed, sort desc
        S-->>C: List of MatchResponse
        C-->>U: 200 OK
    end
```

## 7. Report lifecycle (state diagram)

```mermaid
stateDiagram-v2
    [*] --> ACTIVE : report created
    ACTIVE --> RESOLVED : pet reunited (resolve)
    RESOLVED --> [*]
    note right of RESOLVED
      excluded from match suggestions
      cannot be resolved again
    end note
```

## 8. HTTP status codes used

| Situation | Status |
|---|---|
| Created a user or report | 201 |
| Read, search, resolve, match | 200 |
| Validation failure, bad enum or date | 400 |
| User or report not found | 404 |
| Rule violated (resolved report, duplicate email, already resolved) | 409 |
| Unexpected error (no stack trace leaked) | 500 |

## 9. Step-by-step build order (as the question asks)
1. Spring Initializr: Web, Data JPA, Validation, MySQL Driver (+ Lombok, springdoc for Swagger).
2. Entities: `User`, `LostPetReport`, `FoundAnimalReport` + enums.
3. Repositories extending `JpaRepository`.
4. Services with the business rules.
5. Controllers, one endpoint per feature.
6. Validation annotations + `@RestControllerAdvice`.
7. Test in Swagger/Postman, including edge cases (`docs/api-tests.http`).
8. Bonus: dashboard and filters.
