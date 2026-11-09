# Veröffentlichung in Google Play

Alle Dateien und Antworten für die Play Console. Die Bezeichnungen in der Console können leicht abweichen.

## Dateien

| Was | Datei |
|---|---|
| App-Bundle | `app/build/outputs/bundle/release/app-release.aab` |
| App-Symbol, 512 × 512 | `store/graphics/app-icon-512.png` |
| Vorschaubild, 1024 × 500 | `store/graphics/feature-graphic-1024x500.png` |
| Screenshots Deutsch, 1080 × 1920 | `store/screenshots/de/` |
| Screenshots Englisch, 1080 × 1920 | `store/screenshots/en/` |

Das App-Bundle entsteht mit `./gradlew bundleRelease` und wird nicht in Git gespeichert. Dafür werden
`upload-key.jks` und `keystore.properties` im Projektordner benötigt. Beide nur lokal halten und sicher sichern.

Vor jedem weiteren Upload `versionCode` in `app/build.gradle.kts` erhöhen und `versionName` anpassen.

## App anlegen

| Feld | Eingabe |
|---|---|
| App-Name | Tank Level Indicator |
| Standardsprache | English (United States) – en-US |
| App oder Spiel | App |
| Kostenlos oder kostenpflichtig | Kostenlos |
| Paketname | `de.tanklevelindicator` (nach dem ersten Upload nicht mehr änderbar) |

## Store-Eintrag

Kategorie: **Tools**
Kontakt-E-Mail: `nicoprelease@proton.me`

### English (en-US)

**App name**

```text
Tank Level Indicator
```

**Short description**

```text
Calculate tank contents from the fill height, offline and without tracking.
```

**Full description**

```text
Tank Level Indicator estimates the contents of your tank from the measured fill height, with a focus on heating oil.

Choose a cellar tank, a vertical or horizontal cylinder, or a spherical tank. Enter the tank data or pick a standard DIN size. The app shows the contents, fill level, and the amount up to your adjustable fill limit.

Features:
• Four tank types
• Enter the tank volume or calculate it from the dimensions
• Contents in liters, fill level in percent, and the amount you can still add
• Adjustable fill limit, 95 % by default
• Save your tanks on your device
• German and English

The app works offline, with no account, ads, or data collection.

Values are calculated estimates for a rough check and are not intended for billing. Actual contents may differ.
```

### Deutsch (de-DE)

**App-Name**

```text
Tank Level Indicator
```

**Kurzbeschreibung**

```text
Tankinhalt aus der Füllhöhe berechnen, offline und ohne Datensammlung.
```

**Vollständige Beschreibung**

```text
Tank Level Indicator berechnet aus der gemessenen Füllhöhe den ungefähren Inhalt deines Tanks, mit Schwerpunkt auf Heizöl.

Wähle Kellertank, stehenden oder liegenden Zylinder oder Kugeltank. Gib die Tankdaten ein oder wähle eine genormte DIN-Größe. Die App zeigt Inhalt, Füllstand und die Menge bis zur einstellbaren Füllgrenze.

Funktionen:
• Vier Tankarten
• Tankvolumen eingeben oder aus den Abmessungen berechnen
• Inhalt in Litern, Füllstand in Prozent und die noch einfüllbare Menge
• Einstellbare Füllgrenze, Standard 95 %
• Tanks auf dem Gerät speichern
• Deutsch und Englisch

Die App arbeitet offline, ohne Konto, Werbung oder Datensammlung.

Die Werte sind berechnete Richtwerte zur ungefähren Kontrolle und nicht zur Abrechnung gedacht. Der tatsächliche Inhalt kann abweichen.
```

## App-Inhalte

| Formular | Antwort |
|---|---|
| Datenschutzerklärung | `https://github.com/NicoPaduraru/tank-level-indicator/blob/main/docs/PRIVACY.md` |
| App-Zugriff | Alle Funktionen ohne besonderen Zugriff verfügbar |
| Werbung | Nein, die App enthält keine Werbung |
| Werbe-ID | Nein, wird nicht verwendet |
| Zielgruppe | 18 Jahre und älter |
| Nachrichten-App | Nein |
| Gesundheits-Apps | Keine der Funktionen |
| Finanzfunktionen | Keine |
| Behörden-App | Nein |
| Einstufung Kategorie | Alle anderen App-Typen (Utility) |
| Einstufung alle Inhaltsfragen | Nein |
| Einstufung Interaktion, Standort, Käufe | Nein |
| Datensicherheit: Daten erhoben oder geteilt? | Nein |

Die Einstufung fragt ausserdem nach einer E-Mail-Adresse: `nicoprelease@proton.me`

## Versionshinweise

```text
<en-US>
First release of Tank Level Indicator.
</en-US>
<de-DE>
Erste Version von Tank Level Indicator.
</de-DE>
```

## Länder

Für Europa alle europäischen Länder auswählen.

## Geschlossener Test

Neue private Entwicklerkonten müssen vor der Produktion einen geschlossenen Test mit mindestens 12 Testern
über 14 Tage durchführen. Danach wird der Zugriff auf die Produktion in der Console beantragt und von Google geprüft.
