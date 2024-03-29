package org.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import org.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import org.food.ordering.system.domain.valueobject.Money;
import org.food.ordering.system.domain.valueobject.ProductId;
import org.food.ordering.system.domain.valueobject.RestaurantId;
import org.food.ordering.system.order.service.domain.entity.Product;
import org.food.ordering.system.order.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {
    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue()).toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity =
                restaurantEntities.stream().findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("No restaurant found"));

        List<Product> restaurantProduct = restaurantEntities.stream().map(entity ->
                new Product(
                        new ProductId(entity.getProductId()),
                        entity.getProductName(),
                        new Money(entity.getProductPrice()))
                ).toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(restaurantProduct)
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }
}
