package com.example.socialnetworkapi;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LinkedinResult extends Activity {

	static String amount, public_link, access_token;
	ListView listview;
	private ProgressDialog pd;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_result);
        
        Intent i=getIntent(); 
        amount = i.getStringExtra("AMOUNT");
        public_link = i.getStringExtra("USER_INFO");
        listview = (ListView)findViewById(R.id.result_list);
        
        
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
      	String accessToken = preferences.getString("accessToken", null);
      	
		Log.wtf("~~~~~", public_link);
		if(accessToken!=null){
			String profileUrl = getProfileUrl(public_link, accessToken,amount);
			new GetPostsRequestAsyncTask(LinkedinResult.this,listview).execute(profileUrl);
		}
	}
	
	String getProfileUrl(String link, String token, String amount){

		String result = java.net.URLEncoder.encode(link);
		Log.wtf("~~~~~~~~~~~", "https://api.linkedin.com/v1/people/url=http%3A%2F%2F"+result+"/network?type=SHAR&oauth2_access_token="
				+token+"&format=json&format=json&count="+amount);
		
		return ("https://api.linkedin.com/v1/people/url=http%3A%2F%2F"+result+"/network?type=SHAR&oauth2_access_token="
					+token+"&count="+amount);
	}
	
	class GetPostsRequestAsyncTask extends AsyncTask<String, Void, JSONObject>{

		Context c;
		ListView listview;
		GetPostsRequestAsyncTask(Context context, ListView Listview){
			c = context;
			listview = Listview;
		}
		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(LinkedinResult.this, "", "Loading..",true);
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(params.length>0){
				String url = params[0];
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("x-li-format", "json");
				try{
					HttpResponse response = httpClient.execute(httpget);
					if(response!=null){
						//If status is OK 200
						if(response.getStatusLine().getStatusCode()==200){
							String result = EntityUtils.toString(response.getEntity());
							//Convert the string result to a JSON Object
							return new JSONObject(result);
						}
					}
				}catch(IOException e){
					Log.e("Authorize","Error Http response "+e.getLocalizedMessage());	
				} catch (JSONException e) {
					Log.e("Authorize","Error Http response "+e.getLocalizedMessage());	
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(JSONObject data){
			ArrayList<Post> posts = new ArrayList<Post>();
			if(pd!=null && pd.isShowing()){
				pd.dismiss();
			}
			if(data!=null){
				try {
					String temp1 = data.getString("updates");
					JSONObject jObject1 = new JSONObject(temp1);
					JSONArray jArray = jObject1.getJSONArray("values");
					for(int i=0; i<jArray.length(); i++){
						JSONObject Object1 = jArray.getJSONObject(i);
						String temp2 = Object1.getString("updateContent");
						JSONObject Object2 = new JSONObject(temp2);
						String temp3 = Object2.getString("person");
						JSONObject Object3 = new JSONObject(temp3);
						
						if(Object3.has("currentShare")){
							String temp4 = Object3.getString("currentShare");
							JSONObject Object4 = new JSONObject(temp4);
							if(Object4.has("comment")){
								Post post = new Post(Object4.getString("comment").toString(),
										Object4.getString("timestamp").toString(),
										Object4.getString("id").toString());
								if(Object4.has("content")){
									String temp5 = Object4.getString("content");
									JSONObject Object5 = new JSONObject(temp5);
									post.share_url=Object5.getString("eyebrowUrl");
									post.shareDescription = Object5.getString("description");
									post.share_title = Object5.getString("title");
								}
								posts.add(post);			
							}else{
								Post post = new Post(" ",
										Object4.getString("timestamp").toString(),
										Object4.getString("id").toString());
								if(Object4.has("content")){
									String temp5 = Object4.getString("content");
									JSONObject Object5 = new JSONObject(temp5);
									post.share_url=Object5.getString("eyebrowUrl");
									post.shareDescription = Object5.getString("description");
									post.share_title = Object5.getString("title");
								}
								posts.add(post);
							}					
						}
					}
					
					BarAdapter adapter = new BarAdapter(c, posts);
					listview.setAdapter(adapter);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	// bar adapter for list view
	@SuppressLint("ViewHolder") class BarAdapter extends BaseAdapter
	{
		ArrayList<Post> posts = new ArrayList<Post>();
		private Context context;
		
		BarAdapter(Context c, ArrayList<Post> POSTS){
			context = c;
			posts = POSTS;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return posts.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row=inflater.inflate(R.layout.single_row, parent, false);
			TextView id = (TextView)row.findViewById(R.id.id);
			TextView date = (TextView)row.findViewById(R.id.date);
			TextView message = (TextView)row.findViewById(R.id.message);
			ImageView post_pic = (ImageView) row.findViewById(R.id.post_image);
			post_pic.setImageResource(R.drawable.linkedin);
			TextView des = (TextView) row.findViewById(R.id.share_des);
			TextView ti = (TextView) row.findViewById(R.id.share_title);
			Button view_share = (Button) row.findViewById(R.id.view_share);
			
			final Post temp = posts.get(position);
			
			id.setText(temp.id);
			
			Timestamp stamp = new Timestamp(Long.parseLong(temp.date));
			Date DATE = new Date(stamp.getTime());
			date.setText(DATE.toString());
			message.setText(temp.message);
			if(temp.share_title != null){
				ti.setVisibility(View.VISIBLE);
				ti.setText(temp.share_title);
			}
			if(temp.shareDescription != null){
				des.setVisibility(View.VISIBLE);
				des.setText(temp.shareDescription);
			}
			if(temp.share_url != null){
				view_share.setVisibility(View.VISIBLE);
				view_share.setOnClickListener(new OnClickListener() {
					 public void onClick(View v) {
					  // TODO Auto-generated method stub
					  Toast.makeText(context, "Opening the Webpage of the selected share post",
					    Toast.LENGTH_SHORT).show();
					  
					  Intent web = new Intent(Intent.ACTION_VIEW);
					  web.setData(Uri.parse(temp.share_url));
					  context.startActivity(web);
					 }
				});
			}
			
			
			
			return row;
		}
		
		
	}
	class Post{
		public String message;
		public String date;
		public String id;
		public String shareDescription = null;
		public String share_url = null;
		public String share_title = null;
		
		public Post(String Message, String Date, String ID){
			message = Message;
			date = Date;
			id = ID;
		}
	}
}