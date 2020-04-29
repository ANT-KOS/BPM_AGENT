
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Future;
import org.artofsolving.jodconverter.OfficeDocumentConverter;



public class To_PDF_Converters {
    
    public void Convert_DOC_DOCX_To_PDF(File FILE)
    {
        try
        {
             InputStream in = new BufferedInputStream(new FileInputStream(FILE.getAbsoluteFile()));
             File outFile = new File(FILE.toString().substring(0,FILE.toString().lastIndexOf("."))+".pdf");
            outFile.getParentFile().mkdirs();
             
              IConverter converter = LocalConverter.builder()
                                             .build();
              
              if(FILE.toString().substring(FILE.toString().lastIndexOf(".")).equals("docx"))
              {
                Future<Boolean> conversion = converter
                                          .convert(in).as(DocumentType.DOCX)
                                          .to(outFile).as(DocumentType.PDF)
                                          .schedule();
              }
              else if(FILE.toString().substring(FILE.toString().lastIndexOf(".")).equals("doc"))
              {
                Future<Boolean> conversion = converter
                                          .convert(in).as(DocumentType.DOC)
                                          .to(outFile).as(DocumentType.PDF)
                                          .schedule();  
              }
            
            

        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
    }
    
    public File Convert_To_PDF(File FILE)
    {
        File outFile = null;
        try
        {
               
            outFile = new File(FILE.toString().substring(0,FILE.toString().lastIndexOf("."))+".pdf");
            outFile.getParentFile().mkdirs();
             
            OfficeManagerWrapper officeManagerWrapper = new OfficeManagerWrapper(2002);
            officeManagerWrapper.getDocumentConverter();
      
            OfficeDocumentConverter converter = officeManagerWrapper.getDocumentConverter();
            converter.convert(FILE, outFile);
             
            officeManagerWrapper.stopOfficeManager();
             
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
        return outFile;
    }
    
}
