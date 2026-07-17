package org.skypro.skyshop.service;
//test//test
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.*;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StorageService {
    private final Map<UUID, Product> productMap;
    private final Map<UUID, Article> articleMap;

    public StorageService() {
        productMap = new HashMap<>();
        articleMap = new HashMap<>();
        initData();
    }

    private void initData() {
        // Продукты
        Product apple = new SimpleProduct(UUID.randomUUID(), "Яблоко", 50);
        Product bread = new SimpleProduct(UUID.randomUUID(), "Хлеб", 30);
        Product cheese = new DiscountedProduct(UUID.randomUUID(), "Сыр", 150, 20);
        Product butter = new DiscountedProduct(UUID.randomUUID(), "Масло", 120, 10);
        Product milk = new FixPriceProduct(UUID.randomUUID(), "Молоко");
        Product cookies = new FixPriceProduct(UUID.randomUUID(), "Печенье");

        productMap.put(apple.getId(), apple);
        productMap.put(bread.getId(), bread);
        productMap.put(cheese.getId(), cheese);
        productMap.put(butter.getId(), butter);
        productMap.put(milk.getId(), milk);
        productMap.put(cookies.getId(), cookies);

        // Статьи
        Article a1 = new Article(UUID.randomUUID(), "Полезные свойства яблок", "Яблоки богаты витаминами и клетчаткой.");
        Article a2 = new Article(UUID.randomUUID(), "Как выбрать сыр", "Сыр должен иметь приятный запах.");
        Article a3 = new Article(UUID.randomUUID(), "Молочные продукты", "Молоко полезно для костей.");

        articleMap.put(a1.getId(), a1);
        articleMap.put(a2.getId(), a2);
        articleMap.put(a3.getId(), a3);
    }

    public Collection<Product> getAllProducts() {
        return productMap.values();
    }

    public Collection<Article> getAllArticles() {
        return articleMap.values();
    }

    public Collection<Searchable> getAllSearchable() {
        List<Searchable> result = new ArrayList<>();
        result.addAll(productMap.values());
        result.addAll(articleMap.values());
        return result;
    }
}