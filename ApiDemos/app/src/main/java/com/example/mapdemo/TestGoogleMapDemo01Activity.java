/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.googlemap.ui.fragment.base.BaseMapFragment;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class TestGoogleMapDemo01Activity extends AppCompatActivity {

    public void replaceFragment(int viewId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_01);

        // set fragment
        if (findViewById(R.id.layout) != null) {
            Fragment fragment = new BaseMapFragment();
            //fragment.setArguments(extras);
            replaceFragment(R.id.layout, fragment);
        }

    }

}
