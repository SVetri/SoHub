package com.svelte.sohub;

import java.util.ArrayList;
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;

import java.util.List;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity 
{
	public final static String CONTACT_NAME="com.svelte.sohub.NAME";
	Cursor cursor=null;
	String details[][];
	int len;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		Button btnctc= (Button) findViewById(R.id.contacts);
		Button btnfb= (Button) findViewById(R.id.facebook);
		dispcontacts();
		btnctc.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				dispcontacts();
			}
		});
		btnfb.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent i= new Intent(MainActivity.this, FBFriends.class);
				startActivity(i);
			}
		});
	}
	
	private void dispcontacts()
	{
		List<String> contactnames=new ArrayList<String>();
		int i=0;
		cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		len=cursor.getCount();
		details= new String[len][2];
		if(cursor.getCount()==0)		
		{
			new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("Whoops! SoHub is unable to detect any of your contacts!").setPositiveButton("OK",null).show();
		}
		else
		{
			while(cursor.moveToNext())
			{
				int namecolumn=cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME);
				contactnames.add(cursor.getString(namecolumn));
				details[i][0]=(cursor.getString(namecolumn));
				int idcolumn= cursor.getColumnIndexOrThrow(PhoneLookup._ID);
				details[i][1]= (cursor.getString(idcolumn));
				Log.i("-------","Name: " + details[i][0]);
				Log.i("-------","ID: " + details[i][1]);
				i++;
			}
			ArrayAdapter<String> ctcnames= new ArrayAdapter<String>(this,R.layout.contactrowlayout,contactnames);
			setListAdapter(ctcnames);
		}	
		cursor.close(); 
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Log.d("LIST","SELECTED ITEM #"+ position);
		String selid= new String();
		String ctcname = new String(((TextView)v.findViewById(R.id.text1)).getText().toString());
		Log.i("---------","ctcname: " + ctcname);
		for(int j=0;j<len; j++)
		{
			if(details[j][0].equals(ctcname))
			{
				selid=details[j][1];
			}
		}
		Intent i= new Intent(this,ContactDescription.class);
		i.putExtra(CONTACT_NAME, ctcname);
		i.putExtra("sid", selid);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
