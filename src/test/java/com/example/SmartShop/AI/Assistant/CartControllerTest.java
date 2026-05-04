//package com.example.SmartShop.AI.Assistant;
//
//import com.example.SmartShop.AI.Assistant.Controller.CartController;
//import com.example.SmartShop.AI.Assistant.Controller.CartController.AddToCartRequest;
//import com.example.SmartShop.AI.Assistant.Dto.QuantityRequestDTO;
//import com.example.SmartShop.AI.Assistant.Service.CartService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//class CartControllerTest {
//
//    private CartController cartController;
//    private CartService cartService;
//    private Authentication authentication;
//
//    @BeforeEach
//    void setUp() {
//        cartService = mock(CartService.class);
//        cartController = new CartController(cartService);
//
//        authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("test@example.com");
//        when(authentication.isAuthenticated()).thenReturn(true);
//    }
//
//    // ======================================================
//    // UPDATE QUANTITY
//    // ======================================================
//    @Test
//    void shouldUpdateQuantity_WhenProductExists() {
//        doNothing().when(cartService).updateQuantity("test@example.com", 1L, 5);
//        QuantityRequestDTO quantityRequest = new QuantityRequestDTO(5);
//
//        ResponseEntity<Void> response = cartController.updateQuantity(authentication, 1L, quantityRequest);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(cartService).updateQuantity("test@example.com", 1L, 5);
//    }
//
//    // ======================================================
//    // ADD TO CART (Path + RequestParam)
//    // ======================================================
//    @Test
//    void shouldAddProductToCart_PathParam() {
//        doNothing().when(cartService).addToCart("test@example.com", 1L, 2);
//
//        ResponseEntity<Void> response = cartController.addToCart(authentication, 1L, 2);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        verify(cartService).addToCart("test@example.com", 1L, 2);
//    }
//
//    // ======================================================
//    // ADD TO CART (JSON Body)
//    // ======================================================
//    @Test
//    void shouldAddProductToCart_JSONBody() {
//        doNothing().when(cartService).addToCart("test@example.com", 1L, 3);
//        AddToCartRequest request = new AddToCartRequest(1L, 3);
//
//        ResponseEntity<Void> response = cartController.addToCart(authentication, request);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        verify(cartService).addToCart("test@example.com", 1L, 3);
//    }
//
//    // ======================================================
//    // REMOVE FROM CART (Success)
//    // ======================================================
//    @Test
//    void shouldReturnNoContent_WhenRemoveFromCartSuccess() {
//        when(cartService.removeFromCart("test@example.com", 1L)).thenReturn(true);
//
//        ResponseEntity<Void> response = cartController.removeFromCart(authentication, 1L);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(cartService).removeFromCart("test@example.com", 1L);
//    }
//
//    // ======================================================
//    // REMOVE FROM CART (Not Found)
//    // ======================================================
//    @Test
//    void shouldReturnNotFound_WhenProductNotInCart() {
//        when(cartService.removeFromCart("test@example.com", 1L)).thenReturn(false);
//
//        ResponseEntity<Void> response = cartController.removeFromCart(authentication, 1L);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(cartService).removeFromCart("test@example.com", 1L);
//    }
//}