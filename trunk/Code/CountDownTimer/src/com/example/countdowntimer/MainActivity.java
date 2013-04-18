package com.example.countdowntimer;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private CountDownTimer countDownTimer;
	private Button startButton;
	private TextView textView;
	private ImageView playButton;
	private final long startTime = 30*1000;
	private final long interval = 1*1000;
	private boolean timerHasStarted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startButton = (Button)findViewById(R.id.button);
		startButton.setOnClickListener(MyButtonClickListener);
		
		playButton = (ImageView)findViewById(R.id.buttonPlay);
		playButton.setOnClickListener(MyButtonClickListener);
		
		textView = (TextView)findViewById(R.id.timer);
		textView.setText(textView.getText()+String.valueOf(startTime/1000));
		
		countDownTimer = new MyCountDownTimer(startTime, interval);
	}
	
	private OnClickListener MyButtonClickListener = new OnClickListener() {
	    public void onClick(View v) {
	    	if(!timerHasStarted){
				countDownTimer.start();
				timerHasStarted = true;
				startButton.setText("STOP");
			}else{
				countDownTimer.cancel();
				timerHasStarted = false;
				startButton.setText("RESTART");
			}
	    }
	};
	
	public class MyCountDownTimer extends CountDownTimer{
		public MyCountDownTimer(long startTime,long interval){
			super(startTime, interval);
		}
		
		@Override
		public void onFinish() {
			textView.setText("Time's up!");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			textView.setText("" + millisUntilFinished/1000);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
