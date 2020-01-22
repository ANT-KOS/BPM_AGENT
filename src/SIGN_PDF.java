
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.TSAClientBouncyCastle;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collection;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;



public class SIGN_PDF {

    private static String SRC;
    private static String DEST;
    private static String reason, location, contact;
    private static char[] pass;
    //private static int position;
    //private static boolean complete = false;
    private static int signers;
    private static HashMap<String, Rectangle> sign_locations;
    private static int page;
    private static int x = 10, y = 10;
    private static String userid;
    private static Provider PROV_PKCS11;

    public void init_VARIABLES(String SRC, String DEST, char[] Pass, String reason, String location, String contact, int signers, int page, String userid) throws IOException {
        SIGN_PDF.SRC = SRC;
        SIGN_PDF.DEST = DEST;
        pass = Pass;
        SIGN_PDF.contact = contact;
        SIGN_PDF.location = location;
        SIGN_PDF.reason = reason;
        //position = 1;
        SIGN_PDF.signers = signers;
        SIGN_PDF.page = page;
        SIGN_PDF.userid = userid;
        sign_locations = new HashMap<>();
    }

    public void init_X(int x) {
        SIGN_PDF.x = x;
    }

    public void init_Y(int y) {
        SIGN_PDF.y = y;
    }

    public void calculate_Rect(int signers) throws IOException {
        PDDocument doc = PDDocument.load(new File(SRC));
        PDPage P = doc.getPage(0);

        int page_width = 565;//(int) P.getMediaBox().getWidth(); // Math.round(P.getMediaBox().getWidth());
        int sign_empty_spaces = signers - 1;
        int margin_left = 10, margin_right = 10;
        int total_empty_space = sign_empty_spaces * 30;
        int length_of_every_sign = (int) (((page_width - (margin_left + margin_right)) - total_empty_space) / signers);

        for (int i = 0; i < 1; i++) {
            int a = x + (i * (length_of_every_sign + 30));
            int b = y;
            int c = 150; //a + length_of_every_sign + 30;
            int d = 70;  //y + 60;
            sign_locations.put("sign", new Rectangle(a, b, c, d));
        }

        doc.close();
    }

    public int Sign_Pdf_W_USB() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, Exception {
        calculate_Rect(signers);
        String tsaUrl = "http://timestamp.ermis.gov.gr/TSS/HttpTspServer";
        String DLL = "C:/Windows/System32/eTPKCS11.dll";
        long[] USB_SLOTS_WITH_TOKENS;
        USB_SLOTS_WITH_TOKENS = getSlotsWithTokens(DLL);

        if (USB_SLOTS_WITH_TOKENS.length == 0) {
            return 3;
        } else {
            String config = "name=eToken\n"
                    + "library=" + DLL + "\n"
                    + "slotListIndex = " + getSlotsWithTokens(DLL)[0];
            
            //String pkcs11_path = FILE_OPERATIONS.create_PKCS11_config(config);
            try {
                //ByteArrayInputStream BAIS = new ByteArrayInputStream(config.getBytes());
                //PROV_PKCS11 = Security.getProvider("SunPKCS11");
                //PROV_PKCS11.configure(pkcs11_path);
                PROV_PKCS11 = new SunPKCS11();
                PROV_PKCS11.configure("--"+config);
                Security.addProvider(PROV_PKCS11);
                BouncyCastleProvider PROV_BC = new BouncyCastleProvider();
                Security.addProvider(PROV_BC);

                KeyStore ks = KeyStore.getInstance("PKCS11", PROV_PKCS11);
                ks.load(null, pass);

                String alias = (String) ks.aliases().nextElement();
                PrivateKey pk = (PrivateKey) ks.getKey(alias, pass);
                Certificate[] chain = ks.getCertificateChain(alias);

                IOcspClient OCSP = new OcspClientBouncyCastle(null);
                ITSAClient TSA = null;

                for (int i = 0; i < chain.length; i++) {
                    X509Certificate cert = (X509Certificate) chain[i];
                    String TSA_URL = CertificateUtil.getTSAURL(cert);
                    if (TSA_URL != null) {
                        TSA = new TSAClientBouncyCastle(TSA_URL);
                        break;
                    } else {
                        TSA = new TSAClientBouncyCastle(tsaUrl);
                        break;
                    }
                }
                List<ICrlClient> crlList = new ArrayList<>();
                crlList.add(new CrlClientOnline(chain));

                int result = sign(SRC, DEST, chain, pk, DigestAlgorithms.SHA256, PROV_PKCS11.getName(), CryptoStandard.CADES, reason, location, contact, null, OCSP, TSA, 0);
                ((SunPKCS11) PROV_PKCS11).logout();
                PROV_PKCS11.clear();
                Security.removeProvider(PROV_PKCS11.getName());
                return result;
            } catch (IOException ex) {
                ((SunPKCS11) PROV_PKCS11).logout();
                PROV_PKCS11.clear();
                Security.removeProvider(PROV_PKCS11.getName());
                Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
                return 1;
            }
        }
    }

