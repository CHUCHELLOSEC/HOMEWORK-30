package org.skypro.skyshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.SearchResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StorageService storageService;   // мок хранилища

    @InjectMocks
    private SearchService searchService;     // тестируемый сервис

    // Вспомогательные методы для создания объектов
    private Product createProduct(String name) {
        return new SimpleProduct(UUID.randomUUID(), name, 100);
    }

    private Article createArticle(String title, String text) {
        return new Article(UUID.randomUUID(), title, text);
    }

    // 1. Хранилище пусто
    @Test
    void search_shouldReturnEmptyList_whenStorageIsEmpty() {
        when(storageService.getAllSearchable()).thenReturn(List.of());

        var result = searchService.search("что-то");

        assertTrue(result.isEmpty());
        verify(storageService, times(1)).getAllSearchable();
    }

    // 2. Объекты есть, но ни один не подходит
    @Test
    void search_shouldReturnEmptyList_whenNoMatch() {
        Product apple = createProduct("Яблоко");
        Article article = createArticle("Сыр", "Текст про сыр");
        when(storageService.getAllSearchable()).thenReturn(List.of(apple, article));

        var result = searchService.search("молоко");

        assertTrue(result.isEmpty());
        verify(storageService, times(1)).getAllSearchable();
    }

    // 3. Есть подходящие объекты
    @Test
    void search_shouldReturnMatches_whenFound() {
        Product apple = createProduct("Яблоко");
        Article article = createArticle("Полезные свойства яблок", "Яблоки богаты витаминами");
        when(storageService.getAllSearchable()).thenReturn(List.of(apple, article));

        var result = searchService.search("яблок");

        assertEquals(2, result.size());
        // Проверяем, что все найденные содержат "яблок" в имени
        assertTrue(result.stream().allMatch(r -> r.getName().contains("яблок")));
        verify(storageService, times(1)).getAllSearchable();
    }

    // Дополнительный: проверка, что возвращаются только совпадающие
    @Test
    void search_shouldReturnOnlyMatchingResults() {
        Product apple = createProduct("Яблоко");
        Product bread = createProduct("Хлеб");
        Article article = createArticle("Статья про яблоки", "Текст");
        when(storageService.getAllSearchable()).thenReturn(List.of(apple, bread, article));

        var result = searchService.search("яблок");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getName().contains("яблок")));
    }
}//test