package api.clients;

import junit.framework.TestCase;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class UtilsTest extends TestCase {

    public void testGetKey() throws Exception {
        String publicKeyContent = new String(
                Files.readAllBytes(
                        Paths.get(ClassLoader.getSystemResource(
                                "server_cert.pem"
                        ).toURI())
                )
        );
        publicKeyContent = publicKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        System.out.println("Public key " + publicKey);
        //Utils.getKey("")
    }

    public void testReadKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(
                        Paths.get(ClassLoader.getSystemResource(
                                "public_key.der"
                        ).toURI())
        );

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        final PublicKey publicKey = kf.generatePublic(spec);
        System.out.println("Public key " + publicKey);
    }
}