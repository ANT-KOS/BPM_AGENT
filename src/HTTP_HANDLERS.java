
/*
 * Antonis Kosmidis Copyright (c) 2016.  ALL RIGHTS RESERVED
 */
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
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
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HTTP_HANDLERS {

    private static final String Version = Main.BPM_AGENT_VERSION + "." + Main.BPM_AGENT_VERSION_R;
    protected volatile static HashMap<String, Thread> RUNNING_EDIT_THREADS = new HashMap<>();
    protected volatile static HashMap<String, Integer> RUNNING_EDIT_PROCESSES = new HashMap<>();
    protected volatile static HashMap<String, File> RUNNING_EDIT_FILES = new HashMap<>();
    protected static ArrayList<File> pdf_files;
    protected static ArrayList<String> pdf_files_type;
    public volatile static boolean exit_status = false;
    protected volatile static boolean continuation = false;

    public static class testHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "<h1> Server started successfuly! </h1>" + "<h2> Port: " + Main.port + "</h2>"
                    + "<h3> Version " + Version + "</h3>"
                    + "<h3> " + Main.DATE_OF_CREATION + " </h3>";

            Headers RES_HEAD = he.getResponseHeaders();
            RES_HEAD.set("Access-Control-Allow-Origin", "*");
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
            he.close();
        }

    }

    public static class scanHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException, NullPointerException {

            String scan_name = JOptionPane.showInputDialog(Main.DJF.getDummyJFrame(), "ΠΛΗΚΤΡΟΛΟΓΙΣΤΕ ΤΟ ΟΝΟΜΑ ΤΟΥ/ΤΩΝ ΑΡΧΕΙΟΥ/ΩΝ ΠΟΥ ΘΑ ΔΗΜΙΟΥΡΓΗΘΕΙ/ΟΥΝ \n" + "ΑΝ ΥΠΑΡΧΕΙ ΟΠΟΙΟΔΗΠΟΤΕ ΑΡΧΕΙΟ ΜΕ ΤΟ ΙΔΙΟ ΟΝΟΜΑ ΘΑ ΑΝΤΙΚΑΤΑΣΤΗΘΕΙ", "1ST STEP", JOptionPane.INFORMATION_MESSAGE);
            Main.DJF.getDummyJFrame().dispose();
            if (scan_name != null && scan_name.length() > 0) {
                if (scan_name.equals("")) {
                    JOptionPane.showMessageDialog(Main.DJF.getDummyJFrame(), "ΤΟ ΟΝΟΜΑ ΑΡΧΕΙΟΥ ΔΕΝ ΜΠΟΡΕΙ ΝΑ ΕΙΝΑΙ ΚΕΝΟ.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    Main.DJF.getDummyJFrame().dispose();
                } else if (!scan_name.contains(".pdf")) {
                    scan_name = scan_name + ".pdf";
                    execCommand(scan_name, he);
                } else if (scan_name.contains(".pdf")) {
                    execCommand(scan_name, he);
                }

            } else {
                he.sendResponseHeaders(204, -1);
                he.close();
            }
        }
    }

    public static void execCommand(String name, HttpExchange he) throws IOException, NullPointerException {

        StringBuilder buf;
        Map<String, Object> parameters = new HashMap<>();

        if (he.getRequestMethod().equalsIgnoreCase("POST")) {
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            int b;
            buf = new StringBuilder();
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }

            br.close();
            isr.close();

            parseQuery(buf.toString(), parameters);
        } else {
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
            parseQuery(query, parameters);
        }

        boolean scan_suc = true;

        String STR = FILE_OPERATIONS.get_FILE_DIR().replace("/", "\\");

        
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"" + FILE_OPERATIONS.get_RUNNING_PATH().replace("/", "\\") + "NAPS2\\App\" && naps2.console -o \"" + STR + "scanned\"" + "\\" + name);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            if (line.contains("File already exists. Use --force to overwrite")) {
                scan_suc = false;
                JOptionPane.showMessageDialog(null, "ΤΟ ΑΡΧΕΙΟ ΥΠΑΡΧΕΙ ΗΔΗ", "ERROR", JOptionPane.ERROR_MESSAGE);
                he.sendResponseHeaders(204, -1);
                he.close();
            }
            System.out.println(line);
        }
         
        String FILE_PDF = STR + "scanned" + "\\" + name;
        System.out.println("PDF_PATH: " + FILE_PDF);
        File F_PDF = new File(FILE_PDF);
        ArrayList<Integer> BREAKPOINTS = new ArrayList<>();
        ArrayList<String> BREAKPOINTS_TYPE = new ArrayList<>();
        PDF_OCR.Scan(F_PDF, BREAKPOINTS, BREAKPOINTS_TYPE);

        boolean change_case = false;
        pdf_files = new ArrayList<>();
        pdf_files_type = new ArrayList<>();

        if (!BREAKPOINTS.isEmpty() && !BREAKPOINTS_TYPE.isEmpty() && scan_suc == true) {
            PDF_SPLITTER.splitPDF(FILE_OPERATIONS.get_FILE_DIR() + "\\scanned", F_PDF, BREAKPOINTS, BREAKPOINTS_TYPE);
        }

        for (int i = 0; i < pdf_files.size(); i++) {
            if (change_case == false) {
                if (pdf_files_type.get(i).equals("PACKET")) {
                    parameters.put("app_doc_comment", "ΚΥΡΙΟ ΕΓΓΡΑΦΟ");
                } else {
                    parameters.put("app_doc_comment", "ΕΠΙΣΥΝΑΠΤΟΜΕΝΟ ΕΓΓΡΑΦΟ");
                }

                send_files(parameters, pdf_files.get(i));
                if (i != pdf_files.size()-1) {
                    if (pdf_files_type.get(i + 1).equals("PACKET")) {
                        change_case = true;
                    }
                }
            } else {
                create_case(parameters);
                send_files(parameters, pdf_files.get(i));
                change_case = false;
            }
        }

        if (scan_suc == true) {
            JOptionPane.showMessageDialog(null, "ΤΟ ΣΚΑΝΑΡΙΣΜΑ ΕΧΕΙ ΟΛΟΚΛΗΡΩΘΕΙ.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
        }

        he.sendResponseHeaders(204, -1);
        he.close();

    }

    public static class ViewHandler implements HttpHandler {

        private String fname;
        private String down_URL;
        private StringBuilder buf;

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = new HashMap<>();

            if (he.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);

                int b;
                buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                br.close();
                isr.close();

                parseQuery(buf.toString(), parameters);
            } else {
                URI requestedUri = he.getRequestURI();
                String query = requestedUri.getRawQuery();
                parseQuery(query, parameters);
            }

            String STR = FILE_OPERATIONS.get_FILE_DIR().replace("/", "\\");

            String version;
            if (parameters.containsKey("v")) {
                if (parameters.get("v").toString() != null || !parameters.get("v").toString().equals("")) {
                    version = parameters.get("v").toString();
                } else {
                    version = "";
                }
            } else {
                version = "";
            }

            Path hid_file_edit = Paths.get(STR + "checks\\" + "edit_" + parameters.get("fname").toString() + "_" + parameters.get("app_doc_uid").toString() + ".check");
            Path hid_file_view = Paths.get(STR + "checks\\" + "view_" + parameters.get("fname").toString() + "_" + parameters.get("app_doc_uid").toString() + ".check");

            if (version.equals("")) {
                down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString(); //"&v=" + parameters.get("v").toString();
                fname = parameters.get("fname").toString();
            } else {
                down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString() + "&v=" + version;
                fname = parameters.get("fname").toString().substring(0, parameters.get("fname").toString().lastIndexOf(".")) + "_v" + version + parameters.get("fname").toString().substring(parameters.get("fname").toString().lastIndexOf("."));
            }

            if (continuation == true) {
                Files.createFile(hid_file_view);
                continuation = false;
            }

            File f = new File(FILE_OPERATIONS.get_FILE_DIR() + fname);

            if (Files.exists(hid_file_edit)) {
                int option = JOptionPane.showConfirmDialog(Main.DJF.getDummyJFrame(), "ΤΟ ΑΡΧΕΙΟ ΕΙΝΑΙ ΗΔΗ ΑΝΟΙΧΤΟ ΓΙΑ ΕΠΕΞΕΡΓΑΣΙΑ\nΘΕΛΕΤΕ ΝΑ ΤΗ ΣΥΝΕΧΙΣΕΤΕ;", "ΕΙΔΟΠΟΙΗΣΗ", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (option) {
                    case JOptionPane.YES_OPTION:
                        Main.DJF.getDummyJFrame().dispose();
                        Desktop.getDesktop().open(RUNNING_EDIT_FILES.get(parameters.get("app_doc_uid").toString()));
                        he.sendResponseHeaders(204, -1);
                        he.close();
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        Main.DJF.getDummyJFrame().dispose();
                        he.sendResponseHeaders(204, -1);
                        he.close();
                        break;
                    default:
                        Main.DJF.getDummyJFrame().dispose();
                        Files.delete(hid_file_edit);
                        RUNNING_EDIT_THREADS.get(parameters.get("app_doc_uid").toString()).interrupt();
                        RUNNING_EDIT_THREADS.remove(parameters.get("app_doc_uid").toString());
                        RUNNING_EDIT_FILES.get(parameters.get("app_doc_uid").toString()).delete();
                        RUNNING_EDIT_FILES.remove(parameters.get("app_doc_uid").toString());
                        RUNNING_EDIT_PROCESSES.remove(parameters.get("app_doc_uid").toString());

                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showOptionDialog(Main.DJF.getDummyJFrame(), "ΠΑΡΑΚΑΛΩ ΠΕΡΙΜΕΝΕΤΕ...", "LOADING", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                                }
                            }).start();

                            CloseableHttpClient httpClient = HttpClients.createDefault();
                            HttpGet HTTP_GET = new HttpGet(down_URL);

                            if (!FILE_OPERATIONS.get_PROXY_ADDRESS().equals("") && !FILE_OPERATIONS.get_PROXY_PORT().equals("")) {
                                HttpHost proxy = new HttpHost(FILE_OPERATIONS.get_PROXY_ADDRESS(), Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()), "http");
                                RequestConfig REQUEST = RequestConfig.custom().setProxy(proxy).build();
                                HTTP_GET.setConfig(REQUEST);
                            }

                            HttpResponse response = httpClient.execute(HTTP_GET);
                            HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                                BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                                int inByte;
                                while ((inByte = bis.read()) != -1) {
                                    bos.write(inByte);
                                }
                                bis.close();
                                bos.close();
                            }
                            httpClient.close();

                            Main.DJF.getDummyJFrame().dispose();

                            Desktop.getDesktop().open(f);

                            Files.createFile(hid_file_view);
                            final File F_THREAD = f;
                            Thread.sleep(FILE_OPERATIONS.get_CHECK_VIEW_THREAD_TIMER());
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    while (!F_THREAD.delete()) {
                                    }
                                    if (Files.exists(hid_file_view)) {
                                        try {
                                            Files.delete(hid_file_view);
                                        } catch (IOException ex) {
                                            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            }).start();
                        } catch (IOException | InterruptedException ex) {
                            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        he.sendResponseHeaders(204, -1);
                        he.close();
                        break;
                }
            } else if (Files.exists(hid_file_view)) {
                JOptionPane.showMessageDialog(Main.DJF.getDummyJFrame(), "ΕΠΑΝΕΚΚΙΝΗΣΗ ΠΡΟΒΟΛΗΣ ΑΡΧΕΙΟΥ", "INFO", JOptionPane.INFORMATION_MESSAGE);
                Main.DJF.getDummyJFrame().dispose();
                Desktop.getDesktop().open(f);
                he.sendResponseHeaders(204, -1);
                he.close();
            } else {

                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showOptionDialog(Main.DJF.getDummyJFrame(), "ΠΑΡΚΑΛΩ ΠΕΡΙΜΕΝΕΤΕ...", "LOADING", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                        }
                    }).start();

                    if (version.equals("")) {
                        down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString(); //"&v=" + parameters.get("v").toString();
                        fname = parameters.get("fname").toString();
                    } else {
                        down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString() + "&v=" + version;
                        fname = parameters.get("fname").toString().substring(0, parameters.get("fname").toString().lastIndexOf(".")) + "_v" + version + parameters.get("fname").toString().substring(parameters.get("fname").toString().lastIndexOf("."));
                    }
                    f = new File(FILE_OPERATIONS.get_FILE_DIR() + fname);

                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpGet HTTP_GET = new HttpGet(down_URL);

                    if (!FILE_OPERATIONS.get_PROXY_ADDRESS().equals("") && !FILE_OPERATIONS.get_PROXY_PORT().equals("")) {
                        System.out.println(FILE_OPERATIONS.get_PROXY_ADDRESS());
                        System.out.println(Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()));
                        HttpHost proxy = new HttpHost(FILE_OPERATIONS.get_PROXY_ADDRESS(), Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()), "http");
                        RequestConfig REQUEST = RequestConfig.custom().setProxy(proxy).build();
                        HTTP_GET.setConfig(REQUEST);
                    }

                    HttpResponse response = httpClient.execute(HTTP_GET);
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                        BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                        int inByte;
                        while ((inByte = bis.read()) != -1) {
                            bos.write(inByte);
                        }
                        bis.close();
                        bos.close();
                    }
                    httpClient.close();
                    Main.DJF.getDummyJFrame().dispose();

                    Desktop.getDesktop().open(f);

                    Files.createFile(hid_file_view);
                    final File F_THREAD = f;
                    Thread.sleep(FILE_OPERATIONS.get_CHECK_VIEW_THREAD_TIMER());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!F_THREAD.delete()) {
                            }
                            if (Files.exists(hid_file_view)) {
                                try {
                                    Files.delete(hid_file_view);
                                } catch (IOException ex) {
                                    Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }).start();
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            he.sendResponseHeaders(204, -1);
            he.close();
        }
    }

    public static class EditHandler implements HttpHandler {

        private String fname__;
        private WatchService watcher;
        private WatchKey Key;
        private StringBuilder buf;

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = new HashMap<>();

            if (he.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);

                int b;
                buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                br.close();
                isr.close();
                parseQuery(buf.toString(), parameters);
            } else {
                URI requestedUri = he.getRequestURI();
                String query = requestedUri.getRawQuery();
                parseQuery(query, parameters);
            }

            File FILE = null;
            he.sendResponseHeaders(204, -1);

            String STR = FILE_OPERATIONS.get_FILE_DIR().replace("/", "\\");

            String version;
            if (parameters.containsKey("v")) {
                if (parameters.get("v").toString() != null || !parameters.get("v").toString().equals("")) {
                    version = parameters.get("v").toString();
                } else {
                    version = "";
                }
            } else {
                version = "";
            }

            Path hid_file_edit = Paths.get(STR + "checks\\" + "edit_" + parameters.get("fname").toString() + "_" + parameters.get("app_doc_uid").toString() + ".check");
            Path hid_file_view = Paths.get(STR + "checks\\" + "view_" + parameters.get("fname").toString() + "_" + parameters.get("app_doc_uid").toString() + ".check");

            if (Files.exists(hid_file_edit)) {
                RUNNING_EDIT_THREADS.get(parameters.get("app_doc_uid").toString()).interrupt();
                RUNNING_EDIT_THREADS.replace(parameters.get("app_doc_uid").toString(), Thread.currentThread());
            } else if (Files.exists(hid_file_view)) {
                Files.delete(hid_file_view);
                Files.createFile(hid_file_edit);
            } else if (!Files.exists(hid_file_edit)) {
                Files.createFile(hid_file_edit);
            }

            String down_URL;
            String fname;
            if (version.equals("")) {
                down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString();
                fname = parameters.get("fname").toString();
            } else {
                down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString() + "&v=" + version;
                fname = parameters.get("fname").toString().substring(0, parameters.get("fname").toString().lastIndexOf(".")) + "_v" + version + parameters.get("fname").toString().substring(parameters.get("fname").toString().lastIndexOf("."));
            }

            System.out.println(down_URL);
            try {
                System.out.println(parameters.get("fname").toString());

                FILE = new File(FILE_OPERATIONS.get_FILE_DIR() + fname);

                if (!FILE.exists()) {
                    new Thread(() -> {
                        JOptionPane.showOptionDialog(Main.DJF.getDummyJFrame(), "ΠΑΡAΚΑΛΩ ΠΕΡΙΜΕΝΕΤΕ...", "LOADING", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                    }).start();

                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpGet HTTP_GET = new HttpGet(down_URL);

                    if (!FILE_OPERATIONS.get_PROXY_ADDRESS().equals("") && !FILE_OPERATIONS.get_PROXY_PORT().equals("")) {
                        HttpHost proxy = new HttpHost(FILE_OPERATIONS.get_PROXY_ADDRESS(), Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()), "http");
                        RequestConfig REQUEST = RequestConfig.custom().setProxy(proxy).build();
                        HTTP_GET.setConfig(REQUEST);
                    }

                    HttpResponse response = httpClient.execute(HTTP_GET);
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FILE));
                        BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                        int inByte;
                        while ((inByte = bis.read()) != -1) {
                            bos.write(inByte);
                        }
                        bis.close();
                        bos.close();
                    }
                    httpClient.close();

                    Main.DJF.getDummyJFrame().dispose();
                } else {
                    new Thread(() -> {
                        JOptionPane.showOptionDialog(Main.DJF.getDummyJFrame(), "ΤΟ ΑΡΧΕΙΟ ΕΙΝΑΙ ΗΔΗ ΑΝΟΙΧΤΟ", "ΠΡΟΕΙΔΟΠΟΙΗΣΗ", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                    }).start();
                    Thread.sleep(2000);
                    Main.DJF.getDummyJFrame().dispose();
                }

            } catch (IOException | HeadlessException | InterruptedException ex) {
                Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
            }
            String uri_filepath = parameters.get("fname").toString().substring(0, parameters.get("fname").toString().lastIndexOf(".")) + parameters.get("fname").toString().substring(parameters.get("fname").toString().lastIndexOf("."));
            String EXT = uri_filepath.substring(uri_filepath.lastIndexOf(".") + 1);
            if (EXT.equals("doc") || EXT.equals("docx") || EXT.equals("pdf") || EXT.equals("au") || EXT.equals("bmp") || EXT.equals("gif")
                    || EXT.equals("gz") || EXT.equals("gzip") || EXT.equals("jpg") || EXT.equals("jpeg") || EXT.equals("midi") || EXT.equals("mp3")
                    || EXT.equals("bz") || EXT.equals("bz2") || EXT.equals("ppa") || EXT.equals("ppt") || EXT.equals("pptx") || EXT.equals("text")
                    || EXT.equals("txt") || EXT.equals("wav") || EXT.equals("xl") || EXT.equals("xla") || EXT.equals("xlb") || EXT.equals("xlc")
                    || EXT.equals("xld") || EXT.equals("xlk") || EXT.equals("xll") || EXT.equals("xlm") || EXT.equals("xls") || EXT.equals("xlv")
                    || EXT.equals("xlw") || EXT.equals("zip") || EXT.equals("rar") || EXT.equals("odt") || EXT.equals("ods")) {

                try {
                    Path path = FileSystems.getDefault().getPath(FILE.getParentFile().getCanonicalPath());

                    Desktop.getDesktop().open(FILE);
                    if (!RUNNING_EDIT_THREADS.containsKey(parameters.get("app_doc_uid").toString())) {
                        RUNNING_EDIT_THREADS.put(parameters.get("app_doc_uid").toString(), Thread.currentThread());
                    }
                    if (!RUNNING_EDIT_PROCESSES.containsKey(parameters.get("app_doc_uid").toString())) {
                        RUNNING_EDIT_PROCESSES.put(parameters.get("app_doc_uid").toString(), 1);
                    }
                    if (!RUNNING_EDIT_FILES.containsKey(parameters.get("app_doc_uid").toString())) {
                        RUNNING_EDIT_FILES.put(parameters.get("app_doc_uid").toString(), FILE);
                    }
                    Thread.sleep(FILE_OPERATIONS.get_CHECK_EDIT_THREAD_TIMER());

                    watcher = FileSystems.getDefault().newWatchService();
                    path.register(watcher, ENTRY_MODIFY);
                    System.out.println("Watch Service registered for dir: " + path.getFileName());
                    while (true) {
                        Key = watcher.take();
                        Kind<?> kind = null;
                        for (WatchEvent<?> event : Key.pollEvents()) {
                            kind = event.kind();

                            WatchEvent<?> ev = event;
                            Path file_name = (Path) ev.context();
                            if (kind == ENTRY_MODIFY && file_name.toString().equals(FILE.getName())) {
                                System.out.println("ΤΟ ΑΡΧΕΙΟ ΕΧΕΙ ΤΡΟΠΟΠΟΙΗΘΕΙ");
                                int option = JOptionPane.showConfirmDialog(Main.DJF.getDummyJFrame(), "Έχετε ολοκληρώσει τις αλλαγές για να ανεβάσετε το αρχείο;", "ΕΙΔΟΠΟΙΗΣΗ", JOptionPane.YES_NO_OPTION);
                                if (option == JOptionPane.YES_OPTION) {

                                    /*
                                    String fname_copy = parameters.get("fname").toString();

                                    File COPY_FILE = new File(FILE_OPERATIONS.get_FILE_DIR() + fname_copy);

                                    FileUtils.copyFile(FILE, COPY_FILE);
                                     */
                                    Main.DJF.getDummyJFrame().dispose();
                                    System.out.println("ΑΠΟΣΤΟΛΗ ΑΡΧΕΙΟΥ");

                                    CloseableHttpClient httpClient = null;
                                    try {

                                        System.out.println("\n" + parameters.get("restpostfile").toString());

                                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                                        builder.setCharset(Charset.forName("UTF-8"));
                                        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                                        builder.addTextBody("app_uid", parameters.get("app_uid").toString(), ContentType.TEXT_PLAIN);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.addTextBody("app_doc_uid", parameters.get("app_doc_uid").toString(), ContentType.TEXT_PLAIN);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.addTextBody("tas_uid", parameters.get("tas_uid").toString(), ContentType.TEXT_PLAIN);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.addTextBody("app_doc_comment", parameters.get("app_doc_comment").toString(), ContentType.TEXT_PLAIN);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.addTextBody("userUid", parameters.get("userUid").toString(), ContentType.TEXT_PLAIN);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.addTextBody("inp_doc_uid", parameters.get("inp_doc_uid").toString(), ContentType.TEXT_PLAIN);
                                        builder.setBoundary("----Content Boundary----");
                                        builder.addBinaryBody("form", FILE, ContentType.create("application/pdf"), FILE.getName());
                                        builder.setBoundary("----Content Boundary----");

                                        httpClient = HttpClients.createDefault();
                                        HttpEntity entity = builder.build();

                                        HttpUriRequest request;
                                        if (!FILE_OPERATIONS.get_PROXY_ADDRESS().equals("") && !FILE_OPERATIONS.get_PROXY_PORT().equals("")) {
                                            HttpHost proxy = new HttpHost(FILE_OPERATIONS.get_PROXY_ADDRESS(), Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()), "http");
                                            RequestConfig REQUEST = RequestConfig.custom().setProxy(proxy).build();

                                            request = RequestBuilder
                                                    .post(parameters.get("restpostfile").toString())
                                                    .setConfig(REQUEST)
                                                    .addHeader("Authorization", "Bearer " + parameters.get("access_token").toString())
                                                    .setEntity(entity)
                                                    .build();
                                        } else {
                                            request = RequestBuilder
                                                    .post(parameters.get("restpostfile").toString())
                                                    .addHeader("Authorization", "Bearer " + parameters.get("access_token").toString())
                                                    .setEntity(entity)
                                                    .build();
                                        }

                                        File TEMP_FILE = FILE;

                                        ResponseHandler<String> responseHandler = response
                                                -> {
                                            int status = response.getStatusLine().getStatusCode();
                                            if (status >= 200 && status < 300) {
                                                HttpEntity Entity = response.getEntity();
                                                JOptionPane.showMessageDialog(Main.DJF.getDummyJFrame(), "Το αρχείο εστάλη με επιτυχία. Η εφαρμογή σας μπορεί να τερματιστεί.",
                                                        "ΕΠΙΤΥΧΙΑ", JOptionPane.INFORMATION_MESSAGE);
                                                Main.DJF.getDummyJFrame().dispose();
                                                watcher.close();
                                                Files.delete(hid_file_edit);
                                                while (!TEMP_FILE.delete()) {
                                                }
                                                RUNNING_EDIT_THREADS.remove(parameters.get("app_doc_uid").toString());
                                                RUNNING_EDIT_PROCESSES.remove(parameters.get("app_doc_uid").toString());
                                                RUNNING_EDIT_FILES.remove(parameters.get("app_doc_uid").toString());

                                                return Entity != null ? EntityUtils.toString(Entity) : null;
                                            } else {
                                                throw new ClientProtocolException("Unexpected response status: " + status);
                                            }
                                        };

                                        String responseBody = httpClient.execute(request, responseHandler);
                                        System.out.println("----------------------------------------");
                                        System.out.println(responseBody);
                                        httpClient.close();

                                    } catch (FileNotFoundException ex) {
                                        Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
                                    } catch (ClientProtocolException e) {
                                        Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, e);
                                    } catch (IOException exception) {
                                        Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, exception);
                                    } finally {
                                        try {
                                            httpClient.close();
                                        } catch (IOException ex) {
                                            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
                                        }
                                    }
                                    break;
                                } else {
                                    Main.DJF.getDummyJFrame().dispose();
                                    break;
                                }
                            }
                        }
                        boolean valid = Key.reset();
                        if (!valid) {
                            break;
                        }
                    }
                    watcher.close();
                } catch (IOException | InterruptedException | UnsupportedOperationException ex) {
                    Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
                    if (exit_status == true) {
                        Key.reset();
                    }
                    watcher.close();
                    //he.sendResponseHeaders(204, -1);
                }
            } else {
                JOptionPane.showMessageDialog(Main.DJF.getDummyJFrame(), "Η ΕΠΕΚΤΑΣΗ ΤΟΥ ΑΡΧΕΙΟΥ ΔΕΝ ΥΠΟΣΤΗΡΙΖΕΤΑΙ", "EXTENSION ERROR", JOptionPane.ERROR_MESSAGE);
                Main.DJF.getDummyJFrame().dispose();
            }
            //he.sendResponseHeaders(204, -1);

        }
    }

    public static class SignHandler implements HttpHandler {

        private StringBuilder buf;

        @Override

        public void handle(HttpExchange he) throws IOException {
            // parse request
            Map<String, Object> parameters = new HashMap<>();

            if (he.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);

                int b;
                buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                br.close();
                isr.close();
                parseQuery(buf.toString(), parameters);
            } else {
                URI requestedUri = he.getRequestURI();
                String query = requestedUri.getRawQuery();
                parseQuery(query, parameters);
            }

            File FILE = null;

            JFrame dummyJFrame = new JFrame();
            dummyJFrame.setVisible(false);
            dummyJFrame.setAlwaysOnTop(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            dummyJFrame.setLocation(dim.width / 2 - dummyJFrame.getSize().width / 2, dim.height / 2 - dummyJFrame.getSize().height / 2);

            String STR = FILE_OPERATIONS.get_FILE_DIR().replace("/", "\\");

            String version;
            if (parameters.containsKey("v")) {
                version = parameters.get("v").toString();
            } else {
                version = "0";
            }

            Path hid_file_edit = Paths.get(STR + "checks\\" + "edit_" + parameters.get("fname").toString() + "_" + parameters.get("app_doc_uid").toString() + "_" + version + ".check");
            Path hid_file_view = Paths.get(STR + "checks\\" + "view_" + parameters.get("fname").toString() + "_" + parameters.get("app_doc_uid").toString() + "_" + version + ".check");

            boolean from_view = false;
            if (Files.exists(hid_file_edit)) {
                if (RUNNING_EDIT_PROCESSES.get(parameters.get("app_doc_uid").toString()) == 1) {
                    JOptionPane.showMessageDialog(dummyJFrame, "ΠΑΡΑΚΑΛΩ ΟΛΟΚΛΗΡΩΣΤΕ ΠΡΩΤΑ ΤΗΝ ΠΡΟΗΓΟΥΜΕΝΗ ΕΡΓΑΣΙΑ EΠΕΞΕΡΓΑΣΙΑΣ", "INFO", JOptionPane.ERROR_MESSAGE);
                    RUNNING_EDIT_PROCESSES.replace(parameters.get("app_doc_uid").toString(), 2);
                    he.sendResponseHeaders(204, -1);
                } else if (RUNNING_EDIT_PROCESSES.get(parameters.get("app_doc_uid").toString()) == 2) {
                    RUNNING_EDIT_THREADS.get(parameters.get("app_doc_uid").toString()).interrupt();
                    RUNNING_EDIT_THREADS.remove(parameters.get("app_doc_uid").toString());
                    RUNNING_EDIT_PROCESSES.remove(parameters.get("app_doc_uid").toString());
                    RUNNING_EDIT_FILES.remove(parameters.get("app_doc_uid").toString());
                    Files.delete(hid_file_edit);
                }
            } else if (Files.exists(hid_file_view)) {
                Files.delete(hid_file_view);
                from_view = true;
            }

            String fname = parameters.get("fname").toString().substring(0, parameters.get("fname").toString().lastIndexOf(".")) + "_v" + version + parameters.get("fname").toString().substring(parameters.get("fname").toString().lastIndexOf("."));
            FILE = new File(FILE_OPERATIONS.get_FILE_DIR() + fname);

            new SIGN_GUI(he, FILE, parameters, from_view).setVisible(true);
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
                        @SuppressWarnings("unchecked")
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<>();
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

    public static void send_files(Map<String, Object> parameters, File FILE) {
        CloseableHttpClient httpClient = null;
        try {

            System.out.println("\n" + parameters.get("restpostfile").toString());

            MultipartEntityBuilder MEbuilder = MultipartEntityBuilder.create();
            MEbuilder.setCharset(Charset.forName("UTF-8"));
            MEbuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            MEbuilder.addTextBody("app_uid", parameters.get("app_uid").toString(), ContentType.TEXT_PLAIN);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("tas_uid", parameters.get("tas_uid").toString(), ContentType.TEXT_PLAIN);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("app_doc_uid", parameters.get("app_doc_uid").toString(), ContentType.TEXT_PLAIN);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("app_doc_comment", URLEncoder.encode(parameters.get("app_doc_comment").toString(),"UTF-8"), ContentType.TEXT_PLAIN);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("userUid", parameters.get("userUid").toString(), ContentType.TEXT_PLAIN);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("inp_doc_uid", parameters.get("inp_doc_uid").toString(), ContentType.TEXT_PLAIN);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addBinaryBody("form", FILE, ContentType.create("application/pdf"), FILE.getName());
            MEbuilder.setBoundary("----Content Boundary----");

            httpClient = HttpClients.createDefault();
            HttpEntity entity = MEbuilder.build();

            HttpUriRequest request;
            if (!FILE_OPERATIONS.get_PROXY_ADDRESS().equals("") && !FILE_OPERATIONS.get_PROXY_PORT().equals("")) {
                HttpHost proxy = new HttpHost(FILE_OPERATIONS.get_PROXY_ADDRESS(), Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()), "http");
                RequestConfig REQUEST = RequestConfig.custom().setProxy(proxy).build();

                request = RequestBuilder
                        .post(parameters.get("restpostfile").toString())
                        .setConfig(REQUEST)
                        .addHeader("Authorization", "Bearer " + parameters.get("access_token").toString())
                        .setEntity(entity)
                        .build();
            } else {
                request = RequestBuilder
                        .post(parameters.get("restpostfile").toString())
                        .addHeader("Authorization", "Bearer " + parameters.get("access_token").toString())
                        .setEntity(entity)
                        .build();
            }

            ResponseHandler<String> responseHandler = response
                    -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity Entity = response.getEntity();
                    return Entity != null ? EntityUtils.toString(Entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };

            String responseBody = httpClient.execute(request, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            httpClient.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
        } catch (ClientProtocolException e) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, e);
        } catch (IOException exception) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, exception);
        } finally {
            try {
                httpClient.close();
            } catch (IOException ex) {
                Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public static void create_case(Map<String, Object> parameters) {

        CloseableHttpClient httpClient;
        try {

            MultipartEntityBuilder MEbuilder = MultipartEntityBuilder.create();
            MEbuilder.setCharset(Charset.forName("UTF-8"));
            MEbuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            MEbuilder.addTextBody("pro_uid", parameters.get("pro_uid").toString(), ContentType.DEFAULT_TEXT);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("tas_uid", parameters.get("tas_uid").toString(), ContentType.DEFAULT_TEXT);
            MEbuilder.setBoundary("----Content Boundary----");
            MEbuilder.addTextBody("usr_uid", parameters.get("userUid").toString(), ContentType.DEFAULT_TEXT);
            MEbuilder.setBoundary("----Content Boundary----");

            httpClient = HttpClients.createDefault();
            HttpEntity entity = MEbuilder.build();

            HttpUriRequest request;
            if (!FILE_OPERATIONS.get_PROXY_ADDRESS().equals("") && !FILE_OPERATIONS.get_PROXY_PORT().equals("")) {
                HttpHost proxy = new HttpHost(FILE_OPERATIONS.get_PROXY_ADDRESS(), Integer.parseInt(FILE_OPERATIONS.get_PROXY_PORT()), "http");
                RequestConfig REQUEST = RequestConfig.custom().setProxy(proxy).build();

                request = RequestBuilder
                        .post(parameters.get("restimpersonate").toString())
                        .setConfig(REQUEST)
                        .addHeader("Authorization", "Bearer " + parameters.get("access_token").toString())
                        .setEntity(entity)
                        .build();
            } else {
                request = RequestBuilder
                        .post(parameters.get("restimpersonate").toString())
                        .addHeader("Authorization", "Bearer " + parameters.get("access_token").toString())
                        .setEntity(entity)
                        .build();
            }

            ResponseHandler<String> responseHandler = response
                    -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity Entity = response.getEntity();
                    return Entity != null ? EntityUtils.toString(Entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };

            String responseBody = httpClient.execute(request, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);

            JsonElement JSON_E = new JsonParser().parse(responseBody);
            System.out.println("APP_UID: " + parameters.get("app_uid"));
            JsonObject JOB = JSON_E.getAsJsonObject();
            String app_uid_new = JOB.get("app_uid").toString();
            app_uid_new = app_uid_new.substring(1,app_uid_new.length()-1);
            String app_uid_old = parameters.get("app_uid").toString();
            parameters.put("app_uid", app_uid_new);
            
            String rest_post_url = parameters.get("restpostfile").toString();
            rest_post_url = rest_post_url.replace(app_uid_old, app_uid_new);
            parameters.put("restpostfile",rest_post_url);
            
            System.out.println("APP_UID_NEW: " + parameters.get("app_uid"));
            httpClient.close();
        } catch (IOException ex) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
