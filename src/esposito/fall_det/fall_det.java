package esposito.fall_det;

import android.app.Activity;
import android.os.Bundle;

import android.location.*;
import android.content.Context;

public class fall_det extends Activity {

	private static final String MAP_API_KEY = "06cz479V1NDWE1O7nSLXCSi0AbVf-cnqKmTYdWg";
	private LocationManager locationManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
 	   // get a handle on the location manager
 	   locationManager =
 	     (LocationManager) getSystemService(Context.LOCATION_SERVICE);

 	   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 
 			   0, new LocationUpdateHandler());
    }
    
    // this inner class is the intent reciever that recives notifcations
    // from the location provider about position updates, and then redraws
    // the MapView with the new location centered.
    public class LocationUpdateHandler implements LocationListener {

		public void onLocationChanged(Location loc) {
        	int lat = (int) (loc.getLatitude()*1E6);
        	int lng = (int) (loc.getLongitude()*1E6);
		}

		public void onProviderDisabled(String provider) {}

		public void onProviderEnabled(String provider) {}

		public void onStatusChanged(String provider, int status, 
				Bundle extras) {}
    }
}