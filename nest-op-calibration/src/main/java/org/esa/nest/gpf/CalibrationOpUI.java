package org.esa.nest.gpf;

import org.esa.beam.framework.dataop.dem.ElevationModelDescriptor;
import org.esa.beam.framework.dataop.dem.ElevationModelRegistry;
import org.esa.beam.framework.dataop.maptransf.MapProjection;
import org.esa.beam.framework.dataop.maptransf.MapProjectionRegistry;
import org.esa.beam.framework.dataop.resamp.ResamplingFactory;
import org.esa.beam.framework.gpf.ui.BaseOperatorUI;
import org.esa.beam.framework.gpf.ui.UIValidation;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.visat.VisatApp;
import org.esa.nest.util.DialogUtils;
import org.esa.nest.datamodel.AbstractMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * User interface for RangeDopplerGeocodingOp
 */
public class CalibrationOpUI extends BaseOperatorUI {

    private final JList bandList = new JList();
    private final JComboBox auxFile = new JComboBox(new String[] {CalibrationOp.LATEST_AUX,
                                                                  CalibrationOp.PRODUCT_AUX,
                                                                  CalibrationOp.EXTERNAL_AUX});

    final JLabel externalAuxFileLabel = new JLabel("External Auxiliary File:");
    final JTextField externalAuxFile = new JTextField("");
    final JButton externalAuxFileBrowseButton = new JButton("...");

    final JCheckBox saveInDbCheckBox = new JCheckBox("Save in dB");
    final JCheckBox createGamma0VirtualBandCheckBox = new JCheckBox("Create gamma0 virtual band");
    final JCheckBox createBeta0VirtualBandCheckBox = new JCheckBox("Create beta0 virtual band");

    private boolean saveInDb = false;
    private boolean createGamma0VirtualBand = false;
    private boolean createBeta0VirtualBand = false;

    @Override
    public JComponent CreateOpTab(String operatorName, Map<String, Object> parameterMap, AppContext appContext) {

        initializeOperatorUI(operatorName, parameterMap);
        final JComponent panel = createPanel();
        initParameters();

        auxFile.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                final String item = (String)auxFile.getSelectedItem();
                if(item.equals(CalibrationOp.EXTERNAL_AUX)) {
                    enableExternalAuxFile(true);
                } else {
                    externalAuxFile.setText("");
                    enableExternalAuxFile(false);
                }
            }
        });
        externalAuxFile.setColumns(30);
        auxFile.setSelectedItem(parameterMap.get("auxFile"));
        enableExternalAuxFile(false);

        externalAuxFileBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final File file = VisatApp.getApp().showFileOpenDialog("External Auxiliary File", false, null);
                externalAuxFile.setText(file.getAbsolutePath());
            }
        });

        saveInDbCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    saveInDb = (e.getStateChange() == ItemEvent.SELECTED);
                }
        });
        createGamma0VirtualBandCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    createGamma0VirtualBand = (e.getStateChange() == ItemEvent.SELECTED);
                }
        });
        createBeta0VirtualBandCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    createBeta0VirtualBand = (e.getStateChange() == ItemEvent.SELECTED);
                }
        });

        return new JScrollPane(panel);
    }

    @Override
    public void initParameters() {
        OperatorUIUtils.initBandList(bandList, getBandNames());

        if(sourceProducts != null) {
            final MetadataElement absRoot = AbstractMetadata.getAbstractedMetadata(sourceProducts[0]);
            if (absRoot != null) {
                final String sampleType = absRoot.getAttributeString(AbstractMetadata.SAMPLE_TYPE);
                if (sampleType.equals("COMPLEX")) {
                    auxFile.removeItem(CalibrationOp.PRODUCT_AUX);
                } else if (auxFile.getItemCount() == 2) {
                    auxFile.addItem(CalibrationOp.PRODUCT_AUX);
                }
            }
        }
                
        auxFile.setSelectedItem(paramMap.get("auxFile"));

        final File extFile = (File)paramMap.get("externalAuxFile");
        if(extFile != null) {
            externalAuxFile.setText(extFile.getAbsolutePath());
        }

        saveInDb = (Boolean)paramMap.get("outputImageScaleInDb");
        saveInDbCheckBox.getModel().setPressed(saveInDb);

        createGamma0VirtualBand = (Boolean)paramMap.get("createGammaBand");
        createGamma0VirtualBandCheckBox.getModel().setPressed(createGamma0VirtualBand);

        createBeta0VirtualBand = (Boolean)paramMap.get("createBetaBand");
        createBeta0VirtualBandCheckBox.getModel().setPressed(createBeta0VirtualBand);
    }

    @Override
    public UIValidation validateParameters() {
        return new UIValidation(UIValidation.State.OK, "");
    }

    @Override
    public void updateParameters() {

        OperatorUIUtils.updateBandList(bandList, paramMap);

        paramMap.put("auxFile", auxFile.getSelectedItem());

        final String extFileStr = externalAuxFile.getText();
        if(!extFileStr.isEmpty()) {
            paramMap.put("externalAuxFile", new File(extFileStr));
        }

        paramMap.put("outputImageScaleInDb", saveInDb);
        paramMap.put("createGammaBand", createGamma0VirtualBand);
        paramMap.put("createBetaBand", createBeta0VirtualBand);
    }

    JComponent createPanel() {

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = DialogUtils.createGridBagConstraints();

        contentPane.add(new JLabel("Source Bands:"), gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        contentPane.add(new JScrollPane(bandList), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy++;
        DialogUtils.addComponent(contentPane, gbc, "Auxiliary File:", auxFile);
        gbc.gridy++;
        DialogUtils.addComponent(contentPane, gbc, externalAuxFileLabel, externalAuxFile);
        gbc.gridx = 2;
        contentPane.add(externalAuxFileBrowseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        contentPane.add(saveInDbCheckBox, gbc);
        gbc.gridy++;
        contentPane.add(createGamma0VirtualBandCheckBox, gbc);
        gbc.gridy++;
        contentPane.add(createBeta0VirtualBandCheckBox, gbc);

        //DialogUtils.fillPanel(contentPane, gbc);

        return contentPane;
    }

    private void enableExternalAuxFile(boolean flag) {
        DialogUtils.enableComponents(externalAuxFileLabel, externalAuxFile, flag);
        externalAuxFileBrowseButton.setVisible(flag);
    }
}