package houalert.us.houalert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mike on 5/17/2015.
 */
public class weather extends ActionBarActivity {


    public void onCreate(Bundle savedInstanceState, final MainActivity mainActivity) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.weather);
        getWeatherInfo(this, mainActivity);


    }

    public void getWeatherInfo(final Context thread, final MainActivity mainActivity){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final ListView mDrawerList = (ListView) findViewById(R.id.listview);
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(thread, R.layout.info);


                for(int j = 0; j < 3; j++){
                    String lines = "";
                    try {
                        InputStream in = null;
                        int response = -1;

                        URL url = null;
                        if(j==0)
                            url = new URL("http://alerts.weather.gov/cap/wwaatmget.php?x=TXC157");
                        else if(j==1)
                            url = new URL("http://alerts.weather.gov/cap/wwaatmget.php?x=TXC339");
                        else if(j==2)
                            url = new URL("http://alerts.weather.gov/cap/wwaatmget.php?x=TXC201");

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

                    int numRows = document.getElementsByTag("title").size();
                    if (numRows != 0) {

                        for (int i = 1; i < numRows; i++) {
                            String title = document.getElementsByTag("title").get(i).text();
                            if(!title.equals("There are no active watches, warnings or advisories")) {
                                String summary = document.getElementsByTag("summary").get(i - 1).text();
                                if (title != null && summary != null) {

                                    switch (j) {
                                        case 0:
                                            summary = "Fort Bend: " + summary;
                                            break;
                                        case 1:
                                            summary = "Montgomery: " + summary;
                                            break;
                                        case 2:
                                            summary = "Harris: " + summary;
                                            break;
                                    }
                                    summary = title + " " + summary;
                                    listAdapter.add(summary);

                                }
                            }

                        }


                    }


                }

                final ArrayAdapter<String> newArray = listAdapter;
                if(!listAdapter.isEmpty()) {
                    // Set the adapter for the list view
                    mainActivity.runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       mDrawerList.setAdapter(newArray);
                                                   }
                                               });

                }else{
                    Toast toast = Toast.makeText(getBaseContext(), "No extreme weather to report",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        );
        t.start();
    }
}
