package com.example.socialnetworkapi;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class LinkedinLogin2 extends Activity {
	private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~";
	private static final String OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
	private static final String QUESTION_MARK = "?";
	private static final String EQUALS = "=";
	
	TextView welcomeText;
	Button display;
	NumberPicker np;
	EditText userInfo;;
	
	private ProgressDialog pd;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_login_second);
        
        welcomeText = (TextView) findViewById(R.id.welcome);
        display = (Button)findViewById(R.id.view_posts);
        np = (NumberPicker) findViewById(R.id.number_picker);
        userInfo = (EditText) findViewById(R.id.user_info);
        userInfo.setText("www.linkedin.com/pub/jiashen-wang/89/31a/b39"/*"www.linkedin.com/pub/congming-chen/55/216/621"*/);
        np.setMaxValue(20);
        np.setMinValue(3);
        //Request basic profile of the user
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
      	String accessToken = preferences.getString("accessToken", null);
      	Log.wtf("~~~~~~~~~~~~", accessToken);
      	
        display.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                
				if(userInfo.getText().toString().trim().length()<=0){
					Toast.makeText(getApplicationContext(), "Please enter User name! ", Toast.LENGTH_SHORT).show();
				}else{
	               	Intent intent = new Intent(getBaseContext(), LinkedinResult.class);
	            	intent.putExtra("AMOUNT", np.getValue()+"");
	            	intent.putExtra("USER_INFO", userInfo.getText().toString());
	            	startActivity(intent);	
				}

            	
			}
		});
      	
		if(accessToken!=null){
			String profileUrl = getProfileUrl(accessToken);
			new GetProfileRequestAsyncTask().execute(profileUrl);
		}
	}
	private static final String getProfileUrl(String accessToken){
		return PROFILE_URL
				+QUESTION_MARK
				+OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;
	}
	
	public class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(LinkedinLogin2.this, "", "Loading..",true);
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(params.length>0){
				String url = params[0];
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("x-li-format", "json");
				
				try {
					HttpResponse response = httpClient.execute(httpget);
					if(response!=null){
						if(response.getStatusLine().getStatusCode()==200){
							String result = EntityUtils.toString(response.getEntity());
							//Convert the string result to a JSON Object
							return new JSONObject(result);
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject data){
			if(pd!=null && pd.isShowing()){
				pd.dismiss();
			}
			if(data!=null){
				try {
					String welcomeTextString = String.format("Welcome %1$s %2$s, You are a %3$s",data.getString("firstName"),data.getString("lastName"),data.getString("headline"));
					welcomeText.setText(welcomeTextString);
				} catch (JSONException e) {
					Log.e("Authorize","Error Parsing json "+e.getLocalizedMessage());	
				}
			}
		}
		
	}

}
