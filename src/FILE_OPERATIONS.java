
/*
 * Antonis Kosmidis Copyright (c) 2016.  ALL RIGHTS RESERVED
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Wini;

public final class FILE_OPERATIONS {

    private static File file_out_config, file_out_FILES, file_out_SCANNED, file_out_CHECKS;
    private static String S, S_2;
    protected static String INI_VERSION = "V3";

    protected static void FILE_OPERATIONS_INIT() {
        try {
            S = new File(".").getCanonicalPath();
            S_2 = S;
            S = S.substring(0, S.lastIndexOf("\\"));
            File config_dir = new File("config");
            config_dir.mkdir();
            file_out_config = new File("config/config.ini");
            file_out_config.createNewFile();
            if (file_out_config.length() <= 0 && !file_out_config.isDirectory()) {
                create_INI();
            }
            file_out_FILES = new File(get_FILE_DIR());
            file_out_SCANNED = new File(get_FILE_DIR() + "scanned");
            file_out_CHECKS = new File(get_FILE_DIR() + "checks");
            file_out_FILES.mkdirs();
            file_out_SCANNED.mkdirs();
            file_out_CHECKS.mkdirs();
            File parent = file_out_config.getParentFile();
            parent.mkdirs();
        } catch (IOException ex) {
            Logger.getLogger(FILE_OPERATIONS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static void update_config() throws IOException {
        file_out_config.delete();
        file_out_config.createNewFile();
        create_INI();
    }

    public static String get_UP_PATH() {
        String UP_PATH = S;
        UP_PATH = UP_PATH.replace("\\", "/");
        return UP_PATH + "/BPM_FILES/";
    }

    public static String get_RUNNING_PATH() {
        String RUNNING_PATH = S_2;
        RUNNING_PATH = RUNNING_PATH.replace("\\", "/");
        return RUNNING_PATH + "/";
    }

    public static int get_PORT() throws IOException {
        int port;
        Wini ini = new Wini(file_out_config);
        port = ini.get("BASIC_CONFIG", "port", int.class);

        return port;
    }

    public static int get_DAYS_BEF_DEL() throws IOException {
        int d;
        Wini ini = new Wini(file_out_config);
        d = ini.get("BASIC_CONFIG", "MILLISECONDS_FOR_DELETION", int.class);
        return d;
    }

    public static String get_LIB_OFFICE_WIN_x64() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("LIBREOFFICE CONFIG", "WIN X64 DIR", String.class);
    }

    public static String get_LIB_OFFICE_WIN_x32() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("LIBREOFFICE CONFIG", "WIN X32 DIR", String.class);
    }

    public static String get_LIB_OFFICE_LINUX() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("LIBREOFFICE CONFIG", "LINUX DIR", String.class);
    }

    public static int get_CHECK_VIEW_THREAD_TIMER() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("CHECK THREADS", "CHECK_VIEW_THREAD_TIMER", int.class);
    }

    public static int get_CHECK_EDIT_THREAD_TIMER() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("CHECK THREADS", "CHECK_EDIT_THREAD_TIMER", int.class);
    }

    public static String get_FILE_DIR() throws IOException {
        Wini ini = new Wini(file_out_config);
        String DIR = ini.get("OPTIONAL", "FILE DIRECTORY", String.class);
        if (DIR.endsWith("/")) {
            return DIR;
        } else {
            return DIR + "/";
        }
    }

    public static String get_FIRST_RUN() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("MISC", "FIRST_RUN", String.class);
    }

    public static void set_FIRST_RUN_FALSE() throws IOException {
        Wini ini = new Wini(file_out_config);
        ini.remove("MISC", "FIRST_RUN");
        ini.put("MISC", "FIRST_RUN", "FALSE");
        ini.store();
    }

    public static String get_PROXY_ADDRESS() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("INTERNET_OPTIONS", "PROXY ADDRESS", String.class);
    }

    public static void set_PROXY_ADDRESS(String address) throws IOException {
        Wini ini = new Wini(file_out_config);
        ini.remove("INTERNET_OPTIONS", "PROXY ADDRESS");
        ini.put("INTERNET_OPTIONS", "PROXY ADDRESS", address);
        ini.store();
    }

    public static String get_PROXY_PORT() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("INTERNET_OPTIONS", "PROXY PORT", String.class);
    }

    public static void set_PROXY_PORT(String port) throws IOException {
        Wini ini = new Wini(file_out_config);
        ini.remove("INTERNET_OPTIONS", "PROXY PORT");
        ini.put("INTERNET_OPTIONS", "PROXY PORT", port);
        ini.store();
    }

    public static String get_NAPS_VERSION() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("MISC", "NAPS VERSION", String.class);
    }

    public static void set_NAPS_VERSION(String version) throws IOException {
        Wini ini = new Wini(file_out_config);
        ini.remove("MISC", "NAPS VERSION");
        ini.put("MISC", "NAPS VERSION", version);
        ini.store();
    }

    public static String get_INI_VERSION() throws IOException {
        Wini ini = new Wini(file_out_config);
        return ini.get("MISC", "INI VERSION", String.class);
    }

    public static void set_INI_VERSION(String ini_version) throws IOException {
        Wini ini = new Wini(file_out_config);
        ini.remove("MISC", "INI VERSION");
        ini.put("MISC", "INI VERSION", ini_version);
        ini.store();
    }

    private static void create_INI() throws IOException {
        Wini ini = new Wini(file_out_config);

        ini.put("BASIC_CONFIG", "port", 34300);
        ini.put("BASIC_CONFIG", "MILLISECONDS_FOR_DELETION", 30);
        ini.put("INTERNET_OPTIONS", "PROXY ADDRESS", "");
        ini.put("INTERNET_OPTIONS", "PROXY PORT", "");
        ini.put("LIBREOFFICE CONFIG", "WIN X64 DIR", "C:/Program Files/LibreOffice 5");
        ini.put("LIBREOFFICE CONFIG", "WIN X32 DIR", "C:/Program Files (x86)/LibreOffice 5");
        ini.put("LIBREOFFICE CONFIG", "LINUX DIR", "/usr/bin/soffice");
        ini.put("CHECK THREADS", "CHECK_VIEW_THREAD_TIMER", 6000);
        ini.put("CHECK THREADS", "CHECK_EDIT_THREAD_TIMER", 4000);
        ini.put("OPTIONAL", "FILE DIRECTORY", get_UP_PATH());
        ini.put("MISC", "FIRST_RUN", "TRUE");
        ini.put("MISC", "NAPS VERSION",Main.NAPS_LATEST);
        ini.put("MISC", "INI VERSION", INI_VERSION);
        ini.store();
    }
    
    public static String create_PKCS11_config(String conf) throws IOException
    {
        String path = "config/pkcs11.conf";
        try (BufferedWriter WRITER = new BufferedWriter(new FileWriter(path))) {
            WRITER.write(conf);
            WRITER.flush();
        }
        File pkcs11_conf = new File(path);
        return pkcs11_conf.getCanonicalPath();
    }
}
