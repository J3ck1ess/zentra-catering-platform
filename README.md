# Zentra - Catering SaaS Management Platform

Zentra is a web-based catering SaaS platform designed with a scalable multi-module architecture using Spring Boot.

The project focuses on enterprise-style backend architecture, including DTO layering, dynamic SQL, transaction management, multi-tenant isolation, business validation, and unified CRUD design.

---

## Tech Stack

- Java 21
- Spring Boot 3.x
- Maven (Multi-module)
- MySQL
- Redis
- MyBatis
- Docker
- Docker Compose
- JWT Authentication
- RBAC Authorization
- Spring AOP
- Swagger / OpenAPI 3
- BCrypt Password Hashing
- Jakarta Validation
- JUnit 5
- Mockito
- AssertJ
- Spring MockMvc

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
- Annotation-driven audit runtime
- Aspect-oriented business auditing

---

## Current Progress

### Dashboard Runtime

- Dashboard statistics runtime
- Business overview runtime
- Order overview runtime
- Quick action navigation
- Enterprise admin workbench

### Employee Admin Runtime
- Employee authentication workflow
- Employee pagination runtime
- Employee search runtime
- Employee status management
- Real-time status switch
- JWT-protected admin operations
- Enterprise CRUD workflow

### Employee Module
- Employee CRUD
- Employee pagination query
- Employee search runtime
- Dynamic PATCH update
- Independent status update runtime
- Employee status switch workflow
- DTO architecture
- Tenant isolation using `merchant_id`
- JWT login authentication
- XML-based MyBatis dynamic SQL
- Enterprise CRUD architecture
- BCrypt password management
- Self-delete protection
- Last SUPER_ADMIN deletion protection
- Enterprise business validation

### Category Module
- Category CRUD
- Category pagination query
- Category search runtime
- Category list runtime
- Category description support
- Category sort runtime
- Hot data category cache runtime
- Cache Aside cache governance
- Dynamic query filtering
- Business validation
- Prevent deleting category when dishes exist
- Dynamic PATCH update
- Tenant isolation

### Dish Module
- Dish CRUD
- Pagination query
- Dish list runtime
- Dish detail runtime
- Hot data dish cache runtime
- Cache Aside cache governance
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
- User-driven order cancellation runtime
- Payment simulation runtime
- Scheduled expired order cancellation
- Order lifecycle automation governance
- Tenant isolation
- Order item tenant isolation
- Distributed duplicate order protection
- Concurrent-safe order creation runtime
- Redis-based distributed lock governance
- Lock-aware order submission runtime
- Redis idempotency runtime
- Request fingerprint-based duplicate request protection
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

### Admin Frontend Module

- React 19 + Vite architecture
- Tailwind CSS layout system
- Ant Design component runtime
- React Router nested routing
- Shared HTTP client runtime
- JWT token storage runtime
- Login authentication workflow
- Current user runtime
- Admin header runtime
- Logout runtime
- Route guard runtime
- Invalid token auto redirect
- Dashboard workbench
- Dashboard statistics runtime
- Business overview runtime
- Order overview runtime
- Quick action navigation
- Spring Boot API integration
- Employee management runtime
- Employee pagination query
- Employee status switch runtime
- Employee create workflow
- Employee update workflow
- Employee delete workflow
- Category management runtime
- Category pagination query
- Category search workflow
- Category create workflow
- Category update workflow
- Category delete workflow
- Dish management runtime
- Dish pagination query
- Dish search workflow
- Dish create workflow
- Dish update workflow
- Dish delete workflow
- Dish status switch runtime
- Dynamic category selection runtime
- Order management runtime
- Order pagination query
- Order detail runtime
- Order search workflow
- Order status workflow
- Order lifecycle management
- Dynamic order action runtime
- User management runtime
- User pagination query
- User search workflow
- User status switch runtime
- Unified HTTP response runtime
- Business exception handling runtime
- Ant Design Table runtime
- Ant Design Form runtime
- Server-side pagination integration
- JWT-protected admin CRUD workflow

### Deployment Module

- Dockerized Spring Boot runtime
- Multi-stage Docker image build
- Docker Compose service orchestration
- Containerized MySQL infrastructure
- Containerized Redis infrastructure
- Environment-specific profile governance
- Production profile deployment runtime
- Automated database initialization
- Service network isolation
- Portable one-command deployment architecture

