public class FallDetector implements
    SensorEventListener {
    
  private SensorManager mSensorManager = 
    (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
    
  public void registerListeners() {
    mSensorManager.registerListener(this,
        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_UI);
    mSensorManager.registerListener(this,
        mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        SensorManager.SENSOR_DELAY_UI);
  }  
  ...
}
//=============================================================
@Override
public void onSensorChanged(SensorEvent event) {
  synchronized (this) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      event.values[0]; // Acceleration minus Gx on the x-axis
      event.values[1]; // Acceleration minus Gy on the y-axis
      event.values[2]; // Acceleration minus Gz on the z-axis
    } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {      
      event.values[0]; // Azimuth, angle between the magnetic north direction and the
                       // y-axis, around the z-axis (0 to 359). 
                       // 0=North, 90=East, 180=South, 270=West
      event.values[1]; // Pitch, rotation around x-axis (-180 to 180)
      event.values[2]; // Roll, rotation around y-axis (-90 to 90)
    }
  }
}
//=============================================================
protected final float RssTreshold = 2.8f;
...
float rss = (float) Math.sqrt(Math.pow(event.values[0], 2)
                            + Math.pow(event.values[1], 2)
                            + Math.pow(event.values[2], 2));
if (rss > RssTreshold * SensorManager.STANDARD_GRAVITY) {  
  // Rss feature detected!
}
//=============================================================
private float mRssValues[] = new float[256];
private int mRssCount = 0;
private int mRssIndex = 0;
private long RssStartTime = 0;
protected final float VveWindow = 0.6f;
protected final float VveTreshold = -0.7f;
...
// Store all RSS values in the window in a circular array
if (RssStartTime == 0) {
  RssStartTime = date.getTime();
  mRssCount++;
} else if (date.getTime() - RssStartTime <= VveWindow * 1000
    && mRssCount < mRssValues.length) {
  mRssIndex = mRssCount++;
} else {
  mRssIndex = ++mRssIndex % mRssCount;
}
mRssValues[mRssIndex] = rss
    - SensorManager.STANDARD_GRAVITY;
// Calculate the numerical integer over all stored RSS values
float vve = 0;
for (int i = 0; i < mRssCount; i++) {
  vve += mRssValues[i];
}
vve = (vve * VveWindow) / mRssCount;
if (vve < VveTreshold * SensorManager.STANDARD_GRAVITY) {
  // Vve feature detected!
}
//=============================================================
private final int OriOffset = 1000;
private final int OriWindow = 2000;
private long OriStartTime = 0;
protected final float OriTreshold = 60;
private final float OriConstraint = 0.75f;
private float OriValues[] = new float[256];
private int ori_index = 0;
...
// Calculate orientation wrt horizon
float ori = (90 - Math.abs(event.values[1]));
// Wait one second
long wait_interval = (activity.RssTime != 0 ? date
    .getTime() - activity.RssTime
    : (activity.VveTime != 0 ? date.getTime()
        - activity.VveTime : 0));
if (wait_interval >= OriOffset) {
  // Collect ori values for 2 seconds
  if (OriStartTime == 0)
    OriStartTime = date.getTime();
  else if (date.getTime() - OriStartTime < OriWindow) {
    if (ori_index < OriValues.length)
      OriValues[ori_index++] = ori;
  } else {
    // Calculate percentage above threshold
    int count = 0;
    for (int i = 0; i < ori_index; i++) {
      if (OriValues[i] > OriTreshold)
        count++;
    }
    if (count / ori_index >= OriConstraint) {
       // Posture feature detected
    }
  }
}
//=============================================================



//=============================================================
// Making an HTTP post request and reading out the response
HttpClient httpclient = new DefaultHttpClient();
httpclient.getParams().setParameter(
    CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
HttpPost httppost = new HttpPost("http://web.service.host/falls");
List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
// Set fall timestamp
nameValuePairs.add(new BasicNameValuePair("datetime",
    (activity.VveTime != 0 ? Long.toString(activity.VveTime)
        : (activity.RssTime != 0 ? Long
            .toString(activity.RssTime) : ""))));
// Set RSS feature
nameValuePairs.add(new BasicNameValuePair("rss",
    (activity.RssVal == 0 ? "" : Float.toString(activity.RssVal))));
// Set VVE feature
nameValuePairs.add(new BasicNameValuePair("vve",
    (activity.VveVal == 0 ? "" : Float.toString(activity.VveVal))));
// Set user location
nameValuePairs
    .add(new BasicNameValuePair("lat", Double.toString(latitude)));
nameValuePairs
    .add(new BasicNameValuePair("lon", Double.toString(longitude)));
try {
  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
} catch (UnsupportedEncodingException e) {
  ...
}
// Send the notification
HttpResponse response;
try {
  response = httpclient.execute(httppost);
} catch (Exception e) {
  ...
}
//=============================================================
public class LocationUpdateHandler implements LocationListener {

  private FallActivity activity;
  protected LocationManager locationManager;

  public LocationUpdateHandler(FallActivity activity) {
    this.activity = activity;
    // Get location manager
    locationManager = (LocationManager) activity
        .getSystemService(Context.LOCATION_SERVICE);
    // Request location updates
    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 0, 0,
        activity.locationUpdateHandler);
  }

  // Handle location updates
  public void onLocationChanged(Location loc) {
    synchronized (this) {
      activity.latitude = loc.getLatitude();
      activity.longintude = loc.getLongitude();
    }
  }
}
//=============================================================
public class FallActivity extends Activity {

  protected GraphView mGraphView;

  protected FallDetector mFallDetector;

  protected LocationUpdateHandler locationUpdateHandler;

  /**
   * Initialization of the Activity after it is first created. Must at least
   * call {@link android.app.Activity#setContentView setContentView()} to
   * describe what is to be displayed in the screen.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Be sure to call the super class.
    super.onCreate(savedInstanceState);
    // Create the view
    mGraphView = new GraphView(this);
    setContentView(mGraphView);
    // Create the fall detector
    mFallDetector = new FallDetector(this);
    // Initialize location manager
    locationUpdateHandler = new LocationUpdateHandler(this);
    // Check whether gps is turned on
    locationUpdateHandler.checkGPS();
    // Set app orientation to landscape
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }
}
//=============================================================
//import org.openintents.sensorsimulator.hardware.Sensor;
//import org.openintents.sensorsimulator.hardware.SensorEvent;
//import org.openintents.sensorsimulator.hardware.SensorEventListener;
//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//=============================================================
import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

// import android.hardware.Sensor;
// import android.hardware.SensorEvent;
// import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//=============================================================
//=============================================================
//=============================================================
//=============================================================
//=============================================================
//=============================================================