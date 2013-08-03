package com.svelte.sohub;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class DisplayFriendsPics extends Activity {
	
	ArrayList<Bitmap> bitmaparray= new ArrayList<Bitmap>();
	int lon; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_friends_pics);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	public void onResume()
	{
		super.onResume();
		getfriendpics();
		GridView gridview= (GridView)findViewById(R.id.grid_view);
		gridview.setAdapter(new ImageAdapter(DisplayFriendsPics.this,bitmaparray));
	}
	
	public void getfriendpics()
	{
		final class GetPics extends AsyncTask<String, Void, ArrayList<Bitmap>>
		{
			ProgressDialog pdia;
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				pdia = new ProgressDialog(DisplayFriendsPics.this);
		        pdia.setMessage("Loading...");
		        pdia.show();    
			}
			
			protected ArrayList<Bitmap> doInBackground(String... urls)
			{
				for(String url:urls)
				{
					HttpGet httpget = new HttpGet(url);
					HttpResponse response;
					HttpClient httpclient = new DefaultHttpClient();
					String result;
					try
					{
						 response = httpclient.execute(httpget);
						 HttpEntity entity = response.getEntity();
						 ArrayList<Bitmap> bitmarray= new ArrayList<Bitmap>();
		                 result = EntityUtils.toString(entity);
		                 bitmarray=parseJSON(result);
		                 return bitmarray;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				return null;
			}
			@Override
			protected void onPostExecute(ArrayList<Bitmap> bma)
			{
				bitmaparray= bma;
			}
		}
		GetPics gp = new GetPics();
		gp.execute("https://graph.facebook.com/" + Prefs.getMyStringPref(DisplayFriendsPics.this, "id") + "/photos?access_token=" + Prefs.getMyStringPref(DisplayFriendsPics.this, "accesstoken"));
	}
	
	public ArrayList<Bitmap> parseJSON(String data)
	{
		Bitmap bitmap;
		ArrayList<Bitmap> bmarr= new ArrayList<Bitmap>();
		try
		{
			 JSONObject jObj = new JSONObject(data);if(jObj==null) Log.i("-----------","JOBJ IS NULL");
	         JSONArray jObjArr= jObj.optJSONArray("data");if(jObjArr==null){ Log.i("-----------","JOBJARR IS NULL"); return null;}
             lon = jObjArr.length();
             String[] picurls= new String[5];	//Change
             for(int i=0;i<5;i++)	//Change
             {
                 JSONObject tmp = jObjArr.optJSONObject(i);
                 picurls[i]=tmp.getString("picture");
                 Log.i("----------","picture:");
             }
             for(int m=0;m<5;m++)
             {
            	 bitmap = BitmapFactory.decodeStream((InputStream)new URL(picurls[m]).getContent());
            	 bmarr.add(bitmap);
             }
		}
		catch(Exception e)
		{
			Log.i("Exception1 >> ", e.toString());
		}
		return bmarr;
	}
	
	
	
		/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_friends_pics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
