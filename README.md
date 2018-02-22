# mdh-driftavbrott-service

En tjänst som serverar information om driftavbrott via ett REST-gränssnitt.

## Konfigurera

Nuvarande implementation använder en properties-fil för att lagra aktuella
driftavbrott. Det finns en default-konfigurationen som tillhör källkoden i filen
`src/main/resources/application.properties`. Genom att modifiera den kan man
göra snabba tester på sin lokala dator.

När man installerar mdh-driftavbrott-service hos sig behöver man skapa en egen
properties-fil med information om de driftavbrott som man vill informera om.
Den filen ska placeras på följande sökväg:
```
/sökväg/till/jarfilen/config/driftavbrott.properties
```

Formatet på filen är att varje kanal har en rad i filen där kanalens namn är
nyckel och värdet består av två datumstämplar separerade med semikolon, t.ex.
```
alltid=2017-09-28T00:00;2099-12-31T23:59`
```

Det går även att lägga in två klockslag för driftavbrott som återkommer varje
dygn, t.ex.
```
ladok.backup=01:00;03:40
```

Så här kan en komplett konfiguration se ut:
```
# En kanal som kan användas för att testa produkten
alltid=2017-09-28T00:00;2099-12-31T23:59
# Driftavbrott för Ladok
ladok.backup=01:00;03:40
ladok.produktionssattning=2018-02-21T16:00;2018-02-22T11:00
ladok.uppgradering=2018-02-20T05:30;2018-02-20T09:00
```

## Bygg

```
mvn clean verify
```

## Starta

För att starta tjänsten, kör detta kommando
```
java -jar mdh-driftavbrott-service-${project.version}.jar
```

## Testa

Gå till http://localhost:3301/mdh-driftavbrott/swagger-ui.html

## REST-parametrar

### system (obligatorisk)

När man anropar REST-tjänsten finns det en obligatorisk parameter `system` som
anger vilket system som vill ha information om driftavbrott. Från ett
Java-system rekommenderas att skicka in `artifactId`.

### kanal (valfri)

Det finns också en valfri parameter `kanal` som är multi-valued och anger vilka
typer av driftavbrott som systemet vill lyssna på. Lämnar man den tom får man
info från alla kanaler.

På MDH har vi konfigurerat 4 kanaler

Kanal  | Syfte
------ | ------
alltid                    | Returnerar alltid ett driftavbrott, vilket gör att den kan användas för tester.
ladok.backup              | Informerar om att Ladok håller på att ta backup. Lämplig för applikationer som används utanför kontorstid.
ladok.produktionssattning | Informerar om att Ladok håller på att produktionssätta nya lärosäten.
ladok.uppgradering        | Informerar om att Ladok håller på att uppgraderas.

Exempel på ett REST-anrop mot din lokala miljö som returnerar ett driftavbrott
från kanalen `alltid`:

http://localhost:3301/mdh-driftavbrott/v1/driftavbrott/pagaende?kanal=alltid&system=Integrationstest
