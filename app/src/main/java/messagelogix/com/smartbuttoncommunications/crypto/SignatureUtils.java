package messagelogix.com.smartbuttoncommunications.crypto;

import android.util.Base64;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This class is handling the signatures
 */
public class SignatureUtils {

    private static Signature getInstance() {

        try {
            Signature s = Signature.getInstance("SHA256withRSA/PSS", new BouncyCastleProvider());
            PSSParameterSpec spec1 = new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 0, 1);
            s.setParameter(spec1);
            return s;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public static String genSignature(String text) {

        try {
            Signature s = getInstance();
            s.initSign(Crypto.getRSAPrivateKeyFromString(Preferences.getString(Preferences.RSA_PRIVATE_KEY)));
            s.update(text.getBytes());
            return RSA.stringify(Base64.encode(s.sign(), Base64.DEFAULT));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkSignature(String signature, String input) {

        try {
            Signature s = getInstance();
            s.initVerify(Crypto.getRSAPublicKeyFromString(Preferences.getString(Preferences.RSA_PUBLIC_KEY)));
            s.update(input.getBytes());
            return s.verify(Base64.decode(signature.getBytes(), Base64.DEFAULT));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

