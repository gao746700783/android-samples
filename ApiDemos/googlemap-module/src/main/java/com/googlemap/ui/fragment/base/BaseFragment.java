/**
 *
 */
package com.googlemap.ui.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author King
 */
public class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();

    protected BaseFragment mFContext;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);

        mFContext = this;
        mContext = this.getActivity();
        Log.i(TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //
        Log.i(TAG, "onCreateView called! ");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        //
        super.onResume();
        Log.i(TAG, "onResume called! ");
    }

    @Override
    public void onPause() {
        //
        super.onPause();
        Log.i(TAG, "onPause called! ");
    }

    @Override
    public void onStop() {
        //
        super.onStop();
        Log.i(TAG, "onStop called! ");
    }

    @Override
    public void onDestroy() {
        //
        super.onDestroy();
        Log.i(TAG, "onDestroy called! ");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "onResume called! ");
    }

}
