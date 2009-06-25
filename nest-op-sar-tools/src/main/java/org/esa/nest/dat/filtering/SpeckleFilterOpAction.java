package org.esa.nest.dat.filtering;

import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;
import org.esa.nest.dat.dialogs.NestSingleTargetProductDialog;

/**
 * Speckle Filter action.
 *
 */
public class SpeckleFilterOpAction extends AbstractVisatAction {

    private NestSingleTargetProductDialog dialog;

    @Override
    public void actionPerformed(CommandEvent event) {

        if (dialog == null) {
            dialog = new NestSingleTargetProductDialog("Speckle-Filter", getAppContext(), "Speckle Filter", getHelpId());
            dialog.setTargetProductNameSuffix("_Spk");
        }
        dialog.show();

    }
}