/*
 * Copyright (C) 2011 by Array Systems Computing Inc. http://www.array.ca
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.esa.nest.dataio.dem.aster;

import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.dataop.dem.AbstractElevationModelDescriptor;
import org.esa.beam.framework.dataop.dem.ElevationModel;
import org.esa.beam.framework.dataop.maptransf.Datum;
import org.esa.beam.framework.dataop.resamp.Resampling;
import org.esa.beam.util.SystemUtils;
import org.esa.nest.util.Settings;

import java.io.File;
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
    public static final GeoPos RASTER_ORIGIN = new GeoPos(83, 180);
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
    public int getRasterWidth() {
        return RASTER_WIDTH;
    }

    @Override
    public int getRasterHeight() {
        return RASTER_HEIGHT;
    }

    @Override
    public GeoPos getRasterOrigin() {
        return RASTER_ORIGIN;
    }

    @Override
    public int getDegreeRes() {
        return DEGREE_RES;
    }

    @Override
    public int getPixelRes() {
        return PIXEL_RES;
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
        } catch (Exception e) {
            return null;
        }
    }

    public ElevationModel createDem(Resampling resamplingMethod) {
        try {
            return new AsterElevationModel(this, resamplingMethod);
        } catch (Exception e) {
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