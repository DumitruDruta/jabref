package net.sf.jabref.logic.io;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MimeTypeDetectorTest {
    @Test
    public void beFalseForInvalidUrl() {
        String invalidUrl = "thisisnourl";
        assertFalse(MimeTypeDetector.isPdfContentType(invalidUrl));
    }

    @Test
    public void beFalseForUnreachableUrl() {
        String invalidUrl = "http://idontknowthisurlforsure.de";
        assertFalse(MimeTypeDetector.isPdfContentType(invalidUrl));
    }

    @Test
    public void beTrueForPdfMimeType() {
        String pdfUrl = "https://www.uni-bamberg.de/fileadmin/uni/fakultaeten/wiai_lehrstuehle/praktische_informatik/Dateien/Publikationen/cloud15-application-migration-effort-in-the-cloud.pdf";
        assertTrue(MimeTypeDetector.isPdfContentType(pdfUrl));
    }

    @Test
    public void acceptPDFMimeTypeVariations() {
        // application/pdf;charset=ISO-8859-1
        String pdfUrl = "http://drum.lib.umd.edu/bitstream/1903/19/2/CS-TR-3368.pdf";
        assertTrue(MimeTypeDetector.isPdfContentType(pdfUrl));
    }
}