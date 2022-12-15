import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.util.io.Streams;
import org.pgpainless.PGPainless;
import org.pgpainless.algorithm.EncryptionPurpose;
import org.pgpainless.decryption_verification.ConsumerOptions;
import org.pgpainless.decryption_verification.DecryptionStream;
import org.pgpainless.encryption_signing.EncryptionOptions;
import org.pgpainless.encryption_signing.EncryptionStream;
import org.pgpainless.encryption_signing.ProducerOptions;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class EncryptionController {

    private PGPSecretKeyRing secretKeyRing;
    private PGPPublicKeyRing publicKeyRing;
    private String phonenum;

    public EncryptionController() {

    }

    public void setPhoneNum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getPublicKey() {
        try {
            //System.out.println("getpublickeyring\n"+PGPainless.asciiArmor(PGPainless.extractCertificate(secretKeyRing)));
            return PGPainless.asciiArmor(PGPainless.extractCertificate(secretKeyRing));
        } catch (IOException e) {
            System.err.println("Ошибка при работе с публичным PGP ключом");
            e.printStackTrace();
        }
        return null;
    }

    public void generatePrivateKey() {
        try {
            secretKeyRing = PGPainless.generateKeyRing().modernKeyRing(phonenum);
        } catch (PGPException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            System.err.println("Ошибка при работе с приватным PGP ключом");
            e.printStackTrace();
        }
    }


    public String encrypt(String text, List<User> userList) {
        try {
            EncryptionOptions encryptionOptions = new EncryptionOptions(EncryptionPurpose.COMMUNICATIONS);
            for (User user : userList) {
                //System.out.println("encrypt\n" + user.getPublicPGPkey());
                encryptionOptions.addRecipient(PGPainless.readKeyRing().publicKeyRing(user.getPublicPGPkey()));
            }

            ByteArrayOutputStream ciphertext = new ByteArrayOutputStream();
            EncryptionStream encryptor = PGPainless.encryptAndOrSign()
                    .onOutputStream(ciphertext)
                    .withOptions(ProducerOptions.encrypt(
                                    encryptionOptions
                            ).setAsciiArmor(true)
                    );
            Streams.pipeAll(new ByteArrayInputStream(text.getBytes()), encryptor);
            encryptor.close();
            return ciphertext.toString();
        } catch (IOException | PGPException e) {
            System.err.println("Ошибка при зашифровке сообщения");
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String text) {
        try {
            System.out.println(text);
            DecryptionStream decryptor = PGPainless.decryptAndOrVerify()
                    .onInputStream(new ByteArrayInputStream(text.getBytes()))
                    .withOptions(new ConsumerOptions()
                            .addDecryptionKey(secretKeyRing)
                    );

            ByteArrayOutputStream plaintext = new ByteArrayOutputStream();
            Streams.pipeAll(decryptor, plaintext);
            decryptor.close();
            return plaintext.toString();
        } catch (IOException | PGPException e) {
            System.err.println("Ошибка при расшифровке сообщения");
            e.printStackTrace();
        }
        return "null";
    }

    public static String hash(String password, String phonenum) {
        return Base64.getEncoder().encodeToString(SCrypt.generate(password.getBytes(),
                phonenum.getBytes(),
                16384,
                8,
                1,
                32));
    }
}
