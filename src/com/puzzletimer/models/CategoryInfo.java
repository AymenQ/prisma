package com.puzzletimer.models;

import java.util.UUID;

public class CategoryInfo {
    private UUID categoryId;
    private String scramblerId;
    private String description;

    public CategoryInfo(UUID categoryId, String scramblerId, String description) {
        this.categoryId = categoryId;
        this.scramblerId = scramblerId;
        this.description = description;
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
}
