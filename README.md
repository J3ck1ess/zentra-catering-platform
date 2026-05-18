# Zentra - Catering SaaS Management Platform

Zentra is a web-based catering SaaS platform designed with a scalable multi-module architecture using Spring Boot.

The project focuses on enterprise-style backend architecture, including DTO layering, dynamic SQL, transaction management, multi-tenant isolation, business validation, and unified CRUD design.

---

## Tech Stack

- Java 21
- Spring Boot 3.x
- Maven (Multi-module)
- MySQL
- MyBatis
- JWT Authentication
- BCrypt Password Hashing
- Jakarta Validation

---

## Core Architecture

This project follows a unified enterprise-style backend architecture across all modules.

### Unified CRUD Architecture

All business modules follow the same layered design:

- Controller → Service → Mapper
- DTO-based request and response design
- Entity and DTO separation
- XML-based MyBatis dynamic SQL
- Unified pagination structure
- PATCH-based dynamic update APIs
- Centralized validation and exception handling
- Multi-tenant data isolation using `merchant_id`

### Enterprise Backend Design

The system includes multiple enterprise-oriented backend patterns:

- Transaction management
- Order status flow control
- Snapshot design for order items
- One-to-many DTO nesting
- Dynamic query filtering
- Server-side amount calculation
- Business constraint validation
- Affected-row validation (`checkRows`)
- Constant-based status management 
- JWT-based authentication workflow
- BCrypt password hashing
- User-order business association

---

## Current Progress

### Employee Module
- Employee CRUD
- Pagination query
- Dynamic PATCH update
- DTO architecture
- Tenant isolation using `merchant_id`
- JWT login authentication
- XML-based dynamic SQL

### Category Module
- Category CRUD
- Pagination query
- Dynamic query filtering
- Business validation
- Prevent deleting category when dishes exist
- Dynamic PATCH update
- Tenant isolation

### Dish Module
- Dish CRUD
- Pagination query
- Category JOIN query
- Category name mapping
- Dynamic query filtering
- Category existence validation
- Dynamic PATCH update
- Tenant isolation

### Order Module
- Transactional order creation
- Order pagination query
- Order detail query
- One-to-many DTO nesting
- Snapshot design for order items
- Server-side total amount calculation
- Order status flow control
- State transition validation
- Tenant isolation
- Order item tenant isolation

### User Module
- User registration
- User login authentication
- BCrypt password hashing
- JWT token generation
- User profile query
- User order history query
- User-order association
- Protected API access control
- Multi-user authentication architecture
- ThreadLocal-based authentication context

### User Admin Module
- Admin user pagination query
- User account status management
- Admin-level user governance APIs
- Status validation and update workflow
- Swagger/OpenAPI integration
- DTO-based admin response architecture
- JWT-based admin authorization control
- Multi-domain API access routing

---

## Authentication Module (JWT)

This project implements a stateless authentication system using JSON Web Token (JWT).

### Features

- User login with username and password
- JWT token generation upon successful authentication
- Token validation via interceptor
- Global request authentication control
- ThreadLocal-based user context
- Unified exception handling
- Structured API response with DTO
- BCrypt password hashing
- User and employee authentication support
- AuthContext-based authentication architecture
- Multi-user identity support (`USER` / `EMPLOYEE`)
- Interceptor-based API authorization control
- API access isolation based on authenticated user type
- Admin API authorization routing (`/admin/**`)

### Workflow

1. User or employee logs in via authentication APIs
2. Server validates credentials and generates JWT token
3. Client sends token in `Authorization` header (`Bearer token`)
4. Interceptor validates token before accessing protected APIs
5. User identity is stored in ThreadLocal and accessible throughout request lifecycle
6. API access is restricted based on user identity type

---

## Enterprise OpenAPI Architecture

This project implements an enterprise-style OpenAPI architecture using SpringDoc and Swagger UI.

### Features

