# SmartShop AI Assistant

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-orange.svg)](https://jwt.io/)

> **Intelligent Shopping Assistant** - A comprehensive full-stack e-commerce platform with AI-powered product recommendations, price comparison, and smart search capabilities.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Screenshots](#screenshots)
- [Contributing](#contributing)

---

## Overview

SmartShop AI Assistant is a modern e-commerce platform that helps users find the best products at optimal prices across multiple online stores. It leverages AI for intelligent product recommendations and provides real-time price comparisons, favorite product tracking, and comprehensive store analytics.

**Key Highlights:**
- AI-powered product search and recommendations
- Multi-store price comparison
- User authentication with JWT
- Favorite products and cart management
- Real-time price history tracking
- Store ratings and reviews
- Cloudinary image management
- Responsive React frontend

---

## Features

### Core Features

| Feature | Description | Status |
|---------|-------------|--------|
|  **AI Smart Search** | AI-powered product search with budget optimization |  Implemented |
|  **Price Comparison** | Compare prices across multiple stores in real-time |  Implemented |
|  **Favorites** | Save and manage favorite products |  Implemented |
|  **Cart Management** | Add/remove items with quantity management |  Implemented |
|  **Price History** | Track price changes over time |  Implemented |
|  **Store Management** | Browse stores with ratings and offers |  Implemented |
|  **JWT Authentication** | Secure user authentication |  Implemented |
|  **AI Chat Assistant** | Get product recommendations via chat |  Implemented |

### AI Features

- **Smart Product Recommendations**: AI analyzes user queries and budget to suggest optimal products
- **Search Suggestions**: Autocomplete with trending searches
- **Budget-Aware Filtering**: Automatic filtering within user budget constraints
- **Natural Language Queries**: Search using conversational language

---

## Tech Stack

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.5.6 | Application framework |
| **Java** | 21 | Programming language |
| **Spring Security** | 6.5.5 | Authentication & authorization |
| **Spring Data JPA** | 3.5.6 | Data persistence |
| **PostgreSQL** | 15 | Primary database |
| **JWT** | 0.12.6 | Token-based authentication |
| **Cloudinary** | 2.x | Image storage & management |
| **OpenRouter** | Latest | AI/LLM integration |

### Frontend

| Technology | Version | Purpose |
|------------|---------|---------|
| **React** | 18.x | UI framework |
| **Vite** | 5.x | Build tool |
| **Tailwind CSS** | 3.x | Styling |
| **Axios** | 1.x | HTTP client |
| **React Router** | 6.x | Navigation |
| **Radix UI** | Latest | UI components |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   React     │  │  Tailwind   │  │    Axios HTTP       │ │
│  │  Frontend   │  │     CSS     │  │      Client         │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
│              (Spring Boot - Port 8080)                      │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Security Layer (JWT Filter)                │ │
│  └─────────────────────────────────────────────────────────┘ │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Auth      │  │  Product    │  │      Store          │  │
│  │ Controller  │  │ Controller  │  │    Controller       │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Cart      │  │  Search     │  │       AI            │  │
│  │ Controller  │  │ Controller  │  │    Controller       │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Auth      │  │  Product    │  │      Store          │  │
│  │  Service    │  │  Service    │  │    Service          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │    AI       │  │   Search    │  │    Price            │  │
│  │  Service    │  │  Service    │  │   History           │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Data Access Layer                          │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Spring Data JPA Repositories               │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                            │
│  ┌─────────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   PostgreSQL    │  │  Supabase    │  │  Cloudinary  │  │
│  │   (Primary)     │  │   (Hosting)  │  │   (Images)   │  │
│  └─────────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+
- PostgreSQL 14+ (or use Supabase)
- Node.js 18+ (for frontend)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/smartshop-ai-assistant.git
   cd smartshop-ai-assistant
   ```

2. **Configure environment variables**
   
   Create `src/main/resources/application.properties` with your credentials:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://your-db-host:5432/postgres?sslmode=require
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JWT Configuration
   jwt.secret-key=your-secret-key-here
   
   # Cloudinary Configuration
   cloudinary.cloud-name=your-cloud-name
   cloudinary.api-key=your-api-key
   cloudinary.api-secret=your-api-secret
   
   # OpenRouter AI API
   openrouter.api.key=your-openrouter-key
   ```

3. **Build and run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```cmd
   mvnw.cmd clean install
   mvnw.cmd spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html` (if enabled)

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend  # or your frontend directory
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   Create `.env.local`:
   ```env
   VITE_API_BASE_URL=http://localhost:8080/api
   ```

4. **Run development server**
   ```bash
   npm run dev
   ```

5. **Access the application**
   - URL: `http://localhost:5173` (or `http://localhost:3000`)

---

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/signup` | Register new user |
| `POST` | `/api/auth/login` | User login |
| `GET`  | `/api/auth/me` | Get current user |

### Product Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/products` | Get all products |
| `GET`  | `/api/products/{id}` | Get product by ID |
| `GET`  | `/api/products/search` | Search products |
| `GET`  | `/api/products/filter` | Filter products |
| `GET`  | `/api/products/{id}/offers` | Get product offers |
| `GET`  | `/api/products/{id}/price-history` | Get price history |
| `POST` | `/api/products/create` | Create new product |

### Store Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/stores` | Get all stores (paginated) |
| `GET`  | `/api/stores/{id}` | Get store details |
| `GET`  | `/api/stores/{id}/offers` | Get store offers |
| `GET`  | `/api/stores/{id}/rating` | Get store rating |

### Search Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/search` | Smart search with AI |
| `GET`  | `/api/search/suggestions` | Get search suggestions |
| `POST` | `/api/search/events` | Log search query |
| `GET`  | `/api/search/trending` | Get trending searches |

### AI Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/ai/smart-search` | AI-powered smart search |
| `GET`  | `/api/ai/suggest` | AI suggestions |
| `POST` | `/api/chat` | AI chat assistant |

### User Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/api/favorites` | Get user's favorites |
| `POST` | `/api/favorites/{productId}` | Add to favorites |
| `DELETE` | `/api/favorites/{productId}` | Remove from favorites |
| `GET`  | `/api/favorites/{productId}/check` | Check if favorited |
| `GET`  | `/api/cart` | Get cart items |
| `POST` | `/api/cart/add` | Add to cart |
| `DELETE` | `/api/cart/remove/{itemId}` | Remove from cart |

---

## Database Schema

### Core Tables

```
users (id, email, name, password, created_at, updated_at)
products (id, title, brand, model, image_url, category, price, rating, ...)
stores (id, name, api_url, img_url, created_at)
offers (id, product_id, store_id, price, url, available, fetched_at)
cart_item (id, user_id, product_id, quantity)
favorite (id, user_id, product_id, created_at)
price_history (id, offer_id, price, checked_at)
search_query (id, query_text, user_budget, searched_at)
store_rating (id, store_id, rating, reviews_count, review_text)
```

---

## Configuration

### Security Configuration

The application uses JWT-based authentication:
- JWT secret key configured in `application.properties`
- Token expiration: 24 hours
- Secure endpoints require valid JWT token in `Authorization: Bearer {token}` header

### CORS Configuration

CORS is configured to allow requests from:
- `http://localhost:3000` (React default)
- `http://localhost:5173` (Vite default)

### Database Configuration

Supports both:
- **Local PostgreSQL**: For development
- **Supabase**: For production/cloud hosting

---

## Project Structure

```
smartshop-ai-assistant/
mvn spring-boot:run

SmartShop-AI-Assistant (Spring Boot)
│
├── .idea
├── .mvn
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.SmartShop.AI.Assistant
│   │   │       ├── Config
│   │   │       │   ├── AppConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── CloudinaryConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       │
│   │   │       ├── Controller
│   │   │       │   ├── AIController.java
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── FavoritesController.java
│   │   │       │   ├── OfferController.java
│   │   │       │   ├── ProductController.java
│   │   │       │   ├── ProductImageController.java
│   │   │       │   ├── SearchController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── CloudinaryController.java
│   │   │       │   └── CartController.java
│   │   │       │
│   │   │       ├── Dto
│   │   │       │   ├── AIProductRecommendationDTO.java
│   │   │       │   ├── AuthResponseDTO.java
│   │   │       │   ├── ErrorResponseDTO.java
│   │   │       │   ├── ErrorResponse.java
│   │   │       │   ├── FavoutiteProductDTO.java
│   │   │       │   ├── LoginRequestDTO.java
│   │   │       │   ├── OfferDTO.java
│   │   │       │   ├── PriceHistoryDTO.java
│   │   │       │   ├── ProductDTO.java
│   │   │       │   ├── ProductFilterDTO.java
│   │   │       │   ├── QuantityRequestDTO.java
│   │   │       │   ├── SearchResponseDTO.java
│   │   │       │   ├── UserDTO.java
│   │   │       │   ├── SignupRequestDTO.java
│   │   │       │   ├── StoreDTO.java
│   │   │       │   └── CartItemDTO.java
│   │   │       │
│   │   │       ├── Entity
│   │   │       │   ├── ProductCategory.java
│   │   │       │   ├── Favorite.java
│   │   │       │   ├── Offer.java
│   │   │       │   ├── PriceHistory.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── Store.java
│   │   │       │   ├── User.java
│   │   │       │   └──CartItem.java
│   │   │       │
│   │   │       ├── Exception
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       │
│   │   │       ├── Jobs
│   │   │       │   └── PriceUpdateJob.java
│   │   │       │
│   │   │       ├── Repository
│   │   │       │   ├── FavoriteRepository.java
│   │   │       │   ├── OfferRepository.java
│   │   │       │   ├── ProductRepository.java
│   │   │       │   ├── StoreRepository.java
│   │   │       │   ├── PriceHistoryRepository.java
│   │   │       │   ├── UserRepository.java
│   │   │       │   └── CartRepository.java
│   │   │       │
│   │   │       ├── Security
│   │   │       │   ├── JwtAuthFilter.java
│   │   │       │   └── JwtService.java
│   │   │       │
│   │   │       ├── Service
│   │   │       │   ├── AIService.java
│   │   │       │   ├── AIServiceImpl.java
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── AuthServiceImpl.java
│   │   │       │   ├── CartService.java
│   │   │       │   ├── FavoriteService.java
│   │   │       │   ├── OfferService.java
│   │   │       │   ├── OfferServiceImpl.java
│   │   │       │   ├── ProductService.java
│   │   │       │   ├── ProductServiceImpl.java
│   │   │       │   ├── StoreClientService.java
│   │   │       │   └── StoreService.java
│   │   │       │
│   │   │       ├── Specification
│   │   │       │    └── ProductSpecification.java 
│   │   │       │
│   │   │       ├── Util
│   │   │       │   └── NormalizerUtil.java
│   │   │       │
│   │   │       └── SmartShopApplication.java
│   │   │
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       ├── application.properties
│   │       ├── application.yml
│   │       └── data.sql
│   │
│   └── test
│       └── java
│           └── com.example.SmartShop.AI.Assistant

```

---

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Contact

For questions or support, please contact:
- GitHub: [Ibrahim Amjad](https://github.com/ibrahim-amjad764)



---

<p align="center">Built with  using Spring Boot & React</p>
