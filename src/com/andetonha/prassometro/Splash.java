package com.andetonha.prassometro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Splash extends Activity {
	private long ms = 0;
	private long splashTime = 1500;
	private boolean splashActive = true;
	private boolean paused=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		startSplash();
	}
	
	public void startSplash(){
			Thread mythread = new Thread() {
				public void run() {
					try {
						while (splashActive && ms < splashTime) {
							if(!paused)
								ms=ms+100;
							sleep(100);
						}
					} catch(Exception e) {}
					finally {
						Intent intent = new Intent(Splash.this, MainActivity.class);
						startActivity(intent);
						finish();
					}
				}
			};
			mythread.start();
	}
}
