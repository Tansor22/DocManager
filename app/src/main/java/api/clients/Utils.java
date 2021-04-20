package api.clients;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;
import core.activities.R;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// todo remove
public class Utils {

    @SneakyThrows({NoSuchAlgorithmException.class, KeyStoreException.class,
            KeyManagementException.class})
    public static OkHttpClient getSSLClient(Resources resources) {
        OkHttpClient client;
        SSLContext sslContext;
        SSLSocketFactory sslSocketFactory;
        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory;
        X509TrustManager trustManager;

        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(readKeyStore(resources));
        trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }

        trustManager = (X509TrustManager) trustManagers[0];

        sslContext = SSLContext.getInstance("TLS");

        sslContext.init(null, new TrustManager[]{trustManager}, null);

        sslSocketFactory = sslContext.getSocketFactory();

        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
        return client;
    }

    public static PublicKey getKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key.getBytes(), Base64.DEFAULT);
            //byte[] byteKey = key.getBytes();
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey readKey(Resources resources, int id) throws Exception {
    /*    byte[] keyBytes = Files.readAllBytes(
                Paths.get(ClassLoader.getSystemResource(
                        "public_key.der"
                ).toURI())
        );*/

        byte[] keyBytes = IOUtils.toByteArray(resources.openRawResource(id));

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        final PublicKey publicKey = kf.generatePublic(spec);
        return publicKey;
    }

    @SneakyThrows
    public static OkHttpClient getSSLClient2(Resources resources) {
        try (InputStream is = resources.openRawResource(R.raw.server_cert)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(is);
            final String keyStoreType = KeyStore.getDefaultType();
            final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cert);

            final String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
            trustManagerFactory.init(keyStore);
            final SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, trustManagerFactory.getTrustManagers(), null);
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(tls.getSocketFactory(), new X509TrustManager() {
                                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                            }
                    )
                    .hostnameVerifier((hostname, session) -> {
                                return true;
                            }
                      /*  try {
                            final PublicKey publicKey = readKey(resources, R.raw.public_key);
                            final PublicKey publicKey1 = session.getPeerCertificates()[0].getPublicKey();
                            session.getPeerCertificates()[0].verify(publicKey);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }*/)
                    .build();
            return client;
        }
    }


    /**
     * Get keys store. Key file should be encrypted with pkcs12 standard.
     * It can be done with standalone encrypting java applications like "keytool".
     * File password is also required.
     *
     * @param resources Resources from an activity or some other context.
     * @return Keys store.
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @SneakyThrows({KeyStoreException.class, CertificateException.class,
            NoSuchAlgorithmException.class, IOException.class})
    private static KeyStore readKeyStore(Resources resources) {
        KeyStore keyStore;
        char[] PASSWORD = "pass".toCharArray();
        List<InputStream> certificates = new ArrayList<>();
        // todo android resource
        //certificates.add(resources.openRawResource(R.raw.cert));

        keyStore = KeyStore.getInstance("pkcs12");

        for (InputStream certificate : certificates) {
            try {
                keyStore.load(certificate, PASSWORD);
            } finally {
                if (certificate != null) {
                    certificate.close();
                }
            }
        }
        return keyStore;
    }
}
