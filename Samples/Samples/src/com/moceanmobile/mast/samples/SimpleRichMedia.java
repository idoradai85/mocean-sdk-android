package com.moceanmobile.mast.samples;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class SimpleRichMedia extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_rich_media);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_menu, menu);
		return true;
	}

}