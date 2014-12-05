package com.aml.missaolen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ListIterator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

public class MyMapView extends View{
	private String directory;
	private Context context;
	private Paint paint3;
	private Paint paint;
	private Bitmap bg;
	private Canvas mcanvas;
	private int x;
	private int y;
	private boolean setcenter = false;
	private int map_x;
	private int map_y;
	private DBAdapter db;
	private String[] selectdate;
	private ArrayList <Point>tracpointlist;
	private boolean forceshowtracpoint;
	private boolean map_loading;
	
	public MyMapView(Context context) {
        super(context);
        this.context = context;
        init();
	}
	public MyMapView(Context context, AttributeSet set) {
    	super(context,set);
    	this.context = context;
    	init();
    }
	
	private void init() {
		paint = new Paint();
		paint.setTextSize(40);
        paint3 = new Paint();
        paint3.setStrokeWidth(3);
        x = 10;
        y = 10;
        bg = Bitmap.createBitmap(2700, 2700, Bitmap.Config.ARGB_8888);
        mcanvas = new Canvas(bg);
        db = new DBAdapter(context);
	}
	
	public void setDirectory(String directory){
		this.directory = directory;
	}
	
	public void setDate(String date, boolean forceshowtracpoint){
		if(date == null || date.length() == 0)
			selectdate = null;
		else if(date.length() != 10)
			selectdate = new String[]{"All"};
		else
	    	selectdate = new String[]{date,
    			(new java.sql.Date(java.sql.Date.valueOf(date).getTime()+86400000)).toString()};
		tracpointlist = null;
		this.forceshowtracpoint = forceshowtracpoint;
	}
	
	private void makeMap(){
		map_loading = true;
		/*
		if(bg!=null){
			bg.recycle();
			bg=null;
		}
        bg = Bitmap.createBitmap(2700, 2700, Bitmap.Config.ARGB_8888);
        mcanvas = new Canvas(bg);
        */
        int tx = map_x;
        int ty = map_y;
        
        loadScreenPart(tx,ty,0,0);
		tx+=1800;
		loadScreenPart(tx,ty,900,0);
		tx+=1800;
        loadScreenPart(tx,ty,1800,0);
		tx=map_x;
        ty-=1800;
        loadScreenPart(tx,ty,0,900);
		tx+=1800;
		loadScreenPart(tx,ty,900,900);
		tx+=1800;
		loadScreenPart(tx,ty,1800,900);
		tx=map_x;
		ty-=1800;
		loadScreenPart(tx,ty,0,1800);
		tx+=1800;
		loadScreenPart(tx,ty,900,1800);
		tx+=1800;
		loadScreenPart(tx,ty,1800,1800);
		map_loading = false;
	}

	private void loadScreenPart(int tx,int ty,int px,int py){
	    File imgFile = new File(directory+tx+"-"+ty+".png");
	    if(!imgFile.exists())
	    	new ReadMapTask().execute(String.valueOf(tx),String.valueOf(ty),String.valueOf(px),String.valueOf(py));
	    else {
	    	Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	    	if(bmp != null)
	    		mcanvas.drawBitmap(bmp, px, py, paint);
	    }
	}
	
	public void setCordinateTrac(int mx, int my){
		if(mx >= (map_x+1800) || mx <= (map_x-1800) || my >= (map_y+1800) || my <= (map_y-1800)){
			int f;
			int map_x_t = map_x;
			int map_y_t = map_y;
			
			for(f=188500 ; f<712300 ; f+=1800){
				if(f > (mx - 3600)){
					map_x_t = f;
					break;
				}
			}
			for(f=6628700 ; f<6994100 ; f+=1800){
				if(f > (my + 1800)){
					map_y_t = f;
					break;
				}
			}
			if(map_x_t != map_x || map_y_t != map_y){
				map_x = map_x_t;
				map_y = map_y_t;
				makeMap();
			}
		}
		this.x = (mx - map_x)/2;
		this.y = (map_y - my)/2;
		invalidate();
	}
	
	public int getE(){
		return 2 * this.x + map_x;
	}
	
	public int getN(){
		return map_y - this.y * 2;
	}
	
	public void setCordinate(int x, int y){
		if(!setcenter && !map_loading){
			this.x = x;
			this.y = y;
			invalidate();
		}
	}
	public int getXc(){
		return x;
	}
	public int getYc(){
		return y;
	}

	public void setCenter(boolean setcenter){
		this.setcenter = setcenter;
	}
	
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
		int t_x = this.x - this.getWidth()/2;
		int t_y = this.y - this.getHeight()/2;
    	
