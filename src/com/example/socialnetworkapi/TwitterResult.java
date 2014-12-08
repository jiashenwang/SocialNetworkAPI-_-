package com.example.socialnetworkapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TwitterResult extends Activity {
	
	static ListView listview;
	static String amount, UserID;
	Twitter twitter;
	private ProgressDialog pd;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_result);
        
        Intent i=getIntent(); 
		amount = i.getStringExtra(TwitterLogin.POSTS_AMOUNT);
		UserID = i.getStringExtra("USERID");
		twitter = (Twitter)getIntent().getSerializableExtra("TWITTER");
		
		listview = (ListView)findViewById(R.id.result_list);
		
		Entity entity = new Entity(twitter, Integer.parseInt(amount),UserID);
		
		GetPosts_t info = new GetPosts_t(TwitterResult.this, listview);
		info.execute(entity);
    }
    
    class GetPosts_t extends AsyncTask<Entity, Void, ArrayList<Post_t>>{
    	Context c;
    	ListView listview;
    	GetPosts_t(Context context, ListView Listview){
    		c = context;
    		listview = Listview;
    	}
    	
        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(TwitterResult.this, "", "Loading...",true);
        }
    	@Override
    	protected ArrayList<Post_t> doInBackground(Entity... params) {
    		// TODO Auto-generated method stub
    		List<twitter4j.Status> statuses = null;
    		ArrayList<Post_t> posts = new ArrayList<Post_t>();
    		Entity entity = params[0];
    		Paging paging = new Paging();
    		paging.count(entity.amount);
    		try {
    			Query q = new Query();
    			q.query(entity.user_name_id);
    			q.count(entity.amount);
    	
    			QueryResult query = entity.twitter.search(q);
    			statuses = query.getTweets();
    			
    			
    			for(int i=0; i<statuses.size(); i++){
    				String str = statuses.get(i).getText();
    				String str1 = pulltext(str);
    				String str2 = pullLinks(str);
    				
    				
    				
    				if(str2 == null){
    					ArrayList<Bitmap> pic = new ArrayList<Bitmap>();
    					Post_t p = new Post_t(pic, str, statuses.get(i).getCreatedAt()+"",statuses.get(i).getId());
    					posts.add(p);
    				}else{
    					long temp_id = statuses.get(i).getId();
    					twitter4j.Status temp_status = entity.twitter.showStatus(temp_id);
    					ArrayList<Bitmap> pic = new ArrayList<Bitmap>();
    					
    					for(int j=0; j<temp_status.getExtendedMediaEntities().length; j++){
    						URL status_image = new URL(temp_status.getExtendedMediaEntities()[j].getMediaURL());
    						Bitmap bmp = BitmapFactory.decodeStream(status_image.openConnection().getInputStream());
    						pic.add(bmp);
    					}
    					Post_t p = new Post_t(pic, str1, statuses.get(i).getCreatedAt()+"",statuses.get(i).getId());
    					posts.add(p);
    				}
    			}
    			
    			
    		} catch (TwitterException e) {
    			// TODO Auto-generated catch block
    			if(e.getStatusCode() == 404){
    				posts.clear();
    				return posts;
    			}
    			e.printStackTrace();
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return posts;
    	}
    	
    	protected void onPostExecute(ArrayList<Post_t> posts) {
    		// TODO Auto-generated method stub
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
    		BarAdapter_t adapter = new BarAdapter_t(c, posts);
    		listview.setAdapter(adapter);
    	}

    	private String pullLinks(String text) {
    		String[] splitted = text.split("http");
    		if(splitted.length<=1)
    			return null;
    		else
    			return ("http" + splitted[1]).replaceAll("\\s+","");
    	}
    	private String pulltext(String text){
    		String[] splitted = text.split("http");
    		return (splitted[0]);
    	}
    	
    }

    //bar adapter for list view
    class BarAdapter_t extends BaseAdapter
    {
    	ArrayList<Post_t> posts = null;
    	private Context context;
    	
    	BarAdapter_t(Context c, ArrayList<Post_t> POSTS){
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
    		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View row=inflater.inflate(R.layout.single_row, parent, false);
    		TextView id = (TextView)row.findViewById(R.id.id);
    		TextView date = (TextView)row.findViewById(R.id.date);
    		TextView message = (TextView)row.findViewById(R.id.message);
    		ImageView post_pic = (ImageView) row.findViewById(R.id.post_image);
    		post_pic.setImageResource(R.drawable.twitter);
    		
    		ImageView status_pic1 = (ImageView) row.findViewById(R.id.status_image1);
    		ImageView status_pic2 = (ImageView) row.findViewById(R.id.status_image2);
    		ImageView status_pic3 = (ImageView) row.findViewById(R.id.status_image3);
    		ImageView status_pic4 = (ImageView) row.findViewById(R.id.status_image4);
    		 
    		
    		id.setText(posts.get(position).id+"");
    		date.setText(posts.get(position).message);
    		message.setText(posts.get(position).date);
    		if(posts.get(position).pic.size()>0){
    			if(posts.get(position).pic.size()==4){
    				status_pic1.setVisibility(ImageView.VISIBLE);
    				status_pic2.setVisibility(ImageView.VISIBLE);
    				status_pic3.setVisibility(ImageView.VISIBLE);
    				status_pic4.setVisibility(ImageView.VISIBLE);
    				status_pic1.setImageBitmap(posts.get(position).pic.get(0));
    				status_pic2.setImageBitmap(posts.get(position).pic.get(1));
    				status_pic3.setImageBitmap(posts.get(position).pic.get(2));
    				status_pic4.setImageBitmap(posts.get(position).pic.get(3));
    			}else if(posts.get(position).pic.size()==3){
    				status_pic1.setVisibility(ImageView.VISIBLE);
    				status_pic2.setVisibility(ImageView.VISIBLE);
    				status_pic3.setVisibility(ImageView.VISIBLE);
    				status_pic1.setImageBitmap(posts.get(position).pic.get(0));
    				status_pic2.setImageBitmap(posts.get(position).pic.get(1));
    				status_pic3.setImageBitmap(posts.get(position).pic.get(2));
    			}else if(posts.get(position).pic.size()==2){
    				status_pic1.setVisibility(ImageView.VISIBLE);
    				status_pic2.setVisibility(ImageView.VISIBLE);
    				status_pic1.setImageBitmap(posts.get(position).pic.get(0));
    				status_pic2.setImageBitmap(posts.get(position).pic.get(1));
    			}else{
    				if(posts.get(position).pic.get(0) != null){
    					status_pic1.setVisibility(ImageView.VISIBLE);
    					status_pic1.setImageBitmap(posts.get(position).pic.get(0));		
    				}
    			}
    			
    		}
    		return row;
    	}
    	
    	
    }
    class Post_t{
    	public ArrayList<Bitmap> pic;
    	public String message;
    	public String date;
    	public long id;
    	
    	public Post_t(ArrayList<Bitmap> Pic, String Message, String Date, long ID){
    		pic = Pic;
    		message = Message;
    		date = Date;
    		id = ID;
    	}
    }
    class Entity{
    	Twitter twitter;
    	int amount;
    	String user_name_id;
    	Entity(Twitter tw, int Amount, String User_name_id){
    		twitter = tw;
    		amount =Amount;
    		user_name_id = User_name_id;
    	}
    	
    }

}

