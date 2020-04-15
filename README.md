Datenlieferungen

Steuerprogramm für Datenlieferungen an die Krankenkassen.
Die eigentlichen Datendateien werden noch nicht erzeugt.

Dieses umfangreichere Programm, diente mir dazu,
um Angular 7.0 und Spring5 zu lernen.

Zwar sind bei weitem nicht alle Funktionalitäten dieser beiden umfangreichen Frameworks benutzt worden, aber dennoch genug
um ein Feeling für sie zu bekommen.

------------ Starten -------------------
Vor dem Start sollte vorhanden sein:

Java version: 11.0.2
Apache Maven 3.6.0 

npm Version 6.4.1
Angular CLI: 8.3.2
Node: 10.15.3
Angular: 9.0.0-next.4

datenlieferung/frontend/node_modules ist im git leer
muss also erst installiert werden

Starten der Anwendung:

starteBackend.bat
starteFrontend.bat

der Backend läuft auf Port 9013
der Frontend auf Port 4200

Aufruf https://localhost:4200

------------- Stoppen ----------------
Stoppen in der Powershell als Administrator:

netstat -ano | grep 4200
netstat -ano | grep 9013

dann kill <prozessid>


mvn clean compile install package   spring-boot:run

Vielen Dank an die Teams, die Spring, Angular entwickeln
und den Teams von:

Bouncycastle für Schlüssel, Zertifikate, Verschlüsselung.
Jcraft für den sftp Client jsch.
Apache Commons sshd  für den Test sftp Server, das virtuelle Dateiystem vfs und den HttpClienten.
icegreen für den Test-Mail Server.




 
