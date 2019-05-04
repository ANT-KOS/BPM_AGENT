
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDF_SPLITTER {

    public static void splitPDF(String destination, File source, List<Integer> breakpoints, List<String> breakpoints_type) throws IOException {

        PDDocument DOC = PDDocument.load(source);
        Splitter SPLITTER = new Splitter();
        List<PDDocument> DOC_S = SPLITTER.split(DOC);

        PDFMergerUtility PDFMU = new PDFMergerUtility();

        if (!breakpoints.isEmpty() && !breakpoints_type.isEmpty()) {

            File TEMP = new File(destination + "\\temp");
            TEMP.mkdir();

            int last_page = 0;
            int packet_counter = 1;
            int attachment_counter = 1;
            int inside = 0;

            for (int i = 0; i < breakpoints.size(); i++) {
                int step =0;
                for (int j = last_page; j < breakpoints.get(i); j++) {
                    System.out.println("BREAKPOINT: "+breakpoints.get(i));
                    PDDocument doc = DOC_S.get(j);
                    doc.save(destination + "\\temp\\TEMP_" + j + ".pdf");
                    PDFMU.addSource(destination + "\\temp\\TEMP_" + j + ".pdf");
                    step=j;
                }

                if (breakpoints_type.get(i).equals("PACKET")) {
                    if (inside == 1) {
                        packet_counter++;
                    }
                    inside++;
                    String fname = JOptionPane.showInputDialog(Main.DJF.getDummyJFrame(), "ΠΛΗΚΤΡΟΛΟΓΙΣΤΕ ΤΟ ΟΝΟΜΑ ΤΟΥ ΚΥΡΙΟΥ ΕΓΓΡΑΦΟΥ "+packet_counter, "ΠΑΚΕΤΟ ΝΟ. " + packet_counter, JOptionPane.INFORMATION_MESSAGE);
                    Main.DJF.getDummyJFrame().dispose();
                    attachment_counter = 1;
                    PDFMU.setDestinationFileName(destination + "\\" + fname + ".pdf");
                    PDFMU.mergeDocuments(null);
                    PDFMU = new PDFMergerUtility();
                    HTTP_HANDLERS.pdf_files.add(new File(destination + "\\" + fname + ".pdf"));
                    HTTP_HANDLERS.pdf_files_type.add("PACKET");
                } else if (breakpoints_type.get(i).equals("ATTACHMENT")) {
                    String fname = JOptionPane.showInputDialog(Main.DJF.getDummyJFrame(), "ΠΛΗΚΤΡΟΛΟΓΙΣΤΕ ΤΟ ΟΝΟΜΑ ΤΟΥ ΕΠΙΣΥΝΑΠΤΟΜΕΝΟΥ ΕΓΓΡΑΦΟΥ ΤΟΥ ΠΑΚΕΤΟΥ " + packet_counter, "ΕΠΙΣΥΝΑΠΤΟΜΕΝΟ ΑΡΧΕΙΟ ΝΟ. " + attachment_counter, JOptionPane.INFORMATION_MESSAGE);
                    Main.DJF.getDummyJFrame().dispose();
                    attachment_counter++;
                    PDFMU.setDestinationFileName(destination + "\\" + fname + ".pdf");
                    PDFMU.mergeDocuments(null);
                    PDFMU = new PDFMergerUtility();
                    HTTP_HANDLERS.pdf_files.add(new File(destination + "\\" + fname + ".pdf"));
                    HTTP_HANDLERS.pdf_files_type.add("ATTACHEMENT");
                }

                for (int j = last_page; j < breakpoints.get(i); j++) {
                    new File(destination + "\\temp\\TEMP_" + j + ".pdf").delete();
                }
                last_page = step + 2;
            }
            TEMP.delete();
            DOC.close();
        }
    }
}