    	if(t_x<0)
    		t_x=0;
    	if(t_y<0)
    		t_y=0;
    	if(t_x>=(2700-this.getWidth()))
    		t_x = 2700-this.getWidth();
    	if(t_y>=(2700-this.getHeight()))
    		t_y = 2700-this.getHeight();
			
    	if(!setcenter){
			boolean new_map = false;
	    	if(t_x == 0){
	    		new_map = true;
	    		map_x -= 1800;
	    		x = 900;
	    	}
	    	if(t_y == 0){
	    		new_map = true;
	    		map_y += 1800;
	    		y = 900;
	    	}
	    	if(t_x == (2700-this.getWidth())){
	    		new_map = true;
	    		map_x += 1800;
	    		x -= 900;
	    	}
	    	if(t_y == (2700-this.getHeight())){
	    		new_map = true;
	    		map_y -= 1800;
	    		y -= 900;
	    	}
	    	if(new_map)
	    		makeMap();
    	}
    	canvas.drawBitmap(Bitmap.createBitmap(bg, t_x, t_y, this.getWidth(), this.getHeight()), 0, 0, paint);
    	if(setcenter){
	    	int cx = this.getWidth()/2;
	    	int cy = this.getHeight()/2;
	         paint3.setColor(Color.BLUE);
	         canvas.drawLine(cx-25, cy-25, cx+25, cy+25, paint3);
	         canvas.drawLine(cx+25, cy-25, cx-25, cy+25, paint3);
    	}
    	if(selectdate != null) {
    		paint3.setColor(Color.BLUE);
    		readPointFromDb(canvas,t_x,t_y);
    	}
    	canvas.drawText("N "+(map_y - this.y * 2), 5, 40, paint);
    	canvas.drawText("E "+(2 * this.x + map_x), 5, 80, paint);
    }
    
    private void readPointFromDb(Canvas canvas, int t_x, int t_y){
    	
    	if(tracpointlist == null || forceshowtracpoint){
    		tracpointlist = new ArrayList<Point>();
		    db.readableopen();
		    Cursor c;
		    if(selectdate.length == 1)
		    	c = db.getLocations(null,null,null);
		    else
		    	c = db.getLocations("date>? and date<?",selectdate,null);
		    if(c != null ) {
		        if(c.moveToFirst()) {
		            do {
					    tracpointlist.add(new Point(c.getInt(2), c.getInt(1)));
		            } while (c.moveToNext());
		        }
		    }
		    c.close();
		    db.close();
    	}
    	
    	if(tracpointlist != null){
    	    ListIterator <Point>it = tracpointlist.listIterator();
    	    while(it.hasNext()){
    	    	Point p = (Point)it.next();
			    int tr_x = (p.x - map_x)/2 - t_x;
			    int tr_y = (map_y - p.y)/2 - t_y;
	    		if(tr_x > 0 && tr_y > 0 && tr_x < this.getWidth() && tr_y < this.getHeight())
				    canvas.drawRect(tr_x-2, tr_y-2, tr_x+3, tr_y+3, paint3);
    	    }
    	}
    }
    
    private class ReadMapTask extends AsyncTask
    <String, Void, String> {
    	int a;
    	int b;
        @Override
        protected String doInBackground(String... urls) {
        	a = Integer.parseInt(urls[2]);
        	b = Integer.parseInt(urls[3]);
            return getMaterialFromMap(urls[0],urls[1]);
        }
        @Override
        protected void onPostExecute(String result) {
        	if(result != null)
        		mcanvas.drawBitmap(BitmapFactory.decodeFile(directory+result), a, b, paint);
        }
    }

	public String getMaterialFromMap(String x, String y) {
		try{
			URL url = new URL(
				"http://kansalaisen.karttapaikka.fi/image?request=GetMap&bbox="
				+x+","+y+","+(Integer.parseInt(x) + 1800)+","+(Integer.parseInt(y) - 1800)
				+"&scale=8000&width=900&height=900&srs=EPSG:3067&styles=normal&lang=fi&lmid=1410107944324");
			URLConnection con = url.openConnection();
	
			InputStream input = con.getInputStream();
			byte[] buffer = new byte[4096];
			int n = - 1;
			OutputStream output = new FileOutputStream(new File(directory,x+"-"+y+".png"));
			while ( (n = input.read(buffer)) != -1)
			{
				if (n > 0)
					output.write(buffer, 0, n);
			}
			output.close();
			
		}catch(Exception e){
			return null;
		}
	return x+"-"+y+".png";
	}
}
