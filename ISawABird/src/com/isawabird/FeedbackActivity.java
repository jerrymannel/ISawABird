package com.isawabird;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.extra.SyncUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends Activity {
	
	EditText feedback_editText = null;
	Button feedback_button = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		feedback_editText = (EditText) findViewById(R.id.feedback_editText);
		feedback_button = (Button) findViewById(R.id.feedback_button);
		
		feedback_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.out.println(feedback_editText.getText());
				DBHandler dh = DBHandler.getInstance(getApplicationContext());
				dh.addFeedback(feedback_editText.getText().toString()); 
				feedback_editText.setText("");
				Toast.makeText(getApplicationContext(), "Thank you for your feedback", Toast.LENGTH_SHORT).show();
				SyncUtils.triggerRefresh();
				finish();
			}
		});
	}
}
