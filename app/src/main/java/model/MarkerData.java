package model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by lightshadow on 15/9/2.
 */
public class MarkerData implements ClusterItem{
    private final LatLng latLng;
    private final String name;
    private final int profilePhoto;

    public MarkerData(LatLng latLng, String name, int profilePhoto) {
        this.latLng = latLng;
        this.name = name;
        this.profilePhoto = profilePhoto;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public int getProfilePhoto() {
        return profilePhoto;
    }
}
