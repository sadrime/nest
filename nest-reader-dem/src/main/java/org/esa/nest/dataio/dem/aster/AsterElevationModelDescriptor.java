
package org.esa.nest.dataio.dem.aster;

import org.esa.beam.framework.dataop.dem.AbstractElevationModelDescriptor;
import org.esa.beam.framework.dataop.dem.ElevationModel;
import org.esa.beam.framework.dataop.maptransf.Datum;
import org.esa.beam.framework.dataop.resamp.Resampling;
import org.esa.beam.util.SystemUtils;
import org.esa.nest.util.Settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AsterElevationModelDescriptor extends AbstractElevationModelDescriptor {

    public static final String NAME = "ASTER 1sec GDEM";
    public static final String DB_FILE_SUFFIX = ".TIF";
    public static final String ARCHIVE_URL_PATH = SystemUtils.BEAM_HOME_PAGE + "data/ACE.zip";
    public static final int NUM_X_TILES = 360;
    public static final int NUM_Y_TILES = 166;
    public static final int DEGREE_RES = 1;
    public static final int PIXEL_RES = 3600;
    public static final int NO_DATA_VALUE = -9999;
    public static final int RASTER_WIDTH = NUM_X_TILES * PIXEL_RES;
    public static final int RASTER_HEIGHT = NUM_Y_TILES * PIXEL_RES;
    public static final Datum DATUM = Datum.WGS_84;

    private File demInstallDir = null;

    public AsterElevationModelDescriptor() {
    }

    public String getName() {
        return NAME;
    }

    public Datum getDatum() {
        return DATUM;
    }

    public float getNoDataValue() {
        return NO_DATA_VALUE;
    }

    @Override
    public File getDemInstallDir() {
        if(demInstallDir == null) {
            final String path = Settings.instance().get("DEM/AsterDEMDataPath");
            demInstallDir = new File(path);
            if(!demInstallDir.exists())
                demInstallDir.mkdirs();
        }
        return demInstallDir;
    }

    public boolean isDemInstalled() {
        return true;
    }

    public URL getDemArchiveUrl() {
        try {
            return new URL(ARCHIVE_URL_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("MalformedURLException not expected: " + ARCHIVE_URL_PATH);
        }
    }

    @Deprecated
    public ElevationModel createDem() {
        try {
            return new AsterElevationModel(this, Resampling.BILINEAR_INTERPOLATION);
        } catch (IOException e) {
            return null;
        }
    }

    public ElevationModel createDem(Resampling resamplingMethod) {
        try {
            return new AsterElevationModel(this, resamplingMethod);
        } catch (IOException e) {
            return null;
        }
    }

    public static String createTileFilename(int minLat, int minLon) {
        String name = "ASTGTM_";
        name += minLon < 0 ? "S" : "N";
        String lonString = String.valueOf(Math.abs(minLon));
        while (lonString.length() < 2) {
            lonString = '0' + lonString;
        }
        name += lonString;
        name += minLat < 0 ? "W" : "E";
        String latString = String.valueOf(Math.abs(minLat));
        while (latString.length() < 3) {
            latString = '0' + latString;
        }
        name += latString;

        return name + ".zip";
    }

}