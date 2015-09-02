package lightshadow.markercluster;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.MarkerData;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener, LocationSource.OnLocationChangedListener, ClusterManager.OnClusterItemInfoWindowClickListener<MarkerData>, ClusterManager.OnClusterItemClickListener<MarkerData>, ClusterManager.OnClusterInfoWindowClickListener<MarkerData>, ClusterManager.OnClusterClickListener<MarkerData> {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private ClusterManager<MarkerData> clusterManager;
    private Random random = new Random(1989);
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayService();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap(double, double)} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     *
     * @param location
     */
    private void setUpMapIfNeeded(Location location) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(location.getLatitude(), location.getLongitude());
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     *
     * @param latitude
     * @param longitude
     */
    private void setUpMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        Log.e("latLng", "lat:" + String.valueOf(latitude) + " lng:" + String.valueOf(longitude));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        clusterManager = new ClusterManager<MarkerData>(this, mMap);
        clusterManager.setRenderer(new MarkerRender());

        mMap.setOnCameraChangeListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setOnInfoWindowClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        clusterManager.cluster();
    }

    private void addItems() {
        clusterManager.addItem(new MarkerData(position(), "doge", R.mipmap.doge));

        clusterManager.addItem(new MarkerData(position(), "bike", R.mipmap.bike));

        clusterManager.addItem(new MarkerData(position(), "bird", R.mipmap.bird));

        clusterManager.addItem(new MarkerData(position(), "cat", R.mipmap.cat));

        clusterManager.addItem(new MarkerData(position(), "lizard", R.mipmap.lizzard));

        clusterManager.addItem(new MarkerData(position(), "motorcycle", R.mipmap.motocycle));

        clusterManager.addItem(new MarkerData(position(), "mountain", R.mipmap.mountain));

        clusterManager.addItem(new MarkerData(position(), "waterfall", R.mipmap.waterfall));
    }

    private LatLng position() {
        double minLat = location.getLatitude() - 0.00004;
        double maxLat = location.getLatitude() + 0.00004;

        double minLng = location.getLongitude() - 0.00208;
        double maxLng = location.getLongitude() + 0.00208;
        return new LatLng(random(minLat, maxLat), random(minLng, maxLng));
    }

    private double random(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    private void checkGooglePlayService() {

        int isGooglePlayServiceAvilable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isGooglePlayServiceAvilable == ConnectionResult.SUCCESS) {
            googleApiClient.connect();
        } else {
            String errorText = GooglePlayServicesUtil.getErrorString(isGooglePlayServiceAvilable);
            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
            } catch (ClassCastException e) {
                Toast.makeText(this, "未抓取到位置，請開啟定位服務並更新位置", Toast.LENGTH_LONG).show();
            }

        } else {
            this.location = location;
            setUpMapIfNeeded(location);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            Toast.makeText(this, "CONNECT_GOOGLE_PLAY", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("Google play service", "CONNECT_FAILED" + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        setUpMapIfNeeded(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClusterItemInfoWindowClick(MarkerData markerData) {

    }

    @Override
    public boolean onClusterItemClick(MarkerData markerData) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MarkerData> cluster) {

    }

    @Override
    public boolean onClusterClick(Cluster<MarkerData> cluster) {
        String firstName = cluster.getItems().iterator().next().getName();
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        return true;
    }

    private class MarkerRender extends DefaultClusterRenderer<MarkerData> {

        private final IconGenerator iconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator clusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView imageView;
        private final ImageView clusterImageView;
        private final int dimension;

        public MarkerRender() {
            super(getApplicationContext(), mMap, clusterManager);

            View multiMarker = getLayoutInflater().inflate(R.layout.multi_marker, null);
            clusterIconGenerator.setContentView(multiMarker);
            clusterImageView = (ImageView) multiMarker.findViewById(R.id.iv_image);

            imageView = new ImageView(getApplicationContext());
            dimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(dimension, dimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            imageView.setPadding(padding, padding, padding, padding);
            iconGenerator.setContentView(imageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerData item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            imageView.setImageResource(item.getProfilePhoto());
            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getName());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerData> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = dimension;
            int height = dimension;

            for (MarkerData markerData : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), markerData.getProfilePhoto());
//                        getResources().getDrawable(markerData.getProfilePhoto());
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            clusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MarkerData> cluster) {
            return cluster.getSize() > 1;
        }
    }
}
