package com.cm.hybridmessagingplugin;

import com.cm.hybridmessagingplugin.HybridMessagingApplication;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;

import okhttp3.Headers;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.OnRegistrationListener;
import com.cm.hybridmessagingsdk.listener.OnVerificationStatus;
import com.cm.hybridmessagingsdk.listener.VoiceCallListener;
import com.cm.hybridmessagingsdk.listener.ResponseListener;
import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;
import com.cm.hybridmessagingsdk.util.Registration;
import com.cm.hybridmessagingsdk.util.VerificationStatus;
import com.cm.hybridmessagingsdk.util.Message;
import com.cm.hybridmessagingsdk.util.Notification;
import com.cm.hybridmessagingsdk.util.NotificationUtil;
import com.cm.hybridmessagingsdk.webservice.MessageRestClient;
import com.cm.hybridmessagingsdk.webservice.Filter;

public class HybridMessagingPlugin extends CordovaPlugin implements HybridNotificationListener {

	private final static String TAG = HybridMessagingPlugin.class.getSimpleName();
	private String apiKey;
	private String apiSecret;
	private String senderId;
	private boolean sdkInitialized = false;
	private boolean numberVerified = false;

	public HybridMessagingPlugin() {
	}

	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		Log.d(TAG, "Action: " + action + args.toString());
		final CallbackContext cc = callbackContext;
		if (action.equals("setDevelopment")) {
		} else if (action.equals("configureService")) {
			JSONObject options = args.getJSONObject(0);
			apiKey = options.getString("appKey");
			apiSecret = options.getString("appSecret");
			senderId = options.getString("senderId");
		} else if (apiKey == null || apiSecret == null || senderId == null) {
			cc.error("Incomplete configuration. Call 'configureService' with a complete configuration object as argument");
		} else if (action.equals("startMessagingService")) {
			Context applicationContext = cordova.getActivity().getApplicationContext();
                        HybridMessaging.initialize(applicationContext, apiKey, apiSecret, senderId);
			HybridMessaging.fireNotificationsByDefault(false);
			HybridMessaging.setHybridNotificationListener(this);
			sdkInitialized = true;
			onResume(true);
			cc.success();
		} else if (sdkInitialized == false) {
			 cc.error("Hybrid messaging system not initialized. Call 'startMessagingService' first.");
		} else if (action.equals("requestVerification")) {
			String msisdn = args.getString(0);
			HybridMessaging.registerNewUser(msisdn, new OnRegistrationListener() {
				@Override
				public void onReceivedRegistration(Registration registration) {
                        		cc.success(getVerificationStatusAsInt(registration.getStatus()));
				}

				@Override
				public void onError(Throwable throwable) {
					cc.error("Device verification call returned with an error: " + throwable.getMessage());
				}	
			});
		} else if (action.equals("getVerificationStatus")) {
			HybridMessaging.getVerificationStatus(new OnVerificationStatus() {
				@Override
				public void onVerificationStatus(VerificationStatus status) {
					cc.success(getVerificationStatusAsInt(status));
					if (status == VerificationStatus.Verified) {
						numberVerified = true;
					} else {
						numberVerified = false;
					}
				}

				@Override
				public void onError(Throwable throwable) {
					cc.error("Verification status call returned with an error: " + throwable.getMessage());
                                }
			});
		} else if (action.equals("getMsisdnValue")) {
			cc.success(HybridMessaging.getMsisdn());
		} else if (action.equals("verifyPin")) {
			String pin = args.getString(0);
			HybridMessaging.registerUserByPincode(pin, new OnVerificationStatus() {
				@Override
				public void onVerificationStatus(VerificationStatus status) {
					cc.success(getVerificationStatusAsInt(status));
					if (status == VerificationStatus.Verified) {
						numberVerified = true;
					} else {
						numberVerified = false;
					}
				}

				@Override
				public void onError(Throwable throwable) {
					cc.error("PIN verification call returned with an error: " + throwable.getMessage());
				}
			});
		} else if (action.equals("getDeviceIdValue")) {
			cc.success(HybridMessaging.getDeviceId());
		} else if (action.equals("requestVerificationVoiceCall")) {
			if (numberVerified == true) {
				HybridMessaging.doVoiceCall(HybridMessaging.getMsisdn(), new VoiceCallListener() {
					@Override
					public void onSuccess() {
						cc.success();
					}

					@Override
					public void onError(Throwable throwable) {
						cc.error("Voice call request returned with an error: " + throwable.getMessage());
					}
				});
			}	
		} else if (action.equals("getMessages")) {
			int limit = args.optInt(0, 0);
			int offset = args.optInt(1, 0);
			Filter filter = null;
			if (limit > 0 || offset > 0) {
				filter = new Filter();
				filter.addFilter(Filter.OPTION_SELECT, "ID,Body,DateTime,Sender,UpdateIDs");
				if (offset > 0) {
					filter.addFilter(Filter.OPTION_SKIP, String.valueOf(offset));
				}

				if (limit > 0) {
					 filter.addFilter(Filter.OPTION_TOP, String.valueOf(limit));
				}
			}
			
			MessageRestClient.getMessages(filter, new ResponseListener() {
				@Override
				public void onReceiveJSONObject(int statusCode, Headers headers, JSONObject jsonObject) {
					cc.error("Unexpectedly received JSON object instead of array" );
				}

            			@Override
				public void onReceiveJSONArray(int statusCode, Headers headers, JSONArray jsonArray) {
					if (statusCode == 200) {
						cc.success(jsonArray);
					} else {
						cc.error("Messages call returned with http status code: " + statusCode);
					}
				}

				@Override
                                public void onError(Throwable throwable) {
					cc.error("Request for messages failed wit anh error: " + throwable.getMessage());
				}
			});
		} else {
			return false;
		}

