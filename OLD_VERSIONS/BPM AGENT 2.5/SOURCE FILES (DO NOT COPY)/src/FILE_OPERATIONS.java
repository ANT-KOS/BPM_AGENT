
import java.io.File;
import java.io.IOException;
import org.ini4j.Wini;

public final class FILE_OPERATIONS {
    
    private final File file_out_config,file_out_FILES,file_out_SCANNED,file_out_CHECKS;
    
    
    public FILE_OPERATIONS() throws IOException
    {
        file_out_config=new File("config/config.ini");
        file_out_FILES=new File("files");
        file_out_SCANNED=new File("files/scanned");
        file_out_CHECKS=new File("files/checks");
        file_out_FILES.mkdirs();
        file_out_SCANNED.mkdirs();
        file_out_CHECKS.mkdirs();
        File parent=file_out_config.getParentFile();
        parent.mkdirs();
        file_out_config.createNewFile();
        if(file_out_config.length()<=0 && !file_out_config.isDirectory())
        {  
            create_INI();
        }
        
    }
    
    public int get_PORT() throws IOException
    {
        int port;
        Wini ini = new Wini(file_out_config);
        port=ini.get("BASIC_CONFIG","port",int.class);
        
        
        return port;
    }
    
    public int get_DAYS_BEF_DEL() throws IOException      
    {
        int d;
        Wini ini = new Wini(file_out_config);
        
        d=ini.get("BASIC_CONFIG","DAYS_BEFORE_DELETION",int.class);
        return d;
        
    }
    
    public int get_MAX_TIMEOUT() throws IOException
    {
        int mt;
        Wini ini = new Wini(file_out_config);
        
        mt=ini.get("BASIC_CONFIG","MAX_TIMEOUT",int.class);
        return mt;
    }
    
    private void create_INI() throws IOException
    {
        Wini ini = new Wini(file_out_config);
        
        ini.put("BASIC_CONFIG","port",34300);
        ini.put("BASIC_CONFIG","DAYS_BEFORE_DELETION",30);
        ini.put("BASIC_CONFIG","MAX_TIMEOUT",1);
        ini.store();
        
    }
}
