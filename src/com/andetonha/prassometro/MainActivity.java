package com.andetonha.prassometro;

import andetonha.speedometer.SpeedometerView;
import andetonha.speedometer.SpeedometerView.LabelConverter;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Typeface fonte;
	private MediaPlayer mediaPlayer;
	private SpeedometerView speedometer;
	private static final String DEBUG = "[DEBUG]";
	private RelativeLayout toolbar;
	private MediaRecorder mRecorder;
	private static double mEMA = 0.0;
	private static final double EMA_FILTER = 0.6;
	private Thread runner;
	private ImageButton cazalbe;
	private TextView name;
	final Runnable updater = new Runnable() {

		public void run() {
			updateTv();
		};
	};
	final Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		
		try {
			toolbar = (RelativeLayout) findViewById(R.id.toolbar);
			name = (TextView) findViewById(R.id.prassometro);
			fonte = Typeface.createFromAsset(getAssets(), "fonts/Oxygen-Bold.ttf");
			name.setTypeface(fonte);
			speedometer = (SpeedometerView) findViewById(R.id.speedometer);
			cazalbe = (ImageButton) findViewById(R.id.cazalbe);
			
			speedometer.setLabelConverter(new LabelConverter() {
				
				@Override
				public String getLabelFor(double progress, double maxProgress) {
					return String.valueOf((int) Math.round(progress));
					//return String.valueOf(getAmplitude() + progress);
				}
			});
			
			speedometer.setMaxSpeed(100);
			speedometer.setMajorTickStep(10);
			speedometer.setMinorTicks(2);

		  speedometer.addColoredRange(0, 25, Color.CYAN);
		  speedometer.addColoredRange(25, 50, Color.GREEN);
		  speedometer.addColoredRange(50, 75, Color.YELLOW);
		  speedometer.addColoredRange(75, 100, Color.RED);

			if (runner == null) {
				runner = new Thread() {
					public void run() {
						while (runner != null) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								Log.e(DEBUG, e.getMessage());
							}
							;
							mHandler.post(updater);
						}
					}
				};
				runner.start();
				Log.d(DEBUG, "start runner()");
			}
			
		} catch (Exception e) {
			Log.e(DEBUG, e.getMessage());
		}
	}

	public void onResume() {
		super.onResume();
		startRecorder();
	}

	public void onPause() {
		super.onPause();
		stopRecorder();
	}

	public void startRecorder() {
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");
			try {
				mRecorder.prepare();
			} catch (java.io.IOException ioe) {
				android.util.Log.e(DEBUG, "IOException: "
						+ android.util.Log.getStackTraceString(ioe));

			} catch (java.lang.SecurityException e) {
				android.util.Log.e(DEBUG, "SecurityException: "
						+ android.util.Log.getStackTraceString(e));
			}
			try {
				mRecorder.start();
			} catch (java.lang.SecurityException e) {
				android.util.Log.e(DEBUG, "SecurityException: "
						+ android.util.Log.getStackTraceString(e));
			}

		}

	}

	public void stopRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void updateTv() {
		Double db = (getAmplitude() /350);
		speedometer.setSpeed(db + 1, 900, 0);
		if(db >= 90){
			toolbar.setBackgroundColor(Color.RED);
			name.setText("THE FINAL PRASSADOWN");
			theFinalPrassaDownPlay();
		}
		if(db < 5){
			name.setText("Prassometro");
		}
	}
	
	public void theFinalPrassaDownPlay(){
		mediaPlayer = MediaPlayer.create(this, R.raw.the_final_prassadown);
		
		if(mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
		
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.stop();
				mp.release();
				mp = null;
				
			}
		});
		mediaPlayer.start();
	}
	
	public double soundDb(double ampl) {
		return 20 * Math.log10(getAmplitudeEMA() / ampl);
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return mRecorder.getMaxAmplitude();
		else
			return 0;

	}
	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
