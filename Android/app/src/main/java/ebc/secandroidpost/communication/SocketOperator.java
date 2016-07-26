package ebc.secandroidpost.communication;

/**
 * Created by echeb on 25-Jul-16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;

import ebc.secandroidpost.interfaces.ISocketOperator;

public class SocketOperator implements ISocketOperator {
    static char[] charArray = {':', 't', 'p', 'k', 'i', 'n', 'g', '/', 'a', 'b', 'x'};

    @Override
    public String sendHttpRequest(String params) {
        String result = "";
        try {
            URL url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(KeyPinStore.getInstance().getContext().getSocketFactory()); // Tell the URLConnection to use a SocketFactory from our SSLContext
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.println(params);
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8192);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result = result.concat(inputLine);
            }
            in.close();
            //} catch (IOException e) {
        } catch (IOException | KeyStoreException | CertificateException | KeyManagementException | NoSuchAlgorithmException e) {
            result = e.toString();
            e.printStackTrace();
        }
        return result;
    }

    private static final String AUTHENTICATION_SERVER_ADDRESS = "h" + charArray[1] + charArray[1] + charArray[2] + "s" + charArray[0] + charArray[7] + charArray[7] + charArray[9] + charArray[8] + charArray[5] + charArray[3] + charArray[4] + charArray[5] + charArray[6] + charArray[8] + charArray[2] + charArray[2] + "." + charArray[10] + "yz" + charArray[7];
}