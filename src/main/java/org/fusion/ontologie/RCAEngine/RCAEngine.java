package org.fusion.ontologie.RCAEngine;

import lattice.gui.controller.RCAController;
import lattice.gui.dialog.ExportDialogSelection;
import lattice.util.relation.RelationalContextFamily;

public class RCAEngine {

    public RelationalContextFamily rcf;

    public RCAEngine(RelationalContextFamily rcf) {
        this.rcf = rcf;
    }

    public void computeFTR() {

        // Utilisation de l'éditeur Galicia pour faciliter la génération des treillis
        RelationalContextEditorK rce = new RelationalContextEditorK(rcf);

        System.out.println("test");
        // Exportation des contextes binaires
        ExportDialogSelection eds = new ExportDialogSelection(rcf, rce);
        rce.exportRCF();

        // Génération de treillis
        RCAController rcaController = new RCAController(rce);
        rcaController.MultiFCAGeneral();

//        // Visualisation de la lattice
//        for (int i = 0; i < rcf.getNumberOfRelation(); i++) {
//            CompleteConceptLattice lattice = rcf.getRelation(i).getLattice();
//            System.out.println();
//            lattice.findNode(0);
//        }
    }
}
