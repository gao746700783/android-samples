/**
 *
 */
package com.googlemap.ui.fragment.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlemap.R;

import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.LOCATION_SERVICE;

/**
 * @author King
 */
public class BaseMapFragment extends BaseFragment implements
        OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    GoogleMap mMap;

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private String mCurrentAddress;

    private static final int REQUEST_CODE_LOCATION = 0x010;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                // log it when the location changes
                if (location != null) {
                    Log.i(TAG, "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                }
            }

            public void onProviderDisabled(String provider) {
                // Provider被disable时触发此函数，比如GPS被关闭
            }

            public void onProviderEnabled(String provider) {
                //  Provider被enable时触发此函数，比如GPS被打开
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            }
        };


    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //
        Log.i(TAG, "onCreateView called! ");
        return inflater.inflate(R.layout.fragment_base_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mLocationManager && null != mLocationListener) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    /**
     * init map
     */
    private void initMap() {
        try {
            MapsInitializer.initialize(mContext);
            // in Fragment ,you should use getChildFragmentManager ,not getFragmentManager
            // otherwise,it will return null to SupportMapFragment
            SupportMapFragment fm = (SupportMapFragment) getChildFragmentManager().
                    findFragmentById(R.id.fragment_map);
            fm.getMapAsync(this);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // settings
        UiSettings mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);


        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(mContext,
                        "Example de Message for Android",
                        Toast.LENGTH_SHORT).show();

                // check permissions
                if (ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    EasyPermissions.requestPermissions(mFContext,
                            "使用地图功能，需要请求位置权限来进行定位",
                            REQUEST_CODE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    );

                    return true;
                }

                // check location service is or not enabled
                if (!isEnableLocationService()) {
                    return true;
                }

                //
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, 0, mLocationListener);

                Location location =
                        mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Log.i(TAG, "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());

                    // // remove update of listener
                    //mLocationManager.removeUpdates(mLocationListener);

                    //getAddress(mContext, location);

                    addMarkerAndAnimate(location);
                }

                return true;
            }
        });


        //        if (EasyPermissions.hasPermissions(mContext,
        //                Manifest.permission.ACCESS_FINE_LOCATION,
        //                Manifest.permission.ACCESS_COARSE_LOCATION)) {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            EasyPermissions.requestPermissions(mFContext,
                    "使用地图功能，需要请求位置权限来进行定位",
                    REQUEST_CODE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void addMarkerAndAnimate(Location location) {
        LatLng mMarkerPosition = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(mMarkerPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.common_full_open_on_phone))
                .draggable(true);
        mMap.addMarker(markerOptions);
        //mMap.setOnMarkerDragListener(this);
        //mMap.setOnMarkerClickListener(this);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarkerPosition));
    }

    private boolean isEnableLocationService() {
        LocationManager service = (LocationManager) getActivity().
                getSystemService(LOCATION_SERVICE);

        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!enabledGPS) {

            Toast.makeText(getActivity(),
                    "GPS signal not found", Toast.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

            return false;
        } else if (!enabledWiFi) {

            Toast.makeText(getActivity(),
                    "Network signal not found",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

            return false;
        }

        return true;

    }

    /**
     * 根据 经纬度反解析地理位置
     *
     * @param context  context
     * @param location location
     * @return address
     */
    public void getAddress(final Context context, final Location location) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    Log.i("得到位置当前", "/" + addresses);
                    String fullAddress = "经度：" +
                            String.valueOf(addresses.get(0).getLongitude() * 1E6) + "\n";
                    fullAddress += "纬度：" +
                            String.valueOf(addresses.get(0).getLatitude() * 1E6) + "\n";
                    fullAddress += "国家：" + addresses.get(0).getCountryName() + "\n";
                    fullAddress += "省：" + addresses.get(0).getAdminArea() + "\n";
                    fullAddress += "城市：" + addresses.get(0).getLocality() + "\n";
                    fullAddress += "名称：" + addresses.get(0).getAddressLine(1) + "\n";
                    fullAddress += "街道：" + addresses.get(0).getAddressLine(0);
                    mCurrentAddress = fullAddress;
                } catch (Exception e) {
                    e.printStackTrace();
                    mCurrentAddress = "未知";
                }
            }
        });

    }

    /**
     * 根据地名返回一个有经纬度location,如果查询不到经纬度  则默认经纬度是0
     *
     * @param context context
     * @param address address
     * @return location
     */
    public static Location getLocation(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            Log.i("得到位置name", "/" + addresses);
            Location location = new Location(address);
            location.setLatitude(addresses.get(0).getLatitude());
            location.setLongitude(addresses.get(0).getLongitude());
            return location;
        } catch (Exception e) {
            Log.i("异常", "未获得有效数据");
            e.printStackTrace();
            return new Location(address);
        }
    }

    //    public PositionInfo getPositionInfo(String position_name) throws IOException {
    //        InputStream is_position = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(position_name, "UTF-8") + "&sensor=false").openStream();
    //        //连接是google提供的根据地名查询location的地址 例如:http://maps.googleapis.com/maps/api/geocode/json?address=%E4%B8%AD%E5%85%B3%E6%9D%91&sensor=false 返回的是json格式字符串
    //
    //        String position = StreamTools.readStream(is_position);
    //        Gson gson = new Gson();
    //        return gson.fromJson(position, PositionInfo.class);
    //    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0, mLocationListener);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        Log.i(TAG, "onPermissionsDenied called!");
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }

}
