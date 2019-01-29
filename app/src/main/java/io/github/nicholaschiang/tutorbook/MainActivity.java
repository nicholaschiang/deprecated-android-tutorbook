package io.github.nicholaschiang.tutorbook;


// TODO: replace FragmentManager with new API support
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setupNavigationView();
    }

    private void setupNavigationView() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation);
        if (mBottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = mBottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            mBottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_home:
                // Action to perform when Home Menu item is selected.
                pushFragment(new HomeFragment());
                break;
            case R.id.action_dashboard:
                // Action to perform when Bag Menu item is selected.
                pushFragment(new DashboardFragment());
                break;
            case R.id.action_account:
                // Action to perform when Account Menu item is selected.
                pushFragment(new AccountFragment());
                break;

            case R.id.action_search:
                // Action to perform when Search Menu item is selected.
                pushFragment(new SearchFragment());
                break;
        }
    }

    /**
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }
}
