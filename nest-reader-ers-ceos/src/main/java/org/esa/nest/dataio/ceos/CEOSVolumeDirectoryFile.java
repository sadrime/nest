package org.esa.nest.dataio.ceos;

import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.nest.dataio.BinaryFileReader;
import org.esa.nest.dataio.IllegalBinaryFormatException;
import org.esa.nest.dataio.ceos.records.BaseRecord;
import org.esa.nest.dataio.ceos.records.FilePointerRecord;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;


/**
 * This class represents a volume directory file of a product.
 *
 */
public class CEOSVolumeDirectoryFile {

    private BaseRecord _volumeDescriptorRecord;
    private FilePointerRecord[] _filePointerRecords;
    private BaseRecord _textRecord;

    private final static String volume_desc_recordDefinitionFile = "volume_descriptor.xml";
    private final static String text_recordDefinitionFile = "text_record.xml";

    public CEOSVolumeDirectoryFile(final File baseDir, CEOSConstants constants)
            throws IOException, IllegalBinaryFormatException {
        final File volumeFile = CeosHelper.getVolumeFile(baseDir);
        final BinaryFileReader binaryReader = new BinaryFileReader(new FileImageInputStream(volumeFile));
        _volumeDescriptorRecord = new BaseRecord(binaryReader, -1, constants.getMission(), volume_desc_recordDefinitionFile);
        _filePointerRecords = CeosHelper.readFilePointers(_volumeDescriptorRecord, constants.getMission());
        _textRecord = new BaseRecord(binaryReader, -1, constants.getMission(), text_recordDefinitionFile);

        binaryReader.close();
    }

    public BaseRecord getTextRecord() {
        return _textRecord;
    }

    public BaseRecord getVolumeDescriptorRecord() {
        return _volumeDescriptorRecord;
    }

    public String getProductName() {
        return CeosHelper.getProductName(_textRecord);
    }

    public String getProductType() {
        return CeosHelper.getProductType(_textRecord);
    }

    public void assignMetadataTo(final MetadataElement rootElem) {
        CeosHelper.addMetadata(rootElem, _volumeDescriptorRecord, "Volume Descriptor");
        CeosHelper.addMetadata(rootElem, _textRecord, "Text Record");

        int i = 1;
        for(FilePointerRecord fp : _filePointerRecords) {
            CeosHelper.addMetadata(rootElem, fp, "File Pointer Record " + i++);
        }
    }

}