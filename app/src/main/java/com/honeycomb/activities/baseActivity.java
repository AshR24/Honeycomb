package com.honeycomb.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.honeycomb.R;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.FragmentHelper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

/**
 * Created by Ash on 18/01/2017.
 */

public abstract class baseActivity extends AppCompatActivity
{
    protected final String TAG = this.getClass().getSimpleName();

    protected FragmentHelper fragmentHelper;
    protected Toolbar toolbar;
    protected Drawer drawer;
    protected AccountHeader drawerHeader;
    protected FirebaseAuth auth;
    protected FirebaseUser currentFirebaseUser;
    protected Database db;

    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        fragmentHelper = new FragmentHelper(this);
        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();
        db = new Database();
    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        initToolbar();
        initDrawer();
    }

    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDrawer()
    {
        PrimaryDrawerItem itemSignOut = new PrimaryDrawerItem().withIdentifier(1).withName("Sign Out");

        drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withTextColor(Color.BLACK)
                .addProfiles(
                        new ProfileDrawerItem().withTextColor(Color.BLACK).withName(currentFirebaseUser.getDisplayName()).withEmail(currentFirebaseUser.getEmail())
                )
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(drawerHeader)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        itemSignOut,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) ->
                {
                    // do something with the clicked item :D
                    if (drawerItem.getIdentifier() == itemSignOut.getIdentifier())
                    {
                        signOut();
                    }
                    drawer.closeDrawer();
                    return true;
                })
                .build();
    }

    private void signOut()
    {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task ->
                {
                    startActivity(new Intent(this, Splash.class));
                    finish();
                });

    }
}
