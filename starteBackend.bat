
cd datenlieferungModell

call mvn clean install

cd ..
cd datenlieferung
cd backend

call mvn clean compile install package spring-boot:run

cd ..
cd ..
