package com.svelte.sohub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class ProfPicViewer extends Activity {

	ImageView i;
	Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prof_pic_viewer);
		loadprofpicbig();
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Button picdownload=(Button) findViewById(R.id.downloadpic);
		picdownload.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				 File sdCardDirectory = Environment.getExternalStorageDirectory();
				 File image = new File(sdCardDirectory ,Prefs.getMyStringPref(ProfPicViewer.this, "id")+"sohub.png");
				 Boolean success = false;
				 FileOutputStream outStream;
				 try 
				 {

				        outStream = new FileOutputStream(image);
				        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream); 
				        /* 100 to keep full quality of the image */
				        outStream.flush();
				        outStream.close();
				        success = true;
				 }
				 catch (FileNotFoundException e) 
				 {
					 e.printStackTrace();
				 } 
				 catch (IOException e) 
				 {
					 e.printStackTrace();
				 }
				 if(success==true)
				 {
					 Toast.makeText(ProfPicViewer.this, "Picture Saved Successfully!", Toast.LENGTH_LONG).show();
				 }
				 else if(success==false)
				 {
					 Toast.makeText(ProfPicViewer.this, "Error saving the image!", Toast.LENGTH_LONG).show();
				 }
				 
			}
		});
	}
	
	public void loadprofpicbig()
	{
		final class GetProfPic extends AsyncTask<String, Void, Bitmap>
		{
			ProgressDialog pdia;
			@Override
			protected void onPreExecute()
			{ 
				super.onPreExecute();
				pdia = new ProgressDialog(ProfPicViewer.this);
				pdia.setMessage("Loading...");
				pdia.show();    
			}

			protected Bitmap doInBackground(String... urls)
			{
				for(String url:urls)
				{
					try
					{
						bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
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
					i = (ImageView)findViewById(R.id.profpicbig);
					i.setImageBitmap(b);
					pdia.dismiss();
				}
	
		}
		GetProfPic g= new GetProfPic();
		g.execute(new String[]{"https://graph.facebook.com/"+Prefs.getMyStringPref(this, "id")+"/picture?height=200&width=150"});
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
		getMenuInflater().inflate(R.menu.prof_pic_viewer, menu);
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
