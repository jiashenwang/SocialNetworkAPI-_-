package com.example.socialnetworkapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class FacebookLogin extends Activity {
	
	final static String FACEBOOK_APP_ID = "1497142177217073";
	
	Facebook fb;
	NumberPicker np;
	Button display;
	SharedPreferences sp;
	ImageView personal_pic;
	TextView welcome;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_login);
        
        // initialization 
        fb = new Facebook(FACEBOOK_APP_ID); 
        
        sp = getPreferences(MODE_PRIVATE);
        String access_token = sp.getString("access_token", null);
        long expires = sp.getLong("access_expires", 0);
        
        np = (NumberPicker) findViewById(R.id.number_picker);
        np.setMaxValue(30);
        np.setMinValue(1);
        display = (Button) findViewById(R.id.view_posts);
        personal_pic = (ImageView) findViewById(R.id.personal_pic);
        welcome = (TextView) findViewById(R.id.welcome);
        
        login();
        
        display.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(getBaseContext(), FacebookResult.class);
            	intent.putExtra("AMOUNT", np.getValue()+"");
            	intent.putExtra("ACCESS_TOKEN", fb.getAccessToken());
            	startActivity(intent);
            }
        });
    }

	private void login() {
		// TODO Auto-generated method stub
		fb.authorize(this,new String[] {"user_status"}, new DialogListener(){

			@Override
			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putString("access_token", fb.getAccessToken());
				editor.putLong("access_expires", fb.getAccessExpires());
				editor.commit();
				updateButtons();
			}

			@Override
			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Facebook error", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "User canceled", Toast.LENGTH_SHORT).show();
			}
			
		});
	}

	protected void updateButtons() {
		// TODO Auto-generated method stub
		personalInfo info = new personalInfo();
		info.execute(fb);
	}


	private class personalInfo extends AsyncTask<Facebook, Void, InfoEntity>{

		@Override
		protected InfoEntity doInBackground(Facebook... params) {
			// TODO Auto-generated method stub
			Facebook fb = params[0];
			JSONObject obj = null;
			URL posts_url = null;
			URL personal_img_url = null;
			String status_url = null;
			String jsonUser = null;
			
			try {
				jsonUser = fb.request("me");
				obj = Util.parseJson(jsonUser);
				String id = obj.optString("id");
				String name = obj.optString("name");
				//Log.wtf("!~!~!~!~!~!~", name);
				//welcome.setText("Welcome, "+name);
				personal_img_url = new URL("https://graph.facebook.com/"+id+"/picture?type=large");		
				Bitmap bmp = BitmapFactory.decodeStream(personal_img_url.openConnection().getInputStream());
				InfoEntity task = new InfoEntity(name, bmp);
				return task;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FacebookError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(InfoEntity result) {
			if(result != null){
				personal_pic.setVisibility(ImageView.VISIBLE);
				personal_pic.setImageBitmap(result.personal_pic);
				welcome.setText("Welcome, "+result.name);
				display.setVisibility(Button.VISIBLE);
				np.setVisibility(NumberPicker.VISIBLE);
			}
		}
	    
	    // check network connection
	    public boolean isConnected(){
	        ConnectivityManager connMgr = (ConnectivityManager) getSystemService("connectivity");
	            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	            if (networkInfo != null && networkInfo.isConnected()) 
	                return true;
	            else
	                return false;   
	    }
		
	}
	
	public class InfoEntity{
		public String name;
		public Bitmap personal_pic;
		
		
		InfoEntity(String Name, Bitmap Pic){
			name = Name;
			personal_pic = Pic;
		}
	}
}
