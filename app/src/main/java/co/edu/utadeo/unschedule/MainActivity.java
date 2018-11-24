package co.edu.utadeo.unschedule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import co.edu.utadeo.unschedule.services.ScheduleService;
import co.edu.utadeo.unschedule.subject.SubjectsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SubjectsFragment.OnFragmentInteractionListener {

    private static final String CURRENT_FRAGMENT_TAG = "MAIN_ACTIVITY_TAG";
    private static final int CONFIGURATION_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean isConfigured = ScheduleService.currentAcademicTerm != null || ScheduleService.getInstance().hasValidClassConfiguration(this);

        if (isConfigured) {
            showFirstFragment();
        } else {
            Intent i = new Intent(this, ConfigurationActivity.class);
            startActivityForResult(i, CONFIGURATION_RESULT);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Log.d("onNavigationItem", "OPEN GALLERY");

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            // prueba git
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONFIGURATION_RESULT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Resultado exitoso", Toast.LENGTH_LONG).show();
                    showFirstFragment();
                } else {
                    Toast.makeText(this, "Resultado no exitoso", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentByTag(SubjectsFragment.TAG))
                        .commit();
                break;
        }
    }

    private void showFirstFragment() {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT_TAG) == null) {
            new Handler().post(() -> {
                SubjectsFragment mainFragment = SubjectsFragment.newInstance(null);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_main_content, mainFragment, CURRENT_FRAGMENT_TAG)
                        .commit();
            });
        }
    }

    @Override
    public void onFragmentInteraction() {

    }

    public interface MainActivityListener {
        void onFragmentChanged(View v);
    }
}
