package com.puzzletimer.models;

import java.util.UUID;

public class Category {
    private UUID categoryId;
    private String scramblerId;
    private String description;
    private boolean isUserDefined;
    private String[] tipIds;

    public Category(UUID categoryId, String scramblerId, String description, boolean isUserDefined, String[] tipIds) {
        this.categoryId = categoryId;
        this.scramblerId = scramblerId;
        this.description = description;
        this.isUserDefined = isUserDefined;
        this.tipIds = tipIds;
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
            this.isUserDefined,
            this.tipIds);
    }

    public String getDescription() {
        return this.description;
    }

    public Category setDescription(String description) {
        return new Category(
            this.categoryId,
            this.scramblerId,
            description,
            this.isUserDefined,
            this.tipIds);
    }

    public boolean isUserDefined() {
        return this.isUserDefined;
    }

    public String[] getTipIds() {
        return this.tipIds;
    }

    public Category setTipIds(String[] tipIds) {
        return new Category(
            this.categoryId,
            this.scramblerId,
            this.description,
            this.isUserDefined,
            tipIds);
    }
}
