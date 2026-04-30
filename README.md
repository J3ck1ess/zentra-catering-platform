# Zentra - Catering SaaS Management Platform

Zentra is a web-based catering SaaS platform designed with a scalable multi-module architecture using Spring Boot.

## Tech Stack
- Java 21
- Spring Boot 3.x
- Maven (Multi-module)
- MySQL
- MyBatis
- JWT (Authentication)

## Current Progress
- Project structure initialized
- Employee module implemented
- Employee CRUD (getById, getAll, create) completed
- JWT-based authentication system implemented
- Category module implemented (CRUD + sorting)
- Dish module implemented with full CRUD, pagination, DTO, join query, validation, and dynamic update support 
- Order module implemented with transactional order creation and order item snapshot design

## Optimization & Refactoring
- Refactored **Category module** with DTO layer (Create / Update / Query) and dynamic SQL for flexible querying
- Implemented **multi-tenant data isolation** using `merchant_id` across all CRUD operations
- Added **business validation** (e.g., prevent deleting category when it is used by dishes)
- Introduced **unified exception handling** via `GlobalExceptionHandler`
- Created **AssertUtil** to standardize validation and database operation checks (e.g., affected rows)
- Aligned **Dish module** with Category design to ensure consistency, security, and maintainability

> These improvements enhance code maintainability, data security, and align the project with enterprise-level backend design practices.

## Order Module

The Order module supports full order creation with transactional consistency and item-level snapshot storage.

### Features
- Create orders with multiple dishes
- Transactional operation (orders + order_item)
- Server-side total amount calculation (prevent tampering)
- Snapshot design (store dish name and price at order time)
- Multi-tenant isolation using merchant_id

### Workflow
1. Client submits order items (dishId + quantity)
2. Server validates dish ownership and availability
3. Total amount is calculated on the server
4. Order is created in `orders` table
5. Order items are stored in `order_item` table
6. All operations are wrapped in a transaction

## Authentication Module (JWT)

This project implements a stateless authentication system using JSON Web Token (JWT).

### Features
- User login with username and password
- JWT token generation upon successful authentication
- Token validation via interceptor
- Global request authentication control
- ThreadLocal-based user context (store current user ID)
- Unified exception handling
- Structured API response with DTO

### Workflow
1. User logs in via `/employee/login`
2. Server validates credentials and generates JWT token
3. Client sends token in `Authorization` header (`Bearer token`)
4. Interceptor validates token before accessing protected APIs
5. User identity is stored in ThreadLocal and accessible throughout request lifecycle

## Project Structure (Simplified)
zentra-catering-platform  
├── zentra-common     # Common utilities (Result, constants)  
├── zentra-server     # Core backend service (Controller, Service, JWT, Interceptor)  
├── zentra-admin      # Admin frontend (future)  
├── zentra-user       # User frontend (future)

## Next Steps
- Order querying (pagination + detail view)
- Order status flow (pending → paid → completed → canceled)
- Add role-based access control (admin / staff)
- Order and payment system