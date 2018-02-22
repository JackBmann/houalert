package houalert.us.houalert;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureServiceTable;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureFillSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorReverseGeocodeResult;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Mike on 5/16/2015.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    Point mLocationLayerPoint;
    String mLocationLayerPointString;
    Boolean mIsMapLoaded = false;

    private MyLocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Location currentLocation;

    public GraphicsLayer evac_zones;
    public GraphicsLayer hpd;
    public GraphicsLayer hfd;
    public GraphicsLayer radar;
    public GraphicsLayer mLocationLayer;

    public GeodatabaseFeatureServiceTable _311;

    private boolean report = false;


    public class MyLocationListener implements LocationListener {


        public void onLocationChanged(Location location) {
            // This is where I finally get the latest location information

            currentLocation = location;

            int Bearing = (int) currentLocation.getBearing();

            mMapView.centerAt(currentLocation.getLongitude(), currentLocation.getLatitude(), true);
            //Toast toast = Toast.makeText(MainActivity.this.getBaseContext(), "Check", Toast.LENGTH_SHORT);
            //toast.show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);


                mMapView = (MapView) v.findViewById(R.id.map);

                mMapView.setOnSingleTapListener(new OnSingleTapListener() {
                    @Override
                    public void onSingleTap(float v, float v2) {
                        if(report) {
                            mLocationLayerPoint = mMapView.toMapPoint(new Point((double) v, (double) v2));

                            Toast toast = Toast.makeText(getActivity().getBaseContext(), "Point: " + mLocationLayerPoint.getX() + ", " + mLocationLayerPoint.getY(),
                                    Toast.LENGTH_SHORT);

                            Locator locator = Locator.createOnlineLocator();
                            String resultAddress = mLocationLayerPoint.getX() + ", " + mLocationLayerPoint.getY();


                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "311@houstontx.gov", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "311 Problem Report");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "I would like to report a problem at " + resultAddress);
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    }
                });



        mLocationLayer  = new GraphicsLayer();

        evac_zones      = new GraphicsLayer();
        radar           = new GraphicsLayer();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap x;

                        HttpURLConnection connection = (HttpURLConnection) new URL("http://api.wunderground.com/api/7c0ed5e4840a093b/radar/image.png?centerlat=29.75776&centerlon=-95.36267&radius=100&width=280&height=280").openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();

                        x = BitmapFactory.decodeStream(input);

                        PictureFillSymbol picture = new PictureFillSymbol(new BitmapDrawable(x));

                        Polygon polygonGeometry = new Polygon();
                        polygonGeometry.startPath(-1.0639615, 3451246.0274357);
                        polygonGeometry.lineTo(-1.0594412, 3493370.2);
                        polygonGeometry.lineTo(-1.06386350, 3448384.42);
                        polygonGeometry.lineTo(-1.06386350, 3448384.42);
                        radar.addGraphic(new Graphic(polygonGeometry, picture));
                        Log.d("Picture", "Done");
                    }catch (Exception e)
                    {
                        Log.d("Picture","suxies"+e);
                    }
                }});t.start();
        mMapView.addLayer(radar);




        //311 Feature Layer
        _311 = new GeodatabaseFeatureServiceTable("http://mycity.houstontx.gov/ArcGIS10/rest/services/wm/SR311Display_wm/MapServer", 0);

        // you can set the spatial reference of the table here
        // if not set, the service's spatial reference will be used
        _311.setSpatialReference(SpatialReference.create(3857));


        // initialize the table asynchronously
        _311.initialize(
                new CallbackListener<GeodatabaseFeatureServiceTable.Status>() {

                    @Override
                    public void onError(Throwable e) {
                        // report/handle error as desired
                        Log.d("Callback", _311.getInitializationError());
                    }

                    @Override
                    public void onCallback(GeodatabaseFeatureServiceTable.Status status) {

                        Log.d("Callback", "Done");

                        if (status == GeodatabaseFeatureServiceTable.Status.INITIALIZED) {
                            // ...create a FeatureLayer from the GeodatabaseFeatureServiceTable...
                            Log.d("Callback", "Added");
                            FeatureLayer _311_Layer = new FeatureLayer(_311);
                            mMapView.addLayer(_311_Layer);
                            _311_Layer.setDefinitionExpression("Status='Open'");
                        }
                    }
                });
        //Graphics layer for placed positions
        mMapView.addLayer(mLocationLayer);
        //Add radar image
        mMapView.addLayer(radar);

        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();



        final Button report_button = (Button) v.findViewById(R.id.report_button);
        report_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast toast = Toast.makeText(getActivity().getBaseContext(), "Select location to report" ,
                        Toast.LENGTH_SHORT);
                toast.show();
                report = true;
            }
        });



        return v;
    }

    public void onResume() {
        super.onResume();

        String provider = LocationManager.GPS_PROVIDER;
        long minTime = 0;
        float minDistance = 0;
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, mLocationListener);

    }

    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);

        hpd  = new GraphicsLayer();
        hfd  = new GraphicsLayer();

        HpdPull.getClassNames(this);
        HfdPull.getClassNames(this);

    }

    public void onGetHpdData(String[] address, String[] info)
    {
        for(int i = 0; i < address.length; i++){
            // create a point marker symbol (red, size 10, of type circle)
            SimpleMarkerSymbol simpleMarker = new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE);

            // create Locator parameters from single line address string
            LocatorFindParameters findParams = new LocatorFindParameters(address[i] + " Houston TX");

            // set the search country to USA, max locations to 2,
            // and output spatial reference to match the map
            findParams.setSourceCountry("USA");
            findParams.setMaxLocations(2);
            findParams.setOutSR(mMapView.getSpatialReference());

            Locator locator = Locator.createOnlineLocator();
            List<LocatorGeocodeResult> results;
            try {
                results = locator.find(findParams);
                LocatorGeocodeResult result = results.get(0);

                // Get the returned geometry, create a Graphic from it, and add to GraphicsLayer
                Geometry resultLocGeom = result.getLocation();
                SimpleMarkerSymbol resultSymbol = new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
                Graphic resultLocation = new Graphic(resultLocGeom, resultSymbol);
                hpd.addGraphic(resultLocation);

                TextSymbol textSymbol = new TextSymbol(10, info[i], Color.BLUE);
                Point pt = new Point(result.getLocation().getX()-55, result.getLocation().getY()+5);
                Graphic gr = new Graphic(pt, textSymbol);
                hpd.addGraphic(gr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMapView.addLayer(hpd);
    }

    public void onGetHfdData(LocatorGeocodeResult[] address, String[] info){
        for(int i = 0; i < address.length; i++){

            SimpleMarkerSymbol resultSymbol = new SimpleMarkerSymbol(Color.YELLOW, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
            Graphic resultLocation = new Graphic(address[i].getLocation(), resultSymbol);
            hfd.addGraphic(resultLocation);
            Log.d("Hfd", "New Point!");

            TextSymbol textSymbol = new TextSymbol(10, info[i], Color.YELLOW);
           Point pt = new Point(address[i].getLocation().getX()-55, address[i].getLocation().getY()+5);
            Graphic gr = new Graphic(pt, textSymbol);
            hfd.addGraphic(gr);
        }
        Log.d("Hfd", "Added!");
        mMapView.addLayer(hfd);

    }


    private Point ToGeographic(Point point)
    {
        double mercatorX_lon = point.getX();
        double mercatorY_lat = point.getY();

        if (Math.abs(mercatorX_lon) < 180 && Math.abs(mercatorY_lat) < 90)
            return null;

        if ((Math.abs(mercatorX_lon) > 20037508.3427892) || (Math.abs(mercatorY_lat) > 20037508.3427892))
            return null;

        double x = mercatorX_lon;
        double y = mercatorY_lat;
        double num3 = x / 6378137.0;
        double num4 = num3 * 57.295779513082323;
        double num5 = Math.floor((double) ((num4 + 180.0) / 360.0));
        double num6 = num4 - (num5 * 360.0);
        double num7 = 1.5707963267948966 - (2.0 * Math.atan(Math.exp((-1.0 * y) / 6378137.0)));
        mercatorX_lon = num6;
        mercatorY_lat = num7 * 57.295779513082323;

        return new Point(mercatorX_lon, mercatorY_lat);
    }
}
