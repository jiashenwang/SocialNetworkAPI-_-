package com.example.socialnetworkapi;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	Button facebook, twitter, linkedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        facebook = (Button) findViewById(R.id.facebook_login);
        twitter = (Button) findViewById(R.id.twitter_login);
        linkedin = (Button) findViewById(R.id.linkedin_login);
        
        facebook.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), FacebookLogin.class);
				startActivity(intent);
			}    	
        });
        
        twitter.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), TwitterLogin.class);
				startActivity(intent);
			}    	
        });
        
        linkedin.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), LinkedinLogin.class);
				startActivity(intent);
			}    	
        });
    }


    
}
