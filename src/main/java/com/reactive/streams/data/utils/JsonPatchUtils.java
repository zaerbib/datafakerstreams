package com.reactive.streams.data.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gravity9.jsonpatch.JsonPatch;
import com.gravity9.jsonpatch.JsonPatchException;
import com.gravity9.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class JsonPatchUtils {

    @NotNull
    public static <T> T applyJsonPatch(@NotNull ObjectMapper objectMapper,
                                       @NotNull T originalObject,
                                       @NotNull String patchJson) {
        JavaType targetType = objectMapper.getTypeFactory().constructType(originalObject.getClass());
        return applyJsonPatch(objectMapper, originalObject, patchJson, targetType);
    }

    @NotNull
    public static <T> T applyJsonPatch(@NotNull ObjectMapper objectMapper,
                                       @NotNull T originalObject,
                                       @NotNull String patchJson,
                                       @NotNull JavaType targertType) {
        Objects.requireNonNull(objectMapper, "ObjectMapper must not be null");
        Objects.requireNonNull(originalObject, "Original Object must not be null");
        Objects.requireNonNull(patchJson, "JSON Patch must not be null");
        Objects.requireNonNull(targertType, "Target Type must not be null");

        try {
            JsonNode originalNode = objectMapper.convertValue(originalObject, JsonNode.class);
            JsonPatch jsonPatch = objectMapper.readValue(patchJson, JsonPatch.class);
            JsonNode patchedNode = jsonPatch.apply(originalNode);
            return objectMapper.treeToValue(patchedNode, targertType);
        } catch (JsonProcessingException | JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static <T> T applyJsonMergePatch(@NotNull ObjectMapper objectMapper,
                                            @NotNull T originalObject,
                                            @NotNull String mergePatchJson) {
        JavaType targetType = objectMapper.getTypeFactory().constructType(originalObject.getClass());
        return applyJsonMergePatch(objectMapper, originalObject, mergePatchJson, targetType);
    }

    @NotNull
    public static <T> T applyJsonMergePatch(@NotNull ObjectMapper objectMapper,
                                            @NotNull T originalObject,
                                            @NotNull String mergePatchJson,
                                            @NotNull JavaType targetType) {
        Objects.requireNonNull(objectMapper, "ObjectMapper must not be null");
        Objects.requireNonNull(originalObject, "Original object must not be null");
        Objects.requireNonNull(mergePatchJson, "JSON merge patch must not be null");
        Objects.requireNonNull(targetType, "Target type must not be null");
        try {
            JsonNode originalNode = objectMapper.convertValue(originalObject, JsonNode.class);
            JsonMergePatch jsonMergePatch = objectMapper.readValue(mergePatchJson, JsonMergePatch.class);
            JsonNode patchedNode = jsonMergePatch.apply(originalNode);
            return objectMapper.treeToValue(patchedNode, targetType);
        } catch (JsonProcessingException | JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }
}
