package houalert.us.houalert;

import android.graphics.Color;
import android.util.Log;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Mike on 5/17/2015.
 */
public class HfdPull {
    public static void getClassNames(MapFragment a) {

        final MapFragment thisActivity = a;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String lines = "";
                LocatorGeocodeResult[] incidenceFinal = null;
                String[] incidence1 = null;
                String[] incidence2 = null;
                String[] info = null;
                try {
                    InputStream in = null;
                    int response = -1;

                    URL url = new URL("http://cohweb.houstontx.gov/ActiveIncidents/HFDIncidents.aspx");

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
                int numRows =  document.getElementById("dgResults").getElementsByTag("tr").size();
                incidence1 = new String[numRows];
                incidence2 = new String[numRows];
                info = new String[numRows];
                if (numRows != 0) {

                    for(int i = 1; i < numRows; i++)
                    {
                        String streetOne = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(0).text();
                        String streetTwo = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(1).text();
                        String event = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(4).text();
                        String units = document.getElementById("dgResults").getElementsByTag("tr").get(i).getElementsByTag("td").get(6).text();
                        if(!streetOne.equals("")&&!streetOne.equals(""))
                        {
                            Log.d("new",streetOne+" "+streetTwo+" "+event+" "+units);
                            incidence1[i - 1] = streetOne;
                            incidence2[i - 1] = streetTwo;
                            info[i - 1] = event;
                        }
                    }


                }
                incidenceFinal = new LocatorGeocodeResult[incidence1.length];
                for(int i = 0; i < incidence1.length; i++){
                    // create Locator parameters from single line address string
                    LocatorFindParameters findParams1 = new LocatorFindParameters(incidence1[i] + " Houston TX");

                    // set the search country to USA, max locations to 2,
                    // and output spatial reference to match the map
                    findParams1.setSourceCountry("USA");
                    findParams1.setMaxLocations(2);
                    findParams1.setOutSR(SpatialReference.create(3857));

                    // create Locator parameters from single line address string
                    LocatorFindParameters findParams2 = new LocatorFindParameters(incidence1[i] + " Houston TX");

                    // set the search country to USA, max locations to 2,
                    // and output spatial reference to match the map
                    findParams2.setSourceCountry("USA");
                    findParams2.setMaxLocations(2);
                    findParams2.setOutSR(SpatialReference.create(3857));

                    Locator locator = Locator.createOnlineLocator();

                    List<LocatorGeocodeResult> results1;
                    List<LocatorGeocodeResult> results2;
                    try {
                        results1 = locator.find(findParams1);
                        results2 = locator.find(findParams2);

                        LocatorGeocodeResult nearestResult = null;
                        double shortestDistance = Double.MAX_VALUE;

                        for(LocatorGeocodeResult l1: results1){
                            for(LocatorGeocodeResult l2: results1){
                                if(nearestResult == null) {
                                    nearestResult = l1;
                                    shortestDistance = Math.abs(l1.getLocation().getX() - l2.getLocation().getX())
                                            + Math.abs(l1.getLocation().getY() - l2.getLocation().getY());
                                }
                                else if(Math.abs(l1.getLocation().getX() - l2.getLocation().getX()) + Math.abs(l1.getLocation().getY() - l2.getLocation().getY())
                                        < shortestDistance) {
                                    nearestResult = l1;
                                    shortestDistance = Math.abs(l1.getLocation().getX() - l2.getLocation().getX())
                                            + Math.abs(l1.getLocation().getY() - l2.getLocation().getY());
                                }
                            }
                        }

                        incidenceFinal[i] = nearestResult;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                thisActivity.onGetHfdData(incidenceFinal, info);
            }
        });
        t.start();
    }
}
