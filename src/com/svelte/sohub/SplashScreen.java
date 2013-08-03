package com.svelte.sohub;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		anime();
	}

	private void anime()
	{
		TextView text= (TextView)findViewById(R.id.tv);
		Animation title= AnimationUtils.loadAnimation(this, R.anim.fade_in);
		text.startAnimation(title);
		title.setAnimationListener(new AnimationListener() {
			 public void onAnimationEnd(Animation animation) 
			 {
			 	startActivity(new Intent(SplashScreen.this,MainActivity.class)); 
				SplashScreen.this.finish();
			 }
			 public void onAnimationRepeat(Animation animation) 
			 {
	         };
	         public void onAnimationStart(Animation animation)
	         {
	         };
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
