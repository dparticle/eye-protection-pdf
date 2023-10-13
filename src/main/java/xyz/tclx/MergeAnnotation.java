package xyz.tclx;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import java.io.IOException;
import java.util.List;

public class MergeAnnotation {
    public static final String MERGE_FROM = "/Users/chenli/Documents/Workspace/research/review/Paper-TW-Jul-23-1255_Proof_EnhancingImmersionBCI.pdf";
    public static final String MERGE_TO = "/Users/chenli/Documents/Workspace/research/review/ineye/Paper-TW-Jul-23-1255_Proof_EnhancingImmersionBCI.pdf";

    public static void main(String[] args) throws IOException {
        merge(MERGE_FROM, MERGE_TO);
    }

    public static void merge(String from, String to) throws IOException {
        PdfDocument fromDocument = new PdfDocument(new PdfReader(from));
        PdfDocument toDocument = new PdfDocument(new PdfReader(to));

        String writeFilename = to.substring(0, to.lastIndexOf('.')) + "_MergedVersion.pdf";
        PdfDocument writeDocument = new PdfDocument(new PdfWriter(writeFilename));

        int pageCount = fromDocument.getNumberOfPages();
        for (int i = 1; i <= pageCount; i++) {
            PdfPage page = toDocument.getPage(i);
            writeDocument.addPage(page.copyTo(writeDocument));
            PdfPage pdfPage = fromDocument.getPage(i);
            List<PdfAnnotation> pageAnnots = pdfPage.getAnnotations();
            if (pageAnnots != null) {
                for (PdfAnnotation pdfAnnotation : pageAnnots) {
                    PdfObject annotObject = pdfAnnotation.getPdfObject().copyTo(writeDocument);
                    writeDocument.getPage(i).addAnnotation(PdfAnnotation.makeAnnotation(annotObject));
                }
            }
        }

        fromDocument.close();
        toDocument.close();
        writeDocument.close();
    }
}
