package com.dedo.kanskweather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;

/**
 * Implementation of App Widget functionality. App Widget Configuration
 * implemented in {@link KanskWidgetConfigureActivity
 * KanskWidgetConfigureActivity}
 */
public class KanskWidget extends AppWidgetProvider {
	public static String TAG = "KanskWidget";
	public static String url_graphik = "http://reyo.no-ip.org/temp/rs0-day.png";
	public static String url_temperature = "http://reyo.no-ip.org/temp/term.txt";
	public static String url_bar ="http://reyo.no-ip.org/temp/rs.txt";
	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		//final int N = appWidgetIds.length;
		//for (int i = 0; i < N; i++) {
			//updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
				//	}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//-----------------------------update widgetfrom click on image
			Intent intent = new Intent(context, KanskWidget.class);
			intent.setAction(ACTION_WIDGET_RECEIVER);
            intent.putExtra("msg", "Updated");
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kansk_widget);
		    views.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
		    appWidgetManager.updateAppWidget(appWidgetIds, views);
		    //---------------------------
		    
		   
				
				for (int id : appWidgetIds) {update(context, appWidgetManager, id);}	
				
			
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// When the user deletes the widget, delete the preference associated
		// with it.
		
		
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	public static void update(final Context context, final AppWidgetManager appWidgetManager, final int id) {
		new Thread() {
			public void run() {
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kansk_widget);
			//temperature from term.txt
				
				String temper = "0";
				String bar = "0";
				try {
					temper = getStringFromUrl(context,url_temperature);
					bar = getStringsFromUrl(context,url_bar);
					String[] str = temper.split(";");
				    views.setTextViewText(R.id.textView2, str[0]);
				    views.setTextViewText(R.id.textView4, str[2]+ "мм");
				    
					
				} catch (Exception e) {
					Log.d(TAG,"Error in data");
					
				}	
					
				
				
				//------------------------------------------------------------------
								
				//If you want to Load image from url----- 	Bitmap img = getBitmapFromUrl(context, url_graphik);
				
				
				//---Downloading Data from  from term.txt------------------------------------------
				//get bars and decode from string to float
				
					String bars[] = bar.split("\n");
					
					//float [] f = new float[bars.length];
					float [] f = new float[48];
							
					for(int i = 0; i < bars.length; i++) {
					     f[i] = Float.parseFloat(bars[i]);
					     f[i]=100-10*(f[i]-720)/4;   
					}
					
						
				//image from makegraphik
				Bitmap img = MakeGraphik(context, f);
				
				//Draw  Image
				views.setImageViewBitmap(R.id.imageView1, img);
				
				
				appWidgetManager.updateAppWidget(id, views);	
			}
		}.start();
				
	}
	
private static Bitmap MakeGraphik(Context context, float[] f) 
{
	// make bitmap and canvas
	Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    //paint options
    Paint paint = new Paint();
    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    c.drawColor(Color.LTGRAY);
    paint.setColor(Color.GREEN);
    // Green lines
    for (int i=0; i<101;i=i+25)
    {	
    c.drawLine(i, 0, i, 100, paint);
    c.drawLine(0, i, 100, i, paint);
    }
    paint.setColor(Color.RED);
    paint.setStyle(Paint.Style.STROKE);
    //painting
    
    
    // array for test graphik
    //float nums[] = { 0, 10,20, 20, 30, 30, 40, 40, 50, 50 };
    
    // Path for graphik
    Path graph = new Path();
    graph.moveTo(0, f[0]);
    float x=0;
    for(float y : f) { 
    graph.lineTo(x, y);
    x=x+2;
    }
    c.drawPath(graph, paint);
   
    return b;
	}
//Download image graphic
@SuppressWarnings("unused")
private static Bitmap getBitmapFromUrl(Context context, final String url) {
		try {
			Log.d(TAG, "Downloading image from  url: " + url);
			return BitmapFactory.decodeStream((InputStream) new java.net.URL(url).getContent());
		} catch (Exception e) {
			Log.d(TAG, "Image cannot be loaded: " + e);
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		}	
    }
//Download Misha's data temperature bar and vlag from text file
private static String getStringFromUrl(Context context, final String url) {
	try {
		
		URL urlstr = new URL(url);
		Log.d(TAG, "Downloading string from  url: " + url_temperature);
		BufferedReader in = new BufferedReader(new InputStreamReader(urlstr.openStream()));
	    String str;
	    String out=null;
	    while ((str = in.readLine()) != null) {
	      out = str;
	    }
	    in.close();
		return out;
			}
		catch (Exception e) {
		Log.d(TAG, "Temperature cannot be loaded: " + e);
		return "0";
	}	
}
//downloading all content from URL
private static String getStringsFromUrl(Context context, final String url) {
	try {
		URL urlstr = new URL(url);
		Log.d(TAG, "Downloading string from  url: " + url_bar);
		BufferedReader in = new BufferedReader(new InputStreamReader(urlstr.openStream()));
		StringBuilder content = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null)
		{
		    content.append(line + "\n");
		}
		 
	    in.close();
	    return content.toString();
		} 
		catch (Exception e) {
		Log.d(TAG, "Bar cannot be loaded:  " + e);
		return "0";
		}
		
}
@Override 
//---toast on widget
public void onReceive(Context context, Intent intent) 
{ 


   //  RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kansk_widget);
     // find your TextView here by id here and update it.
     final String action = intent.getAction();
     if (ACTION_WIDGET_RECEIVER.equals(action)) {
          String msg = "null";
          try {
                msg = intent.getStringExtra("msg");
//----------------------------------------------Update widget
                //context.sendBroadcast(new Intent(FORCE_WIDGET_UPDATE)); from any place
                Bundle extras = intent.getExtras();
                if(extras!=null) {
                 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                 ComponentName thisAppWidget = new ComponentName(context.getPackageName(), KanskWidget.class.getName());
                 int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                 onUpdate(context, appWidgetManager, appWidgetIds);
//------------------------------------------------------------                 
                }
          } catch (NullPointerException e) {
                Log.e("Error", "msg = null");
          }
         // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
     } 
     super.onReceive(context, intent);
     	
} 
/*
private static Byte PingServer() throws IOException, InterruptedException
{
	String server1 = "vssassddf123sd.org";
    Runtime runtime = Runtime.getRuntime();
    String commandping="ping -c 1 -W 1 " +server1;
    Process proc = runtime.exec(commandping); 
    proc.waitFor();
    int exit = proc.exitValue();
    Log.d(TAG,"Ping KOD " + String.valueOf(exit));
    if (exit == 0) { 
    return 0;
    } else { 
    return 1;	
    }
	
}*/
}
