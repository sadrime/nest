
package org.esa.nest.dataio.ceos.records;

import junit.framework.TestCase;
import org.esa.nest.dataio.ceos.CeosFileReader;
import org.esa.nest.dataio.ceos.CeosTestHelper;
import org.esa.nest.dataio.ceos.IllegalCeosFormatException;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageRecordTest extends TestCase {

    private MemoryCacheImageOutputStream _ios;
    private String _prefix;
    private CeosFileReader _reader;

    protected void setUp() throws Exception {
        final ByteArrayOutputStream os = new ByteArrayOutputStream(24);
        _ios = new MemoryCacheImageOutputStream(os);
        _prefix = "ImageRecordTest_prefix";
        _ios.writeBytes(_prefix);
        writeRecordData(_ios);
        _ios.writeBytes("ImageRecordTest_suffix"); // as suffix
        _reader = new CeosFileReader(_ios);
    }

    public void testInit_SimpleConstructor() throws IOException,
                                                    IllegalCeosFormatException {
        _reader.seek(_prefix.length());

        final ImageRecord record = new ImageRecord(_reader);

        assertRecord(record);
    }

    public void testInit() throws IOException,
                                  IllegalCeosFormatException {
        final ImageRecord record = new ImageRecord(_reader, _prefix.length());

        assertRecord(record);
    }

    private void writeRecordData(final ImageOutputStream ios) throws IOException {
        BaseRecordTest.writeRecordData(ios);

        ios.writeInt(1234); // prefixDataLineNumber // B4
        ios.writeInt(2345); // ccdUnitNumber // B4
        ios.writeInt(3456); // scanStartTimeMillisAtDay // B4
        ios.writeShort(4567); // scanStartTimeMicros // B2
        ios.writeInt(5678); // numLeftDummyPixels // B4
        ios.writeInt(6789); // numRightDummyPixels // B4
        CeosTestHelper.writeBlanks(ios, BaseRecordTest.RECORD_LENGTH - 34);
    }

    private void assertRecord(final ImageRecord record) throws IOException {
        BaseRecordTest.assertRecord(record);
        assertEquals(_prefix.length(), record.getStartPos());
        assertEquals(_prefix.length() + BaseRecordTest.RECORD_LENGTH, _ios.getStreamPosition());

        assertEquals(1234, record.getPrefixDataLineNumber());
        assertEquals(2345, record.getImageNumber());
        assertEquals(3456, record.getScanStartTimeMillisAtDay());
        assertEquals(4567, record.getScanStartTimeMicros());
        assertEquals(5678, record.getNumLeftDummyPixels());
        assertEquals(6789, record.getNumRightDummyPixels());
    }
}