- Grouped OpenAPI documentation (`user-api` / `admin-api`)
- Reusable global response components
- Unified validation error documentation
- Centralized error response governance
- Generic API response schema documentation
- DTO-based request and response documentation
- Global OpenAPI component registry
- Tag governance and API grouping
- Admin user API documentation governance
- Query parameter governance
- Reusable pagination query infrastructure
- JWT authentication integration in Swagger UI
- Enterprise-style API metadata management and documentation portal

### API Documentation Access

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Architecture

The project follows a layered enterprise-style OpenAPI governance architecture:

```text
Controller Layer
    ↓
Business Semantic Annotations
    ↓
OpenAPI Infrastructure Layer
    ↓
Reusable OpenAPI Components
```

The API documentation system includes reusable response schemas, centralized examples, grouped APIs, global metadata management, and enterprise-oriented documentation governance.

---

## Order Module Design

The Order module supports full order lifecycle including creation, querying, detailed retrieval, and state transition control.

### Features

- Create orders with multiple dishes
- Transactional operation (`orders + order_item`)
- Snapshot storage for dish name and price
- Server-side amount calculation
- Pagination query support
- One-to-many DTO assembly
- State flow validation
- Multi-tenant isolation

### Workflow

1. Client submits order items (`dishId + quantity`)
2. Server validates dish ownership and availability
3. Server calculates total amount
4. Order is inserted into `orders`
5. Order items are inserted into `order_item`
6. All operations are executed within a transaction

### Status Flow

Allowed transitions:

- PENDING → PAID
- PENDING → CANCELLED
- PAID → COMPLETED

Invalid transitions are rejected at the service layer.

The flow is implemented using a centralized transition map to ensure maintainability and consistency.

### Query Design

- Pagination query using `LIMIT + OFFSET`
- Order detail retrieval using two-step query (`order + order_item`)
- One-to-many DTO assembly in Service layer

---

## Security & Validation

### Multi-Tenant Isolation

All business queries include `merchant_id` restrictions to ensure tenant-level data isolation.  
User-level order ownership is enforced using `user_id` association.

### Validation Strategy

Validation is implemented at multiple layers:

- DTO validation (`@Valid`)
- Business validation in Service layer
- Affected-row validation using `AssertUtil.checkRows()`
- Status transition validation
- Ownership validation
- Password hashing using BCrypt

### Exception Handling

Global exception handling is implemented via `GlobalExceptionHandler`.

### Global Response Code System

The project implements a unified business exception architecture based on:

- Global business error codes (`ErrorCode`)
- Centralized error messages (`ErrorMessage`)
- Custom business exceptions (`BusinessException`)
- Unified API response wrapper (`Result<T>`)
- Global exception interception (`GlobalExceptionHandler`)
- Validation and business exception separation
- Consistent error response structure across all modules

All business exceptions are standardized into unified JSON responses:

```json
{
  "code": 50001,
  "msg": "Category not found"
}
```

Business validation, authentication, authorization, and tenant isolation errors are all integrated into the same response architecture.


---

## Project Structure

```text
zentra-catering-platform
├── zentra-common     # Common utilities, auth, context, constants, Result wrapper
├── zentra-server     # Core backend service
├── zentra-admin      # Admin-side frontend (future)
├── zentra-user       # User-side frontend (future)
```

---

## Future Improvements

- Admin/User frontend implementation
- Payment integration
- Redis caching
- Docker deployment
- MyBatis interceptor for automatic tenant injection
- Order payment workflow
- Employee permission management
- Fine-grained RBAC permission system
- Annotation-based permission control
- Redis-based token blacklist

---

## Project Goals

This project is designed not only as a CRUD practice project, but also as a backend architecture practice focused on:

- Enterprise backend design
- SaaS multi-tenant architecture
- Business-oriented service design
- Maintainable layered architecture
- Transactional business workflows
- Secure API development
- Real-world authentication and authorization architecture