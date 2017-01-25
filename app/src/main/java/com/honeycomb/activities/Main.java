package com.honeycomb.activities;

import android.os.Bundle;

import com.honeycomb.R;
import com.honeycomb.fragments.FragMain;

public class Main extends baseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentHelper.switchToFragment(R.id.fragmentContainer, FragMain.newInstance("test"));
    }
}
