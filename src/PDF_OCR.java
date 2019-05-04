
/*
 * Antonis Kosmidis Copyright (c) 2016.  ALL RIGHTS RESERVED
 */
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public final class PDF_OCR {

    public static void Scan(File F,ArrayList<Integer> BREAKPOINTS,ArrayList<String> TYPE) {

        PDDocument document = null;
        String text = "";
        try {
            document = PDDocument.load(F);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            int pages = document.getNumberOfPages();
            for (int i = 0; i < pages; i++) {
                PDPage P = document.getPage(i);
                int width;
                int height;
                width = Math.round(P.getMediaBox().getWidth());
                height = Math.round(P.getMediaBox().getHeight());
                Rectangle rect = new Rectangle(width, height);
                stripper.addRegion("class1", rect);

                stripper.extractRegions(P);
                text = stripper.getTextForRegion("class1");
                text = text.trim();
                /*PACKET BREAKPOINT*/
                if (text.equals("2222222222222222")) {
                    BREAKPOINTS.add(i);
                    TYPE.add("PACKET");
                } 
                /*ATTACHEMENT BREAKPOINT*/
                else if (text.equals("4444444444444444")) {
                    BREAKPOINTS.add(i);
                    TYPE.add("ATTACHMENT");
                }
            }
            document.close();
        } catch (IOException ex) {
            Logger.getLogger(PDF_OCR.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException ex) {
                    Logger.getLogger(PDF_OCR.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
