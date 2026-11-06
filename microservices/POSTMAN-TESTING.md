# ISTAD Microservices - Postman Testing Guide

This guide provides end-to-end instructions for testing all microservices in the ISTAD platform using Postman (both HTTP REST and gRPC protocols), including automated test assertions, OAuth2 security flows, and inter-service routing.

---

## 1. Service Map & Port Reference

| Service | Protocol | Base URL / Port | Auth Required | Backing Store |
| :--- | :--- | :--- | :--- | :--- |
| **Auth / IAM Server** | HTTP | `http://localhost:9000` | Basic / Client Secret | PostgreSQL (`db_oauth2`, port `9543`) |
| **Customer Service** | HTTP (WebFlux) | `http://localhost:9091` | Bearer JWT (`customer.read`) | MongoDB (port `2026`) |
| **Order Service** | HTTP (WebFlux) | `http://localhost:9092` | None (Client caller) | PostgreSQL (`db_order`, port `9543`) |
| **Product Service** | HTTP REST | `http://localhost:9093` | None | PostgreSQL (`db_product`, port `9543`) |
| **Product Service (gRPC)** | gRPC | `localhost:1013` | None | PostgreSQL (`db_product`, port `9543`) |
| **Payment REST Bridge** | HTTP | `http://localhost:1011` | None | In-Memory / gRPC bridge |
| **Payment Service (gRPC)** | gRPC | `localhost:1012` | None | In-Memory |
| **Gateway Server** | HTTP | `http://localhost:9999` | Route-specific | Consul Discovery |

---

## 2. Quick Setup & Postman Import

