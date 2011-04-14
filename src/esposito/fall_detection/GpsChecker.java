package esposito.fall_detection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.GpsStatus;
import android.location.LocationManager;

public class GpsChecker {

	private FallDetection activity;
	protected GpsStatus.Listener gpsListener = null;

	public GpsChecker(FallDetection activity) {
		this.activity = activity;
	}

	protected void checkGPS() {
		// add listener to the gps status if needed
		if (activity.locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			// create a dialog for waiting the gps to start
			final ProgressDialog progD = new ProgressDialog(activity);
			progD.setMessage("Activating Gps | Please wait...");
			progD.setButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
					progD.cancel();
				}
			});
			// button to by pass the gps as he won't find the satellites in a
			// building and we need to demo it...
			progD.setButton2("Just go", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					progD.cancel();
					activity.locationManager
							.removeGpsStatusListener(gpsListener);
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

			activity.locationManager.addGpsStatusListener(gpsListener);

			activity.locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0,
					activity.locationUpdateHandler);

			progD.show();

			// set the screen in landscape mode

		} else {
			// Alert the user gps is disabled
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					activity);
			builder.setMessage("Your GPS seems to be disabled, please enable it");
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

}
