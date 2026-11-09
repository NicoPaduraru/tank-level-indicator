# Tank Level Calculator

Android-App, die aus der gemessenen Füllhöhe den Inhalt eines Tanks berechnet, mit Schwerpunkt auf Heizöl.

## Funktionen

- Tankarten: Kellertank, Zylinder stehend, Zylinder liegend (DIN-Grössen), Kugeltank
- Tankvolumen eingeben oder aus den Abmessungen berechnen
- Resultat: Inhalt in Litern, Füllstand in Prozent und einfüllbare Menge
- Einstellbare Füllgrenze (Standard 95 %)
- Deutsch und Englisch
- Tanks lokal speichern, ohne Internet, Konto oder Datensammlung

## Berechnung

| Tank | Gefüllter Anteil bei Füllhöhe h |
|---|---|
| Kellertank, Zylinder stehend | h / H |
| Zylinder liegend | (θ − sin θ) / 2π, mit θ = 2·arccos(1 − 2h/D) |
| Kugel | r²·(3 − 2r), mit r = h/D |

Die Resultate sind berechnete Richtwerte und nicht zur Abrechnung gedacht. Gewölbte Tankböden und
Fertigungstoleranzen werden nicht berücksichtigt.

## Entwicklung

Voraussetzung ist Android Studio mit Android SDK 37.

```bash
./gradlew testDebugUnitTest
```

```bash
./gradlew assembleDebug
```

Die Veröffentlichung ist in [docs/PLAY_STORE.md](docs/PLAY_STORE.md) beschrieben.
