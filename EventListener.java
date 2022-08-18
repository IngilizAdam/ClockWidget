import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.awt.Desktop;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class EventListener implements Runnable {
    @Override
    public void run() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String SERVER = "";
        String link = "";
        try{
            Scanner sc = new Scanner(new FileInputStream("server.tuna"));
            SERVER = sc.nextLine();
            link = sc.nextLine();
            sc.close();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        if(SERVER.length() == 0){
            System.out.println("Server is not set");
            return;
        }

        // Now you can access an https URL without having the certificate in the
        // truststore
        while (true) {
            try {
                HttpsURLConnection https = (HttpsURLConnection) (new URL(SERVER + link + "get")).openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                
                String linkRead = br.readLine();
                br.close();
                https.disconnect();

                if(linkRead != null && linkRead.length() > 0 && !linkRead.equals("null")){
                    https = (HttpsURLConnection) (new URL(SERVER + link + "null")).openConnection();
                    https.getResponseCode();
                    https.getResponseMessage();
                    https.disconnect();

                    if(!linkRead.startsWith("http"))
                        linkRead = "https://duckduckgo.com/?q=!ducky+" + linkRead.replaceAll(" ", "%20");
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(linkRead));
                    }
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }
        }
    }

}
