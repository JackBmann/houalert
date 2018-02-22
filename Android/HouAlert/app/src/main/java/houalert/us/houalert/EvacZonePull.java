package houalert.us.houalert;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mike on 5/17/2015.
 */
public class EvacZonePull extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    //public DownloadTask(Context context) {
      //  this.context = context;
    //}

    //@Override
    protected String doInBackground() {
        InputStream input = null;
        OutputStream outputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://gisdata.houstontx.gov/gis_open_data/FLOOD%20HAZARD/HURRICANE_EVAC_ZONES.zip");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();


            //outputStream = openFileOutput("evac_zones.shp", Context.MODE_PRIVATE);
            //outputStream.write(string.getBytes());
            //outputStream.close();
        } catch (Exception e) {
            return e.toString();
        }
        return null;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}
