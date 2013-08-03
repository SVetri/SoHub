package com.svelte.sohub;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mcontext;
	ArrayList<Bitmap> bitmaparray = new ArrayList<Bitmap>();
	
	public ImageAdapter(Context c, ArrayList<Bitmap> bmarray)
	{
		mcontext=c;
		bitmaparray = new ArrayList<Bitmap>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bitmaparray.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bitmaparray.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ImageView iv = new ImageView(mcontext);
		iv.setImageBitmap(bitmaparray.get(position));
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		iv.setLayoutParams(new GridView.LayoutParams(70,70));
		iv.setBackgroundResource(R.drawable.picborder);
		return iv;
	}

}
