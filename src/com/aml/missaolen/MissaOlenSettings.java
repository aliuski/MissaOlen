package com.aml.missaolen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class MissaOlenSettings {
	static final String DEFAULTCORDINATE = "/DEFAULTCORDINATE";

    public static void saveEandNsettings(Context ctx, String startPointE, String startPointN){
		try{
			OutputStream outputStream = new FileOutputStream(ctx.getExternalFilesDir(null)+DEFAULTCORDINATE);
			outputStream.write((startPointE+"-"+startPointN).getBytes());
			outputStream.close();
		}catch(Exception e){
			Log.d("AML","MissaOlenSettings: "+ e);
		}
    }
    
    public static String[] loadEandNsettings(Context ctx) {
    	String out[];
		try{
			FileInputStream fis = new FileInputStream(ctx.getExternalFilesDir(null).getAbsolutePath()+DEFAULTCORDINATE);
			InputStreamReader isr = new InputStreamReader(fis);
			char[] inputBuffer = new char[20];
			int len = isr.read(inputBuffer,0,20);
			String tmp = new String(inputBuffer,0,len);
			out = tmp.split("-");
	    	isr.close();
	    	fis.close();
		} catch(Exception e){
			return null;
		}
		return out;
    }
}
