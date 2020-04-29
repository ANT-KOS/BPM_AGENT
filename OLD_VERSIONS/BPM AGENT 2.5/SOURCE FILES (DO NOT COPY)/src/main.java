
import java.io.IOException;



public class main {

public static int port;
public static FILE_OPERATIONS FOP;

    public static void main(String[] args) throws IOException, InterruptedException
    {
        System.setProperty("file.encoding","ISO-8859-7");
        FILE_OPERATIONS OP=new FILE_OPERATIONS();
        FOP=OP;
        MASS_DELETE_FILES MDF= new MASS_DELETE_FILES();
        MDF.deleteFilesOlderThanNdays(OP.get_DAYS_BEF_DEL(),"files");
        MDF.deleteFilesOlderThanNdays(OP.get_DAYS_BEF_DEL(),"files\\scanned");
        MDF.deleteFilesOlderThanNdays(OP.get_DAYS_BEF_DEL(),"files\\checks");
        int PORT=OP.get_PORT();
        port=PORT;
        HTTP_SERVER JWS=new HTTP_SERVER();
        JWS.Start(port);
        
        
    }
    
}
