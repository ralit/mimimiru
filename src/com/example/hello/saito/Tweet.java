package com.example.hello.saito;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class Tweet implements OnInitListener{

	private Twitter twitter;
	private TwitterStream twitterStream;
	public static OAuthAuthorization _oauth;
	public static RequestToken _req;
	private boolean isSpeechInitialized = false;
	private TextToSpeech textToSpeech;
	private Context context;
	public String message;

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			isSpeechInitialized = true;
		}
	}
	
	public String getMessage() {
		return message;
	}
	
	public Tweet(Twitter twitter_, String message, SharedPreferences sp, Context context) {
		this.twitter = twitter_;
		this.context = context;
		textToSpeech = new TextToSpeech(context, this, "jp.kddilabs.n2tts");
		this.message = message;

		UserStreamListener listener = new UserStreamListener() {

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatus(Status status) {
				// TODO Auto-generated method stub
				try {
					log("twitterStream.getScreenName()" + twitterStream.getScreenName());
					log("twitter.getScreenName()" + twitter.getScreenName());
					log("status.getInReplyToScreenName()" + status.getInReplyToScreenName());

					if (twitter.getScreenName().equals(status.getInReplyToScreenName()) ) {
						if (isSpeechInitialized) {
							textToSpeech.speak(status.getUser().getName() + " : " + status.getText(), TextToSpeech.QUEUE_ADD, null);
						}
						log(status.getUser().getName() + " : " + status.getText());
					}

				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//					Pattern pattern;
				//					pattern = Pattern.compile("^(.+):");
				//					Matcher matcher = pattern.matcher(status.getUser().getName() + " : " + status.getText());
				//					log(matcher.group(1));
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserProfileUpdate(User arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListUpdate(User arg0, UserList arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListDeletion(User arg0, UserList arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUserListCreation(User arg0, UserList arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUnfavorite(User arg0, User arg1, Status arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUnblock(User arg0, User arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFriendList(long[] arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFollow(User arg0, User arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFavorite(User arg0, User arg1, Status arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDirectMessage(DirectMessage arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDeletionNotice(long arg0, long arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBlock(User arg0, User arg1) {
				// TODO Auto-generated method stub

			}
		};

		//			StatusListener listener = new StatusListener(){
		//				public void onStatus(Status status) {
		//					log(status.getUser().getName() + " : " + status.getText());
		//				}
		//				public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
		//				public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
		//				public void onException(Exception ex) {
		//					ex.printStackTrace();
		//				}
		//				@Override
		//				public void onScrubGeo(long arg0, long arg1) {
		//					// TODO Auto-generated method stub
		//					
		//				}
		//				@Override
		//				public void onStallWarning(StallWarning arg0) {
		//					// TODO Auto-generated method stub
		//					
		//				}
		//			};
		twitterStream = new TwitterStreamFactory().getInstance();
		//			sp = PreferenceManager.getDefaultSharedPreferences(this);
		twitterStream.setOAuthConsumer(DocomoAPI.getoauthconsumer(), DocomoAPI.getoauthconsumer2());
		twitterStream.setOAuthAccessToken(new AccessToken(sp.getString("TwitterAccessToken", null), sp.getString("TwitterAccessTokenSecret", null)));
		twitterStream.addListener(listener);

		// sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
		twitterStream.user();

		Post post = new Post(message, twitter);
		post.start();
		
	}


	private void log(String log) {
		Log.i("ralit", log);
	}



}

class Post extends Thread {
	private String message;
	private Twitter twitter;
	
	public Post(String message, Twitter twitter) {
		this.message = message;
		this.twitter = twitter;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//		    		try {
		//		    			Status status = twitter.updateStatus("hogehoge");
		//		    		} catch (TwitterException e) {
		//		    			// TODO 自動生成された catch ブロック
		//		    			e.printStackTrace();
		//		    		}

		try {
			StatusUpdate status = new StatusUpdate(message + " #ミミミル");
			if(message.equals("(no result)")) {
				status = new StatusUpdate("何が映っているか、リプライして伝えてね。 #ミミミル");
			}
			if(!message.contains("ありがとう")) {
				status.media("mimimiru_pict", new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mimimiru/mimimirupic.jpg"));
			}					
			twitter.updateStatus(status);
		} catch(TwitterException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
