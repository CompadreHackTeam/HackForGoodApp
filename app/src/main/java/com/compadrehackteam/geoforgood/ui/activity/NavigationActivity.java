package com.compadrehackteam.geoforgood.ui.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.compadrehackteam.geoforgood.R;
import com.compadrehackteam.geoforgood.listener.GeofenceListener;
import com.compadrehackteam.geoforgood.manager.GeofenceManager;
import com.compadrehackteam.geoforgood.manager.MessageManager;
import com.compadrehackteam.geoforgood.manager.PermissionManager;
import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.compadrehackteam.geoforgood.model.MessageObject;
import com.compadrehackteam.geoforgood.ui.fragment.HomeFragment;
import com.compadrehackteam.geoforgood.ui.fragment.MapFragment;
import com.compadrehackteam.geoforgood.ui.fragment.MessageFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* The delay in milliseconds to perform the close of the drawer. */
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    /* The Handler element for delay*/
    private Handler mDrawerActionHandler;
    /* The navigation view */
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;
    /* The drawer layout */
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    /* The toolbar */
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    /* The Geofence Manager */
    private GeofenceManager sGeofenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);
        initUI();
        initPermissionListener();

        if(GeofenceManager.getAllGeofencesFromDatabase().size()>0) {
            initService();
        }

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getString("geofenceID")!=null){
                navigateToChat(getIntent().getExtras().getString("geofenceID"));
            }
        }
    }

    private void navigateToChat(String geofenceID) {

        getFragmentManager().beginTransaction().replace(R.id.content, new MessageFragment(geofenceID)).commit();
    }

    private void initService() {

        GeofenceManager.init(this);
        sGeofenceManager = GeofenceManager.getGeofenceManager();

        // Service init
        assert sGeofenceManager != null;

        if(GeofenceManager.getAllGeofencesFromDatabase().size()>0) {
            sGeofenceManager.startService();
        }else{
            GeofenceManager.downloadGeofencesFromServer(new GeofenceManager.DownloadListener() {
                @Override
                public void onDownloadSuccess(int i) {
                    sGeofenceManager.startService();
                }

                @Override
                public void onDownloadError(String error) {

                }
            });
        }
    }

    /**
     * This method start the permission listener
     */
    private void initPermissionListener() {

        PermissionManager.initLocationListener(new PermissionManager.PermissionManagerListener() {
            @Override
            public void onPermissionGranted() {
                initService();
            }

            @Override
            public void onPermissionDeny() {

            }
        });
        PermissionManager.checkPermissions();
    }

    /**
     * This method starts the UI
     */
    private void initUI() {

        setSupportActionBar(mToolbar);
        mDrawerActionHandler = new Handler();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        mDrawer.closeDrawer(GravityCompat.START);
        // This handler lets a delay in the naviation action of closing
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(item.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    /**
     * This method is called whenever a item is clicked in the left navigation drawer
     * using the fragment manager to replace the content frame with the Fragment desired
     * and the message for the tutorial.
     *
     * @param itemId The item ID resource
     */
    private void navigate(final int itemId) {

        FragmentManager mFragmentManager = getFragmentManager();
        switch (itemId) {
            case R.id.nav_home:
                mFragmentManager.beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                break;
            case R.id.nav_map:
                getFragmentManager().beginTransaction().replace(R.id.content, new MapFragment()).commit();
                break;
            default:
                mFragmentManager.beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                break;
        }
    }
}
