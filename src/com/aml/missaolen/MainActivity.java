package com.aml.missaolen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.aml.missaolen.DataHandling.DataHandlingListener;
import com.aml.missaolen.ScreenType.ScreenTypeListener;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ScreenTypeListener,DataHandlingListener {
    static final int STADIOLETUSE=386272;
    static final int STADIOLETUSN=6673692;
	LocationManager lm;
    LocationListener locationListener;
    boolean type = false;
	String selecteddate;
	String scale;
    MyMapView mmv;
    
    public void onFinishInputDialog(boolean type,boolean savetrack,String startPointE,String startPointN,String scale) {
    	this.scale = scale;
    	mmv.setScale(scale,true);
    	initScreenType(type);
        if(savetrack){
        	if(!isServiceRunning(LocationService.class))
        		startService(new Intent(getBaseContext(), LocationService.class));
        	mmv.setDate((new java.sql.Date((new java.util.Date()).getTime())).toString(),true);
        }else
        	stopService(new Intent(getBaseContext(), LocationService.class));
    }
    
    private void initScreenType(boolean type){
    	this.type = type;
        mmv.setCenter(type);
        mmv.invalidate();
        if(type && locationListener == null){
        	lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        	locationListener = new MyLocationListener();
        	lm.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER,0,0,locationListener);
        } else if(!type && locationListener != null ){
        	lm.removeUpdates(locationListener);
        	locationListener = null;
        }
    }
    
    public void onFinishDataDialog(int type, String date, boolean in_out){
    	if(type == DataHandling.SHOWDATE){
    		this.selecteddate = date;
    		mmv.setDate(date, false);
    		mmv.invalidate();
    	} else if(type == DataHandling.DELETE){
	        DBAdapter db = new DBAdapter(this);
	        db.open();
	        db.deleteLocations(date);
	        db.close();
    	} else if (type == DataHandling.COPYDB){
         	CopyDatabaseOut(in_out);
    	}
    }
    
    private class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null && type) {
            	int latlon[] = Cordinat.WGS84toETRSTM35FIN(loc.getLatitude(),loc.getLongitude());
            	mmv.setCordinateTrac(latlon[1],latlon[0]);
            }
        }
        @Override
        public void onProviderDisabled(String provider) {
//            Toast.makeText(getBaseContext(),
//                    "Provider: " + provider + " disabled",
//                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
//            Toast.makeText(getBaseContext(),
//                    "Provider: " + provider + " enabled",
//                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status,
                Bundle extras) {/*
            String statusString = "";            
            switch (status) {
                case android.location.LocationProvider.AVAILABLE:
                    statusString = "available";
                case android.location.LocationProvider.OUT_OF_SERVICE:
                    statusString = "out of service";
                case 
                    android.location.LocationProvider.TEMPORARILY_UNAVAILABLE:
                    statusString = "temporarily unavailable";
            }            
            Toast.makeText(getBaseContext(),
                    provider + " " + statusString,
                    Toast.LENGTH_SHORT).show(); */
        }
    }    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mmv = (MyMapView)findViewById(R.id.imageviewTest);
        
        CopyDatabase();

        if(IsExternalStorageAvailableAndWriteable())
        	mmv.setDirectory(getExternalFilesDir(null).getAbsolutePath()+"/");
        
    	if(savedInstanceState != null){
    		scale = savedInstanceState.getString("scale");
    		mmv.setScale(scale, false);
    	}
        
        if(savedInstanceState == null || savedInstanceState.getString("startPointN") == null){
    		int out[] = MissaOlenSettings.loadEandNsettings(this);
    		if(out != null)
    			mmv.setCordinateTrac(out[0],out[1]);
    		else
    			mmv.setCordinateTrac(STADIOLETUSE,STADIOLETUSN);
        } else
    			mmv.setCordinateTrac(Integer.parseInt(savedInstanceState.getString("startPointE")),
    					Integer.parseInt(savedInstanceState.getString("startPointN")));
        
    	if(savedInstanceState != null){
    		selecteddate = savedInstanceState.getString("selecteddate");
    		if(isServiceRunning(LocationService.class))
    			mmv.setDate((new java.sql.Date((new java.util.Date()).getTime())).toString(), true);
    		else
    			mmv.setDate(selecteddate,false);
    		initScreenType(savedInstanceState.getBoolean("type"));
    	}
        
        mmv.setOnTouchListener(new View.OnTouchListener(){
        	
            private int _xDelta = 0;
            private int _yDelta = 0;
            
            public boolean onTouch(View view, MotionEvent event) {
            	
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                        
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        _xDelta = X;
                        _yDelta = Y;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                    	mmv.setCordinate(mmv.getXc() - (X - _xDelta)/2,mmv.getYc() - (Y - _yDelta)/2);
                        break;
                }
                return true;
            }
        });
    }
    
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
	      savedInstanceState.putBoolean("type",type);
	      savedInstanceState.putString("selecteddate",selecteddate);
	      savedInstanceState.putString("scale",scale);
	      if(mmv.getE() > 20000){
	    	  savedInstanceState.putString("startPointE",String.valueOf(mmv.getE()));
	    	  savedInstanceState.putString("startPointN",String.valueOf(mmv.getN()));
	      }
    }

	@Override
    public void onResume() {
        super.onResume();
        if(locationListener!=null)
	        lm.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER,0,0,locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationListener!=null)
        	lm.removeUpdates(locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	
        	FragmentManager fragmentManager = getSupportFragmentManager();
        	ScreenType sc = new ScreenType();
            sc.setCancelable(false);
            sc.setDialogTitle(getResources().getString(R.string.selecttype),
            		type,isServiceRunning(LocationService.class),mmv.getE(),mmv.getN(),scale);
            sc.show(fragmentManager, "Input dialog");

            return true;
        } else if (id == R.id.action_saveddata && !isServiceRunning(LocationService.class)){

        	FragmentManager fragmentManager = getSupportFragmentManager();
        	DataHandling sc = new DataHandling();
            sc.setCancelable(false);
            sc.setDialogTitle(getResources().getString(R.string.trachandel),selecteddate);
            sc.show(fragmentManager, "Input dialog");

        	return true;
        } else if (id == R.id.action_godefaultposition && !isServiceRunning(LocationService.class)){
    		int out[] = MissaOlenSettings.loadEandNsettings(this);
    		if(out != null)
    			mmv.setCordinateTrac(out[0],out[1]);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void CopyDB(InputStream inputStream, 
        OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public void CopyDatabase() {
        String destDir = "/data/data/" + getPackageName() + "/databases/";
        String destPath = destDir + "locations";
        File f = new File(destPath);
        if (!f.exists()) {
        	File directory = new File(destDir);
        	directory.mkdirs();
            try {
				CopyDB(getBaseContext().getAssets().open("locations"),
				        new FileOutputStream(destPath));
			} catch (FileNotFoundException e) {		
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public void CopyDatabaseOut(boolean type) {
    	if(IsExternalStorageAvailableAndWriteable()){
	        String destPath = "/data/data/" + getPackageName() + "/databases/locations";
	        File f = new File(destPath);
	        if (f.exists()) {
	        	File extStorage = getExternalFilesDir(null);
	            try {
	            	if(type)
	            		CopyDB(new FileInputStream(new File(extStorage,"locations")),
								new FileOutputStream(destPath));
	            	else
						CopyDB(new FileInputStream(destPath),
						        new FileOutputStream(new File(extStorage,"locations")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
    	}
    }
    
    public boolean IsExternalStorageAvailableAndWriteable() {
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
        return externalStorageAvailable && externalStorageWriteable;
    }
}
