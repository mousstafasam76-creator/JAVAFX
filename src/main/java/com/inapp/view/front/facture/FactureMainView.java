 package com.inapp.view.front.facture;

import com.inapp.controller.front.FactureController;
import com.inapp.model.Facture;
import com.inapp.utils.NavigationManager;
import com.inapp.view.components.Sidebar;
import javafx.scene.layout.BorderPane;

public class FactureMainView {

    private FactureController controller;
    private BorderPane root;
    private FactureList listView;
    private FactureDetail detailView;
    private FactureForm formView;
    private NavigationManager navManager;
    private Sidebar sidebar;

    public FactureMainView(NavigationManager navManager) {
        this.navManager = navManager;
        controller = new FactureController();
        root = new BorderPane();
        sidebar = new Sidebar(navManager);

        listView = new FactureList(controller);
        detailView = new FactureDetail(controller);
        formView = new FactureForm(controller);

        // Par défaut : liste
        root.setCenter(listView.createView());
        root.setLeft(sidebar);

        // ========== NAVIGATION ==========

        // Liste → Détail
        listView.setNavigateToDetail(() -> root.setCenter(detailView.createView()));

        // Liste → Formulaire (création)
        listView.setNavigateToForm(() -> {
            formView.setFactureToEdit(null);
            formView.setOnSave(() -> {
                // Retour à la liste après création
                root.setCenter(listView.createView());
            });
            formView.setOnCancel(() -> root.setCenter(listView.createView()));
            root.setCenter(formView.createView());
        });

        // Détail → Liste
        detailView.setBackToList(() -> root.setCenter(listView.createView()));

        // Détail → Formulaire (édition)
        detailView.setNavigateToEdit(() -> {
            Facture facture = controller.getFilteredFactures().stream()
                    .filter(f -> f.getId() == FactureDetail.getFactureId()).findFirst().orElse(null);
            if (facture != null) {
                formView.setFactureToEdit(facture);
                formView.setOnSave(() -> {
                    // Retour au détail après modification
                    root.setCenter(detailView.createView());
                });
                formView.setOnCancel(() -> root.setCenter(detailView.createView()));
                root.setCenter(formView.createView());
            }
        });

        // Détail → Impression (déjà géré dans FacturePrint)
        detailView.setNavigateToPrint(() -> FacturePrint.show(FactureDetail.getFactureId()));
    }

    public BorderPane getView() {
        return root;
    }
}