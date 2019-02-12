package com.starikov.getweather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.action_my_location);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);

        startMyLocationFragment();
    }



    OnNavigationItemSelectedListener bottomNavigationListener = new OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.action_search_history:
                    break;
                case R.id.action_my_location:
                    startMyLocationFragment();
                    break;
                case R.id.action_search_place:
                    break;
            }
            return true;
        }
    };

    private void startMyLocationFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment myLocationFragment = new MyLocationFragment();
        transaction.replace(R.id.fragment_container, myLocationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

//    private void showInputDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Change city");
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                newQuery("q=" + input.getText().toString());
//            }
//        });
//        builder.show();
//    }
}
