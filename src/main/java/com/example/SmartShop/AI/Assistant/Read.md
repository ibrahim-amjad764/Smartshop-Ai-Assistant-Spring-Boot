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
