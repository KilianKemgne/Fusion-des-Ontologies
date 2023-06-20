package org.fusion.ontologie.RCAEngine;

import lattice.gui.RelationalContextEditor;
import lattice.util.relation.RelationalContextFamily;

public class RelationalContextEditorK extends RelationalContextEditor {
    public RelationalContextEditorK(RelationalContextFamily rc) {
        super(rc);
    }

    public void exportRCF() {
        this.exportData();
    }
}
