package com.aml.missaolen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;

public class MissaOlenSettings {
	static final String DEFAULTCORDINATE = "/DEFAULTCORDINATE";

    public static void saveEandNsettings(Context ctx, int startPointE, int startPointN){
		try{
			OutputStream outputStream = new FileOutputStream(ctx.getExternalFilesDir(null)+DEFAULTCORDINATE);			
			outputStream.write((startPointE+"-"+startPointN).getBytes());
			outputStream.close();
		}catch(Exception e){
		}
    }
    
    public static int[] loadEandNsettings(Context ctx) {
    	int out[] = null;
		try{
			FileInputStream fis = new FileInputStream(ctx.getExternalFilesDir(null).getAbsolutePath()+DEFAULTCORDINATE);
			InputStreamReader isr = new InputStreamReader(fis);
			char[] inputBuffer = new char[20];
			int len = isr.read(inputBuffer,0,20);
			String tmp = new String(inputBuffer,0,len);
			String str[] = tmp.split("-");
			out = new int[2];
			out[0] = Integer.parseInt(str[0]);
			out[1] = Integer.parseInt(str[1]);
	    	if(out[0] == 0 || out[1] == 0)
	    		out = null;
			isr.close();
	    	fis.close();
		} catch(Exception e){
			return null;
		}
		return out;
    }
}