    public int sign(String src, String dest,
            Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, String provider, CryptoStandard subfilter,
            String reason, String location, String contact,
            Collection<ICrlClient> crlList,
            IOcspClient ocspClient,
            ITSAClient tsaClient,
            int estimatedSize)
            throws GeneralSecurityException, IOException, Exception {
        try {

            // Creating the reader and the stamper
            PdfReader reader = new PdfReader(src);

            PdfDocument PDFDOC = new PdfDocument(reader);
            PdfAcroForm form = PdfAcroForm.getAcroForm(PDFDOC, false);
            boolean first_sign = true;
            if (form != null) {
                Map<String, PdfFormField> fields = form.getFormFields();

                for (Entry<String, PdfFormField> entry : fields.entrySet()) {
                    if (entry.getKey().startsWith("sign-userid")) {
                        first_sign = false;
                        break;
                    }
                }
            }
            reader.close();
            
            StampingProperties SP = new StampingProperties();
            SP.preserveEncryption();
            SP.useAppendMode();
            
            PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), SP);
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            // Creating the appearance
            if (!reason.equals("")) {
                appearance.setReason(reason);
            }
            if (!location.equals("")) {
                appearance.setLocation(location);
            }
            if (!contact.equals("")) {
                appearance.setContact(contact);
            }
            appearance.setPageRect(sign_locations.get("sign"));
            appearance.setPageNumber(page);
            signer.setFieldName("sign-userid: " + userid);

            appearance.setLayer2Font(PdfFontFactory.createFont("font/arial.ttf", PdfEncodings.IDENTITY_H, true));
            appearance.setLayer2FontSize(7);
            appearance.setReuseAppearance(false);
            // Creating the signature
            IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
            IExternalDigest digest = new BouncyCastleDigest();

            if (first_sign != true) {
                signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);
            }
            else
            {
                signer.setCertificationLevel(PdfSigner.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS);
            }
            signer.signDetached(digest, pks, chain, crlList, ocspClient, tsaClient, estimatedSize, subfilter);
            
            return 0;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
            return 2;
        }
    }

    public static long[] getSlotsWithTokens(String libraryPath) throws IOException {
        CK_C_INITIALIZE_ARGS initArgs = new CK_C_INITIALIZE_ARGS();
        String functionList = "C_GetFunctionList";

        initArgs.flags = 0;
        PKCS11 tmpPKCS11 = null;
        long[] slotList = null;
        try {
            tmpPKCS11 = PKCS11.getInstance(libraryPath, functionList, initArgs, false);
        } catch (IOException ex) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
            throw ex;
        } catch (PKCS11Exception ex) {
            Logger.getLogger(SIGN_PDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            slotList = tmpPKCS11.C_GetSlotList(true);

            for (long slot : slotList) {
                CK_TOKEN_INFO tokenInfo = tmpPKCS11.C_GetTokenInfo(slot);
                System.out.println("slot: " + slot + "\nmanufacturerID: "
                        + String.valueOf(tokenInfo.manufacturerID) + "\nmodel: "
                        + String.valueOf(tokenInfo.model));
            }
        } catch (PKCS11Exception ex) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, ex);
        } catch (Throwable t) {
            Logger.getLogger(HTTP_HANDLERS.class.getName()).log(Level.WARNING, null, t);
        }

        return slotList;

    }

}
