package com.svelte.sohub;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class FBDescription extends Activity {
	
	String maccesstoken;
	String frndid;
	String frndname;
	ImageView i;
	TextView name;
	TextView bioname;
	Button frndphotos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fbdescription);
		i = (ImageView)findViewById(R.id.profilepic);
		frndphotos=(Button) findViewById(R.id.fbphotosbutton);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	public void onResume()
	{
		super.onResume();
		name= (TextView) findViewById(R.id.fbname);
		name.setText(Prefs.getMyStringPref(this, "name"));
		bioname=(TextView) findViewById(R.id.fbbioname);
		bioname.setText(Prefs.getMyStringPref(this, "name")+ "'s Bio:");
		TextView txt=(TextView)findViewById(R.id.fbbio);
		txt.setMovementMethod(new ScrollingMovementMethod());
		if(Prefs.getMyStringPref(this, "bio").contains("default"))
		{
		loadfrienddata();
		loadfriendpic();
		}
		else
		{
			Log.i("--------------------","Prefs are being used!");
			TextView fbbio=(TextView) findViewById(R.id.fbbio);
			fbbio.setText(Prefs.getMyStringPref(this, "bio"));
			TextView fbbir=(TextView) findViewById(R.id.friendbirthday);
			fbbir.setText(Prefs.getMyStringPref(this, "birthday"));
			loadfriendpic();
		}
		i.setOnClickListener(new ImageView.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent f= new Intent(FBDescription.this, ProfPicViewer.class);
				startActivity(f);
			}
		});
		
		frndphotos.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent j=new Intent(FBDescription.this, DisplayFriendsPics.class);
				startActivity(j);
			}
		});
	}
	public void loadfrienddata()
	{
		final class GetInfo extends AsyncTask<String, Void, String[]>
		{
			protected String[] doInBackground(String... urls)
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
						 String info[]= new String[2];
		                 result = EntityUtils.toString(entity);
		                 info=parseJSON(result);
		                 return info;
			        }
					catch (Exception e) 
					{
			            e.printStackTrace();
			        }
				}
				return null;
			}
			@Override
			protected void onPostExecute(String[] result) 
			{
				String bio;
				String birthday;
				bio=result[0];
				birthday=result[1];
				TextView fbbio=(TextView) findViewById(R.id.fbbio);
				fbbio.setText(bio);
				Prefs.delPref(FBDescription.this, "bio");
				Prefs.setMyStringPref(FBDescription.this, "bio", bio);
				TextView fbbir=(TextView) findViewById(R.id.friendbirthday);
				fbbir.setText(birthday);
				Prefs.delPref(FBDescription.this, "birthday");
				Prefs.setMyStringPref(FBDescription.this, "birthday", birthday);
			}
		}
		GetInfo f= new GetInfo();
		f.execute(new String[] {"https://graph.facebook.com/"+ Prefs.getMyStringPref(FBDescription.this, "id") + "?access_token=" + Prefs.getMyStringPref(FBDescription.this, "accesstoken")+"&fields=bio,birthday"});
	}
	
	public void loadfriendpic()
	{
		final class GetProfPic extends AsyncTask<String, Void, Bitmap>
		{
			ProgressDialog pdia;
			@Override
			protected void onPreExecute()
			{ 
			   super.onPreExecute();
			        pdia = new ProgressDialog(FBDescription.this);
			        pdia.setMessage("Loading...");
			        pdia.show();    
			}

			protected Bitmap doInBackground(String... urls)
			{
				for(String url:urls)
				{
					try
	                 {
	                	 Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
	                	 return bitmap;
	                 }
	                 catch (MalformedURLException e) 
	                 {
	                	  e.printStackTrace();
	                 } 
	                 catch (IOException e) 
	                 {
	                	  e.printStackTrace();
	                 }
				}
				return null;
			}
				@Override
				protected void onPostExecute(Bitmap b)
				{
					i = (ImageView)findViewById(R.id.profilepic);
					i.setImageBitmap(b);
					pdia.dismiss();
				}
			}
		GetProfPic g= new GetProfPic();
		g.execute(new String[]{"https://graph.facebook.com/"+ Prefs.getMyStringPref(FBDescription.this, "id") + "/picture?width=60&height=80"});
	}
	
	public String[] parseJSON(String data)
	{
		String retinfo[]=new String[2];
		String frnd_bio=new String();
		String frnd_birthday= new String();
		try
		{
            JSONObject jObj = new JSONObject(data);if(jObj==null) Log.i("-----------","JOBJ IS NULL");
            frnd_bio=jObj.getString("bio");
            Log.i("------------","Bio:"+frnd_bio);
		}
		catch(Exception e)
		{
			Log.i("Exception1 >> ", e.toString());
			frnd_bio="Looks Like " + Prefs.getMyStringPref(FBDescription.this,"name") +" doesn't have a Facebook bio!";
		}
		try
		{
			JSONObject jObj = new JSONObject(data);if(jObj==null) Log.i("-----------","JOBJ IS NULL");
			frnd_birthday=jObj.getString("birthday");
			Log.i("------------","Birthday"+frnd_birthday);
		}
		catch(Exception e)
		{
			Log.i("Exception1 >> ", e.toString());
		}
		retinfo[0]=frnd_bio;
        retinfo[1]=frnd_birthday;
		return retinfo;
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
		getMenuInflater().inflate(R.menu.fbdescription, menu);
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
