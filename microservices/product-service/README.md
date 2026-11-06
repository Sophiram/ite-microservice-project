# Product Service

Product Service owns product metadata and stock. REST is used for basic product administration; Order Service uses the versioned gRPC contract in `istad-common/istad-common-grpc/src/main/proto`.

## gRPC contract

The service is `istad.product.v1.ProductService` and imports `istad.common.v1.Money` (`amount` is a decimal string, never a floating-point value). The RPCs are:

- `GetProduct` and `BatchGetProducts` for product and price reads.
- `ReserveStock`, idempotent by `order_id`, for atomic stock reservation.
- `ReleaseStock`, idempotent by `order_id`, for reservation compensation.

The contract also defines `PageRequest` and `PageInfo`; these support future paged catalog reads. Stock changes are persisted in the same transaction as the reservation record. The unique `stock_reservations.order_id` index protects the idempotency key.

## Ports and adapters

`product-domain-core` contains the Product and StockReservation aggregates. `product-application-service` defines use cases and repository ports. `product-service-persistence` implements the ports with PostgreSQL/JPA and Liquibase. The gRPC and REST modules are inbound adapters and call application use cases only.

## Running locally

Start PostgreSQL with the repository's deployment configuration (the init script creates `db_product`), then run:

```powershell
.\gradlew.bat -p microservices\product-service :product-service-main:bootRun --args="--spring.profiles.active=dev"
```

HTTP listens on `9093`; gRPC listens on `1013`. Override them with `SERVER_PORT` and `GRPC_SERVER_PORT`. Set the `dev` Spring profile to enable reflection; gRPC health is registered by Spring gRPC.

With `grpcurl`:

```text
grpcurl -plaintext localhost:1013 list
grpcurl -plaintext -d '{"productId":"<product-uuid>"}' localhost:1013 istad.product.v1.ProductService/GetProduct
grpcurl -plaintext -d '{"orderId":"<order-uuid>","businessId":"<business-uuid>","items":[{"productId":"<product-uuid>","quantity":2}]}' localhost:1013 istad.product.v1.ProductService/ReserveStock
grpcurl -plaintext -d '{"orderId":"<order-uuid>","reason":"order cancelled"}' localhost:1013 istad.product.v1.ProductService/ReleaseStock
```

`GetProduct` and `BatchGetProducts` accept UUID strings. `ReserveStock` rejects inactive products and insufficient quantity with `FAILED_PRECONDITION`; missing products return `NOT_FOUND`.

## Running tests

Run all Product Service tests and compile every module from the repository root:

```powershell
.\gradlew.bat -p microservices\product-service build
```

Run a focused test suite when iterating:

```powershell
.\gradlew.bat -p microservices\product-service :product-service-domain:product-domain-core:test
.\gradlew.bat -p microservices\product-service :product-service-domain:product-application-service:test
.\gradlew.bat -p microservices\product-service :product-service-grpc:test
```

The domain-core tests cover stock bounds and aggregate release idempotency. Application-service tests exercise reserve/release idempotency by order ID. The gRPC suite uses an in-process server to test all four RPCs and the `NOT_FOUND`, `FAILED_PRECONDITION`, `INVALID_ARGUMENT`, and `INTERNAL` status mappings; it does not require PostgreSQL or a running Product Service.

The Order Service gRPC client adapter can be tested independently:

```powershell
.\gradlew.bat -p microservices\order-service :order-service-client:test
```

That in-process client suite checks product mapping, reserve/release requests, and downstream status mapping. Run the full Order Service build with `.\gradlew.bat -p microservices\order-service build`.

## Publishing the protobuf contract

The generated Java protobuf and gRPC classes are built from source protos and are not checked in. Publish the shared module to the local Maven repository with:

```powershell
.\gradlew.bat -p istad-common :istad-common-grpc:publishToMavenLocal
```

The Product and Order builds include `istad-common` as a composite build, so local development does not require the publication step.
