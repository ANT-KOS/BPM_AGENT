

/*
 * Antonis Kosmidis Copyright (c) 2016.  ALL RIGHTS RESERVED
 */
import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import jupar.Downloader;
import jupar.Updater;
import jupar.objects.Modes;
import jupar.objects.Release;
import jupar.parsers.ReleaseXMLParser;
import org.xml.sax.SAXException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    protected static int port;
    protected static String BPM_AGENT_VERSION = "4.4";
    protected static String BPM_AGENT_VERSION_R = "7";
    protected static String NAPS_LATEST = "5.8.2";
    protected static boolean INSTALL_NAPS = true;
    protected static String DATE_OF_CREATION = "18/03/2019";
    private static final int BUFFER_SIZE = 4096;
    private static MASS_DELETE_FILES MDF = new MASS_DELETE_FILES();
    protected static DUMMYJFRAME DJF;

    public static void update() {
        try {
            //VERSION OF PROGRAM - START
            Release current = new Release();
            current.setpkgver(BPM_AGENT_VERSION);
            current.setPkgrel(BPM_AGENT_VERSION_R);
            //VERSION OF PROGRAM - END
            
            String updateRepositoryUrl = "http://83.212.93.83/bpm_agent_update/";
            ReleaseXMLParser parser = new ReleaseXMLParser();
            Release update = parser.parse(updateRepositoryUrl + "latest.xml", Modes.URL);
            if (update.compareTo(current) > 0) {

                String temporaryDirectoryForUpdates = "tmp";
                File F = new File(".");
                F.getCanonicalPath();
                Updater updater = new Updater();
                Downloader downloader = new Downloader();

                if (INSTALL_NAPS == true) {
                    if (!FILE_OPERATIONS.get_NAPS_VERSION().equals(NAPS_LATEST)) {
                        FILE_OPERATIONS.set_NAPS_VERSION(NAPS_LATEST);
                    }
                    downloader.download(updateRepositoryUrl + "files.xml", temporaryDirectoryForUpdates, Modes.URL);
                    updater.update("update.xml", temporaryDirectoryForUpdates, F.getCanonicalPath(), Modes.FILE);
                } else if (INSTALL_NAPS == false) {
                    downloader.download(updateRepositoryUrl + "files_no_naps.xml", temporaryDirectoryForUpdates, Modes.URL);
                    updater.update("update_no_naps.xml", temporaryDirectoryForUpdates, F.getCanonicalPath(), Modes.FILE);
                }

                File tmp = new File(temporaryDirectoryForUpdates);
                if (tmp.exists()) {
                    for (File file : tmp.listFiles()) {
                        file.delete();
                    }
                    tmp.delete();
                }

                if (Files.exists(Paths.get(F.getCanonicalPath() + "/NAPS2.zip"))) {
                    String OUTPUT_FOLDER = F.getCanonicalPath() + "/NAPS2";
                    unzip(OUTPUT_FOLDER, F.getCanonicalPath() + "/NAPS2.zip");
                    Files.delete(Paths.get(F.getCanonicalPath() + "/NAPS2.zip"));
                }
                String STR_RUN = System.getProperty("java.home") + "/bin/java.exe -jar " + "\"" + FILE_OPERATIONS.get_RUNNING_PATH() + "BPM_AGENT.jar" + "\"";
                STR_RUN = STR_RUN.replace("\\", "/");
                Runtime.getRuntime().exec(STR_RUN);
                System.exit(0);
            }
        } catch (SAXException | IOException | InterruptedException | ParserConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void unzip(String path, String filepath) {
        try {
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(filepath));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String filePath = path + File.separator + ze.getName();
                if (!ze.isDirectory()) {
                    extractFile(zis, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.close();
        } catch (IOException ex) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, InstantiationException, IllegalAccessException {
        FILE_OPERATIONS FOP = new FILE_OPERATIONS();
        DJF = new DUMMYJFRAME();
        if (FILE_OPERATIONS.get_INI_VERSION() == null || !FILE_OPERATIONS.get_INI_VERSION().equals(FILE_OPERATIONS.INI_VERSION)) {
            FILE_OPERATIONS.update_config();
        }
        System.setProperty("file.encoding", "ISO-8859-7");
        Locale.setDefault(Locale.forLanguageTag("el-GR"));
        if (FILE_OPERATIONS.get_FIRST_RUN().equals("TRUE")) {
            FIRST_TIME_GUI.install_naps();
        }
        update();
        if (FILE_OPERATIONS.get_FIRST_RUN().equals("TRUE")) {
            FIRST_TIME_GUI.first_time_config();
        }
        MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR());
        MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR() + "scanned");
        MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR() + "checks");
        port = FILE_OPERATIONS.get_PORT();

        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("icon/icon.png"));

        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(bi, "BPM AGENT", popup);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("Περί");
        MenuItem deleteItem = new MenuItem("Καθαρισμός Φακέλων");
        MenuItem updateItem = new MenuItem("Έλεγχος για Αναβαθμίσεις");
        MenuItem restartItem = new MenuItem("Επανεκκίνηση");
        MenuItem exitItem = new MenuItem("Έξοδος");
        MenuItem installNaps2Item = new MenuItem("Εγκατάσταση/Αναβάθμιση NAPS2");
        MenuItem installLIBSItem = new MenuItem("Εγκατάσταση/Αναβάθμιση Βιβλιοθηκών Προγράμματος");

        updateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    //VERSION OF PROGRAM - START
                    Release current = new Release();
                    current.setpkgver(BPM_AGENT_VERSION);
                    current.setPkgrel(BPM_AGENT_VERSION_R);
                    //VERSION OF PROGRAM - END

                    String updateRepositoryUrl = "http://83.212.93.83/bpm_agent_update/";
                    ReleaseXMLParser parser = new ReleaseXMLParser();
                    Release update = parser.parse(updateRepositoryUrl + "latest.xml", Modes.URL);
                    if (update.compareTo(current) > 0) {
                        int option = JOptionPane.showConfirmDialog(DJF.getDummyJFrame(), "ΥΠΑΡΧΕΙ ΜΙΑ ΝΕΑ ΕΚΔΟΣΗ ΔΙΑΘΕΣΙΜΗ.\nΘΕΛΕΤΕ ΝΑ ΤΗΝ ΚΑΤΕΒΑΣΕΤΕ;", "UPDATE", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {

                            String temporaryDirectoryForUpdates = "tmp";
                            File F = new File(".");
                            F.getCanonicalPath();
                            Updater updater = new Updater();
                            Downloader downloader = new Downloader();

                            if (!FILE_OPERATIONS.get_NAPS_VERSION().equals(NAPS_LATEST)) {
                                FILE_OPERATIONS.set_NAPS_VERSION(NAPS_LATEST);
                                downloader.download(updateRepositoryUrl + "files.xml", temporaryDirectoryForUpdates, Modes.URL);
                                trayIcon.displayMessage("Το πρόγραμμα αναβαθμίζεται.", "Το πρόγραμμα θα ξεκινίσει αυτόματα\n"
                                        + "μόλις ολοκληρωθεί η διαδικασία αναβάθμισης.", TrayIcon.MessageType.INFO);
                                updater.update("update.xml", temporaryDirectoryForUpdates, F.getCanonicalPath(), Modes.FILE);
                            } else {
                                downloader.download(updateRepositoryUrl + "files_no_naps.xml", temporaryDirectoryForUpdates, Modes.URL);
                                trayIcon.displayMessage("Το πρόγραμμα αναβαθμίζεται.", "Το πρόγραμμα θα ξεκινίσει αυτόματα\n"
                                        + "μόλις ολοκληρωθεί η διαδικασία αναβάθμισης.", TrayIcon.MessageType.INFO);
                                updater.update("update_no_naps.xml", temporaryDirectoryForUpdates, F.getCanonicalPath(), Modes.FILE);
                            }

                            File tmp = new File(temporaryDirectoryForUpdates);
                            if (tmp.exists()) {
                                for (File file : tmp.listFiles()) {
                                    file.delete();
                                }
                                tmp.delete();
                            }

                            if (Files.exists(Paths.get(F.getCanonicalPath() + "/NAPS2.zip"))) {
                                String OUTPUT_FOLDER = F.getCanonicalPath() + "/NAPS2";
                                unzip(OUTPUT_FOLDER, F.getCanonicalPath() + "/NAPS2.zip");
                                Files.delete(Paths.get(F.getCanonicalPath() + "/NAPS2.zip"));
                            }
                            String STR_RUN = System.getProperty("java.home") + "/bin/java.exe -jar " + "\"" + FILE_OPERATIONS.get_RUNNING_PATH() + "BPM_AGENT.jar" + "\"";
                            STR_RUN = STR_RUN.replace("\\", "/");
                            Runtime.getRuntime().exec(STR_RUN);
                            System.exit(0);
                        }
                    } else {
                        JOptionPane.showMessageDialog(DJF.getDummyJFrame(), "ΕΧΕΤΕ ΤΗΝ ΤΕΛΕΥΤΑΙΑ ΕΚΔΟΣΗ", "UPDATE", JOptionPane.INFORMATION_MESSAGE);
                        DJF.getDummyJFrame().dispose();
                    }
                } catch (SAXException | IOException | InterruptedException | ParserConfigurationException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR());
                    MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR() + "scanned");
                    MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR() + "checks");
                    tray.remove(trayIcon);
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        installNaps2Item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {

                    String updateRepositoryUrl = "http://83.212.93.83/bpm_agent_update/";
                    Updater updater = new Updater();
                    Downloader downloader = new Downloader();
                    String temporaryDirectoryForUpdates = "tmp";
                    File F = new File(".");
                    F.getCanonicalPath();

                    downloader.download(updateRepositoryUrl + "files_only_naps.xml", temporaryDirectoryForUpdates, Modes.URL);
                    updater.update("update_only_naps.xml", temporaryDirectoryForUpdates, F.getCanonicalPath(), Modes.FILE);

                    File tmp = new File(temporaryDirectoryForUpdates);
                    if (tmp.exists()) {
                        for (File file : tmp.listFiles()) {
                            file.delete();
                        }
                        tmp.delete();
                    }

                    if (Files.exists(Paths.get(F.getCanonicalPath() + "/NAPS2.zip"))) {
                        String OUTPUT_FOLDER = F.getCanonicalPath() + "/NAPS2";
                        unzip(OUTPUT_FOLDER, F.getCanonicalPath() + "/NAPS2.zip");
                        Files.delete(Paths.get(F.getCanonicalPath() + "/NAPS2.zip"));
                    }

                    JOptionPane.showMessageDialog(DJF.getDummyJFrame(), "Η εγκατάσταση του NAPS2 ολοκληρώθηκε με επιτυχία", "ΕΠΙΤΥΧΙΑ", JOptionPane.INFORMATION_MESSAGE);
                    DJF.getDummyJFrame().dispose();
                } catch (SAXException | IOException | InterruptedException | ParserConfigurationException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        installLIBSItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {

                    String updateRepositoryUrl = "http://83.212.93.83/bpm_agent_update/";
                    Updater updater = new Updater();
                    Downloader downloader = new Downloader();
                    String temporaryDirectoryForUpdates = "tmp";
                    File F = new File(".");
                    F.getCanonicalPath();

                    downloader.download(updateRepositoryUrl + "files_no_naps_no_bin.xml", temporaryDirectoryForUpdates, Modes.URL);
                    updater.update("update_no_naps_no_bin.xml", temporaryDirectoryForUpdates, F.getCanonicalPath(), Modes.FILE);

                    File tmp = new File(temporaryDirectoryForUpdates);
                    if (tmp.exists()) {
                        for (File file : tmp.listFiles()) {
                            file.delete();
                        }
                        tmp.delete();
                    }

                    JOptionPane.showMessageDialog(DJF.getDummyJFrame(), "Η εγκατάσταση των βιβλιοθηκών ολοκληρώθηκε με επιτυχία", "ΕΠΙΤΥΧΙΑ", JOptionPane.INFORMATION_MESSAGE);
                    DJF.getDummyJFrame().dispose();
                } catch (SAXException | IOException | InterruptedException | ParserConfigurationException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        restartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String STR_RUN = System.getProperty("java.home") + "/bin/java.exe -jar " + "\"" + FILE_OPERATIONS.get_RUNNING_PATH() + "BPM_AGENT.jar" + "\"";
                    STR_RUN = STR_RUN.replace("\\", "/");
                    Runtime.getRuntime().exec(STR_RUN);
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    HTTP_HANDLERS.exit_status = true;
                    HTTP_HANDLERS.RUNNING_EDIT_THREADS.values().forEach((TH) -> {
                        TH.interrupt();
                    });
                    HTTP_HANDLERS.RUNNING_EDIT_THREADS.clear();
                    HTTP_HANDLERS.RUNNING_EDIT_PROCESSES.clear();
                    HTTP_HANDLERS.RUNNING_EDIT_FILES.clear();
                    HTTP_HANDLERS.exit_status = false;

                    MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR());
                    MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR() + "scanned");
                    MDF.deleteFilesOlderThanNdays(Long.valueOf(FILE_OPERATIONS.get_DAYS_BEF_DEL()), FILE_OPERATIONS.get_FILE_DIR() + "checks");
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "BPM AGENT v" + BPM_AGENT_VERSION + "." + BPM_AGENT_VERSION_R + "\nMADE BY:\nANTONIS KOSMIDIS \n"
                        + "© 2016-2019", "ABOUT", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.add(deleteItem);
        popup.addSeparator();
        popup.add(updateItem);
        popup.add(installNaps2Item);
        popup.add(installLIBSItem);
        popup.add(restartItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

        //BPM AGENT BEGIN SERVER
        HTTP_SERVER JWS = new HTTP_SERVER();
        JWS.Start(port);

    }

}