---

## Testing & Quality

The project adopts unit testing practices for service-layer business logic to improve regression safety, business rule verification, and maintainability.

### Employee Service Testing

- EmployeeServiceImpl unit test suite
- JUnit 5 test lifecycle management
- Mockito-based dependency isolation
- AssertJ fluent assertions
- Business exception validation
- Error code and error message verification
- Mapper interaction verification
- Dynamic update behavior verification
- Pagination query behavior verification
- Authentication failure scenario testing
- Employee status transition validation
- Self-delete protection testing
- Last SUPER_ADMIN deletion protection testing
- Database duplicate-key exception handling testing
- Multi-tenant context isolation testing

### Employee Controller Testing

- EmployeeController web-layer test suite
- JUnit 5 test lifecycle management
- Mockito-based service isolation
- MockMvc HTTP request testing
- JSON request and response validation
- DTO request binding verification
- Jakarta Bean Validation testing
- Controller-to-Service interaction verification
- Representative business exception response testing
- Unified API response contract verification
- Employee CRUD endpoint testing
- Employee pagination request binding testing
- Employee authentication endpoint testing
- Employee status update endpoint testing
- Employee deletion endpoint testing

### Category Service Testing

- CategoryServiceImpl unit test suite
- CRUD business flow testing
- Pagination query behavior verification
- Category list cache hit and cache miss testing
- Cache Aside cache invalidation testing
- Dynamic update behavior verification
- Category type and status validation testing
- Business exception validation
- Error code and error message verification
- Mapper interaction verification
- Database duplicate-key exception handling testing
- Related dish existence constraint testing
- Multi-tenant context isolation testing

### Category Controller Testing

- CategoryController web-layer test suite
- JUnit 5 test lifecycle management
- Mockito-based service isolation
- MockMvc HTTP request testing
- JSON request and response validation
- DTO request binding verification
- Jakarta Bean Validation testing
- Controller-to-Service interaction verification
- Representative business exception response testing
- Unified API response contract verification
- Category CRUD endpoint testing
- Category pagination request binding testing
- Category list endpoint testing

### Dish Service Testing

- DishServiceImpl unit test suite
- JUnit 5 test lifecycle management
- Mockito-based dependency isolation
- AssertJ fluent assertions
- Business exception validation
- Error code and error message verification
- Mapper interaction verification
- Redis cache interaction verification
- Cache Aside behavior testing
- Dynamic PATCH update behavior testing
- Duplicate-key exception handling testing
- Multi-tenant context isolation testing

### Dish Controller Testing

- DishController web-layer test suite
- JUnit 5 test lifecycle management
- Mockito-based service isolation
- MockMvc HTTP request testing
- JSON request and response validation
- DTO request binding verification
- Jakarta Bean Validation testing
- Controller-to-Service interaction verification
- Representative business exception response testing
- Unified API response contract verification
- Dish CRUD endpoint testing
- Dish pagination request binding testing
- Dish list endpoint testing
- Dish detail endpoint testing

### User Service Testing

- UserServiceImpl unit test suite
- JUnit 5 test lifecycle management
- Mockito-based dependency isolation
- AssertJ fluent assertions
- Business exception validation
- Error code and error message verification
- Mapper interaction verification
- Redis cache interaction verification
- Cache Aside behavior testing
- Negative cache / empty cache testing
- JWT blacklist interaction testing
- Authentication failure scenario testing
- User profile cache hit and cache miss testing
- User profile cache eviction testing
- User registration validation testing
- User login rate limiting testing
- Verification code retry protection testing
- User order pagination testing
- Order cancellation and payment state transition testing
- Multi-tenant context isolation testing

### User Controller Testing

- UserController web-layer test suite
- JUnit 5 test lifecycle management
- Mockito-based service isolation
- MockMvc HTTP request testing
- JSON request and response validation
- DTO request binding verification
- Jakarta Bean Validation testing
- Controller-to-Service interaction verification
- Representative business exception response testing
- Unified API response contract verification
- User registration endpoint testing
- User login endpoint testing
- User logout endpoint testing
- User profile endpoint testing
- User detail endpoint testing
- User order pagination endpoint testing
- User order cancellation endpoint testing
- User order payment endpoint testing
- Authorization header token extraction testing
- Query parameter binding testing
- Path variable binding testing

