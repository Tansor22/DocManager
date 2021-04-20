package api.clients;

import android.content.res.Resources;
import core.activities.R;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class UtilsTLS {
    @SneakyThrows
    // todo ginish implementing trust manager
    public static OkHttpClient getSSLClient(Resources resources) {
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
                    .sslSocketFactory(tls.getSocketFactory()/*, new X509TrustManager() {
                                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                            }*/
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
}
