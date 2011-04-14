package esposito.fall_detection;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationUpdateHandler implements LocationListener {
	
	private FallDetection activity;
	
	public LocationUpdateHandler(FallDetection activity){
		this.activity = activity;
	}
	
	public void onLocationChanged(Location loc) {

		synchronized (this) {
			activity.lat = loc.getLatitude();
			activity.lon = loc.getLongitude();
		}

	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
