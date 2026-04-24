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
- Dish module implemented with pagination, DTO, and join query optimization

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
- Order module (orders + order_item)
- Add role-based access control (admin / staff)
- Order and payment system