### User Admin Service Testing

- UserAdminServiceImpl unit test suite
- Mockito-based dependency isolation
- AssertJ fluent assertions
- Business exception validation
- Error code and error message verification
- Mapper interaction verification
- User pagination query behavior verification
- Username and status filter behavior testing
- Empty result pagination testing
- User status validation testing
- User status update workflow testing
- Database update failure handling testing

### User Admin Controller Testing

- UserAdminController web-layer test suite
- JUnit 5 test lifecycle management
- Mockito-based service isolation
- MockMvc HTTP request testing
- JSON request and response validation
- DTO request binding verification
- Jakarta Bean Validation testing
- Controller-to-Service interaction verification
- Representative business exception response testing
- Unified API response contract verification
- User pagination endpoint testing
- User status update endpoint testing
- Query parameter binding testing
- Path variable binding testing
- Request body validation testing

### Order Service Testing

- OrderServiceImpl unit test suite
- Mockito-based dependency isolation
- AssertJ fluent assertions
- Business exception validation
- Error code and error message verification
- Mapper interaction verification
- Transactional order creation testing
- Redis idempotency behavior testing
- Distributed lock behavior testing
- Duplicate order request protection testing
- Empty order validation testing
- Dish existence and availability validation testing
- Server-side order amount calculation testing
- Multiple order item creation testing
- Order creation failure handling testing
- Order item creation failure handling testing
- Order pagination query behavior verification
- Order detail query and DTO assembly testing
- Order status validation testing
- Order status transition testing
- Database status update failure handling testing
- Automatic expired order cancellation testing
- Multi-tenant context isolation testing

### Order Controller Testing

- OrderController web-layer test suite
- JUnit 5 test lifecycle management
- Mockito-based service isolation
- MockMvc HTTP request testing
- JSON request and response validation
- DTO request binding verification
- Nested DTO validation testing
- Jakarta Bean Validation testing
- Controller-to-Service interaction verification
- Representative business exception response testing
- Unified API response contract verification
- Order creation endpoint testing
- Order pagination endpoint testing
- Order detail endpoint testing
- Order status update endpoint testing
- Query parameter binding testing
- Path variable binding testing
- Request body validation testing
- Nested order item validation testing

### Current Test Status

- EmployeeServiceImpl: 28 unit tests
- EmployeeController: 25 web-layer tests
- CategoryServiceImpl: 22 unit tests
- CategoryController: 11 web-layer tests
- DishServiceImpl: 22 unit tests
- DishController: 17 web-layer tests
- UserServiceImpl: 32 unit tests
- UserController: 22 web-layer tests
- UserAdminServiceImpl: 7 unit tests
- UserAdminController: 4 web-layer tests
- OrderServiceImpl: 21 unit tests
- OrderController: 11 web-layer tests
- Test result: 222 passed, 0 failed, 0 errors, 0 skipped

The test suite validates both successful business flows and failure paths, including application-level validation and database constraint fallback handling.

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
- JWT interceptor support for CORS preflight requests
- Current authenticated employee runtime
- Protected admin route runtime
- Admin logout workflow
- Invalid token auto redirect runtime

### Workflow

1. User or employee logs in via authentication APIs
2. Server validates credentials and generates JWT token
3. Client stores JWT token locally
4. Client sends token in Authorization header
5. JWT interceptor validates the token
6. User identity is stored in ThreadLocal
7. Current authenticated user information is retrieved
8. Protected admin pages become accessible
9. Logout removes local authentication state

### Admin Authentication Runtime

The admin frontend implements a complete authentication lifecycle:

- JWT-based login
- Current authenticated employee retrieval (`/employee/me`)
- Dynamic admin header
- Protected admin route runtime
- Logout workflow
- Invalid token auto redirection

Authentication flow:

```text
Login
    ↓
JWT Storage
    ↓
HTTP Interceptor
    ↓
Current Employee Runtime
    ↓
Admin Header
    ↓
Protected Admin Pages
    ↓
Logout / Token Expiration
```

---

## Enterprise RBAC Architecture

This project implements an enterprise-style RBAC (Role-Based Access Control) architecture for admin-side authorization governance.

### Role Permission Matrix

The system adopts a centralized role permission matrix model.

Built-in roles:

