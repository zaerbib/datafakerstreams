package com.reactive.streams.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactive.streams.data.utils.JsonPatchUtils;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonPatchUtilsTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testApplyJsonPatchOnProduct() {
        // Given
        Product originalProduct = new Product("Laptop", 1000.0, "Electronics");
        String patchJson = """
                    [
                        { "op": "replace",
                          "path": "/price",
                          "value": 1200.0 }
                    ]
                """;

        // When
        Product patchedProduct = JsonPatchUtils.applyJsonPatch(objectMapper, originalProduct, patchJson);

        // Then
        assertEquals("Laptop", patchedProduct.getName());
        assertEquals(1200.0, patchedProduct.getPrice());
        assertEquals("Electronics", patchedProduct.getCategory());
    }

    @Test
    void testApplyJsonMergePatchOnProduct() {
        // Given
        Product originalProduct = new Product("Laptop", 1000.0, "Electronics");
        String mergePatchJson = """
                    { "price": "1200.0" }
                """;


        // When
        Product patchedProduct = JsonPatchUtils.applyJsonMergePatch(objectMapper, originalProduct, mergePatchJson);

        // Then
        assertEquals("Laptop", patchedProduct.getName());
        assertEquals(1200.0, patchedProduct.getPrice());
        assertEquals("Electronics", patchedProduct.getCategory());
    }

    @Test
    void testApplyJsonPatchWithInvalidJson() {
        // Given
        Product originalProduct = new Product("Laptop", 1000.0, "Electronics");
        String invalidPatchJson = "invalid json";

        // When & Then
        assertThrows(RuntimeException.class, () ->
                JsonPatchUtils.applyJsonPatch(objectMapper, originalProduct, invalidPatchJson));
    }

    @Test
    void testApplyJsonMergePatchWithInvalidJson() {
        // Given
        Product originalProduct = new Product("Laptop", 1000.0, "Electronics");
        String invalidMergePatchJson = "invalid json";

        // When & Then
        assertThrows(RuntimeException.class, () ->
                JsonPatchUtils.applyJsonMergePatch(objectMapper, originalProduct, invalidMergePatchJson));
    }

    @Test
    void testApplyJsonPatchWithNullObject() {
        // Given
        String patchJson = """
                    [
                        { "op": "replace",
                          "path": "/price",
                          "value": 1200.0 }
                    ]
                """;

        // When & Then
        assertThrows(NullPointerException.class, () ->
                JsonPatchUtils.applyJsonPatch(objectMapper, null, patchJson));
    }

    @Test
    void testApplyJsonMergePatchWithNullObject() {
        // Given
        String mergePatchJson = """
                    { "price": 1200.0 }
                """;

        // When & Then
        assertThrows(NullPointerException.class, () ->
                JsonPatchUtils.applyJsonMergePatch(objectMapper, null, mergePatchJson));
    }

    @Test
    void testApplyJsonPatchWithNullPatch() {
        // Given
        Product originalProduct = new Product("Laptop", 1000.0, "Electronics");

        // When & Then
        assertThrows(NullPointerException.class, () ->
                JsonPatchUtils.applyJsonPatch(objectMapper, originalProduct, null));
    }

    @Test
    void testApplyJsonMergePatchWithNullPatch() {
        // Given
        Product originalProduct = new Product("Laptop", 1000.0, "Electronics");

        // When & Then
        assertThrows(NullPointerException.class, () ->
                JsonPatchUtils.applyJsonMergePatch(objectMapper, originalProduct, null));
    }

    @Getter
    private static class Product {
        private final String name;
        private final double price;
        private final String category;

        @JsonCreator
        public Product(@JsonProperty("name") String name,
                       @JsonProperty("price") double price,
                       @JsonProperty("category") String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }

    }
}
