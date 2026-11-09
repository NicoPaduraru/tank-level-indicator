# Veröffentlichung in Google Play

## Vor dem ersten Upload festlegen

- `applicationId` in `app/build.gradle.kts` prüfen. Nach dem ersten Upload kann sie nicht mehr geändert werden.
- Kontaktadresse in `docs/PRIVACY.md` eintragen.
- Die Datenschutzerklärung unter einer öffentlichen Adresse bereitstellen. Google Play verlangt einen Link.
- Prüfen, ob die Regeln von Google für neue Entwicklerkonten einen geschlossenen Test vorschreiben.

## 1. Upload-Schlüssel erzeugen

Der Befehl fragt nach einem Passwort. Schlüssel und Passwort sicher aufbewahren und nie in Git einchecken.

```bash
keytool -genkeypair -v -keystore upload-key.jks -alias upload -keyalg RSA -keysize 4096 -validity 10000
```

Danach `keystore.properties.example` als `keystore.properties` kopieren und die Werte eintragen.

## 2. App-Bundle bauen

```bash
./gradlew bundleRelease
```

Ergebnis: `app/build/outputs/bundle/release/app-release.aab`

Vor jedem weiteren Upload `versionCode` erhöhen und `versionName` anpassen.

## 3. Angaben in der Play Console

- **Datensicherheit:** Es werden keine Daten erhoben oder weitergegeben.
- **Werbung:** Die App enthält keine Werbung.
- **App-Zugriff:** Alle Funktionen sind ohne Anmeldung nutzbar.
- **Grafiken:** App-Symbol 512 × 512 PNG, Vorschaubild 1024 × 500 und Screenshots vom Handy werden noch benötigt.

## 4. Store-Texte

### Deutsch

**Kurzbeschreibung:** Tankinhalt aus der Füllhöhe berechnen, offline und ohne Datensammlung.

**Beschreibung:**
Tank Level Indicator berechnet aus der gemessenen Füllhöhe den ungefähren Inhalt deines Tanks, mit Schwerpunkt auf Heizöl.

Wähle Kellertank, stehenden oder liegenden Zylinder oder Kugeltank. Gib die Tankdaten ein oder wähle eine genormte DIN-Grösse. Die App zeigt Inhalt, Füllstand und die Menge bis zur einstellbaren Füllgrenze.

Tanks lassen sich speichern. Die App arbeitet offline, ohne Konto, Werbung oder Datensammlung.

Die Werte sind berechnete Richtwerte und nicht zur Abrechnung gedacht.

### English

**Short description:** Calculate tank contents from the fill height, offline and without tracking.

**Description:**
Tank Level Indicator estimates the contents of your tank from the measured fill height, with a focus on heating oil.

Choose a cellar tank, a vertical or horizontal cylinder, or a spherical tank. Enter the tank data or pick a standard DIN size. The app shows the contents, fill level, and the amount up to your adjustable fill limit.

You can save your tanks. The app works offline, with no account, ads, or data collection.

Values are calculated estimates and are not intended for billing.
