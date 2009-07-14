package org.esa.beam.framework.ui;

import com.bc.ceres.binding.ValueContainer;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerContext;
import com.bc.ceres.glayer.LayerType;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.Product;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Marco
 * Date: 17.06.2009
 */
public class WorldMapPaneDataModel {

    public static final String PROPERTY_LAYER = "layer";
    public static final String PROPERTY_SELECTED_PRODUCT = "selectedProduct";
    public static final String PROPERTY_PRODUCTS = "products";
    public static final String PROPERTY_ADDITIONAL_GEO_BOUNDARIES = "additionalGeoBoundaries";
    public static final String PROPERTY_AUTO_ZOOM_ENABLED = "autoZoomEnabled";

    private PropertyChangeSupport changeSupport;
    private static final LayerType layerType = LayerType.getLayerType("org.esa.beam.worldmap.BlueMarbleLayerType");
    private Layer worldMapLayer;
    private Product selectedProduct;
    private boolean autoZoomEnabled;
    private ArrayList<Product> productList;
    private ArrayList<GeoPos[]> additionalGeoBoundaryList;

    public WorldMapPaneDataModel() {
        productList = new ArrayList<Product>();
        additionalGeoBoundaryList = new ArrayList<GeoPos[]>();
        autoZoomEnabled = false;
    }

    public Layer getWorldMapLayer(LayerContext context) {
        if (worldMapLayer == null) {
            worldMapLayer = layerType.createLayer(context, new ValueContainer());
        }
        return worldMapLayer;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product product) {
        Product oldSelectedProduct = selectedProduct;
        if (oldSelectedProduct != product) {
            selectedProduct = product;
            firePropertyChange(PROPERTY_SELECTED_PRODUCT, oldSelectedProduct, selectedProduct);
        }
    }

    public Product[] getProducts() {
        return productList.toArray(new Product[productList.size()]);
    }

    public void setProducts(Product[] products) {
        final Product[] oldProducts = getProducts();
        productList.clear();
        if (products != null) {
            productList.addAll(Arrays.asList(products));
        }
        firePropertyChange(PROPERTY_PRODUCTS, oldProducts, getProducts());
    }

    public GeoPos[][] getAdditionalGeoBoundaries() {
        return additionalGeoBoundaryList.toArray(new GeoPos[additionalGeoBoundaryList.size()][]);
    }

    public void setAdditionalGeoBoundaries(GeoPos[][] geoBoundarys) {
        final GeoPos[][] oldGeoBoundarys = getAdditionalGeoBoundaries();
        additionalGeoBoundaryList.clear();
        if (geoBoundarys != null) {
            additionalGeoBoundaryList.addAll(Arrays.asList(geoBoundarys));
        }
        firePropertyChange(PROPERTY_ADDITIONAL_GEO_BOUNDARIES, oldGeoBoundarys, additionalGeoBoundaryList);
    }

    public void addModelChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removeModelChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    public void addProduct(Product product) {
        if (!productList.contains(product)) {
            final Product[] oldProducts = getProducts();
            if (productList.add(product)) {
                firePropertyChange(PROPERTY_PRODUCTS, oldProducts, getProducts());
            }
        }
    }

    public void removeProduct(Product product) {
        if (productList.contains(product)) {
            final Product[] oldProducts = getProducts();
            if (productList.remove(product)) {
                firePropertyChange(PROPERTY_PRODUCTS, oldProducts, getProducts());
            }
        }
    }

    public boolean isAutoZommEnabled() {
        return autoZoomEnabled;
    }

    public void setAutoZoomEnabled(boolean autoZoomEnabled) {
        final boolean oldAutoZommEnabled = isAutoZommEnabled();
        if (oldAutoZommEnabled != autoZoomEnabled) {
            this.autoZoomEnabled = autoZoomEnabled;
            firePropertyChange(PROPERTY_AUTO_ZOOM_ENABLED, oldAutoZommEnabled, autoZoomEnabled);
        }
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}