		return true;

	}

	@Override
	public void onPause(boolean multitasking) {
		if (sdkInitialized == true) {
			HybridMessaging.onPause();
		}
		setBackgroundNotificationListener();
	}

	@Override
	public void onResume(boolean multitasking) {
                if (sdkInitialized == true) {
			HybridMessaging.onResume();
		}

		HybridMessaging.setHybridNotificationListener(this);

		Bundle extras = cordova.getActivity().getIntent().getExtras();
		if (extras != null) {
			Bundle notificationPayload = extras.getBundle("notificationPayload");
			if (notificationPayload != null) {
				handleNotificationPayload(notificationPayload);
				cordova.getActivity().getIntent().removeExtra("notificationPayload");
			}
		}
	}

	@Override
	public void onDestroy() {
		setBackgroundNotificationListener();
	}

	@Override
	public void onNewIntent(Intent intent) {
		cordova.getActivity().setIntent(intent);
	}

	private void setBackgroundNotificationListener() {
		HybridMessagingApplication application = (HybridMessagingApplication)cordova.getActivity().getApplication();
		HybridMessaging.setHybridNotificationListener(application);
	}

	@Override
	public void onReceiveHybridNotification(Context context, Notification notification) {
		handleNotificationPayload(notification.getExtras());
	}

	private void handleNotificationPayload(Bundle notificationPayload) {
		JSONObject notificationJson = new JSONObject();
		notificationPayload.putString("alert", notificationPayload.getString("message"));
		try {
			Set<String> keys = notificationPayload.keySet();
			for (String key : keys) {
				notificationJson.put(key, JSONObject.wrap(notificationPayload.get(key)));
			}
		} catch(JSONException e) {
		}

		notifyJavascript("notificationCallback", notificationJson);
	}

	private void notifyJavascript(String callbackName, Object... args) {
		String jsString = "javascript: hybridmessaging." + callbackName + "(";

		for (int i = 0; i < args.length-1; i++) {
			String arg = args[i].toString();
			if (args[i] instanceof String) {
				arg = "\"" + arg + "\"";
			}

			jsString = jsString + arg + ",";
		}
		
		if (args.length > 0) {
			String arg = args[args.length-1].toString();
			if (args[args.length-1] instanceof String) {
                                arg = "\"" + arg + "\"";
                        }

			jsString = jsString + arg;
		}

		jsString = jsString+")";

		Log.d(TAG, "sendJavascript : " + jsString);
                this.webView.sendJavascript(jsString);
	}

	private int getVerificationStatusAsInt(VerificationStatus status) {
		switch (status) {
			case Unverified : return 1;
			case WaitingForPin : return 2;
			case LastPinVerificationFailed : return 3;
			case Verified : return 4;
			default : return 6; // status UNKNOWN 
        	}
	}
}
