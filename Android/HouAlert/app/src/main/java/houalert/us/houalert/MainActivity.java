package houalert.us.houalert;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends AppCompatActivity {
    private String[] mPlanetTitles = {};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MapFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(getResources().getDrawable(R.drawable.hurricane));

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.info);


        listAdapter.add("Hurricane Evacuation Information");
        listAdapter.add("Get an Emergency Kit");

        listAdapter.add("Emergency Assistance Registry");
        listAdapter.add("Alert Houston");
        listAdapter.add("Make an Emergency Plan");
        listAdapter.add("Where to get Information during Emergencies");
        listAdapter.add("Potential Hazards");
        listAdapter.add("Hurricane Evacuation Map");

        // Set the adapter for the list view
        mDrawerList.setAdapter(listAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.hurricane_button){
            Intent myIntent = new Intent(this, weather.class);
            startActivity(myIntent);
        }
        if(id == R.id.filter_button){

        }

        return super.onOptionsItemSelected(item);
    }

   private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Uri url = null;
            switch (position) {
                case 0:
                    url = Uri.parse("http://www.houstonoem.org/go/doc/4027/1080643/Hurricane-Evacuation");
                    break;
                case 1:
                    url = Uri.parse("http://www.houstonoem.org/go/doc/4027/1146795");
                    break;
                case 2:
                    url = Uri.parse("http://www.houstonoem.org/go/doc/4027/1130387");
                    break;
                case 3:
                    url = Uri.parse("http://www.houstonemergency.org/go/mailinglist/2263");
                    break;
                case 4:
                    url = Uri.parse("http://www.houstonoem.org/go/doc/4027/1147079");
                    break;
                case 5:
                    url = Uri.parse("http://www.houstonoem.org/go/doc/4027/1198223");
                    break;
                case 6:
                    url = Uri.parse("http://www.houstonoem.org/go/doc/4027/1138879/");
                    break;
                case 7:
                    url = Uri.parse("http://www.h-gac.com/taq/hurricane/documents/2015-evac-map(small).pdf");
                    break;

            }

            try{
                Intent openBrowser = new Intent(Intent.ACTION_VIEW,url);
                startActivity(openBrowser);
            }
            catch (Exception e)
            {
                //:(
            }
        }
    }


}
