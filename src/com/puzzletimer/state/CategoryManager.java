package com.puzzletimer.state;

import java.util.ArrayList;

import com.puzzletimer.models.Category;

public class CategoryManager {
    private ArrayList<CategoryListener> listeners;
    private Category currentCategory;

    public CategoryManager(Category category) {
        this.listeners = new ArrayList<CategoryListener>();
        this.currentCategory = category;
    }

    public Category getCurrentCategory() {
        return this.currentCategory;
    }

    public void setCategory(Category category) {
        this.currentCategory = category;
        notifyListeners();
    }

    public void notifyListeners() {
        for (CategoryListener listener : this.listeners) {
            listener.categoryChanged(this.currentCategory);
        }
    }

    public void addCategoryListener(CategoryListener listener) {
        this.listeners.add(listener);
    }

    public void removeCategoryListener(CategoryListener listener) {
        this.listeners.remove(listener);
    }
}
