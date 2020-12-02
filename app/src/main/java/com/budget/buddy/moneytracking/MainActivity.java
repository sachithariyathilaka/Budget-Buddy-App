package com.budget.buddy.moneytracking;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budget.buddy.moneytracking.Activities.ArchiveActivity;
import com.budget.buddy.moneytracking.Activities.CategoriesActivity;
import com.budget.buddy.moneytracking.Activities.ChartTypeActivity;
import com.budget.buddy.moneytracking.Activities.NewItemActivity;
import com.budget.buddy.moneytracking.Activities.PlannedActivity;
import com.budget.buddy.moneytracking.Activities.ReportActivity;
import com.budget.buddy.moneytracking.Activities.SettingsActivity;
import com.budget.buddy.moneytracking.Adapters.ViewPagerAdapter;
import com.budget.buddy.moneytracking.Broadcast.MoneyReminder;
import com.budget.buddy.moneytracking.Database.DBHelper;
import com.budget.buddy.moneytracking.Entities.Category;
import com.budget.buddy.moneytracking.Fragments.TabFragment;

import org.joda.time.LocalDate;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private ViewPagerAdapter vpage_adapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private TextView total_amount;

    DBHelper dbHelper;
    SharedPreferences prefs;
    Context context;
    MoneyReminder moneyReminder;

    private static final int MY_PERMISSION_REQUEST_RECEIVE_SMS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)){

            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},MY_PERMISSION_REQUEST_RECEIVE_SMS);
            }
        }

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");



        if (message!=null) {
            String[] splited = message.split("\\s+");

            for (int i = 0; i < splited.length; i++) {

                if (splited[i].equalsIgnoreCase("withdraw") || splited[i].equalsIgnoreCase("withdrawal") || splited[i].equalsIgnoreCase("transaction")) {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You Have Withdraw.. We coppied ammount and go new item and past it." + splited[i+2])
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                    ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("withdraw","-"+splited[i+2]);
                    clipboardManager.setPrimaryClip(clipData);

                }
                if (splited[i].equalsIgnoreCase("deposit") ) {
                    ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("deposit",splited[i+2]);
                    clipboardManager.setPrimaryClip(clipData);

                }
            }
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DBHelper(this);
        if (prefs.getBoolean("firstTime", true)) {
            init_firstTimeStart();
        }
        context = this;
        init_tabview();
        init_toolbar();
        init_fab();
        init_navview();
        init_planned_notification();

    }

    //@Override
    public void onRequestPermissionsResult(int requesyCode, String permission, int[] grantResults){
        switch (requesyCode)
        {
            case MY_PERMISSION_REQUEST_RECEIVE_SMS:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
            }
        }
    }


    private void init_planned_notification() {
        moneyReminder = new MoneyReminder();
        moneyReminder.cancelAlarm(context);
        moneyReminder.setAlarm(context);
    }

    private void init_firstTimeStart() {

        String categories[] = getResources().getStringArray(R.array.categories_array);
        for (String item : categories) {
            Category c = new Category(null, item);
            dbHelper.getDaoSession().getCategoryDao().insert(c);
        }

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("firstTime", false);
        editor.putBoolean("notifications_switch", true);
        editor.putString("notification_reminder", "1");


        editor.commit();

    }

    private void init_navview() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void init_fab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewItemActivity.class);
                intent.putExtra("planned", false);
                startActivity(intent);
            }
        });
    }

    private void init_toolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        total_amount = (TextView) findViewById(R.id.available_amount);
        String amount = "0.00";
        double d_amount = dbHelper.getTotal(new LocalDate(0), LocalDate.now());
        if (d_amount > 0) {
            DecimalFormat df = new DecimalFormat("#.00");
            amount = df.format(d_amount);
        }
        total_amount.setText(String.valueOf(amount + "Rs"));
        setSupportActionBar(toolbar);
    }


    private void init_tabview() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpage_adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TabFragment one = new TabFragment().newInstance(1), two = new TabFragment().newInstance(2), three = new TabFragment().newInstance(3);
        vpage_adapter.addFragment(one, getResources().getString(R.string.tab_day));
        vpage_adapter.addFragment(two, getResources().getString(R.string.tab_week));
        vpage_adapter.addFragment(three, getResources().getString(R.string.tab_month));
        viewPager.setAdapter(vpage_adapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_planned) {
            startActivity(new Intent(MainActivity.this, PlannedActivity.class));

        } else if (id == R.id.nav_category) {
            startActivity(new Intent(MainActivity.this, CategoriesActivity.class));

        } else if (id == R.id.nav_graph) {
            startActivity(new Intent(MainActivity.this, ChartTypeActivity.class));

        } else if (id == R.id.nav_archive) {
            startActivity(new Intent(MainActivity.this, ArchiveActivity.class));

        } else if (id == R.id.nav_report) {
            startActivity(new Intent(MainActivity.this, ReportActivity.class));

        } else if(id == R.id.nav_chatbot) {
            startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.projects.budget_buddy.budget_chatbot")));
        } else if(id == R.id.prediction) {
            startActivity(new Intent(MainActivity.this, PredictionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

}