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
- RBAC Authorization
- Swagger / OpenAPI 3
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
- Distributed duplicate order protection
- Concurrent-safe order creation runtime
- Redis-based distributed lock governance
- Lock-aware order submission runtime
- Fast-fail duplicate request interception

### User Module
- User registration
- User login authentication
- BCrypt password hashing
- JWT token generation
- User profile query
- User order history query
- User-order association
- Protected API access control
- ThreadLocal-based authentication context
- Hot data profile cache runtime
- Tenant-aware user cache governance
- Profile cache eviction runtime
- Distributed cache consistency protection

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
- Multi-user authentication architecture
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

## Enterprise RBAC Architecture

This project implements an enterprise-style RBAC (Role-Based Access Control) architecture for admin-side authorization governance.

### Features

- Role-based permission management
- Annotation-driven authorization (`@RequirePermission`)
- Interceptor-based permission validation
- ThreadLocal-based permission context
- JWT + RBAC integration
- Role-permission mapping infrastructure
- CRUD permission matrix governance
- Permission-aware Swagger/OpenAPI documentation
- Admin API authorization isolation
- Multi-domain authentication and authorization architecture

### RBAC Architecture

The RBAC system follows a layered enterprise authorization architecture:

```text
JWT Authentication
    ↓
Role Resolution
    ↓
Permission Provider
    ↓
Permission Context
    ↓
Permission Interceptor
    ↓
Annotation-driven Authorization
    ↓
Controller Access Control
```

### Permission Governance

The project implements fine-grained permission governance using CRUD-based permission design:

- employee:create
- employee:view
- employee:update
- employee:delete

The same governance model is applied across employee, user admin, category, dish, and order management APIs.

### Security Design

- JWT-based identity authentication
- RBAC-based authorization control
- ThreadLocal request-scoped permission context
- Role-to-permission mapping infrastructure
- Annotation-driven permission validation
- Centralized authorization interception
- Permission-aware API documentation

---

## Enterprise Redis Verification Architecture

This project implements an enterprise-style Redis-based verification runtime architecture for authentication security governance.

### Features

- Redis infrastructure integration
- Redis namespace and TTL governance
- Verification runtime and retry governance
- Login rate limiting and traffic protection
- JWT blacklist and token revocation governance
- Hot data cache runtime architecture
- Cache Aside consistency governance
- Cache penetration protection
- Distributed lock runtime infrastructure
- Concurrent-safe order runtime governance
- Fast-fail duplicate request protection
- Runtime observability governance
- Structured runtime logging architecture
- Swagger/OpenAPI verification integration

### Distributed Runtime Flow

The verification system follows a distributed runtime security architecture:

```text
Client Request
    ↓
Authentication Runtime
    ↓
Login Rate Limiting
    ↓
Verification Runtime
    ↓
JWT Generation
    ↓
Authenticated Request
    ↓
JWT Blacklist Validation
    ↓
RBAC Authorization
    ↓
Hot Data Cache Runtime
    ↓
Cache Hit → Return
    ↓
Cache Miss
    ↓
Database Query
    ↓
Cache Writeback
    ↓
Distributed Lock Runtime
    ↓
Duplicate Request Protection
    ↓
Concurrent-safe Business Runtime
    ↓
Business Response
```
  
### Runtime Domains

Verification Runtime
- Redis-based verification storage
- TTL-based expiration governance
- Verification retry protection
- Anti-bruteforce validation strategy

Rate Limit Runtime
- Redis atomic request counter
- Fixed-window login rate limiting
- Authentication traffic protection

JWT Runtime
- Distributed JWT blacklist validation
- Token revocation governance
- Logout lifecycle synchronization

Cache Runtime
- Cache Aside architecture
- Tenant-aware cache isolation
- Cache eviction and rebuild governance
- Cache penetration protection

Lock Runtime
- Distributed lock infrastructure
- Duplicate request protection
- Lock ownership verification
- Concurrent-safe business execution

---

## Runtime Observability Architecture

The project implements a structured runtime observability architecture to improve traceability, troubleshooting efficiency, and operational governance.

### Runtime Domains

- AUTH Runtime
- RBAC Runtime
- CACHE Runtime
- LOCK Runtime
- ORDER Runtime
- VALIDATION Runtime
- BUSINESS Runtime
- SYSTEM Runtime

### Observability Design

The runtime logging system follows a lifecycle-oriented design:

Request Start
↓
Business Validation
↓
Runtime Execution
↓
Persistence Validation
↓
Runtime Completion

All critical runtime events are recorded using structured logging with unified domain prefixes.

### Governance Principles

- Structured log format
- Domain-based log classification
- Runtime lifecycle tracing
- Security event auditing
- Cache runtime observability
- Distributed lock observability
- Exception governance integration

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
- Distributed concurrency runtime documentation
- Duplicate request API governance
- Concurrent-safe order runtime documentation

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
- Payment workflow integration
- Docker deployment
- MyBatis interceptor for automatic tenant injection
- Employee permission management
- Redisson-based distributed lock optimization
- Distributed lock watchdog renewal
- Idempotency token runtime

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