Ready-to-use Postman files are located in the repository at:
- **Collection**: [istad-microservices.postman_collection.json](file:///D:/MicroserviceProject/istad-ite-project/microservices/postman/istad-microservices.postman_collection.json)
- **Environment**: [istad-local.postman_environment.json](file:///D:/MicroserviceProject/istad-ite-project/microservices/postman/istad-local.postman_environment.json)

### How to Import:
1. Open Postman.
2. Click **Import** (top-left) and select both JSON files above.
3. In the top-right environment dropdown, select **ISTAD Microservices - Local**.

---

## 3. Starting the Required Infrastructure

Before making API calls, verify backing services are up:

```powershell
# 1. PostgreSQL (Port 9543)
docker compose -f deployment/postgres/docker-compose.yaml up -d

# 2. MongoDB (Port 2026)
docker compose -f deployment/mongodb/docker-compose.yaml up -d

# 3. Consul (Optional, for Gateway & dynamic config)
docker compose -f deployment/consul/docker-compose.yaml up -d
```

---

## 4. Step-by-Step Microservice Testing

### Part 1: OAuth2 Auth Server (Port 9000)

Start Auth Server:
```powershell
.\gradlew.bat :microservices:spring-auth-server:bootRun
```

#### 1.1 Request Access Token (Client Credentials)
- **Method**: `POST`
- **URL**: `{{auth_url}}/oauth2/token`
- **Headers**: `Content-Type: application/x-www-form-urlencoded`
- **Body (`x-www-form-urlencoded`)**:
  - `grant_type`: `client_credentials`
  - `client_id`: `order-service`
  - `client_secret`: `order-secret`
  - `scope`: `customer.read business.read`
- **Postman Test Script**:
  ```javascript
  pm.test("Status code is 200", function () {
      pm.response.to.have.status(200);
  });
  var jsonData = pm.response.json();
  pm.test("Token returned with valid scope", function () {
      pm.expect(jsonData.access_token).to.be.a('string');
      pm.expect(jsonData.scope).to.include('customer.read');
  });
  // Automatically saves token to environment for subsequent calls:
  pm.environment.set("access_token", jsonData.access_token);
  ```

---

### Part 2: Customer Service (Port 9091)

Customer Service is a reactive WebFlux service secured by OAuth2 Resource Server. All `/api/customers/**` endpoints require an active Bearer JWT containing the `customer.read` scope.

Start Customer Service:
```powershell
.\gradlew.bat -p microservices/customer-service :customer-service-main:bootRun
```

#### 2.1 Initiate Customer
- **Method**: `POST`
- **URL**: `{{customer_service_url}}/api/customers`
- **Auth**: `Bearer Token` (`{{access_token}}`)
- **Headers**: `Content-Type: application/json`
- **Body (raw JSON)**:
  ```json
  {
    "username": "dara_sok",
    "familyName": "Sok",
    "givenName": "Dara",
    "email": "dara.sok@example.com",
    "phoneNumber": "012345678"
  }
  ```
- **Assertions & Environment Capture**:
  ```javascript
  pm.test("Status code is 201 Created", function () {
      pm.response.to.have.status(201);
  });
  var data = pm.response.json();
  pm.environment.set("customerId", data.customerId);
  pm.environment.set("customerUsername", data.username);
  ```

#### 2.2 Get Customer by ID
- **Method**: `GET`
- **URL**: `{{customer_service_url}}/api/customers/{{customerId}}`
- **Auth**: `Bearer Token` (`{{access_token}}`)
- **Assertions**:
  ```javascript
  pm.test("Status is 200", () => pm.response.to.have.status(200));
  var data = pm.response.json();
  pm.test("Status is ACTIVE and segment initialized", () => {
      pm.expect(data.status).to.eql("ACTIVE");
      pm.expect(data.segment).to.be.oneOf(["SILVER", "GOLD", "PREMIUM"]);
  });
  ```

#### 2.3 Search by Username
- **Method**: `GET`
- **URL**: `{{customer_service_url}}/api/customers/search?username={{customerUsername}}`
- **Auth**: `Bearer Token` (`{{access_token}}`)

#### 2.4 Update Customer Profile
- **Method**: `PUT`
- **URL**: `{{customer_service_url}}/api/customers/{{customerId}}`
- **Body (raw JSON)**:
  ```json
  {
    "familyName": "Sok Updated",
    "givenName": "Dara Updated"
  }
  ```

#### 2.5 Upgrade Customer Segment
- **Method**: `PATCH`
- **URL**: `{{customer_service_url}}/api/customers/{{customerId}}/segment`
- **Body (raw JSON)**:
  ```json
  {
    "segment": "GOLD"
  }
  ```

#### 2.6 Deactivate & Activate
- **Deactivate**: `PATCH {{customer_service_url}}/api/customers/{{customerId}}/deactivate` -> Status `INACTIVE`
- **Activate**: `PATCH {{customer_service_url}}/api/customers/{{customerId}}/activate` -> Status `ACTIVE`

#### 2.7 Filter and Statistics
- **Filter**: `GET {{customer_service_url}}/api/customers/filter?status=ACTIVE&segment=GOLD`
- **Statistics**: `GET {{customer_service_url}}/api/customers/statistic`

#### 2.8 Negative Security Test (Unauthorized Access)
- **Method**: `GET`
- **URL**: `{{customer_service_url}}/api/customers`
- **Auth**: `No Auth`
- **Assertions**:
  ```javascript
  pm.test("Status code is 401 Unauthorized", function () {
      pm.response.to.have.status(401);
  });
  var data = pm.response.json();
  pm.test("Problem details formatted", function () {
      pm.expect(data.status).to.eql(401);
  });
  ```

#### 2.9 Delete Customer
- **Method**: `DELETE`
- **URL**: `{{customer_service_url}}/api/customers/{{customerId}}`
- **Assertions**: Status `204 No Content`

---

### Part 3: Product Service (Port 9093 REST & Port 1013 gRPC)

Start Product Service:
```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
.\gradlew.bat -p microservices/product-service :product-service-main:bootRun
```

#### 3.1 REST API: Create Product
- **Method**: `POST`
- **URL**: `{{product_service_url}}/api/products`
- **Body (raw JSON)**:
  ```json
  {
    "businessId": "11111111-1111-4111-8111-111111111111",
    "sku": "SKU-MECH-001",
    "name": "Wireless Mechanical Keyboard",
    "description": "Hot-swappable RGB Keyboard",
    "unitPrice": 89.99,
    "currency": "USD",
    "availableQuantity": 50
  }
  ```
- **Postman Test**:
  ```javascript
  pm.test("Status code is 201 Created", () => pm.response.to.have.status(201));
  var data = pm.response.json();
  pm.environment.set("productId", data.id);
  ```

#### 3.2 REST API: Get Product by ID & List
- **Get by ID**: `GET {{product_service_url}}/api/products/{{productId}}`
- **List All**: `GET {{product_service_url}}/api/products`

#### 3.3 gRPC Testing in Postman (Port 1013)
1. In Postman, click **New** > **gRPC Request**.
2. Enter URL: `localhost:1013`.
3. Select **Service definition**: Use Server Reflection (enabled under `dev` profile) or import proto files from `istad-common/istad-common-grpc/src/main/proto`.
4. Test methods:
   - **`GetProduct`**:
     ```json
     { "productId": "{{productId}}" }
     ```
   - **`ReserveStock`**:
     ```json
     {
       "orderId": "22222222-2222-4222-8222-222222222222",
       "businessId": "11111111-1111-4111-8111-111111111111",
       "items": [
         {
           "productId": "{{productId}}",
           "quantity": 2
         }
       ]
     }
     ```
   - **`ReleaseStock`**:
     ```json
     {
       "orderId": "22222222-2222-4222-8222-222222222222",
       "reason": "Postman automated test"
     }
     ```

---

### Part 4: Order Service & Gateway Server

#### 4.1 Order Service (Port 9092)
Start Order Service:
```powershell
.\gradlew.bat -p microservices/order-service :order-service-main:bootRun
```
- **Consul Configuration**: `GET http://localhost:9092/consul/config`
- **Inter-service Customer Fetch**: `GET http://localhost:9092/customers` (Order Service acquires client credentials and calls Customer Service automatically).

#### 4.2 Gateway Server (Port 9999)
Start Gateway Server:
```powershell
.\gradlew.bat -p microservices/gateway-server :gateway-server:bootRun
```
- **Health Check**: `GET http://localhost:9999/actuator/health`
- **Inspect Routes**: `GET http://localhost:9999/actuator/gateway/routes`
- **Routed Request**: `GET http://localhost:9999/my-order/customers` (Rewrites to `http://localhost:9092/customers`)

---

### Part 5: Payment Service (Port 1011 REST & 1012 gRPC)

Start Payment Service:
```powershell
.\gradlew.bat :microservices:grpc-server:bootRun
```

- **REST Process Payment**: `POST http://localhost:1011/api/payments/process?orderId=ORD-1001&amountCents=2500`
- **Direct gRPC**: In Postman gRPC, connect to `localhost:1012`, import `microservices/grpc-server/src/main/proto/payment.proto`, invoke `payment.PaymentService/ProcessPayment`.

---

## 5. Automated Collection Run via Newman CLI

To run the entire suite automatically in CI or from PowerShell without the GUI:

```powershell
# Install Newman globally (if not installed)
npm install -g newman

# Execute the complete collection
newman run microservices/postman/istad-microservices.postman_collection.json `
  -e microservices/postman/istad-local.postman_environment.json `
  --reporters cli,junit `
  --reporter-junit-export build/postman-results.xml
```

---

## 6. Troubleshooting Checklist

| Issue | Probable Cause | Fix |
| :--- | :--- | :--- |
| `401 Unauthorized` on Customer Service | Missing token or expired token | Run request `1.1 Get Token` first to save `access_token` into the environment. |
| `403 Forbidden` on Customer Service | Token lacks `customer.read` scope | Ensure the client request has `scope=customer.read business.read`. |
| Connection Refused on `localhost:9091` / `9093` | Service not started | Start service with `./gradlew.bat bootRun`. |
| gRPC Reflection does not appear in Postman | Service running without `dev` profile | Start with `$env:SPRING_PROFILES_ACTIVE = "dev"` or manually import `.proto` files. |
| Database Connection Failed | Docker container not running | Check `docker ps` and start containers via `docker compose up -d`. |
