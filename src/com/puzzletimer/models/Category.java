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

    public String getDescription() {
        return this.description;
    }

    public boolean isUserDefined() {
        return this.isUserDefined;
    }
}
