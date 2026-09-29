# LostPetFinder (Spring Boot + MySQL)

## Run
1. Install JDK 17+, Maven, MySQL 8.
2. Edit `src/main/resources/application.properties` and set your MySQL password.
   (Tables are auto-created by Hibernate. `docs/schema.sql` is the equivalent manual script.)
3. `mvn spring-boot:run`
4. Swagger UI: http://localhost:8080/swagger-ui.html
5. Try the requests in `docs/api-tests.http` (IntelliJ or VS Code REST Client) or Postman.

Design docs with UML, class, ER, sequence diagrams: `docs/DESIGN.md` (Mermaid; renders on GitHub / VS Code / mermaid.live).
