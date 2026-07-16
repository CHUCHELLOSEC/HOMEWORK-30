package org.skypro.skyshop.service;
//test
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private ProductBasket productBasket;   // мок корзины (сессионный компонент)

    @Mock
    private StorageService storageService; // мок хранилища

    @InjectMocks
    private BasketService basketService;   // тестируемый сервис

    private Product createProduct(UUID id, String name, int price) {
        return new SimpleProduct(id, name, price);
    }

    // 1. Добавление несуществующего товара → исключение
    @Test
    void addToBasket_shouldThrowException_whenProductNotFound() {
        UUID id = UUID.randomUUID();
        when(storageService.getProductById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchProductException.class, () -> basketService.addToBasket(id));

        verify(storageService, times(1)).getProductById(id);
        verify(productBasket, never()).addProduct(any());
    }

    // 2. Добавление существующего → вызов addProduct
    @Test
    void addToBasket_shouldCallBasketAddProduct_whenProductExists() {
        UUID id = UUID.randomUUID();
        Product product = createProduct(id, "Яблоко", 50);
        when(storageService.getProductById(id)).thenReturn(Optional.of(product));

        basketService.addToBasket(id);

        verify(storageService, times(1)).getProductById(id);
        verify(productBasket, times(1)).addProduct(id);
    }

    // 3. getUserBasket при пустой корзине
    @Test
    void getUserBasket_shouldReturnEmptyBasket_whenBasketIsEmpty() {
        when(productBasket.getItems()).thenReturn(Map.of());

        UserBasket result = basketService.getUserBasket();

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotal());
        verify(productBasket, times(1)).getItems();
        // storageService не вызывается, так как нет товаров
        verify(storageService, never()).getProductById(any());
    }

    // 4. getUserBasket с товарами
    @Test
    void getUserBasket_shouldReturnBasketWithItems_whenBasketHasProducts() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Product product1 = createProduct(id1, "Яблоко", 50);
        Product product2 = createProduct(id2, "Хлеб", 30);

        when(productBasket.getItems()).thenReturn(Map.of(id1, 2, id2, 1));
        when(storageService.getProductById(id1)).thenReturn(Optional.of(product1));
        when(storageService.getProductById(id2)).thenReturn(Optional.of(product2));

        UserBasket result = basketService.getUserBasket();

        assertEquals(2, result.getItems().size());
        // Проверяем сумму: 50*2 + 30*1 = 130
        assertEquals(130, result.getTotal());

        verify(storageService, times(1)).getProductById(id1);
        verify(storageService, times(1)).getProductById(id2);
        verify(productBasket, times(1)).getItems();
    }

    // Дополнительно: проверка, что при отсутствии продукта в хранилище выбрасывается IllegalStateException
    @Test
    void getUserBasket_shouldThrowException_whenProductMissingInStorage() {
        UUID id = UUID.randomUUID();
        when(productBasket.getItems()).thenReturn(Map.of(id, 1));
        when(storageService.getProductById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> basketService.getUserBasket());
        verify(storageService, times(1)).getProductById(id);
    }
}