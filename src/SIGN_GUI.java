
import com.sun.net.httpserver.HttpExchange;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
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

public class SIGN_GUI extends javax.swing.JFrame {

    /**
     * Creates new form SIGN_GUI
     */
    private File FILE;
    private HttpExchange he;
    private Map<String, Object> parameters;
    private boolean from_view;
    private boolean exit = false;

    public SIGN_GUI(HttpExchange he, File FILE, Map<String, Object> parameters, boolean from_view) throws IOException {
        this.FILE = FILE;
        this.parameters = parameters;
        this.he = he;
        this.from_view = from_view;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SIGN_GUI_PANEL = new javax.swing.JPanel();
        KEYSTORE_PASSWORD_LABEL = new javax.swing.JLabel();
        KEYSTORE_PASSWORD = new javax.swing.JPasswordField();
        REASON_LABEL = new javax.swing.JLabel();
        REASON = new javax.swing.JTextField();
        LOCATION_LABEL = new javax.swing.JLabel();
        LOCATION = new javax.swing.JTextField();
        CONTACT_LABEL = new javax.swing.JLabel();
        CONTACT = new javax.swing.JTextField();
        OK = new javax.swing.JButton();
        CANCEL = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ΣΤΟΙΧΕΙΑ ΥΠΟΓΡΑΦΗΣ");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setIconImages(null);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        setLocationRelativeTo(null);

        if(parameters.get("contact") != null)
        {
            CONTACT.setText(parameters.get("contact").toString());
        }
        if(parameters.get("location") != null)
        {
            LOCATION.setText(parameters.get("location").toString());
        }
        if(parameters.get("reason") != null)
        {
            REASON.setText(parameters.get("reason").toString());
        }
        SIGN_GUI_PANEL.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SIGN_GUI_PANEL.setForeground(new java.awt.Color(102, 102, 102));
        SIGN_GUI_PANEL.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        KEYSTORE_PASSWORD_LABEL.setText("Κωδικός Υπογραφής (Password):");

        KEYSTORE_PASSWORD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KEYSTORE_PASSWORDKeyPressed(evt);
            }
        });

        REASON_LABEL.setText("Αιτιολόγιση (Reason):");

        REASON.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                REASONKeyPressed(evt);
            }
        });

        LOCATION_LABEL.setText("Τοποθεσία (Location):");

        LOCATION.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LOCATIONKeyPressed(evt);
            }
        });

        CONTACT_LABEL.setText("Επικοινωνία (Contact):");

        CONTACT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CONTACTKeyPressed(evt);
            }
        });

        OK.setText("OK");
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });

        CANCEL.setText("ΑΚΥΡΟ");
        CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CANCELActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SIGN_GUI_PANELLayout = new javax.swing.GroupLayout(SIGN_GUI_PANEL);
        SIGN_GUI_PANEL.setLayout(SIGN_GUI_PANELLayout);
        SIGN_GUI_PANELLayout.setHorizontalGroup(
            SIGN_GUI_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SIGN_GUI_PANELLayout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(OK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(CANCEL, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
            .addGroup(SIGN_GUI_PANELLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(SIGN_GUI_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CONTACT_LABEL)
                    .addComponent(LOCATION_LABEL)
                    .addComponent(REASON_LABEL)
                    .addComponent(KEYSTORE_PASSWORD_LABEL)
                    .addGroup(SIGN_GUI_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(CONTACT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(LOCATION, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(REASON, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(KEYSTORE_PASSWORD, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SIGN_GUI_PANELLayout.setVerticalGroup(
            SIGN_GUI_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SIGN_GUI_PANELLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(KEYSTORE_PASSWORD_LABEL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(KEYSTORE_PASSWORD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(REASON_LABEL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(REASON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LOCATION_LABEL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LOCATION, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CONTACT_LABEL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CONTACT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(SIGN_GUI_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CANCEL, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(OK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SIGN_GUI_PANEL, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SIGN_GUI_PANEL, javax.swing.GroupLayout.PREFERRED_SIZE, 296, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        try {

            SIGN_PDF spdf = new SIGN_PDF();
            String FILE_COPY_PATH = FILE.getAbsolutePath().substring(0, FILE.getAbsolutePath().lastIndexOf("\\")) + "\\"
                    + FILE.getName().substring(0, FILE.getName().lastIndexOf("_")) + ".pdf";
            int signers = 4, page = 1;

            if (parameters.containsKey("signers")) {
                if (parameters.get("signers") != null) {
                    signers = Integer.parseInt(parameters.get("signers").toString());
                }
            }

            if (parameters.containsKey("page")) {
                if (parameters.get("page") != null) {
                    page = Integer.parseInt(parameters.get("page").toString());
                }
            }

            String reason = null;
            if (parameters.containsKey("reason")) {
                if (parameters.get("reason") != null && REASON.getText().equals("")) {
                    reason = parameters.get("reason").toString();
                } else {
                    reason = REASON.getText();
                }
            } else {
                reason = REASON.getText();
            }

            String location = null;
            if (parameters.containsKey("location")) {
                if (parameters.get("location") != null && LOCATION.getText().equals("")) {
                    location = parameters.get("location").toString();
                } else {
                    location = LOCATION.getText();
                }
            } else {
                location = LOCATION.getText();
            }

            String contact = null;
            if (parameters.containsKey("contact")) {
                if (parameters.get("contact") != null && CONTACT.getText().equals("")) {
                    contact = parameters.get("contact").toString();
                } else {
                    contact = CONTACT.getText();
                }
            } else {
                contact = CONTACT.getText();
            }

            String down_URL = parameters.get("restgetfile").toString() + "?access_token=" + parameters.get("access_token").toString() + "&v=" + parameters.get("v").toString();
            System.out.println(down_URL);
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showOptionDialog(Main.DJF.getDummyJFrame(), "ΠΑΡΚΑΛΩ ΠΕΡΙΜΕΝΕΤΕ...", "LOADING", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
                }
            }).start();

            if (FILE.delete() || !FILE.exists()) {
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
            } else {
                if (from_view == false) {
                    Main.DJF.getDummyJFrame().dispose();
                    JOptionPane.showMessageDialog(Main.DJF.getDummyJFrame(), "ΤΟ ΑΡΧΕΙΟ ΕΙΝΑΙ ΗΔΗ ΑΝΟΙΧΤΟ", "ΣΦΑΛΜΑ", JOptionPane.INFORMATION_MESSAGE);
                    Main.DJF.getDummyJFrame().dispose();
                }
            }

            spdf.init_VARIABLES(FILE.getCanonicalPath(), FILE_COPY_PATH, KEYSTORE_PASSWORD.getPassword(), reason, location, contact, signers, page, parameters.get("userUid").toString());

            if (parameters.containsKey("X")) {
                if (parameters.get("X") != null) {
                    spdf.init_X(Integer.parseInt(parameters.get("X").toString()));
                }
            }

            if (parameters.containsKey("Y")) {
                if (parameters.get("Y") != null) {
                    spdf.init_Y(Integer.parseInt(parameters.get("Y").toString()));
                }
            }

            int result = spdf.Sign_Pdf_W_USB();
            switch (result) {
                case 0:
                    System.out.println("ΑΠΟΣΤΟΛΗ ΑΡΧΕΙΟΥ");
                    CloseableHttpClient httpClient = null;
                    try {
                        File FILE_TO_SEND = new File(FILE_COPY_PATH);
                        System.out.println("\n" + parameters.get("restpostfile").toString());

                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setCharset(Charset.forName("UTF-8"));
                        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
                        builder.setBoundary("----Content Boundary----");
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                        builder.addTextBody("app_uid", parameters.get("app_uid").toString(), ContentType.TEXT_PLAIN);
                        System.out.println("app_uid: " + parameters.get("app_uid").toString());
                        builder.setBoundary("----Content Boundary----");
                        builder.addTextBody("app_doc_uid", parameters.get("app_doc_uid").toString(), ContentType.TEXT_PLAIN);
                        System.out.println("app_doc_uid: " + parameters.get("app_doc_uid").toString());
                        builder.setBoundary("----Content Boundary----");
                        builder.addTextBody("tas_uid", parameters.get("tas_uid").toString(), ContentType.TEXT_PLAIN);
                        System.out.println("tas_uid: " + parameters.get("tas_uid").toString());
                        System.out.println("COMMENT_CONTENT: " + parameters.get("app_doc_comment"));
                        if (parameters.get("app_doc_comment") != null) {
                            if (!parameters.get("app_doc_comment").toString().equals("")) {
                                builder.setBoundary("----Content Boundary----");
                                builder.addTextBody("app_doc_comment", parameters.get("app_doc_comment").toString(), ContentType.TEXT_PLAIN);
                                System.out.println("app_doc_comment: " + parameters.get("app_doc_comment").toString());
                            }
                        }
                        builder.setBoundary("----Content Boundary----");
                        builder.addTextBody("userUid", parameters.get("userUid").toString(), ContentType.TEXT_PLAIN);
                        System.out.println("userUid: " + parameters.get("userUid").toString());
                        builder.setBoundary("----Content Boundary----");
                        builder.addTextBody("inp_doc_uid", parameters.get("inp_doc_uid").toString(), ContentType.TEXT_PLAIN);
                        System.out.println("inp_doc_uid: " + parameters.get("inp_doc_uid").toString());
                        builder.setBoundary("----Content Boundary----");
                        builder.addBinaryBody("form", FILE_TO_SEND, ContentType.create("application/pdf"), FILE_TO_SEND.getName());
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

                        ResponseHandler<String> responseHandler = response
                                -> {
                            int status = response.getStatusLine().getStatusCode();
                            if (status >= 200 && status < 300) {
                                HttpEntity Entity = response.getEntity();
                                Main.DJF.getDummyJFrame().dispose();
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(SIGN_GUI.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                JOptionPane.showMessageDialog(Main.DJF.getDummyJFrame(), "Το έγγραφο υπογράφηκε ψηφιακά.",
                                        "ΕΠΙΤΥΧΙΑ", JOptionPane.INFORMATION_MESSAGE);
                                Main.DJF.getDummyJFrame().dispose();
                                while (!FILE.delete()) {
                                }
                                while (!FILE_TO_SEND.delete()) {
                                }
                                return Entity != null ? EntityUtils.toString(Entity) : null;
                            } else {
                                Main.DJF.getDummyJFrame().dispose();
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
                    }

                    exit = true;
                    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "Δώσατε λάθος κωδικό πρόσβασης.", "ΣΦΑΛΜΑ", JOptionPane.ERROR_MESSAGE);
                    KEYSTORE_PASSWORD.setText("");
                    break;
                case 2:
                    Main.DJF.getDummyJFrame().dispose();
                    JOptionPane.showMessageDialog(null, "Έχετε ήδη υπογράψει το αρχείο.\n", "ΣΦΑΛΜΑ", JOptionPane.ERROR_MESSAGE);
                    FILE.delete();
                    new File(FILE_COPY_PATH).delete();
                    exit = true;
                    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "Δεν έχετε τοποθετήσει στον Η/Υ σας το USB TOKEN.", "ΣΦΑΛΜΑ", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    break;
            }
        } catch (IOException | GeneralSecurityException ex) {
            Logger.getLogger(SIGN_GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SIGN_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_OKActionPerformed

    private void CANCELActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CANCELActionPerformed
        exit = true;
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_CANCELActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            if (exit == true) {
                FILE.delete();
                exit = false;
                he.sendResponseHeaders(204, -1);
                he.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(SIGN_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void KEYSTORE_PASSWORDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KEYSTORE_PASSWORDKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            OK.doClick();
        }
    }//GEN-LAST:event_KEYSTORE_PASSWORDKeyPressed

    private void REASONKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_REASONKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            OK.doClick();
        }
    }//GEN-LAST:event_REASONKeyPressed

    private void LOCATIONKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LOCATIONKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            OK.doClick();
        }
    }//GEN-LAST:event_LOCATIONKeyPressed

    private void CONTACTKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CONTACTKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            OK.doClick();
        }
    }//GEN-LAST:event_CONTACTKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CANCEL;
    private javax.swing.JTextField CONTACT;
    private javax.swing.JLabel CONTACT_LABEL;
    private javax.swing.JPasswordField KEYSTORE_PASSWORD;
    private javax.swing.JLabel KEYSTORE_PASSWORD_LABEL;
    private javax.swing.JTextField LOCATION;
    private javax.swing.JLabel LOCATION_LABEL;
    private javax.swing.JButton OK;
    private javax.swing.JTextField REASON;
    private javax.swing.JLabel REASON_LABEL;
    private javax.swing.JPanel SIGN_GUI_PANEL;
    // End of variables declaration//GEN-END:variables
}