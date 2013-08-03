package com.svelte.sohub;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

public class ContactDescription extends Activity {

	String name;
	TextView num;
	TextView em;
	TextView notename;
	TextView notedata;
	String id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_description);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent=getIntent();
		name=intent.getStringExtra(MainActivity.CONTACT_NAME);
		id=intent.getStringExtra("sid");
		Log.i("-------","From contactdesc: " + id);
		TextView tv =(TextView)findViewById(R.id.ctcname);
		tv.setText(name);
		num = (TextView) findViewById(R.id.phnum);
		em = (TextView) findViewById(R.id.email);
		notename = (TextView) findViewById(R.id.ctcnotename);
		notename.setText(name + "'s Bio:");
		notedata = (TextView) findViewById(R.id.ctcnote);
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onResume()
	{
		super.onResume();
		String number;
		String email;
		String note;
		String sel = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
		String[] phnums= new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
		Cursor c1= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phnums, sel, null, null);
		if (c1.moveToFirst()) 
		{
		    number = c1.getString(0);
		    num.setText(number);
		}
		
		else
		{
			num.setText("N/A");
		}
		c1.close();
		
		String sel2 = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id;
		String[] emailids= new String[] {ContactsContract.CommonDataKinds.Email.ADDRESS};
		Cursor c2= getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, emailids, sel2, null, null);
		if (c2.moveToFirst()) 
		{
		    email = c2.getString(0);
		    em.setText(email);
		    Log.i("------------","email:" + email);
		}
		
		else
		{
			em.setText("N/A");
		}
		
		c2.close();
		
		String sel3 = ContactsContract.Data.CONTACT_ID + " = ? AND " +ContactsContract.Data.MIMETYPE + " = ?";
		String[] sel3params = new String[] {id,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
		String[] notes = new String[] {ContactsContract.CommonDataKinds.Note.NOTE};
		Cursor c3= getContentResolver().query(ContactsContract.Data.CONTENT_URI, notes, sel3, sel3params, null);
		if(c3.moveToFirst())
		{
			note=c3.getString(0);
			notedata.setText(note);
		}
		else
		{
			notedata.setText("N/A");
		}
		
		c3.close(); 
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
		getMenuInflater().inflate(R.menu.contact_description, menu);
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
