package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Arrays;

import com.puzzletimer.models.Category;

public class CategoryManager {
    private ArrayList<CategoryListener> listeners;
    private ArrayList<Category> categories;
    private Category currentCategory;

    public CategoryManager(Category[] categories, Category currentCategory) {
        this.listeners = new ArrayList<CategoryListener>();
        this.categories = new ArrayList<Category>(Arrays.asList(categories));
        this.currentCategory = currentCategory;
    }

    public Category[] getCategories() {
        return this.categories.toArray(new Category[this.categories.size()]);
    }

    public void addCategory(Category category) {
        this.categories.add(category);

        for (CategoryListener listener : this.listeners) {
            listener.categoryAdded(category);
        }

        notifyListeners();
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);

        for (CategoryListener listener : this.listeners) {
            listener.categoryRemoved(category);
        }

        notifyListeners();
    }

    public void updateCategory(Category category) {
        for (CategoryListener listener : this.listeners) {
            listener.categoryUpdated(category);
        }

        notifyListeners();
    }

    public Category getCurrentCategory() {
        return this.currentCategory;
    }

    public void setCurrentCategory(Category category) {
        this.currentCategory = category;

        for (CategoryListener listener : this.listeners) {
            listener.currentCategoryChanged(this.currentCategory);
        }

        notifyListeners();
    }

    public void notifyListeners() {
        Category[] categories = getCategories();
        for (CategoryListener listener : this.listeners) {
            listener.categoriesUpdated(categories, this.currentCategory);
        }
    }

    public void addCategoryListener(CategoryListener listener) {
        this.listeners.add(listener);
    }

    public void removeCategoryListener(CategoryListener listener) {
        this.listeners.remove(listener);
    }
}
