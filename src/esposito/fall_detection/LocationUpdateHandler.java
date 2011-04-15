package esposito.fall_detection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationUpdateHandler implements LocationListener {

	private FallDetection activity;
	protected LocationManager locationManager;
	protected GpsStatus.Listener gpsListener = null;

	public LocationUpdateHandler(FallDetection activity) {
		this.activity = activity;

		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);

		// Uncomment the lines below to create a location update for
		// demonstration purposes or use telnet to send a geo fix like this:
		// telnet localhost 5554
		// geo fix 6.5365 53.24015

		// Location location = new Location(LocationManager.GPS_PROVIDER);
		// location.setLatitude(53.24015);
		// location.setLongitude(6.5365);
		// location.setTime((new Date()).getTime());
		// onLocationChanged(location);
	}

	public void onLocationChanged(Location loc) {
		synchronized (this) {
			activity.latitude = loc.getLatitude();
			activity.longintude = loc.getLongitude();
		}
	}

	public void unregisterListeners() {
		if (gpsListener != null) {
			locationManager.removeGpsStatusListener(gpsListener);
		}
		locationManager.removeUpdates(this);
	}

	protected void checkGPS() {
		// add listener to the gps status if needed
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			// create a dialog for waiting the gps to start
			final ProgressDialog progD = new ProgressDialog(activity);
			progD.setMessage("Activating GPS | Please wait ...");
			progD.setButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
					progD.cancel();
				}
			});
			// button to bypass the gps as he won't find the satellites in a
			// building and we need to demo it...
			progD.setButton2("Just go", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					progD.cancel();
					locationManager.removeGpsStatusListener(gpsListener);
					activity.hasAcquiredGps = true;
				}
			});

			progD.setCancelable(false);

			// listener to close the dialog when the gps has started
			gpsListener = new GpsStatus.Listener() {

				private boolean propagateEvent = true;

				@Override
				public void onGpsStatusChanged(int event) {
					if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
						if (propagateEvent) {
							if (progD != null) {
								progD.cancel();
								propagateEvent = false;
								activity.hasAcquiredGps = true;
							}
						}
					}
				}
			};

			locationManager.addGpsStatusListener(gpsListener);

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0,
					activity.locationUpdateHandler);

			progD.show();
		} else {
			// Alert the user gps is disabled
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					activity);
			builder.setMessage("Your GPS seems to be disabled. Please enable it.");
			builder.setCancelable(false);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.finish();
						}
					});
			builder.create().show();
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
