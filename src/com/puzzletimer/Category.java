package com.puzzletimer;

import java.util.UUID;

import com.puzzletimer.models.CategoryInfo;

public class Category extends CategoryInfo {
    private char mnemonic;
    private char accelerator;
    private boolean isDefault;

    public Category(UUID categoryId, String scramblerId, String description, char mnemonic, char accelerator, boolean isDefault) {
        super(categoryId, scramblerId, description);
        this.mnemonic = mnemonic;
        this.accelerator = accelerator;
        this.isDefault = isDefault;
    }

    public char getMnemonic() {
        return this.mnemonic;
    }

    public char getAccelerator() {
        return this.accelerator;
    }

    public boolean isDefault() {
        return this.isDefault;
    }
}
