
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;



public final class PDF_OCR
{

    
    public PDF_OCR()
    {
    }


    /**
     * This will print the documents text in a certain area.
     *
     * @param F
     * @return
     */
    public String Scan(File F)
    {

            PDDocument document = null;
            String text="";
            try
            {
                document = PDDocument.load(F);
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition( true );
                int pages=document.getNumberOfPages();
                System.out.println(pages);

                for(int i=0;i<pages;i++)
                {
                    PDPage P=document.getPage(i);
                    int width;
                    int height;
                    width=Math.round(P.getMediaBox().getWidth());
                    height=Math.round(P.getMediaBox().getHeight());
                    Rectangle rect=new Rectangle(width,height);
                    stripper.addRegion("class1", rect);
                    
                    stripper.extractRegions( P );
                    text+=stripper.getTextForRegion("class1");
                }
                System.out.println( "Text:");
                System.out.println(text);
            }
        catch (IOException ex) {
            Logger.getLogger(PDF_OCR.class.getName()).log(Level.SEVERE, null, ex);
        }            finally
            {
                if( document != null )
                {
                    try {
                        document.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PDF_OCR.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return text;
        }
    }



