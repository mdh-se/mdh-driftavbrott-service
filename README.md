# mdh-driftavbrott-service

## Konfigurera

Konfigurationen ligger i ``src/main/resources/application.properties``.

## Bygg

```
mvn clean verify
```

## Starta

För att starta tjänsten´, kör detta kommando
```
java -jar mdh-driftavbrott-service-${project.version}.jar
```

## Testa

Gå till http://localhost:3301/mdh-driftavbrott/swagger-ui.html
