
/*
 * Antonis Kosmidis Copyright (c) 2016.  ALL RIGHTS RESERVED
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

public class MASS_DELETE_FILES {

    private ArrayList<File> listFiles;

    public MASS_DELETE_FILES() {
        this.listFiles = new ArrayList<>();
    }

    public void deleteFilesOlderThanNdays(Long daysBack, String dirWay) throws IOException {
        System.out.println(dirWay);
        System.out.println(daysBack);

        final File directory = new File(dirWay);
        if (directory.exists()) {
            System.out.println("Directory Exists");

            for (File F : directory.listFiles()) {
                if (!F.isDirectory()) {
                    System.out.println(F);
                    listFiles.add(F);
                }

                if (dirWay.equals(FILE_OPERATIONS.get_FILE_DIR()) && F.isDirectory()) {
                    if (F.isDirectory() && !(F.getName().equals("scanned") || F.getName().equals("checks"))) {
                        FileUtils.deleteDirectory(F);
                    }
                }
            }

            long purgeTime = System.currentTimeMillis() - (daysBack * 10L);

            System.out.println("System.currentTimeMillis " + System.currentTimeMillis());

            System.out.println("purgeTime " + purgeTime);

            for (File listFile : listFiles) {
                System.out.println("Length : " + listFiles.size());
                System.out.println("listFile.getName() : " + listFile.getName());
                System.out.println("listFile.lastModified() :" + listFile.lastModified());

                if (listFile.lastModified() < purgeTime) {
                    try {
                        Files.deleteIfExists(listFile.toPath());
                        System.out.println("FILE DELETION");
                    } catch (FileSystemException ex) {
                        Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.SEVERE, null, ex);
                        HTTP_HANDLERS.continuation=true;
                        continue;
                    }
                }
            }
        } else {
        }

    }
}
