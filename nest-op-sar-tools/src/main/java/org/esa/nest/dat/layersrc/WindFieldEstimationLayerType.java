package org.esa.nest.dat.layersrc;

import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerContext;
import com.bc.ceres.glayer.LayerType;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.accessors.DefaultPropertyAccessor;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;

/**
 * The type descriptor of the {@link org.esa.nest.dat.layersrc.WindFieldEstimationLayer}.
 *
 */
public class WindFieldEstimationLayerType extends LayerType {

    static LayerType instance = new WindFieldEstimationLayerType();

    public static WindFieldEstimationLayer createLayer(final Product product, final Band band) {
        // todo - weird!!!
        final PropertyContainer template = instance.createLayerConfig(null);
        template.setValue("product", product);
        template.setValue("band", band);
        return new WindFieldEstimationLayer(template);
    }

    @Override
    public String getName() {
        return "Wind Field Estimation";
    }

    @Override
    public boolean isValidFor(LayerContext ctx) {
        // todo - need to check for availability of windu, windv  (nf)
        return true;
    }

    @Override
    public Layer createLayer(LayerContext ctx, PropertyContainer configuration) {
        return new WindFieldEstimationLayer(configuration);
    }

    // todo - rename getDefaultConfiguration  ? (nf)
    @Override
    public PropertyContainer createLayerConfig(LayerContext ctx) {
        final PropertyContainer valueContainer = new PropertyContainer();
        // todo - how do I know whether my value model type can be serialized or not? (nf)
        valueContainer.addProperty(new Property(new PropertyDescriptor("product", Product.class), new DefaultPropertyAccessor()));
        valueContainer.addProperty(new Property(new PropertyDescriptor("band", Band.class), new DefaultPropertyAccessor()));
        return valueContainer;
    }
}