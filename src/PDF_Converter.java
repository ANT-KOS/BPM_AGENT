
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import org.jodconverter.JodConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeUtils;


public class PDF_Converter {
    public String convert(File input,File output) throws OfficeException
    {
        final LocalOfficeManager LOM = LocalOfficeManager.install();
        try
        {
            LOM.start();
            JodConverter
                    .convert(input)
                    .to(output)
                    .execute();
        }
        finally
        {
            OfficeUtils.stopQuietly(LOM);
        }
        return "CONVERT_COMPLETE";
    }
    
    public String convert(InputStream inputS, OutputStream outputS) throws OfficeException
    {
        final LocalOfficeManager LOM = LocalOfficeManager.install();
        try
        {
            LOM.start();
            JodConverter
                    .convert(inputS)
                    .as(DefaultDocumentFormatRegistry.DOCX)
                    .to(outputS)
                    .as(DefaultDocumentFormatRegistry.PDF)
                    .execute();
        }
        finally
        {
            OfficeUtils.stopQuietly(LOM);
        }
        return "CONVERT_COMPLETE";
    }
}
