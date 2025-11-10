If you want to free port 8080 (Windows)

1) Find the process using port 8080:
   # Open CMD as Administrator
   netstat -ano | findstr :8080

   You will see a line like:
   TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    <PID>

2) Get the process details:
   tasklist /FI "PID eq <PID>"

3) Stop the process (if it's safe to stop):
   taskkill /PID <PID> /F

4) Verify port freed:
   netstat -ano | findstr :8080

Alternate: run this app on another port without stopping the other process
- Temporary (CMD before running):
  set PORT=8081
  mvn spring-boot:run
  # or
  java -jar target\MathAssistantWeb-1.0.0.jar --server.port=8081

- Or pass on command line:
  mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

Notes:
- If you prefer, set the PORT environment variable permanently (or use the application.properties override above).
- After freeing the port or changing PORT, re-run:
  mvn -U clean package
  mvn spring-boot:run
  or
  java -jar target\MathAssistantWeb-1.0.0.jar
