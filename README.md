# OMS Services - Order Management System Microservices

A multi-service order management system built with Spring Boot, Spring Cloud, and various microservice patterns.

## 🏗️ Architecture Overview

This is a **monorepo** containing 5 independent microservices that work together:

```
oms-services (root)
├── discovery-server          (Eureka Service Registry) - Port 8761
├── api-gateway              (Spring Cloud Gateway) - Port 8080
├── auth-service             (JWT Authentication) - Port 8081
├── order-service            (Order Management) - Port 8082
└── payment-service          (Payment Processing) - Port 8083
```

## 📋 Services Description

### Discovery Server (Port 8761)
- **Role**: Eureka service registry and discovery server
- **Purpose**: Central registry where all services register and discover each other
- **Start First**: Required for all other services to work
- **Database**: No database required

### Auth Service (Port 8081)
- **Role**: Authentication and authorization
- **Purpose**: Handles user registration, login, and JWT token generation
- **Database**: H2 (in-memory) - `jdbc:h2:mem:authdb`
- **H2 Console**: http://localhost:8081/h2-console

### Order Service (Port 8082)
- **Role**: Order management
- **Purpose**: Create, retrieve, and cancel orders; uses Saga pattern with payment-service
- **Database**: H2 (in-memory) - `jdbc:h2:mem:orderdb`
- **H2 Console**: http://localhost:8082/h2-console
- **Resilience4j**: Circuit breaker for payment-service calls

### Payment Service (Port 8083)
- **Role**: Payment processing
- **Purpose**: Process payments for orders
- **Database**: H2 (in-memory) - `jdbc:h2:mem:paymentdb`
- **H2 Console**: http://localhost:8083/h2-console

### API Gateway (Port 8080)
- **Role**: API entry point
- **Purpose**: Routes all requests to appropriate services
- **Database**: No database required
- **Features**: JWT authentication, circuit breaker, service discovery
- **Start Last**: Ensure all backend services are ready first

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Gradle 8.0+
- macOS, Linux, or Windows

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/oms-services.git
cd oms-services

# Build all services
gradle clean build -x test

# Or build a specific service
gradle :auth-service:build
```

### Running Services (Recommended Order)

**Terminal 1 - Discovery Server:**
```bash
gradle :discovery-server:bootRun
```
Wait for startup (~10 seconds)

**Terminal 2 - Auth Service:**
```bash
gradle :auth-service:bootRun
```
Wait for "Successfully registered with Eureka"

**Terminal 3 - Order Service:**
```bash
gradle :order-service:bootRun
```
Wait for registration

**Terminal 4 - Payment Service:**
```bash
gradle :payment-service:bootRun
```
Wait for registration

**Terminal 5 - API Gateway (start last):**
```bash
gradle :api-gateway:bootRun
```

### Health Checks

Once all services are running:

```bash
# Eureka Dashboard
http://localhost:8761/

# Auth Service H2 Console
http://localhost:8081/h2-console
# JDBC URL: jdbc:h2:mem:authdb
# User: sa, Password: (empty)

# Order Service H2 Console
http://localhost:8082/h2-console
# JDBC URL: jdbc:h2:mem:orderdb

# Payment Service H2 Console
http://localhost:8083/h2-console
# JDBC URL: jdbc:h2:mem:paymentdb

# API Gateway Health
http://localhost:8080/actuator/health
```

## 🔐 Authentication

### Demo Credentials
- **Username**: demo
- **Password**: password

### Get JWT Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expirationSeconds": 3600
}
```

### Use Token for API Calls
```bash
curl -H "Authorization: Bearer <YOUR_TOKEN>" \
  http://localhost:8080/orders
```

## 📦 Project Structure

```
com.oms.*
├── auth
│   ├── AuthServiceApplication.java
│   ├── controller
│   ├── service
│   ├── security
│   ├── entity
│   ├── repository
│   └── dto
├── order
│   ├── OrderServiceApplication.java
│   ├── controller
│   ├── service
│   ├── model
│   ├── client
│   ├── config
│   └── dto
├── payment
│   ├── PaymentServiceApplication.java
│   ├── controller
│   ├── service
│   ├── model
│   └── dto
├── gateway
│   ├── ApiGatewayApplication.java
│   ├── controller
│   ├── security
│   └── config
└── discovery
    └── DiscoveryServerApplication.java
```

## 🛠️ Technologies & Dependencies

- **Java**: 17
- **Spring Boot**: 3.3.5
- **Spring Cloud**: 2023.0.3
- **Eureka**: Service discovery
- **Spring Cloud Gateway**: API routing
- **Spring Security**: Authentication
- **JWT (JJWT)**: Token-based auth
- **Spring Data JPA**: Database access
- **H2 Database**: In-memory database
- **Resilience4j**: Circuit breaker pattern
- **WebFlux**: Reactive web for order-service

## 📝 Configuration Files

Each service has an `application.yml` in `src/main/resources/`:

```yaml
# Example: auth-service/src/main/resources/application.yml
server:
  port: 8081
spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:h2:mem:authdb
    driverClassName: org.h2.Driver
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🔄 Inter-Service Communication

- **Order → Payment**: REST calls via WebClient (with Resilience4j circuit breaker)
- **All Services → Discovery**: Register and heartbeat with Eureka
- **API Gateway → Services**: Route requests via Spring Cloud Gateway with service discovery

## 🧪 Testing

```bash
# Run tests for all services
gradle test

# Run tests for specific service
gradle :auth-service:test

# Build without running tests (faster)
gradle build -x test
```

## 📊 Key Patterns Used

1. **Microservices Pattern**: Independent deployable services
2. **Service Discovery**: Eureka for dynamic service location
3. **API Gateway**: Central entry point with routing
4. **Circuit Breaker**: Resilience4j for fault tolerance
5. **JWT Authentication**: Stateless auth with tokens
6. **Saga Pattern**: Distributed transaction for order + payment
7. **H2 In-Memory DB**: Quick dev/test database

## 🚀 Deployment

### Docker (Optional)

```bash
# Build JAR for a service
gradle :auth-service:bootJar

# Build Docker image (requires Dockerfile)
docker build -t oms-auth-service .

# Run container
docker run -p 8081:8081 oms-auth-service
```

### Gradle Wrapper (Recommended)

This project uses a Gradle wrapper for consistent builds:

```bash
# On macOS/Linux
./gradlew clean build -x test

# Or use the wrapper explicitly
./gradlew :discovery-server:bootRun
```

## 📚 API Endpoints

### Auth Service
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT token

### Order Service
- `POST /orders` - Create new order
- `GET /orders` - List all orders
- `GET /orders/{orderId}` - Get order details
- `POST /orders/{orderId}/cancel` - Cancel order

### Payment Service
- `POST /payments` - Process payment
- `GET /payments` - List all payments

### API Gateway
- All above endpoints available via gateway with authentication

## 🐛 Troubleshooting

### Services can't find each other
- Ensure Discovery Server is running first
- Check Eureka dashboard: http://localhost:8761/

### H2 console not loading
- Verify service is running
- Ensure `spring.h2.console.enabled: true` in application.yml
- Use correct JDBC URL for each service

### JWT token invalid
- Check token expiration (default: 3600 seconds)
- Verify secret key in application.yml
- Use format: `Authorization: Bearer <token>`

## 📄 License

MIT License - See LICENSE file for details

## 👥 Contributing

1. Create a feature branch
2. Make changes and commit
3. Push to GitHub
4. Create a Pull Request

## 📞 Support

For issues and questions, please use GitHub Issues.

---

**Happy coding!** 🎉

