package xyz.tclx;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleaner;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveBackground {
    public static final String SRC = "/Users/chenli/Downloads/test/renDistributedEdgeSystem2022.pdf";
    public static final String DEST = "/Users/chenli/Downloads/test/renDistributedEdgeSystem20221.pdf";

    static final Color EYE_COLOR = new Color(0xdfedd3);

    public static void main(String[] args) throws IOException {
        removeLowestContent(SRC, DEST);
    }

    public static void removeLowestContent(String src, String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        for (int i = 0; i < pdfDoc.getNumberOfPages(); i++) {
            int pageNum = i + 1;
            PdfDictionary pageDict = pdfDoc.getPage(pageNum).getPdfObject();
            PdfArray contentArray = pageDict.getAsArray(PdfName.Contents);
            if (contentArray != null) {
                for (int j = 0; j < contentArray.size(); j++) {
                    PdfStream prStream = contentArray.getAsStream(j);
                    // 最下层
                    if (j == 0) {
                        prStream.clear();
                    }
                }
            }
        }

        pdfDoc.close();
    }

    private void removeContentInRectangle(String src, String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();

        float width = pdfDoc.getFirstPage().getPageSize().getWidth();
        float height = pdfDoc.getFirstPage().getPageSize().getHeight();
        float text_height = 12;
        for (int i = 0; i < pdfDoc.getNumberOfPages(); i++) {
            int pageNum = i + 1;
            PdfCleanUpLocation location = new PdfCleanUpLocation(pageNum,
                    new Rectangle(0, height - text_height, width, text_height),
                    new DeviceRgb(EYE_COLOR));
            cleanUpLocations.add(location);
        }

        PdfCleaner.cleanUp(pdfDoc, cleanUpLocations);

        pdfDoc.close();
    }
}