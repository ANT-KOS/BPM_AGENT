



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



public class HTTP_HANDLERS {
    
private static String Version="3.0.0";
    
    public static class testHandler implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response="<h1> Server started successfuly! </h1>"+"<h2> Port: "+main.port+"</h2>"+
                    "<h3> Version "+Version+"</h3>"+
                    "<h3> 20/10/2016 </h3>";
                    
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
    }
    
    public static class sign implements HttpHandler
    {
        @Override
        public void handle(HttpExchange he) throws IOException
        {
            
        }
    }
    
    public static class scanHandler implements HttpHandler
    {
        public void handle(HttpExchange he) throws IOException
        {
            String response="Prepearing program for scan.";
            he.sendResponseHeaders(200,response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            
            JFrame dummyJFrame = new JFrame();
            dummyJFrame.setVisible(true);
            dummyJFrame.setAlwaysOnTop(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            dummyJFrame.setLocation(dim.width/2-dummyJFrame.getSize().width/2, dim.height/2-dummyJFrame.getSize().height/2);
            
            String scan_name="";

            while(scan_name.equals(""))
            {
              scan_name=JOptionPane.showInputDialog(dummyJFrame, "ΠΛΗΚΤΡΟΛΟΓΙΣΤΕ ΤΟ ΟΝΟΜΑ ΤΟΥ/ΤΩΝ ΑΡΧΕΙΟΥ/ΩΝ ΠΟΥ ΘΑ ΔΗΜΙΟΥΡΓΗΘΕΙ/ΟΥΝ \n"+"ΑΝ ΥΠΑΡΧΕΙ ΟΠΟΙΟΔΗΠΟΤΕ ΑΡΧΕΙΟ ΜΕ ΤΟ ΙΔΙΟ ΟΝΟΜΑ ΘΑ ΑΝΤΙΚΑΤΑΣΤΗΘΕΙ","1ST STEP",JOptionPane.INFORMATION_MESSAGE);
              dummyJFrame.dispose();
              if(scan_name.equals(""))
              {
                 JOptionPane.showMessageDialog(dummyJFrame, "ΤΟ ΟΝΟΜΑ ΑΡΧΕΙΟΥ ΔΕΝ ΜΠΟΡΕΙ ΝΑ ΕΙΝΑΙ ΚΕΝΟ.","ERROR",JOptionPane.ERROR_MESSAGE); 
                 dummyJFrame.dispose();
              }
              if(!scan_name.contains(".pdf"))
              {
                  scan_name=scan_name+".pdf";
              }
            }
            
            execCommand(scan_name);

            
            
            
        }
    }
    
    public static void execCommand(String name) throws IOException
    {
        ProcessBuilder builder=new ProcessBuilder("cmd.exe","/c","cd \"C:\\BPM AGENT\\NAPS2\" && naps2.console -o \"C:\\BPM AGENT\\files\\scanned\""+"\\"+name);
        Process p=builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) 
            { 
                break; 
            }
            System.out.println(line);
        }
        
    }
    
    public static class ViewHandler implements HttpHandler
    {
        String fname;
        String doc_ver;
        String down_URL;
        
        @Override
        public void handle(HttpExchange he) throws IOException
        {
                 Map<String, Object> parameters = new HashMap<String, Object>();
                 URI requestedUri = he.getRequestURI();
                 String query = requestedUri.getRawQuery();
                 parseQuery(query, parameters);
                 
                 if(parameters.get("ver")!=null)
                 {
                     doc_ver=parameters.get("ver").toString();
                     down_URL=parameters.get("urlFile").toString()+"?a="+parameters.get("fuid").toString()+"&v="+doc_ver;
                 }
                 else
                 {
                     down_URL=parameters.get("urlFile").toString()+"?a="+parameters.get("fuid").toString();
                 }
             
                 
                 try
                 {  
                    InputStream in = new URL(down_URL).openStream();
                    fname=parameters.get("fname").toString();
                    Files.copy(in, Paths.get("files",fname),StandardCopyOption.REPLACE_EXISTING);
                     
                    File f=new File("files/"+fname);
                    
                    Desktop.getDesktop().open(f);
                    
                    
                    
                 }
                 catch(IOException ex)
                 {
                      Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 
                 String response="VIEW END";
                 he.sendResponseHeaders(200, response.length());
                 OutputStream os = he.getResponseBody();
                 os.write(response.getBytes());

                 os.close();
                 
        }


    }
          

    
    public static class EditHandler implements HttpHandler {
        private String fname__;
        private File FILE__;
        private File hidden_file_;
        private WatchService watcher;


         @Override

         public void handle(HttpExchange he) throws IOException {
                 // parse request
                 Map<String, Object> parameters = new HashMap<String, Object>();
                 URI requestedUri = he.getRequestURI();
                 String query = requestedUri.getRawQuery();
                 System.out.println(query);
                 parseQuery(query, parameters);

                 
                 File FILE = null;
                 ContentBody cbFile = null;
                 
                 JFrame dummyJFrame = new JFrame();
                 dummyJFrame.setVisible(false);
                 dummyJFrame.setAlwaysOnTop(true);
                 Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                 dummyJFrame.setLocation(dim.width/2-dummyJFrame.getSize().width/2, dim.height/2-dummyJFrame.getSize().height/2);

                 for(String key : parameters.keySet())
                 {
                     
                     if(key.equals("fname"))
                     {


                         Path hid_file = Paths.get("files\\checks\\"+"edit_"+parameters.get("fname").toString()+".check");
                         
                         
                         if(Files.exists(hid_file))
                         {
                            watcher.close();
                                                        
                         }
                         else
                         {
                             File hidden_file=new File("files/checks/"+"edit_"+parameters.get("fname").toString()+".check");
                             hidden_file_=hidden_file;
                             hidden_file.createNewFile();
                             hidden_file.setWritable(false);
                         }
                         
                         String down_URL=parameters.get("urlFile").toString()+"?a="+parameters.get("fuid").toString()+"&v="+parameters.get("ver").toString();
                         if(parameters.get("ver").toString().equals("1"))
                         {
                             down_URL+="&ver=1";
                         }
                            try
                            {
                                InputStream in = new URL(down_URL).openStream();
                                String fname=parameters.get("fname").toString();
                                fname__=fname;
                                 
                                
                                FILE=new File("files/"+fname__);
                                FILE__=FILE;

                                if(FILE.delete() || !FILE.exists())
                                {
                    
                                    Files.copy(in, Paths.get("files",fname__),StandardCopyOption.REPLACE_EXISTING);
                                }
                                else
                                {
                                    JOptionPane.showMessageDialog(dummyJFrame,"ΤΟ ΑΡΧΕΙΟ ΕΙΝΑΙ ΗΔΗ ΑΝΟΙΧΤΟ","ΣΦΑΛΜΑ",JOptionPane.INFORMATION_MESSAGE);
                                    dummyJFrame.dispose();
                                }
                    
                            }
                            catch(IOException | HeadlessException ex)
                            {
                                 Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                         
                         String uri_filepath=parameters.get(key).toString();
                         String EXT=uri_filepath.substring(parameters.get(key).toString().lastIndexOf(".")+1);
                         
                         if(EXT.equals("doc") || EXT.equals("docx") || EXT.equals("pdf") || EXT.equals("au") || EXT.equals("bmp") || EXT.equals("gif") ||
                            EXT.equals("gz") || EXT.equals("gzip") || EXT.equals("jpg") || EXT.equals("jpeg") || EXT.equals("midi") || EXT.equals("mp3") ||
                            EXT.equals("bz") || EXT.equals("bz2") || EXT.equals("ppa") || EXT.equals("ppt") || EXT.equals("pptx") || EXT.equals("text") ||
                            EXT.equals("txt") || EXT.equals("wav") || EXT.equals("xl")|| EXT.equals("xla")|| EXT.equals("xlb")|| EXT.equals("xlc")
                            || EXT.equals("xld")|| EXT.equals("xlk")|| EXT.equals("xll")|| EXT.equals("xlm")|| EXT.equals("xls")|| EXT.equals("xlv")
                            || EXT.equals("xlw")|| EXT.equals("zip")|| EXT.equals("rar") || EXT.equals("odt") || EXT.equals("ods"))
                         {

                             String filename=uri_filepath.substring(uri_filepath.lastIndexOf("/")+1);
                                                           
                             
                                 
                             try
                             {
                                 final Path path=FileSystems.getDefault().getPath(FILE.getParentFile().getAbsolutePath());

                                 Desktop.getDesktop().open(FILE);

                                 



                                 try
                                 {
                                     
                                     watcher = FileSystems.getDefault().newWatchService();
                                     path.register(watcher,ENTRY_MODIFY);
                                     System.out.println("Watch Service registered for dir: " + path.getFileName());
                                     
                                     

                                     
                                     while(true)
                                     {
                                         WatchKey KEY;

                                             KEY=watcher.take();   
                                             
                                         for(WatchEvent<?> event : KEY.pollEvents())
                                         {
                                             WatchEvent.Kind<?> kind= event.kind();
                                            
                                             WatchEvent<Path> ev = (WatchEvent<Path>) event;
                                             Path file_name=ev.context();

                                             if(kind==ENTRY_MODIFY && file_name.toString().equals(filename))
                                             {
                                                 System.out.println("ΤΟ ΑΡΧΕΙΟ ΕΧΕΙ ΤΡΟΠΟΠΟΙΗΘΕΙ");

                                                 
                                                
                                                 
                                                 if (JOptionPane.showConfirmDialog(dummyJFrame, "Έχετε ολοκληρώσει τις αλλαγές για να ανεβάσετε το αρχείο;", "ΕΙΔΟΠΟΙΗΣΗ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                                 {
                                                     dummyJFrame.dispose();
                                                     
                                                     
                                                     
                                                     System.out.println("ΑΠΟΣΤΟΛΗ ΑΡΧΕΙΟΥ");
                                                         

                                                         HttpClient httpclient = new DefaultHttpClient();
                                                         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                                                         String URL_1=parameters.get("urlFile").toString().substring(0,parameters.get("urlFile").toString().lastIndexOf("/"));
                                                         String URL_2=URL_1.substring(0,URL_1.lastIndexOf("/"));
                                                         
                                                          HttpPost httppost = new HttpPost(URL_2+"/services/upload");
                                                          
                                                          MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));
    
                                                     switch (EXT) {
                                                         case "pdf":
                                                             cbFile = new FileBody(FILE,"application/pdf");                                         
                                                             break;
                                                         case "doc":
                                                             cbFile = new FileBody(FILE,"application/msword");
                                                             break;
                                                         case "docx":
                                                             cbFile = new FileBody(FILE,"application/vnd.openxmlformats-"
                                                                     + "officedocument.wordprocessingml.document");
                                                             break;
                                                         case "au":
                                                             cbFile = new FileBody(FILE,"audio/basic");
                                                             break;
                                                         case "bmp":
                                                             cbFile = new FileBody(FILE,"image/bmp");
                                                             break;
                                                         case "gif":
                                                             cbFile = new FileBody(FILE,"image/gif");
                                                             break;
                                                         case "gz":
                                                         case "gzip":
                                                             cbFile = new FileBody(FILE,"application/x-gzip");
                                                             break;
                                                         case "jpg":
                                                         case "jpeg":
                                                             cbFile = new FileBody(FILE,"image/jpeg");
                                                             break;
                                                         case "midi":
                                                             cbFile = new FileBody(FILE,"audio/x-midi");
                                                             break;
                                                         case "mp3":
                                                             cbFile = new FileBody(FILE,"audio/mpeg3");
                                                             break;
                                                         case "png":
                                                             cbFile = new FileBody(FILE,"image/png");
                                                             break;
                                                         case "bz":
                                                              cbFile = new FileBody(FILE,"application/x-bzip");
                                                             break;   
                                                         case "bz2":
                                                             cbFile = new FileBody(FILE,"application/x-bzip2");
                                                             break;
                                                         case "ppa":
                                                         case "ppt":
                                                         case "pptx":
                                                             cbFile = new FileBody(FILE,"application/powerpoint");
                                                             break;
                                                         case "text":
                                                         case "txt":
                                                             cbFile = new FileBody(FILE,"text/plain");
                                                             break;
                                                         case "wav":
                                                             cbFile = new FileBody(FILE,"audio/wav");
                                                             break;
                                                         case "xl":
                                                         case "xla":
                                                         case "xlb":
                                                         case "xlc":
                                                         case "xld":
                                                         case "xlk":
                                                         case "xll":
                                                         case "xlm":
                                                         case "xls":
                                                         case "xlt":
                                                         case "xlv":
                                                         case "xlw":
                                                         case "xlsx":
                                                             cbFile = new FileBody(FILE,"application/excel");
                                                             break;
                                                         case "zip":
                                                         case "rar":
                                                             cbFile = new FileBody(FILE,"application/x-compressed");
                                                             break;
                                                         default:
                                                    
                                                             break;
                                                     }
                                                          if(parameters.get("caseid")!=null)
                                                          {
                                                            ContentBody cbS01=new StringBody(parameters.get("caseid").toString());
                                                            mpEntity.addPart("APPLICATION", cbS01);
                                                          }
                                                          if(parameters.get("usrid")!=null)
                                                          {
                                                            ContentBody cbS02=new StringBody(parameters.get("usrid").toString());
                                                            mpEntity.addPart("USR_UID", cbS02);
                                                          }
                                                          if(parameters.get("indx")!=null)
                                                          {
                                                            ContentBody cbS03=new StringBody(parameters.get("indx").toString());
                                                            mpEntity.addPart("INDEX",cbS03);
                                                          }
                                                          if(parameters.get("fuid")!=null)
                                                          {
                                                            ContentBody cbS04=new StringBody(parameters.get("fuid").toString());
                                                            mpEntity.addPart("APP_DOC_UID",cbS04);
                                                          }
                                                          if(parameters.get("dtyp")!=null)
                                                          {
                                                            ContentBody cbS05=new StringBody(parameters.get("dtyp").toString());
                                                            mpEntity.addPart("APP_DOC_TYPE",cbS05);
                                                          }
                                                          if(parameters.get("title")!=null)
                                                          {
                                                            ContentBody cbS06=new StringBody(parameters.get("title").toString());
                                                            mpEntity.addPart("TITLE",cbS06);
                                                          }
                                                          if(parameters.get("ver")!=null)
                                                          {
                                                              if(parameters.get("ver").toString().equals("0"))
                                                              {
                                                                ContentBody cbS07=new StringBody(parameters.get("ver").toString());
                                                                mpEntity.addPart("VERSION",cbS07);
                                                              }
                                                          }
                                                          if(parameters.get("comm")!=null)
                                                          {
                                                            ContentBody cbS08=new StringBody(parameters.get("comm").toString());
                                                            mpEntity.addPart("COMMENT",cbS08);
                                                          }
                                                          if(parameters.get("ffieldn")!=null)
                                                          {
                                                            ContentBody cbS09=new StringBody(parameters.get("ffieldn").toString());
                                                            mpEntity.addPart("APP_DOC_FIELDNAME",cbS09);
                                                          }
                                                          System.out.println(parameters.get("ffieldn"));

                                                          mpEntity.addPart("ATTACH_FILE",cbFile);


                                                          /*
                                                          if(EXT.equals("pdf"))
                                                          {
                                                              PDF_OCR PO=new PDF_OCR();
                                                              ContentBody cbS09=new StringBody(PO.Scan(FILE));
                                                              mpEntity.addPart("CONTENT",cbS09);
                                                          }
                                                          
                                                          if(EXT.equals("doc")||EXT.equals("docx"))
                                                          {
                                                              XWPFDocument doc  = new XWPFDocument(new FileInputStream(FILE));
                                                              XWPFWordExtractor we = new XWPFWordExtractor(doc);
                                                              
                                                              ContentBody cbS09=new StringBody(we.getText());
                                                              mpEntity.addPart("CONTENT",cbS09);
                                                          }
                                                          */
                                    
                                                          httppost.setEntity(mpEntity);
                                                          System.out.println("");
                                                          System.out.println("executing request " + httppost.getRequestLine());
                                                          HttpResponse response = httpclient.execute(httppost);
                                                          HttpEntity resEntity = response.getEntity();
     
                                                          
                                                          String RESPONSE="";
                                                          System.out.println(response.getStatusLine());
                                                          RESPONSE=response.getStatusLine().toString();
                                                          
                                                            if (resEntity != null) {
                                                              System.out.println(EntityUtils.toString(resEntity));
                                                              
                                                            }
                                                            if (resEntity != null) {
                                                              resEntity.consumeContent();
                                                            }
                                                            
                                                            if(RESPONSE.contains("HTTP/1.1 200 OK"))
                                                            {
                                                                JOptionPane.showMessageDialog(dummyJFrame,"Το αρχείο εστάλη με επιτυχία. Η εφαρμογή σας θα τερματιστεί."
                                                                        ,"ΕΠΙΤΥΧΙΑ",JOptionPane.INFORMATION_MESSAGE);
                                                                dummyJFrame.dispose();
                                                                watcher.close();
                                                                hidden_file_.setWritable(true);
                                                                hidden_file_.delete();
                                                                while(!FILE.delete())
                                                                {
                                                                }
                                                                
                                                                
                                                            }
                                                            RESPONSE="";
                                                            httpclient.getConnectionManager().shutdown(); 


                                                     break;
                                                 }
                                                 else
                                                 {
                                                     
                                                     dummyJFrame.dispose();
                                                     break;
                                                 }
                                             }
                                         }
                                         
                                         boolean valid = KEY.reset();
                                         if(!valid)
                                         {
                                             break;
                                         }
                                                                          

                                     }
                                   
                                 }
                                 catch(IOException ex)
                                 {
                                     System.err.println(ex);
                                 } catch (InterruptedException | UnsupportedOperationException ex) {
                                     Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                                 }
                                     
                                 
                             }
                             
                             catch(IOException e)
                             {
                                 System.out.println("FILE ERROR");
                             }
                                 
                             

                         }                            
                         else
                         {
                            
                             JOptionPane.showMessageDialog(dummyJFrame,"Η ΕΠΕΚΤΑΣΗ ΤΟΥ ΑΡΧΕΙΟΥ ΔΕΝ ΥΠΟΣΤΗΡΙΖΕΤΑΙ","EXTENSION ERROR",JOptionPane.ERROR_MESSAGE);
                             dummyJFrame.dispose();
                         }
                     }
                                     
         }
                 /*
                 String response="Μπορείτε να κλείσετε τώρα τη παρούσα καρτέλα. ";
                 he.sendResponseHeaders(200, response.length());
                 OutputStream os = he.getResponseBody();
                 os.write(response.getBytes());

                 os.close();
*/
         }
    }


    
    public static class SignHandler implements HttpHandler {
        private String fname__;
        private File FILE__;
        private File hidden_file_;
        private WatchService watcher;

         @Override

         public void handle(HttpExchange he) throws IOException {
                 // parse request
                 Map<String, Object> parameters = new HashMap<String, Object>();
                 URI requestedUri = he.getRequestURI();
                 String query = requestedUri.getRawQuery();
                 parseQuery(query, parameters);

                 
                 File FILE = null;
                 ContentBody cbFile = null;
                 
                 JFrame dummyJFrame = new JFrame();
                 dummyJFrame.setVisible(false);
                 dummyJFrame.setAlwaysOnTop(true);
                 Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                 dummyJFrame.setLocation(dim.width/2-dummyJFrame.getSize().width/2, dim.height/2-dummyJFrame.getSize().height/2);

                 for(String key : parameters.keySet())
                 {
                     
                     if(key.equals("fname"))
                     {

                         Path hid_file = Paths.get("files\\checks\\"+"sign_"+parameters.get("fname").toString()+".check");
                         
                         
                         if(Files.exists(hid_file))
                         {
                            watcher.close();                          
                         }
                         else
                         {
                             File hidden_file=new File("files/checks/"+"sign_"+parameters.get("fname").toString()+".check");
                             hidden_file_=hidden_file;
                             hidden_file.createNewFile();
                             hidden_file.setWritable(false);
                         }
                        
                         String down_URL=parameters.get("urlFile").toString()+"?a="+parameters.get("fuid").toString()+"&v="+parameters.get("ver").toString();
                         System.out.println(down_URL);
                            try
                            {
                                InputStream in = new URL(down_URL).openStream();
                                String fname=parameters.get("fname").toString();
                                fname__=fname;
                                 
                                
                                FILE=new File("files/"+fname__);
                                FILE__=FILE;

                                if(FILE.delete() || !FILE.exists())
                                {
                    
                                    Files.copy(in, Paths.get("files",fname__),StandardCopyOption.REPLACE_EXISTING);
                                }
                                else
                                {
                                    JOptionPane.showMessageDialog(dummyJFrame,"ΤΟ ΑΡΧΕΙΟ ΕΙΝΑΙ ΗΔΗ ΑΝΟΙΧΤΟ","ΣΦΑΛΜΑ",JOptionPane.INFORMATION_MESSAGE);
                                    dummyJFrame.dispose();
                                }
                    
                            }
                            catch(IOException | HeadlessException ex)
                            {
                                 Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                         
                         String uri_filepath=parameters.get(key).toString();
                         String EXT=uri_filepath.substring(parameters.get(key).toString().lastIndexOf(".")+1);
                         
                         if(EXT.equals("doc") || EXT.equals("docx") || EXT.equals("pdf") || EXT.equals("xls")||EXT.equals("odt") || EXT.equals("ods"))
                         {

                             String filename=uri_filepath.substring(uri_filepath.lastIndexOf("/")+1);
                                                           
                             To_PDF_Converters PDFCON=new To_PDF_Converters();
                             File FILE_CONVERTED=null;

                                 if(!EXT.equals("pdf"))
                                 {
                                     FILE_CONVERTED=PDFCON.Convert_To_PDF(FILE);
                                     while(!FILE__.delete())
                                     {
                                         
                                     }

                                 }
                                 else
                                 {
                                     FILE_CONVERTED=FILE__;
                                 }
                                 
                                 
                                 try
                                 {
                                 final Path path=FileSystems.getDefault().getPath(FILE_CONVERTED.getParentFile().getCanonicalPath());

                                 
                                 //BEGIN SIGN
                                    Desktop.getDesktop().open(FILE_CONVERTED);
                                 try
                                 {
                                     
                                     watcher = FileSystems.getDefault().newWatchService();
                                     path.register(watcher,ENTRY_MODIFY);
                                     System.out.println("Watch Service registered for dir: " + path.getFileName());
                                     
                                     

                                     
                                     while(true)
                                     {
                                         WatchKey KEY;

                                             KEY=watcher.take();   
                                             
                                         for(WatchEvent<?> event : KEY.pollEvents())
                                         {
                                             WatchEvent.Kind<?> kind= event.kind();
                                            
                                             WatchEvent<Path> ev = (WatchEvent<Path>) event;
                                             Path file_name=ev.context();

                                             if(kind==ENTRY_MODIFY && file_name.toString().equals(filename))
                                             {
                                                 System.out.println("ΤΟ ΑΡΧΕΙΟ ΕΧΕΙ ΤΡΟΠΟΠΟΙΗΘΕΙ");

                                                 
                                                
                                                 
                                                 if (JOptionPane.showConfirmDialog(dummyJFrame, "Έχετε ολοκληρώσει τις αλλαγές για να ανεβάσετε το αρχείο;", "ΕΙΔΟΠΟΙΗΣΗ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                                 {
                                                     dummyJFrame.dispose();
                                                     
                                                     
                                                     
                                                     System.out.println("ΑΠΟΣΤΟΛΗ ΑΡΧΕΙΟΥ");
                                 
                                 
                                 
                                                         HttpClient httpclient = new DefaultHttpClient();
                                                         httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                                                         String URL_1=parameters.get("urlFile").toString().substring(0,parameters.get("urlFile").toString().lastIndexOf("/"));
                                                         String URL_2=URL_1.substring(0,URL_1.lastIndexOf("/"));

                                                          HttpPost httppost = new HttpPost(URL_2+"/services/upload");
                                                          
                                                          MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));

                                                         cbFile = new FileBody(FILE_CONVERTED,"application/pdf");                                         
                                                           
                                                     
                                                          if(parameters.get("caseid")!=null)
                                                          {
                                                            ContentBody cbS01=new StringBody(parameters.get("caseid").toString());
                                                            mpEntity.addPart("APPLICATION", cbS01);
                                                          }
                                                          if(parameters.get("usrid")!=null)
                                                          {
                                                            ContentBody cbS02=new StringBody(parameters.get("usrid").toString());
                                                            mpEntity.addPart("USR_UID", cbS02);
                                                          }
                                                          if(parameters.get("indx")!=null)
                                                          {
                                                            ContentBody cbS03=new StringBody(parameters.get("indx").toString());
                                                            mpEntity.addPart("INDEX",cbS03);
                                                          }
                                                          if(parameters.get("fuid")!=null)
                                                          {
                                                            ContentBody cbS04=new StringBody(parameters.get("fuid").toString());
                                                            mpEntity.addPart("APP_DOC_UID",cbS04);
                                                          }
                                                          if(parameters.get("dtyp")!=null)
                                                          {
                                                            ContentBody cbS05=new StringBody(parameters.get("dtyp").toString());
                                                            mpEntity.addPart("APP_DOC_TYPE",cbS05);
                                                          }
                                                          if(parameters.get("title")!=null)
                                                          {
                                                            ContentBody cbS06=new StringBody(parameters.get("title").toString());
                                                            mpEntity.addPart("TITLE",cbS06);
                                                          }
                                                          if(parameters.get("ver")!=null)
                                                          {
                                                            ContentBody cbS07=new StringBody(parameters.get("ver").toString());
                                                            mpEntity.addPart("VERSION",cbS07);
                                                          }
                                                          if(parameters.get("comm")!=null)
                                                          {
                                                            ContentBody cbS08=new StringBody(parameters.get("comm").toString());
                                                            mpEntity.addPart("COMMENT",cbS08);
                                                          }
                                                          if(parameters.get("ffieldn")!=null)
                                                          {
                                                            ContentBody cbS09=new StringBody(parameters.get("ffieldn").toString());
                                                            mpEntity.addPart("APP_DOC_FIELDNAME",cbS09);
                                                          }

                                                          mpEntity.addPart("ATTACH_FILE",cbFile);


                                                          /*
                                                          if(EXT.equals("pdf"))
                                                          {
                                                              PDF_OCR PO=new PDF_OCR();
                                                              ContentBody cbS09=new StringBody(PO.Scan(FILE));
                                                              mpEntity.addPart("CONTENT",cbS09);
                                                          }
                                                          
                                                          if(EXT.equals("doc")||EXT.equals("docx"))
                                                          {
                                                              XWPFDocument doc  = new XWPFDocument(new FileInputStream(FILE));
                                                              XWPFWordExtractor we = new XWPFWordExtractor(doc);
                                                              
                                                              ContentBody cbS09=new StringBody(we.getText());
                                                              mpEntity.addPart("CONTENT",cbS09);
                                                          }
                                                          */
                                    
                                                          httppost.setEntity(mpEntity);
                                                          System.out.println("");
                                                          System.out.println("executing request " + httppost.getRequestLine());
                                                          HttpResponse response = httpclient.execute(httppost);
                                                          HttpEntity resEntity = response.getEntity();
     
                                                          
                                                          String RESPONSE="";
                                                          System.out.println(response.getStatusLine());
                                                          RESPONSE=response.getStatusLine().toString();
                                                          
                                                            if (resEntity != null) {
                                                              System.out.println(EntityUtils.toString(resEntity));
                                                              
                                                            }
                                                            if (resEntity != null) {
                                                              resEntity.consumeContent();
                                                            }
                                                            
                                                            if(RESPONSE.contains("HTTP/1.1 200 OK"))
                                                            {
                                                                JOptionPane.showMessageDialog(dummyJFrame,"Το αρχείο εστάλη με επιτυχία. Μπορείτε να τερματίσετε την εφαρμογή"
                                                                        + " σας.","ΕΠΙΤΥΧΙΑ",JOptionPane.INFORMATION_MESSAGE);
                                                                dummyJFrame.dispose();
                                                                while(!FILE_CONVERTED.delete())
                                                                {
                                                                }
                                                            }
                                                            RESPONSE="";
                                                            httpclient.getConnectionManager().shutdown(); 
                                                            
                                                            break;
                                                 }
                                                 else
                                                 {
                                                     dummyJFrame.dispose();
                                                     break;
                                                 }
                                                 
                                             }
                                         }
                             
                                     boolean valid = KEY.reset();
                                         if(!valid)
                                         {
                                             break;
                                         }
                                                                          

                                     }
                                   
                                 }
                                 catch(IOException ex)
                                 {
                                     System.err.println(ex);
                                 } catch (InterruptedException | UnsupportedOperationException ex) {
                                     Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                                 }
                                     
                                 
                             }
                             
                             catch(IOException e)
                             {
                                 System.out.println("FILE ERROR");
                             }
                                 
                             

                         }                            
                         else
                         {
                            
                             JOptionPane.showMessageDialog(dummyJFrame,"Η ΕΠΕΚΤΑΣΗ ΤΟΥ ΑΡΧΕΙΟΥ ΔΕΝ ΥΠΟΣΤΗΡΙΖΕΤΑΙ","EXTENSION ERROR",JOptionPane.ERROR_MESSAGE);
                             dummyJFrame.dispose();
                         }
                     }
                                     
         }
                 /*
                 String response="Μπορείτε να κλείσετε τώρα τη παρούσα καρτέλα. ";
                 he.sendResponseHeaders(200, response.length());
                 OutputStream os = he.getResponseBody();
                 os.write(response.getBytes());

                 os.close();
*/
         }
    }
                 
         
                                     
         


         
    

    
    
    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

         if (query != null) {
                 String pairs[] = query.split("[&]");
                 for (String pair : pairs) {
                          String param[] = pair.split("[=]");
                          String key = null;
                          String value = null;
                          if (param.length > 0) {
                          key = URLDecoder.decode(param[0], 
                          	System.getProperty("file.encoding"));
                          }

                          if (param.length > 1) {
                                   value = URLDecoder.decode(param[1], 
                                   System.getProperty("file.encoding"));
                          }

                          if (parameters.containsKey(key)) {
                                   Object obj = parameters.get(key);
                                    if (obj instanceof List<?>) {
                                            List<String> values = (List<String>) obj;
                                            values.add(value);

                                   } else if (obj instanceof String) {
                                            List<String> values = new ArrayList<String>();
                                            values.add((String) obj);
                                            values.add(value);
                                            parameters.put(key, values);
                                   }
                          } else {
                                   parameters.put(key, value);
                          }
                 }
         }
    }
}




