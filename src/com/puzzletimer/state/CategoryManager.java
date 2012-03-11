package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.Arrays;

import com.puzzletimer.models.Category;

public class CategoryManager {
    public static class Listener {
        public void categoryAdded(Category category) { }
        public void categoryRemoved(Category category) { }
        public void categoryUpdated(Category category) { }
        public void currentCategoryChanged(Category category) { }
        public void categoriesUpdated(Category[] categories, Category currentCategory) { }
    }

    private ArrayList<Listener> listeners;
    private ArrayList<Category> categories;
    private Category currentCategory;

    public CategoryManager(Category[] categories, Category currentCategory) {
        this.listeners = new ArrayList<Listener>();
        this.categories = new ArrayList<Category>(Arrays.asList(categories));
        this.currentCategory = currentCategory;
    }

    public Category[] getCategories() {
        return this.categories.toArray(new Category[this.categories.size()]);
    }

    public void addCategory(Category category) {
        this.categories.add(category);

        for (Listener listener : this.listeners) {
            listener.categoryAdded(category);
        }

        notifyListeners();
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);

        for (Listener listener : this.listeners) {
            listener.categoryRemoved(category);
        }

        notifyListeners();
    }

    public void updateCategory(Category category) {
        for (int i = 0; i < this.categories.size(); i++) {
            if (this.categories.get(i).getCategoryId().equals(category.getCategoryId())) {
                this.categories.set(i, category);
                break;
            }
        }

        for (Listener listener : this.listeners) {
            listener.categoryUpdated(category);
        }

        notifyListeners();
    }

    public Category getCurrentCategory() {
        return this.currentCategory;
    }

    public void setCurrentCategory(Category category) {
        this.currentCategory = category;

        for (Listener listener : this.listeners) {
            listener.currentCategoryChanged(this.currentCategory);
        }

        notifyListeners();
    }

    public void notifyListeners() {
        Category[] categories = getCategories();
        for (Listener listener : this.listeners) {
            listener.categoriesUpdated(categories, this.currentCategory);
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}
