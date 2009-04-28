/*
 * $Id: ApplicationData.java,v 1.1 2009-04-28 14:37:14 lveci Exp $
 *
 * Copyright (C) 2008 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.framework.gpf.internal;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.XppDomReader;
import com.thoughtworks.xstream.io.xml.XppDomWriter;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;

/**
 * Represents the application data for graphs. 
 *
 * @author marcoz
 */
public class ApplicationData {

    private final String appId;
    private final Xpp3Dom data;

    /** Constructs an Application Data object from the given Id and data objects.
     * 
     * @param string
     * @param xpp3Dom
     */
    public ApplicationData(String string, Xpp3Dom xpp3Dom) {
        appId = string;
        data = xpp3Dom;
    }
    
    /**
     * Returns the Application ID.
     * 
     * @return the appId
     */
    public String getId() {
        return appId;
    }
    
    /**
     * Returns the Application data as Xpp3Dom
     * 
     * @return the data
     */
    public Xpp3Dom getData() {
        return data;
    }
 
    public static class AppConverter implements Converter {

        private static final String ID_ATTRIBUTE_NAME = "id";

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer,
                MarshallingContext context) {
            
            ApplicationData applicationData = (ApplicationData) source;
            writer.addAttribute(ID_ATTRIBUTE_NAME, applicationData.appId);
            Xpp3Dom[] children = applicationData.data.getChildren();
            for (Xpp3Dom child : children) {
                HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
                XppDomReader reader = new XppDomReader(child);
                copier.copy(reader, writer);
            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
            XppDomWriter xppDomWriter = new XppDomWriter();
            String appId = reader.getAttribute(ID_ATTRIBUTE_NAME);
            copier.copy(reader, xppDomWriter);
            Xpp3Dom xpp3Dom = xppDomWriter.getConfiguration();
            return new ApplicationData(appId, xpp3Dom);
        }

        @Override
        public boolean canConvert(Class type) {
            return ApplicationData.class.equals(type);
        }
    }
}