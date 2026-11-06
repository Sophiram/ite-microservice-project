# gRPC Server Testing Instructions

> **Note:** For the full documentation and architecture details, see [README.md](file:///D:/MicroserviceProject/istad-ite-project/microservices/grpc-server/README.md).

---

## Quick Reference

| Service | Protocol | Host / Port | Details |
| :--- | :--- | :--- | :--- |
| **Payment REST Bridge** | HTTP | `http://localhost:1011` | `POST /api/payments/process` |
| **PaymentService** | gRPC | `localhost:1012` | `payment.PaymentService/ProcessPayment` |

---

## 1. Automated Test Execution

Run the JUnit integration tests:
```powershell
.\gradlew.bat :microservices:grpc-server:test
```

---

## 2. Start the Service

```powershell
.\gradlew.bat :microservices:grpc-server:bootRun
```

---

## 3. Test HTTP REST Endpoint (Port 1011)

### PowerShell:
```powershell
# Success test
Invoke-RestMethod -Method Post -Uri "http://localhost:1011/api/payments/process?orderId=ORD-1001&amountCents=2500"

# Failure test
Invoke-RestMethod -Method Post -Uri "http://localhost:1011/api/payments/process?orderId=ORD-1002&amountCents=0"
```

### cURL:
```bash
# Success test
curl -X POST "http://localhost:1011/api/payments/process?orderId=ORD-1001&amountCents=2500"

# Failure test
curl -X POST "http://localhost:1011/api/payments/process?orderId=ORD-1002&amountCents=0"
```

---

## 4. Test Direct gRPC (Port 1012) via `grpcurl`

```powershell
grpcurl -plaintext -proto microservices/grpc-server/src/main/proto/payment.proto -d '{\"order_id\": \"ORD-001\", \"amount_cents\": 5000}' localhost:1012 payment.PaymentService/ProcessPayment
```

---

## 5. Postman gRPC Testing

1. **New Request** > Select **gRPC**.
2. **Server URL**: `localhost:1012`.
3. **Proto file**: Import `microservices/grpc-server/src/main/proto/payment.proto`.
4. **Method**: `payment.PaymentService/ProcessPayment`.
5. **JSON Payload**:
   ```json
   {
     "order_id": "ORD-001",
     "amount_cents": 5000
   }
   ```
6. Click **Invoke**.
