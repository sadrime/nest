/*
 * $Id: MaskTableModel.java,v 1.1 2009-11-04 17:04:33 lveci Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package org.esa.beam.visat.toolviews.mask;

import org.esa.beam.framework.datamodel.Mask;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeEvent;
import org.esa.beam.framework.datamodel.ProductNodeGroup;
import org.esa.beam.framework.datamodel.ProductNodeListenerAdapter;
import org.esa.beam.framework.datamodel.RasterDataNode;

import javax.swing.table.AbstractTableModel;
import java.awt.Color;

class MaskTableModel extends AbstractTableModel {

    private static final int IDX_VISIBILITY = 0;
    private static final int IDX_NAME = 1;
    private static final int IDX_TYPE = 2;
    private static final int IDX_COLOR = 3;
    private static final int IDX_TRANSPARENCY = 4;
    private static final int IDX_DESCRIPTION = 5;

    /**
     * Mask management mode, no visibility control.
     */
    private static final int[] IDXS_MODE_MANAG_NO_BAND = {
            IDX_NAME,
            IDX_TYPE,
            IDX_COLOR,
            IDX_TRANSPARENCY,
            IDX_DESCRIPTION,
    };

    /**
     * Mask management mode, with visibility control.
     */
    private static final int[] IDXS_MODE_MANAG_BAND = {
            IDX_VISIBILITY,
            IDX_NAME,
            IDX_TYPE,
            IDX_COLOR,
            IDX_TRANSPARENCY,
            IDX_DESCRIPTION,
    };

    /**
     * List only, no mask type management, no visibility control.
     */
    private static final int[] IDXS_MODE_NO_MANAG_NO_BAND = {
            IDX_NAME,
            IDX_COLOR,
            IDX_TRANSPARENCY,
            IDX_DESCRIPTION,
    };

    /**
     * List only, no mask type management, with visibility control.
     */
    private static final int[] IDXS_MODE_NO_MANAG_BAND = {
            IDX_VISIBILITY,
            IDX_NAME,
            IDX_COLOR,
            IDX_TRANSPARENCY,
            IDX_DESCRIPTION,
    };

    private static final Class[] COLUMN_CLASSES = {
            Boolean.class,
            String.class,
            String.class,
            Color.class,
            Double.class,
            String.class,
    };

    private static final String[] COLUMN_NAMES = {
            "Visibility",
            "Name",
            "Type",
            "Colour",
            "Transparency",
            "Description",
    };

    private static final boolean[] COLUMN_EDITABLE_STATES = {
            true,
            true,
            false,
            true,
            true,
            true,
    };

    static int[] COLUMN_WIDTHS = {
            24,
            60,
            60,
            60,
            40,
            320,
    };


    private final boolean inManagmentMode;
    private int[] modeIdxs;
    private Product product;
    private RasterDataNode visibleBand;
    private final MaskPNL maskPNL;

    MaskTableModel(boolean inManagmentMode) {
        this.inManagmentMode = inManagmentMode;
        updateModeIdxs();
        maskPNL = new MaskPNL();
    }

    Product getProduct() {
        return product;
    }

    void setProduct(Product product) {
        setProduct(product, null);
    }

    void setProduct(Product product, RasterDataNode visibleBand) {
        if (this.product != product) {
            if (this.product != null) {
                this.product.removeProductNodeListener(maskPNL);
            }
            this.product = product;
            if (this.product != null) {
                this.product.addProductNodeListener(maskPNL);
            }
        }
        this.visibleBand = visibleBand;
        updateModeIdxs();
        fireTableStructureChanged();
    }

    RasterDataNode getVisibleBand() {
        return visibleBand;
    }

    Mask getMask(int selectedRow) {
        return getMaskGroup().get(selectedRow);
    }

    int getMaskIndex(String name) {
        return getMaskGroup().indexOf(name);
    }

    void addMask(Mask mask) {
        getProduct().getMaskGroup().add(mask);
        fireTableDataChanged();
    }

    void removeMask(Mask mask) {
        getProduct().getMaskGroup().remove(mask);
        fireTableDataChanged();
    }

    private ProductNodeGroup<Mask> getMaskGroup() {
        return product != null ? product.getMaskGroup() : null;
    }

    boolean isInManagmentMode() {
        return product != null && inManagmentMode;
    }

    int getVisibilityColumnIndex() {
        for (int i = 0; i < modeIdxs.length; i++) {
            if (modeIdxs[i] == IDX_VISIBILITY) {
                return i;
            }
        }
        return -1;
    }

    int getPreferredColumnWidth(int columnIndex) {
        return COLUMN_WIDTHS[modeIdxs[columnIndex]];
    }

    private void updateModeIdxs() {
        this.modeIdxs =
                inManagmentMode
                        ? (this.visibleBand != null ? IDXS_MODE_MANAG_BAND : IDXS_MODE_MANAG_NO_BAND)
                        : (this.visibleBand != null ? IDXS_MODE_NO_MANAG_BAND : IDXS_MODE_NO_MANAG_NO_BAND);
    }

    void clear() {
        setProduct(null, null);
    }

    /////////////////////////////////////////////////////////////////////////
    // TableModel Implementation

    @Override
    public Class getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[modeIdxs[columnIndex]];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[modeIdxs[columnIndex]];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return COLUMN_EDITABLE_STATES[modeIdxs[columnIndex]];
    }

    @Override
    public int getColumnCount() {
        return modeIdxs.length;
    }

    @Override
    public int getRowCount() {
        ProductNodeGroup<Mask> maskGroup = getMaskGroup();
        return maskGroup != null ? maskGroup.getNodeCount() : 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Mask mask = getMaskGroup().get(rowIndex);
        int column = modeIdxs[columnIndex];

        if (column == IDX_VISIBILITY) {
            if (visibleBand.getOverlayMaskGroup().contains(mask)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (column == IDX_NAME) {
            return mask.getName();
        } else if (column == IDX_TYPE) {
            return mask.getImageType().getName();
        } else if (column == IDX_COLOR) {
            return mask.getImageColor();
        } else if (column == IDX_TRANSPARENCY) {
            return mask.getImageTransparency();
        } else if (column == IDX_DESCRIPTION) {
            return mask.getDescription();
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        Mask mask = getMaskGroup().get(rowIndex);
        int column = modeIdxs[columnIndex];

        if (column == IDX_VISIBILITY) {
            boolean visible = (Boolean) aValue;
            if (visible) {
                visibleBand.getOverlayMaskGroup().add(mask);
            } else {
                visibleBand.getOverlayMaskGroup().remove(mask);
            }
            visibleBand.fireImageInfoChanged();
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (column == IDX_NAME) {
            mask.setName((String) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (column == IDX_TYPE) {
            // type is not editable!
        } else if (column == IDX_COLOR) {
            mask.setImageColor((Color) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (column == IDX_TRANSPARENCY) {
            mask.setImageTransparency((Double) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if (column == IDX_DESCRIPTION) {
            mask.setDescription((String) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }

    }

    private class MaskPNL extends ProductNodeListenerAdapter {

        @Override
        public void nodeAdded(ProductNodeEvent event) {
            processEvent(event);
        }

        @Override
        public void nodeRemoved(ProductNodeEvent event) {
            processEvent(event);
        }

        @Override
        public void nodeChanged(ProductNodeEvent event) {
            processEvent(event);
        }

        private void processEvent(ProductNodeEvent event) {
            if (event.getSourceNode() instanceof Mask) {
                fireTableDataChanged();
            }
            if (event.getSourceNode() == visibleBand
                    && event.getPropertyName().equals(RasterDataNode.PROPERTY_NAME_IMAGE_INFO)) {
                fireTableDataChanged();
            }
        }

    }

}