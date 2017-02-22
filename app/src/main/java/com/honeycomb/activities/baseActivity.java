package com.honeycomb.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.honeycomb.R;
import com.honeycomb.helper.FragmentHelper;

/**
 * Created by Ash on 18/01/2017.
 */

public abstract class baseActivity extends AppCompatActivity
{
    protected final String TAG = this.getClass().getSimpleName();

    protected FragmentHelper fragmentHelper;
    protected Toolbar toolbar;
    //protected Drawer drawer;
    //protected AccountHeader drawerHeader;

    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        fragmentHelper = new FragmentHelper(this);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        initToolbar();
        //initDrawer();
    }

    private void initToolbar()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDrawer()
    {
        // TODO, not sure if I need a drawer
        /*PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Hello :)");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Goodbye :(");

        drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withTextColor(Color.BLACK)
                //.withHeaderBackground(R.drawable.ic_mode_edit_black_24dp)
                .addProfiles(
                        new ProfileDrawerItem().withTextColor(Color.BLACK).withName("Ash Reynolds").withEmail("test@test.com").withIcon(R.drawable.ic_done_black_24dp)
                )
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                //.withRootView(R.id.drawer)
                .withAccountHeader(drawerHeader)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName("Settings")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        System.out.println("Say hi");
                        drawer.closeDrawer();
                        return true;
                    }
                })
                .build();*/
    }
}
