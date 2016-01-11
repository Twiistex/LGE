package com.lumi_dos.lge;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Intent serviceIntent;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public SharedPreferences sharedPreferences;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Preferences.DEVICE_REGISTERED, false);
                if (!sharedPreferences.getBoolean(Preferences.GCM_STATUS_SHOWN, false)) {
                    if (sentToken) {
                        Toast toast = Toast.makeText(context, getString(R.string.device_registered), Toast.LENGTH_SHORT);
                        toast.show();
                        sharedPreferences.edit().putBoolean(Preferences.GCM_STATUS_SHOWN, true).apply();
                    } else {
                        Toast toast = Toast.makeText(context, getString(R.string.registration_error), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        };


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            serviceIntent = new Intent(this, RegistrationIntentService.class);
            startService(serviceIntent);
        }

        //Get a Tracker (should auto-report)
        ((LGE) getApplication()).getTracker(LGE.TrackerName.APP_TRACKER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String selectedStartUpScreen = sharedPreferences.getString("selected_startup_screen", getString(R.string.nav_welcome));
        Log.i("LGE", sharedPreferences.getString("selected_startup_screen", getString(R.string.nav_welcome)));
        if (selectedStartUpScreen.equals(getString(R.string.nav_welcome))) {
            fragmentTransaction.replace(R.id.fragment_container, new WelcomeFragment()).commit();
        } else if (selectedStartUpScreen.equals(getString(R.string.nav_intranet))) {
            fragmentTransaction.replace(R.id.fragment_container, new IntranetFragment()).commit();
        } else if (selectedStartUpScreen.equals(getString(R.string.nav_schedule))) {
            fragmentTransaction.replace(R.id.fragment_container, new ScheduleFragment()).commit();
        }
    }

    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        setUpLatVocAd();
    }

    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_welcome) {
            fragmentTransaction.replace(R.id.fragment_container, new WelcomeFragment()).commit();
        } else if (id == R.id.nav_intranet) {
            fragmentTransaction.replace(R.id.fragment_container, new IntranetFragment()).commit();
        } else if (id == R.id.nav_schedule) {
            fragmentTransaction.replace(R.id.fragment_container, new ScheduleFragment()).commit();
        } else if (id == R.id.nav_contact) {
            fragmentTransaction.replace(R.id.fragment_container, new ContactFragment()).commit();
        } else if (id == R.id.nav_about) {
            fragmentTransaction.replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getString(R.string.feedback_email), null));
            String feedback_message = getString(R.string.feedback_header) + "App version: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ") " + "Android version: " + Build.VERSION.RELEASE + " Device: " + Build.DEVICE + "\n";
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
            intent.putExtra(Intent.EXTRA_TEXT, feedback_message);
            Intent.createChooser(intent, getString(R.string.choose_email_client));
            startActivity(intent);
        } else if (id == R.id.nav_website) {
            Uri uri = Uri.parse("http://www.lge.lu");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Preferences.REGISTRATION_COMPLETE));
        setUpLatVocAd();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    public void setUpLatVocAd() {
        if(sharedPreferences.getString("my_class", getString(R.string.select_class)).endsWith("latine") && (sharedPreferences.getBoolean("showLAad", true))) sharedPreferences.edit().putBoolean("showLAad", true).apply();

        if(sharedPreferences.getBoolean("showLAad", false)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.vocTitle)).setMessage(getString(R.string.vocMessage))
                    .setIcon(R.mipmap.learnvoc)
                    .setPositiveButton(getString(R.string.vocgoto), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(getString(R.string.latvocmarketlink)));
                                startActivity(intent);
                            } catch (Exception e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.latvocplaystorelink))));
                            }
                            sharedPreferences.edit().putBoolean("showLAad", false).apply();
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean("showLAad", false).apply();
                        }
                    }).show();
        }
    }


}