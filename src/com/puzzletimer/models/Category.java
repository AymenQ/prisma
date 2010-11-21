package com.puzzletimer.models;

import java.util.UUID;

public class Category {
    private UUID categoryId;
    private String scramblerId;
    private String description;
    private boolean isUserDefined;

    public Category(UUID categoryId, String scramblerId, String description, boolean isUserDefined) {
        this.categoryId = categoryId;
        this.scramblerId = scramblerId;
        this.description = description;
        this.isUserDefined = isUserDefined;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String getScramblerId() {
        return this.scramblerId;
    }

    public Category setScramblerId(String scramblerId) {
        return new Category(
            this.categoryId,
            scramblerId,
            this.description,
            this.isUserDefined);
    }

    public String getDescription() {
        return this.description;
    }

    public Category setDescription(String description) {
        return new Category(
            this.categoryId,
            this.scramblerId,
            description,
            this.isUserDefined);
    }

    public boolean isUserDefined() {
        return this.isUserDefined;
    }
}
