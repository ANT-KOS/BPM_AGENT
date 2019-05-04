
/*
 * Antonis Kosmidis Copyright (c) 2016.  ALL RIGHTS RESERVED
 */
import java.io.File;
import java.io.IOException;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeException;

public class To_PDF_Converters {

    public File Convert_To_PDF(File FILE) throws IOException {
        File outFile;
        try {
            outFile = new File(FILE.toString().substring(0, FILE.toString().lastIndexOf(".")) + ".pdf");
            outFile.getParentFile().mkdirs();

            OfficeManagerWrapper officeManagerWrapper = new OfficeManagerWrapper(2002);
            officeManagerWrapper.getDocumentConverter();

            OfficeDocumentConverter converter = officeManagerWrapper.getDocumentConverter();
            converter.convert(FILE, outFile);

            officeManagerWrapper.stopOfficeManager();

        } catch (OfficeException e) {
            return null;
        }
        return outFile;
    }
}
