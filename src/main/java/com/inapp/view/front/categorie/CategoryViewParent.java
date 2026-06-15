package com.inapp.view.front.categorie;

import com.inapp.model.Category;

public interface CategoryViewParent {
    void showList();
    void showAdd();
    void showEdit(Category category);
    void showDetails(Category category);
    void showPending();
}
