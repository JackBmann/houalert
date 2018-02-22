package houalert.us.houalert;

/**
 * Created by Mike on 5/16/2015.
 */
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.os.Debug;
        import android.util.Log;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;
        import org.apache.http.util.EntityUtils;
        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;


        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.net.URLConnection;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Scanner;

/**
 * Created by justi_000 on 5/16/2015.
 */
public class HpdPull {
    public static void getClassNames(MapFragment a) {

        final MapFragment thisActivity = a;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String lines = "";
                String[] incidence;
                String[] info;
                try {
                    InputStream in = null;
                    int response = -1;

                    URL url = new URL("http://cohweb.houstontx.gov/ActiveIncidents/HPDIncidents.aspx");

                    URLConnection conn = url.openConnection();


                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setAllowUserInteraction(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.setRequestMethod("GET");
                    httpConn.connect();
                    response = httpConn.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        in = httpConn.getInputStream();
                        Scanner s = new Scanner(in);

                        while (s.hasNext()) {
                            lines += s.nextLine();
                        }
                        Log.d("run", lines);
                    }
                } catch (Exception ex) {
                    Log.d("ex", "" + ex);
                }


                //parse the file

                Document document = Jsoup.parse(lines);
                //System.out.print(String.valueOf(document.isBlock()));
                int numRows = document.getElementById("dgResults").getElementsByTag("tr").size();
                incidence = new String[numRows];
                info = new String[numRows];
                if (numRows != 0) {
                    for (int i = 1; i < numRows; i++) {
                        String address = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(0).text();
                        String crossStreet = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(1).text();
                        String event = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(4).text();
                        if (!address.equals("")) {
                            Log.d("new", address + " " + crossStreet + " " + event);
                            incidence[i - 1] = address;
                            info[i - 1] = event;
                        }
                    }


                }
                thisActivity.onGetHpdData(incidence, info);
            }
            });
            t.start();
    }
}
