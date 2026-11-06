# Testing Product and Order gRPC

This guide covers manual gRPC calls to Product Service with grpcurl or Postman,
and automated verification of Order Service's gRPC client adapter.

## What can be tested manually in Postman

Product Service exposes a gRPC server. Order Service currently contains a gRPC client adapter, not a gRPC server, and no Order creation use case or HTTP route calls that adapter yet. Therefore, Postman can call Product Service directly, but cannot trigger an Order-to-Product gRPC call through Order Service at this stage. Use the Order adapter test below to verify the client behavior.

## Start Product Service

1. Make sure PostgreSQL is running and `db_product` exists. Product Service uses Liquibase to create its tables.
2. From the repository root, start Product Service with the `dev` profile:

   ```powershell
   $env:SPRING_PROFILES_ACTIVE = "dev"
   .\gradlew.bat -p microservices\product-service :product-service-main:bootRun
   ```

3. Wait until the application starts. Its default HTTP port is `9093` and gRPC port is `1013`. The `dev` profile enables gRPC reflection.

## Create a product for testing

In Postman, create an HTTP `POST` request to `http://localhost:9093/api/products` with `Content-Type: application/json` and a raw JSON body:

```json
{
  "businessId": "11111111-1111-4111-8111-111111111111",
  "sku": "POSTMAN-001",
  "name": "Postman gRPC test product",
  "description": "Product used for Product Service gRPC testing",
  "unitPrice": 12.50,
  "currency": "USD",
  "availableQuantity": 10
}
```

Save the returned `id` as `productId`; use it in the requests below. Product creation is done through REST because there is no create-product RPC.

## Call Product Service with grpcurl

Install [grpcurl](https://github.com/fullstorydev/grpcurl). With the `dev`
profile running, reflection is enabled, so the schema files are not needed:

```powershell
grpcurl -plaintext localhost:1013 list
grpcurl -plaintext localhost:1013 describe istad.product.v1.ProductService
grpcurl -plaintext -d '{"productId":"<product-id>"}' localhost:1013 istad.product.v1.ProductService/GetProduct
grpcurl -plaintext -d '{"productIds":["<product-id>"]}' localhost:1013 istad.product.v1.ProductService/BatchGetProducts
grpcurl -plaintext -d '{"orderId":"22222222-2222-4222-8222-222222222222","businessId":"11111111-1111-4111-8111-111111111111","items":[{"productId":"<product-id>","quantity":2}]}' localhost:1013 istad.product.v1.ProductService/ReserveStock
grpcurl -plaintext -d '{"orderId":"22222222-2222-4222-8222-222222222222","reason":"grpcurl test"}' localhost:1013 istad.product.v1.ProductService/ReleaseStock
```

Replace `<product-id>` with the ID returned by the REST create request above.
If reflection is unavailable, supply the schemas from the sibling `istad-proto`
repository:

```powershell
grpcurl -plaintext -import-path istad-proto\proto -proto istad\product\v1\product.proto -proto istad\common\v1\common.proto -d '{"productId":"<product-id>"}' localhost:1013 istad.product.v1.ProductService/GetProduct
```

## Send Product Service gRPC requests in Postman

1. Create a new **gRPC Request** in Postman.
2. Set the server URL to `localhost:1013`.
3. Use server reflection to list services and select `istad.product.v1.ProductService`. If reflection does not load, ensure the service is running with the `dev` profile. Alternatively, import both proto files from `istad-proto\proto` (`istad\common\v1\common.proto` and `istad\product\v1\product.proto`) into Postman.
4. Choose an RPC, enter the JSON message, and select **Invoke**.

### `GetProduct`

```json
{
  "productId": "{{productId}}"
}
```

Expected: product details including `unitPrice.amount` as a decimal string, `unitPrice.currency`, and the current available quantity.

### `BatchGetProducts`

```json
{
  "productIds": ["{{productId}}"]
}
```

Expected: a `products` array containing the requested product.

### `ReserveStock`

Use a new UUID for `orderId` each time you want to test a new reservation. The `businessId` must match the one used to create the product.

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

Expected: a reservation ID and `reservedProducts` price snapshot. Invoke the exact same request again with the same `orderId` to test idempotency; stock must not be deducted a second time. Check available stock with `GetProduct`.

### `ReleaseStock`

Use the same `orderId` as the reservation:

```json
{
  "orderId": "22222222-2222-4222-8222-222222222222",
  "reason": "Postman test"
}
```

Expected: `released: true` the first time and `released: false` for a repeated release. Check stock again with `GetProduct`; the reserved quantity should have been restored once.

### Error behavior

- Unknown product ID: gRPC `NOT_FOUND`.
- Inactive product or insufficient stock: `FAILED_PRECONDITION`.
- Invalid UUID, missing required values, or invalid quantities: `INVALID_ARGUMENT`.
- Reusing an order ID with different reservation items: `INVALID_ARGUMENT`.

## Verify the Order Service gRPC client

The client adapter is exercised against an in-process fake gRPC Product Service, so it does not require either service or PostgreSQL to be running. From the repository root, run:

```powershell
.\gradlew.bat -p microservices\order-service :order-service-client:test
```

`ProductServiceGrpcClientAdapterTest` verifies product response mapping, reserve and release request mapping, and handling of `NOT_FOUND`, `FAILED_PRECONDITION`, `UNAVAILABLE`, and `DEADLINE_EXCEEDED`. To build and test the complete Order Service:

```powershell
.\gradlew.bat -p microservices\order-service build
```

When an Order use case is wired to the adapter, configure the Product Service address with `PRODUCT_SERVICE_GRPC_ADDRESS` (default `localhost:1013`) and the per-call timeout with `PRODUCT_SERVICE_GRPC_DEADLINE` (default `3s`). The blocking gRPC calls are executed on Reactor's bounded-elastic scheduler.

## Validate and publish the Buf module

The schemas and Buf configuration live in the sibling `istad-proto` repository.
From the project root, lint them with:

```powershell
Push-Location istad-proto
buf lint
Pop-Location
```

Pushes and pull requests run the Buf lint and breaking-change checks through
`.github\workflows\buf.yaml` in that repository. Log in and create the public
BSR module once before pushing:

```powershell
buf registry login
buf registry module create buf.build/sophiram/istad-proto --visibility public
buf push
buf registry sdk version --module=buf.build/sophiram/istad-proto --plugin=buf.build/protocolbuffers/java
buf registry sdk version --module=buf.build/sophiram/istad-proto --plugin=buf.build/grpc/java
```

Pass the first result as `bufProtoJavaSdkVersion` and the second as
`bufGrpcJavaSdkVersion` when building the microservices repository. For example:

```powershell
.\gradlew.bat build -PbufProtoJavaSdkVersion=PROTOBUF_SDK_VERSION -PbufGrpcJavaSdkVersion=GRPC_SDK_VERSION
```
