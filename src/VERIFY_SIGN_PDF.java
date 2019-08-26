
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.CRLVerifier;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.VerificationOK;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class VERIFY_SIGN_PDF {

    private static boolean verified = false;
    private static KeyStore ks;

    public VERIFY_SIGN_PDF() {

    }

    public static void showCertificateInfo(X509Certificate cert, Date signDate) {
        System.out.println("Issuer: " + cert.getIssuerDN());
        System.out.println("Subject: " + cert.getSubjectDN());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        date_format.setTimeZone(TimeZone.getTimeZone("Universal"));
        System.out.println("Valid from: " + date_format.format(cert.getNotBefore()));
        System.out.println("Valid to: " + date_format.format(cert.getNotAfter()));
        try {
            cert.checkValidity(signDate);
            System.out.println("The certificate was valid at the time of signing.");
        } catch (CertificateExpiredException e) {
            System.out.println("The certificate was expired at the time of signing.");
        } catch (CertificateNotYetValidException e) {
            System.out.println("The certificate wasn't valid yet at the time of signing.");
        }
        try {
            cert.checkValidity();
            System.out.println("The certificate is still valid.");
        } catch (CertificateExpiredException e) {
            System.out.println("The certificate has expired.");
        } catch (CertificateNotYetValidException e) {
            System.out.println("The certificate isn't valid yet.");
        }
    }

    public static void checkRevocation(PdfPKCS7 pkcs7, X509Certificate signCert, X509Certificate issuerCert, Date date) throws GeneralSecurityException, IOException {
        List<BasicOCSPResp> ocsps = new ArrayList<>();
        if (pkcs7.getOcsp() != null) {
            ocsps.add(pkcs7.getOcsp());
        }
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, ocsps);
        List<VerificationOK> verification = ocspVerifier.verify(signCert, issuerCert, date);
        if (verification.isEmpty()) {
            List<X509CRL> crls = new ArrayList<>();
            if (pkcs7.getCRLs() != null) {
                pkcs7.getCRLs().forEach((crl) -> {
                    crls.add((X509CRL) crl);
                });
            }
            CRLVerifier crlVerifier = new CRLVerifier(null, crls);
            verification.addAll(crlVerifier.verify(signCert, issuerCert, date));
        }
        if (verification.isEmpty()) {
            System.out.println("The signing certificate couldn't be verified");
        } else {
            verification.forEach((v) -> {
                System.out.println(v);
            });
        }
    }

    public static PdfPKCS7 verifySignature(SignatureUtil signUtil, String name) throws GeneralSecurityException, IOException {
        PdfPKCS7 pkcs7 = signUtil.verifySignature(name);
        Certificate[] certs = pkcs7.getSignCertificateChain();
        Calendar cal = pkcs7.getSignDate();
        List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, cal);
        if (errors.isEmpty()) {
            System.out.println("Certificates verified against the KeyStore");
        } else {
            System.out.println(errors);
        }
        for (int i = 0; i < certs.length; i++) {
            X509Certificate cert = (X509Certificate) certs[i];
            System.out.println("=== Certificate " + i + " ===");
            showCertificateInfo(cert, cal.getTime());
        }
        X509Certificate signCert = (X509Certificate) certs[0];
        X509Certificate issuerCert = (certs.length > 1 ? (X509Certificate) certs[1] : null);
        System.out.println("=== Checking validity of the document at the time of signing ===");
        checkRevocation(pkcs7, signCert, issuerCert, cal.getTime());
        System.out.println("=== Checking validity of the document today ===");
        checkRevocation(pkcs7, signCert, issuerCert, new Date());
        return pkcs7;
    }

    private static void setKeyStore(KeyStore ks) {
        VERIFY_SIGN_PDF.ks = ks;
    }

    public static void verifySignatures(String path, String userid) throws IOException, GeneralSecurityException {

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ks.setCertificateEntry(userid, cf.generateCertificate(new FileInputStream("C:\\Users\\path4\\Downloads\\Music\\Desktop\\keystore\\KEYSTORE_1.CER")));
        setKeyStore(ks);

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path));
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();
        for (String name : names) {
            if (name.equals("sign-userid: " + userid)) {
                System.out.println("===== " + name + " =====");
                verifySignature(signUtil, name);
            }
        }
    }

}
