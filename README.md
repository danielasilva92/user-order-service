# user-order-service

En av två backend-tjänster i CloudStore, ett mikrotjänstbaserat e-handelsprojekt. Den här tjänsten hanterar användare, inloggning, JWT-säkerhet och beställningar. Den pratar med product-service över HTTP för att hämta produktinformation när en beställning skapas.

## Vad tjänsten gör

- Registrering och inloggning med Spring Security
- Skapar JWT-token vid inloggning och verifierar token på varje skyddat anrop
- Skapar beställningar som kan innehålla flera produkter (CustomerOrder med flera OrderItem)
- Hämtar pris och titel från product-service när en beställning skapas, så att priset inte kan manipuleras från frontend
- Sparar användare och beställningar i en MySQL-databas

## Teknik

- Java 17, Spring Boot 3.2
- Spring Security och JWT (jjwt)
- Spring Data JPA / Hibernate
- MySQL (RDS i produktion, MySQL i Docker lokalt, H2 i tester)
- Docker, GitHub Actions, driftsatt på AWS EC2

## Köra lokalt

Tjänsten körs enklast tillsammans med de andra delarna via docker-compose. Se huvud-repot där `docker-compose.yml` ligger. Den startar databasen, båda backend-tjänsterna och frontend med ett kommando:
docker-compose up --build

Tjänsten lyssnar då på port 8081.

## Miljövariabler

Tjänsten använder Spring-profiler. Lokalt (dev) pekar den mot MySQL i Docker, i produktion (prod) mot RDS. Profilen väljs med `SPRING_PROFILES_ACTIVE`.

| Variabel | Beskrivning |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` lokalt, `prod` i AWS |
| `DB_URL` | Databasens adress (prod) |
| `DB_USERNAME` | Databasanvändare |
| `DB_PASSWORD` | Databaslösenord |
| `JWT_SECRET` | Hemlig nyckel för att signera och verifiera JWT (måste vara samma som i product-service) |
| `JWT_EXPIRATION` | Hur länge en token gäller i millisekunder |
| `CORS_ALLOWED_ORIGINS` | Tillåtna origins, t.ex. frontendens adress |
| `PRODUCT_SERVICE_URL` | Adressen till product-service |

Hemligheter läses lokalt från en `.env`-fil (som inte ligger i Git). Se `.env.example` för vilka värden som behövs.

## API-endpoints

| Metod | Sökväg | Beskrivning | Kräver token |
|-------|--------|-------------|--------------|
| POST | `/api/auth/register` | Registrera ny användare | Nej |
| POST | `/api/auth/login` | Logga in, returnerar JWT | Nej |
| GET | `/api/products` | Lista produkter (via product-service) | Nej |
| POST | `/api/orders` | Skapa en beställning | Ja |
| GET | `/api/orders` | Hämta inloggad användares beställningar | Ja |

## Tester

Enhetstester med JUnit och Mockito. `AuthServiceTest` testar registrering och inloggning, och `OrderServiceTest` testar att beställningar byggs korrekt, att fel kastas när en produkt saknas, och att en användare bara ser sina egna beställningar. Testerna använder en H2-databas i minnet så att de kan köras utan en riktig databas.

## CI/CD

Vid varje push till `main` kör GitHub Actions en pipeline som bygger projektet, kör testerna, bygger en Docker-image och pushar den till DockerHub, och slutligen deployar till EC2 via SSH med produktionsprofilen.

## Drift

Tjänsten körs i en Docker-container på en AWS EC2-instans, bakom nginx som reverse proxy med HTTPS via Let's Encrypt. Databasen ligger i AWS RDS.

Publik adress: https://cloudstore-daniela.duckdns.org