- SUPER_ADMIN
- STORE_MANAGER
- CASHIER
- KITCHEN_STAFF

Permission resolution flow:

Role
→ PermissionProvider
→ PermissionContext
→ PermissionInterceptor
→ API Access Decision

The permission matrix is maintained through an immutable role-permission mapping and validated through runtime authorization testing.

Role responsibilities:

- SUPER_ADMIN: Full system permissions.
- STORE_MANAGER: Store operation and management permissions.
- CASHIER: Order processing and customer management permissions.
- KITCHEN_STAFF: Kitchen workflow and order status update permissions.

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

### Role Migration History

Legacy role values:

- admin
- staff

were migrated to the standardized enterprise role model:

- SUPER_ADMIN
- STORE_MANAGER

to ensure consistent RBAC governance across development, testing, and containerized deployment environments.

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
- Generic Redis cache infrastructure
- Cache Aside consistency governance
- Cache penetration protection
- Distributed lock runtime infrastructure
- Concurrent-safe order runtime governance
- Redis idempotency runtime
- Request fingerprint-based duplicate request protection
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
- User profile cache runtime
- Category list cache runtime
- Dish detail cache runtime
- Generic collection cache support
- Redis TypeReference-based cache infrastructure
- Cache eviction and rebuild governance
- Cache penetration protection

Lock Runtime
- Distributed lock infrastructure
- Lock ownership verification
- Concurrent-safe business execution

Idempotency Runtime
- Redis SETNX-based request protection
- Request fingerprint generation
- SHA-256 fingerprint hashing
- Order create idempotency governance
- Fast-fail duplicate request interception

---

## Runtime Observability Architecture

The project implements a structured runtime observability architecture to improve traceability, troubleshooting efficiency, and operational governance.

### Runtime Domains

- AUTH Runtime
- AUDIT Runtime
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
- Annotation-driven audit governance
- Business operation traceability

---

## Enterprise Audit Runtime

This project implements an enterprise-style audit runtime based on Spring AOP for business operation governance.

### Features

- Annotation-driven audit logging (`@AuditLog`)
- Aspect-oriented audit interception
- ThreadLocal-based operator resolution
- Unified audit persistence runtime
- Business operation traceability
- Success and failure audit recording
- Structured audit runtime logging
- Runtime execution time recording

### Runtime Flow

```text
Controller
    ↓
@AuditLog
    ↓
Audit Aspect
    ↓
Operator Resolution
    ↓
Business Execution
    ↓
Audit Persistence
```

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
- User order cancellation support
- Payment simulation workflow
- Automatic expiration cancellation
- Scheduler-driven order lifecycle governance

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
  
Additional lifecycle automation:

- User-triggered order cancellation
- User-triggered payment simulation
- Automatic cancellation for expired pending orders
- Idempotent order creation protection

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
- Database unique constraint governance
- Redis idempotency validation
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
├── zentra-admin      # React admin frontend
├── zentra-user       # User-side frontend (planned)
```

---

## Docker Deployment

### Deployment Architecture

The project supports containerized deployment using Docker and Docker Compose.

The deployment stack includes:

- Spring Boot Runtime Container
- MySQL Database Container
- Redis Cache Container
- Dedicated Docker Network
- Persistent Volume Management

### Runtime Topology

```text
Client Request
    ↓
Spring Boot Container
    ├── MySQL Container
    └── Redis Container
```

### Build Image

```bash
docker build -t zentra-server:1.0 .
```

### Start Services

```bash
docker compose up -d
```

### Stop Services

```bash
docker compose down
```

### View Runtime Logs

```bash
docker logs -f zentra-server
```

### Service Ports

| Service | Port |
|----------|------|
| Zentra Server | 8080 |
| MySQL | 3307 |
| Redis | 6380 |

### Environment Profiles

Development Environment:

```text
application.yml
```

Containerized Production Environment:

```text
application-prod.yml
```

The production profile is activated through Docker Compose runtime variables.

---

## Future Improvements

- Admin frontend feature expansion
- Admin profile management
- Password reset runtime
- User frontend implementation
- Payment workflow integration
- MyBatis interceptor for automatic tenant injection
- Employee permission management
- Redisson-based distributed lock optimization
- Distributed lock watchdog renewal
- Idempotency token 
- CI/CD pipeline integration
- Container health check governance
- Production monitoring and observability

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