# 🧮 Mathematical Assistant Web Application

A modern, responsive web application for performing mathematical calculations and visualizing Fibonacci sequences. Built with **Spring Boot 3.3.5** and **Java 17**, featuring a beautifully designed interface with dark mode support and interactive visualizations.

---

## ✨ Features

### Basic Calculator
- **Arithmetic Operations**: Addition, subtraction, multiplication, and division
- **Real-time Results**: Instant calculation feedback with animated displays
- **Intuitive Interface**: Clean and user-friendly input layout
- **Error Handling**: Division by zero and input validation

### Fibonacci Curve Generator
- **Dynamic Visualization**: Generate Fibonacci spiral curves up to 1000 terms
- **Customizable Output**: Adjust curve size and complexity
- **PNG Image Generation**: High-quality rendered graphics using Java Graphics2D
- **Responsive Design**: Scales seamlessly across desktop and mobile devices

### User Experience
- **🌙 Dark Mode Toggle**: Seamless theme switching with local storage persistence
- **Animated UI Elements**: Smooth transitions and floating particle effects
- **Mobile Responsive**: Optimized layouts for all screen sizes
- **Fast Performance**: Efficient backend processing and headless rendering

---

## 🚀 Quick Start

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+** (or use Maven wrapper)
- Docker (optional, for containerized deployment)

### Build the Application

```bash
mvn clean package
```

### Run the Application

#### Option 1: Direct JAR Execution
```bash
java -jar target/MathAssistantWeb-1.0.0.jar
```

#### Option 2: Using Maven
```bash
mvn spring-boot:run
```

#### Option 3: Docker
```bash
docker build -t math-assistant .
docker run -p 8080:8080 math-assistant
```

### Access the Application
- **Main Calculator**: [http://localhost:8080/](http://localhost:8080/)
- **Fibonacci Generator**: [http://localhost:8080/fibonacci-page](http://localhost:8080/fibonacci-page)

---

## 📋 API Endpoints

### Calculate Basic Operations
**POST** `/calculate`
- **Parameters**:
  - `a` (double): First operand
  - `b` (double): Second operand
  - `op` (string): Operation type (`add`, `sub`, `mul`, `div`)
- **Response**: JSON with `result` field

**Example**:
```bash
curl -X POST "http://localhost:8080/calculate?a=10&b=5&op=add"
# Response: {"result": 15.0}
```

### Generate Fibonacci Curve
**GET** `/fibonacci`
- **Parameters**:
  - `n` (int, optional): Number of Fibonacci terms (1-1000, default: 8)
  - `size` (int, optional): Image size in pixels (100-2000, default: 600)
- **Response**: PNG image

**Example**:
```bash
curl "http://localhost:8080/fibonacci?n=15&size=800" -o curve.png
```

### Web Pages
- **GET** `/`: Main calculator interface
- **GET** `/fibonacci-page`: Fibonacci curve generator page

---

## 🏗️ Project Structure

```
Math-web-vs/
├── src/main/
│   ├── java/com/mathassistant/
│   │   ├── MathAssistantWebApplication.java    # Spring Boot entry point & REST controllers
│   │   └── FibonacciCurve.java                  # Fibonacci visualization logic
│   └── resources/
│       ├── application.properties                # Server configuration
│       ├── static/                               # Static assets (if needed)
│       └── templates/
│           ├── index.html                        # Main calculator UI
│           └── fibonacci.html                    # Fibonacci generator UI
├── pom.xml                                       # Maven dependencies & build config
└── Dockerfile                                    # Container configuration
```

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 3.3.5 |
| **Language** | Java 17 |
| **Build Tool** | Maven |
| **Frontend** | HTML5 + CSS3 + JavaScript |
| **Graphics** | Java AWT (BufferedImage, Graphics2D) |
| **Web Server** | Embedded Tomcat |
| **Template Engine** | Thymeleaf |
| **Monitoring** | Spring Boot Actuator |

---

## ⚙️ Configuration

### Server Settings (`application.properties`)
```properties
server.address=0.0.0.0              # Bind to all interfaces
server.port=                        # Auto-detected if not set
logging.level.root=INFO
spring.main.banner-mode=off
management.endpoints.web.exposure.include=health
```

### Environment Variables
- **PORT**: Override the default server port (e.g., `export PORT=9000`)
- **JAVA_OPTS**: Pass custom JVM arguments

The application automatically selects a free port if none is specified, ensuring no conflicts.

---

## 📊 Advanced Usage

### Generating Large Fibonacci Curves
For high-complexity visualizations, adjust parameters:
```bash
curl "http://localhost:8080/fibonacci?n=50&size=1200" -o large_curve.png
```
⚠️ Note: Very large values (n > 500) may take longer to render.

### Batch Calculations
Script multiple API calls:
```bash
for op in add sub mul div; do
  curl -X POST "http://localhost:8080/calculate?a=100&b=25&op=$op" | jq .
done
```

---

## 🧪 Testing

The application includes:
- **Spring Boot Actuator**: Health check at `/actuator/health`
- **Input Validation**: Boundary checks on Fibonacci terms (1-1000)
- **Error Handling**: Safe division by zero, headless rendering support

Test connectivity:
```bash
curl http://localhost:8080/actuator/health
```

---

## 🌐 Browser Support

- ✅ Chrome/Chromium (latest)
- ✅ Firefox (latest)
- ✅ Safari (latest)
- ✅ Edge (latest)
- ✅ Mobile browsers (iOS Safari, Chrome Mobile)

---

## 📝 Troubleshooting

| Issue | Solution |
|-------|----------|
| Port already in use | Set `PORT=9000` and restart, or kill existing process |
| Fibonacci generation slow | Reduce `n` value or `size` parameter |
| Dark mode not persisting | Check browser localStorage settings |
| Port detection fails | Explicitly set `server.port=8080` in application.properties |

---

## 🚢 Deployment

### Production Recommendations
1. **Enable HTTPS** with a reverse proxy (nginx, HAProxy)
2. **Set explicit port**: `export PORT=8080`
3. **Configure logging**: Adjust `logging.level.root` based on needs
4. **Use Docker**: See Dockerfile for containerized deployment
5. **Monitor health**: Regularly check `/actuator/health`

### Docker Deployment
```bash
docker build -t math-assistant .
docker run -d -p 8080:8080 --name math-app math-assistant
docker logs -f math-app
```

---

## 📄 License

This project is provided as-is for educational and development purposes.

---

## 👤 Author

**Anbuganesh** - B.Tech.AI&DS Student


