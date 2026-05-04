//package com.example.SmartShop.AI.Assistant.Service;
//
//import com.example.SmartShop.AI.Assistant.Entity.Product;
//import com.example.SmartShop.AI.Assistant.Entity.Store;
//import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
//import com.example.SmartShop.AI.Assistant.Entity.Offer;
//import com.example.SmartShop.AI.Assistant.Repository.OfferRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@ExtendWith(MockitoExtension.class)
//class OfferServiceTest {
//
//  @Mock
//  private OfferRepository offerRepository;
//
//  @InjectMocks
//  private OfferServiceImpl offerService;
//
//  @Test
//  void testGetCheapestOffer() {
//    Product product = new Product();
//    product.setId(UUID.randomUUID());
//    product.setTitle("Test Product");
//
//    Store store = new Store();
//    store.setId(1L);
//    store.setName("Test Store");
//
//    Offer offer = new Offer();
//    offer.setId(1L);
//    offer.setPrice(1000.0);
//    offer.setAvailable(true);
//    offer.setProduct(product); // ✅ set product
//    offer.setStore(store); // ✅ set store
//
//    Mockito.when(offerRepository.findFirstByProduct_IdAndAvailableTrueOrderByPriceAsc(product.getId()))
//        .thenReturn(Optional.of(offer));
//
//    Optional<OfferDTO> result = offerService.getCheapestOffer(product.getId());
//
//    Assertions.assertTrue(result.isPresent());
//    Assertions.assertEquals(1000.0, result.get().price());
//  }
//
//  @Test
//  void testGetAllOffers() {
//    Product product = new Product();
//    product.setId(UUID.randomUUID());
//    product.setTitle("Test Product");
//
//    Store store = new Store();
//    store.setId(1L);
//    store.setName("Test Store");
//
//    Offer offer = new Offer();
//    offer.setId(1L);
//    offer.setPrice(500.0);
//    offer.setAvailable(true);
//    offer.setProduct(product); // ✅ set product
//    offer.setStore(store); // ✅ set store
//
//    Page<Offer> page = new PageImpl<>(List.of(offer));
//    Mockito.when(offerRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
//
//    Page<OfferDTO> result = offerService.getAllOffers(PageRequest.of(0, 10));
//    Assertions.assertEquals(1, result.getTotalElements());
//    Assertions.assertEquals(500.0, result.getContent().get(0).price());
//  }
//
//}
