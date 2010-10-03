package com.puzzletimer.models;

import java.util.UUID;

public class Category {
    private UUID categoryId;
    public String scramblerId;
    public String description;
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

    public boolean isUserDefined() {
        return this.isUserDefined;
    }
}
