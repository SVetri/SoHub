package com.svelte.sohub;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class FBFriends extends ListActivity {

	private UiLifecycleHelper uiHelper;
	LoginButton authButton;
	Session s;
	String maccesstoken=null;
	String frnd_data[][];
	int lon;
	int flag=0;
	String result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fbfriends);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		s=new Session(FBFriends.this);
		authButton= (LoginButton)findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("friends_about_me","friends_birthday","friends_photos"));
		// Show the Up button in the action bar.
		setupActionBar();
	}
	

	private Session.StatusCallback callback = new Session.StatusCallback() 
	{
		@Override
	    public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) 
	{
	    if (state.isOpened()) 
	    {
	    	// make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() 
	          {

	            // callback after Graph API response with user object
	            @SuppressLint("NewApi")
				@Override
	            public void onCompleted(GraphUser user, Response response) 
	            {
	              Log.i("-----------","In the state.isOpened!");
	              Prefs.setMyBoolPref(FBFriends.this, "ses", true);
	              if (user != null) 
	              {
	            	  new AlertDialog.Builder(FBFriends.this).setIcon(R.drawable.ic_launcher).setTitle("Hello " + user.getName() + "! Welcome to SoHub!").setPositiveButton("OK",null).show();
	            	  Log.i("------------","Flag: " +flag);
	            	  if(flag==0)
	            	  {
	            			  loadfriendinfo();
	            	  }
	              }
	              }
	          });
	    } 	
	    else if (state.isClosed()) 
	    {
	    	Log.i("------------","In the state.isClosed()!");
	    	Prefs.setMyBoolPref(this, "ses", false);
	    	Log.i("-----------","The ses: "+ Prefs.getMyBoolPref(this, "ses"));
	    	new AlertDialog.Builder(FBFriends.this).setIcon(R.drawable.ic_launcher).setTitle("You Have Logged Out Of Facebook").setPositiveButton("OK",null).show();
	    	flag=0;
	    	Intent inten=new Intent(FBFriends.this,MainActivity.class);
	    	startActivity(inten);
	    }
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onResume()
	{
		super.onResume();
		Prefs.deletePref(this);
		uiHelper.onResume();
		Log.i("--------------","In onResume() ses: " + Prefs.getMyBoolPref(this, "ses"));
		maccesstoken=s.getAccessToken();
		if(!(maccesstoken.isEmpty())) //This should actually be false
		{
			Prefs.setMyStringPref(this, "access_token", maccesstoken);
			Log.i("-------","Calling loadfriendinfo from onResume!");
			loadfriendinfo();
		}
	}
 
	@SuppressLint("NewApi")
	public void loadfriendinfo()
	{
		final class RetreiveFriends extends AsyncTask<String, Void, String[]>
		{
			ProgressDialog pdia;
			@Override
			protected void onPreExecute()
			{ 
			   super.onPreExecute();
			        pdia = new ProgressDialog(FBFriends.this);
			        pdia.setMessage("Loading...");
			        pdia.show();    
			}

			 @SuppressLint("NewApi")
			protected String[] doInBackground(String... urls) 
			 {
				 flag=1;
				 Log.i("-----","Im in");
				for(String url:urls)
				{
				 HttpGet httpget = new HttpGet(url);
					HttpResponse response;
					HttpClient httpclient = new DefaultHttpClient();
					Log.i("--------","No probs");
					try
			        {
						Log.i("-----------","In the try!"); 
						response = httpclient.execute(httpget);
						Log.i("-----------------","Got the response!");
			                HttpEntity entity = response.getEntity();
			             		Log.i("------------","Got response");
			                    String friends[]=null;
			                	result = EntityUtils.toString(entity);
			                	friends=parseJSON(result);
			                	Log.i("-----------","Im in here!");
			                    if(friends==null)
			                    {
			                    	Log.e("------------------","parseJSON() error!");
			                    }
			                    else
			                    {
			                    	return friends;
			                    }
			        }
					catch (Exception e) 
					{
			            e.printStackTrace();
			        }
				}
				Log.e("--------------------","ITS returning null!");
				return null;
			}
			
			@Override
			protected void onPostExecute(String[] result) 
			{
				if(result==null)
				{
					  new AlertDialog.Builder(FBFriends.this).setIcon(R.drawable.ic_launcher).setTitle("There was a problem with your Facebook Login! Trying logging in again!").setPositiveButton("OK",null).show();
				}
				else
				{
				ArrayAdapter<String>friend_names= new ArrayAdapter<String>(FBFriends.this,R.layout.fbfriendslayout,result);
				setListAdapter(friend_names);
				pdia.dismiss();
				}
			}
		}
		String maccesstoken=s.getAccessToken();
		RetreiveFriends f= new RetreiveFriends();
		Log.i("-----------","maccesstoken: " + maccesstoken);
		if(!(maccesstoken.isEmpty()))
	  	{
			f.execute(new String[] {"https://graph.facebook.com/me/friends?access_token=" + maccesstoken});
	  	}
	  else
	  {
		  new AlertDialog.Builder(FBFriends.this).setIcon(R.drawable.ic_launcher).setTitle("There was a problem with your Facebook Login! We're trying to get around it!!").setPositiveButton("OK",null).show();
		  f.execute(new String[] {"https://graph.facebook.com/me/friends?access_token=" + Prefs.getMyStringPref(this, "access_token")});
		  Log.i("--------------","Prefs maccesstoken: " + Prefs.getMyStringPref(FBFriends.this, "access_token"));
	  }
	}
	
	public String[] parseJSON(String data)
	{	
		String entity1[];
		try
		{
            JSONObject jObj = new JSONObject(data);if(jObj==null) Log.i("-----------","JOBJ IS NULL");
            JSONArray jObjArr= jObj.optJSONArray("data");if(jObjArr==null){ Log.i("-----------","JOBJARR IS NULL"); return null;} 
                lon = jObjArr.length();	
                frnd_data = new String[jObjArr.length()][2];
                entity1 = new String[jObjArr.length()];
                Log.i("Friends >>", "" + lon);
                for(int i = 0 ;i< lon; i++)
                    {
                      JSONObject tmp = jObjArr.optJSONObject(i);
                      frnd_data[i][0]=tmp.getString("name");
                      frnd_data[i][1]=tmp.getString("id");
                      Log.i("Name", frnd_data[i][0]);
                      Log.i("Name AND Id" , frnd_data[i][0] +"  "+ frnd_data[i][1]);
                    }
                String frndnames[]= new String[lon];
                for(int j=0; j<lon; j++)
                {
                	frndnames[j]= frnd_data[j][0];
                }
                return frndnames;
        }
		catch(Exception e)
		{
			Log.i("Exception1 >> ", e.toString());
			parseJSON(result); // If it reaches here, theres an error with the maccesstoken, exit the activity accordingly
		}
		return null;
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Log.d("LIST","SELECTED ITEM #"+ position);
		String fbname = new String(((TextView)v.findViewById(R.id.fbfriends)).getText().toString());
		String frnd_id=null;
		for(int i=0; i<lon; i++)
		{
			Log.i("-------",fbname+frnd_data[i][0] );
			if(frnd_data[i][0].equals(fbname))
			{
				frnd_id=frnd_data[i][1];
				break;
			}
		}
		if(frnd_id==null)
		{
			Log.e("---------------------","onListItemClick error");
		}
			Intent i= new Intent(this,FBDescription.class);
			maccesstoken=s.getAccessToken();
			Prefs.setMyStringPref(this, "name", fbname);
			Prefs.setMyStringPref(this, "id", frnd_id);
			Prefs.setMyStringPref(this, "accesstoken", maccesstoken);
			startActivity(i);
	}
	
	public String getFriend(String tmpurl) 
    {
        HttpClient httpclient = new DefaultHttpClient();
        String temp_result = null;
          HttpGet httpget = new HttpGet(tmpurl); 
          HttpResponse response;
          try
          {
               response = httpclient.execute(httpget);
                  HttpEntity entity = response.getEntity();
                  if (entity != null)
                  {
                      temp_result = EntityUtils.toString(entity);
                      Log.i("Request String",temp_result);
                      return temp_result;
                  }
          }
         catch (Exception e) 
         {
           e.printStackTrace();
         }
         return temp_result;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
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
		getMenuInflater().inflate(R.menu.fbfriends, menu);
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
