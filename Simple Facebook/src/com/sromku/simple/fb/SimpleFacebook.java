package com.sromku.simple.fb;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

import com.facebook.AppEventsLogger;
import com.sromku.simple.fb.actions.DeleteRequestAction;
import com.sromku.simple.fb.actions.GetAlbumsAction;
import com.sromku.simple.fb.actions.GetAppRequestsAction;
import com.sromku.simple.fb.actions.GetFriendsAction;
import com.sromku.simple.fb.actions.GetProfileAction;
import com.sromku.simple.fb.actions.GetScoresAction;
import com.sromku.simple.fb.actions.InviteAction;
import com.sromku.simple.fb.actions.PublishAction;
import com.sromku.simple.fb.actions.PublishFeedDialogAction;
import com.sromku.simple.fb.entities.Album;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.entities.Publishable;
import com.sromku.simple.fb.entities.Score;
import com.sromku.simple.fb.entities.Story;
import com.sromku.simple.fb.entities.Video;
import com.sromku.simple.fb.listeners.OnAlbumsRequestListener;
import com.sromku.simple.fb.listeners.OnAppRequestsListener;
import com.sromku.simple.fb.listeners.OnDeleteRequestListener;
import com.sromku.simple.fb.listeners.OnFriendsRequestListener;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnNewPermissionsListener;
import com.sromku.simple.fb.listeners.OnProfileRequestListener;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.sromku.simple.fb.listeners.OnScoresRequestListener;

/**
 * Simple Facebook SDK which wraps original Facebook SDK 3.6
 * 
 * @author sromku
 */
public class SimpleFacebook {
    private static SimpleFacebook mInstance = null;
    private static SimpleFacebookConfiguration mConfiguration = new SimpleFacebookConfiguration.Builder().build();

    private static Activity mActivity;
    private static SessionManager mSessionManager = null;

    private SimpleFacebook() {
    }

    /**
     * Initialize the library and pass an {@link Activity}. This kind of
     * initialization is good in case you have a one base activity and many
     * fragments. In this case you just initialize this library and then just
     * get an instance of this library by {@link SimpleFacebook#getInstance()}
     * in any other place.
     * 
     * @param activity
     *            Activity
     */
    public static void initialize(Activity activity) {
	if (mInstance == null) {
	    mInstance = new SimpleFacebook();
	    mSessionManager = new SessionManager(mActivity, mConfiguration);
	}
	mActivity = activity;
	SessionManager.activity = activity;
    }

    /**
     * Get the instance of {@link SimpleFacebook}. This method, not only returns
     * a singleton instance of {@link SimpleFacebook} but also updates the
     * current activity with the passed activity. <br>
     * If you have more than one <code>Activity</code> in your application. And
     * more than one activity do something with facebook. Then, call this method
     * in {@link Activity#onResume()} method
     * 
     * <pre>
     * &#064;Override
     * protected void onResume() {
     *     super.onResume();
     *     mSimpleFacebook = SimpleFacebook.getInstance(this);
     * }
     * </pre>
     * 
     * @param activity
     * @return {@link SimpleFacebook} instance
     */
    public static SimpleFacebook getInstance(Activity activity) {
	if (mInstance == null) {
	    mInstance = new SimpleFacebook();
	    mSessionManager = new SessionManager(activity, mConfiguration);
	}
	mActivity = activity;
	SessionManager.activity = activity;
	return mInstance;
    }

    /**
     * Get the instance of {@link SimpleFacebook}. <br>
     * <br>
     * <b>Important:</b> Use this method only after you initialized this library
     * or by: {@link #initialize(Activity)} or by {@link #getInstance(Activity)}
     * 
     * @return The {@link SimpleFacebook} instance
     */
    public static SimpleFacebook getInstance() {
	return mInstance;
    }

    /**
     * Set facebook configuration. <b>Make sure</b> to set a configuration
     * before first actual use of this library like (login, getProfile, etc..).
     * 
     * @param configuration
     *            The configuration of this library
     */
    public static void setConfiguration(SimpleFacebookConfiguration configuration) {
	mConfiguration = configuration;
	SessionManager.configuration = configuration;
    }

    /**
     * Get configuration
     * 
     * @return
     */
    public static SimpleFacebookConfiguration getConfiguration() {
	return mConfiguration;
    }

    /**
     * Login to Facebook
     * 
     * @param onLoginListener
     */
    public void login(OnLoginListener onLoginListener) {
	mSessionManager.login(onLoginListener);
    }

    /**
     * Logout from Facebook
     */
    public void logout(OnLogoutListener onLogoutListener) {
	mSessionManager.logout(onLogoutListener);
    }

    /**
     * Are we logged in to facebook
     * 
     * @return <code>True</code> if we have active and open session to facebook
     */
    public boolean isLogin() {
	return mSessionManager.isLogin(true);
    }

    /**
     * Get my profile from facebook.<br>
     * This method will return profile with next default properties depends on
     * permissions you have:<br>
     * <em>id, name, first_name, middle_name, last_name, gender, locale, languages, link, username, timezone, updated_time, verified, bio, birthday, education, email, 
     * hometown, location, political, favorite_athletes, favorite_teams, quotes, relationship_status, religion, website, work</em>
     * 
     * <br>
     * <br>
     * If you need additional or other profile properties like:
     * <em>age_range, picture and more</em>, then use this method:
     * {@link #getProfile(Properties, OnProfileRequestListener)} <br>
     * <br>
     * <b>Note:</b> If you need only few properties for your app, then it is
     * recommended <b>not</b> to use this method, since getting unnecessary
     * properties is time consuming task from facebook side.<br>
     * It is recommended in this case, to use
     * {@link #getProfile(Properties, OnProfileRequestListener)} and mention
     * only needed properties.
     * 
     * @param onProfileRequestListener
     */
    public void getProfile(OnProfileRequestListener onProfileRequestListener) {
	getProfile(null, onProfileRequestListener);
    }

    /**
     * Get my profile from facebook by mentioning specific parameters. <br>
     * For example, if you need: <em>square picture 500x500 pixels</em>
     * 
     * @param onProfileRequestListener
     * @param properties
     *            The {@link Properties}. <br>
     *            To create {@link Properties} instance use:
     * 
     *            <pre>
     * // define the profile picture we want to get
     * PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
     * pictureAttributes.setType(PictureType.SQUARE);
     * pictureAttributes.setHeight(500);
     * pictureAttributes.setWidth(500);
     * 
     * // create properties
     * Properties properties = new Properties.Builder().add(Properties.ID).add(Properties.FIRST_NAME).add(Properties.PICTURE, attributes).build();
     * </pre>
     */
    public void getProfile(Properties properties, OnProfileRequestListener onProfileRequestListener) {
	GetProfileAction getProfileAction = new GetProfileAction(mSessionManager);
	getProfileAction.setProperties(properties);
	getProfileAction.setOnProfileRequestListener(onProfileRequestListener);
	getProfileAction.execute();
    }

    /**
     * Get my friends from facebook.<br>
     * This method will return profile with next default properties depends on
     * permissions you have:<br>
     * <em>id, name</em>
     * 
     * <br>
     * <br>
     * If you need additional or other friend properties like:
     * <em>education, location and more</em>, then use this method:
     * {@link #getFriends(Properties, OnFriendsRequestListener)} <br>
     * <br>
     * 
     * @param onFriendsRequestListener
     */
    public void getFriends(OnFriendsRequestListener onFriendsRequestListener) {
	getFriends(null, onFriendsRequestListener);
    }

    /**
     * Get my friends from facebook by mentioning specific parameters. <br>
     * For example, if you need: <em>id, last_name, picture, birthday</em>
     * 
     * @param onFriendsRequestListener
     * @param properties
     *            The {@link Properties}. <br>
     *            To create {@link Properties} instance use:
     * 
     *            <pre>
     * // define the friend picture we want to get
     * PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
     * pictureAttributes.setType(PictureType.SQUARE);
     * pictureAttributes.setHeight(500);
     * pictureAttributes.setWidth(500);
     * 
     * // create properties
     * Properties properties = new Properties.Builder().add(Properties.ID).add(Properties.LAST_NAME).add(Properties.PICTURE, attributes).add(Properties.BIRTHDAY).build();
     * </pre>
     */
    public void getFriends(Properties properties, OnFriendsRequestListener onFriendsRequestListener) {
	GetFriendsAction getFriendsAction = new GetFriendsAction(mSessionManager);
	getFriendsAction.setProperties(properties);
	getFriendsAction.setOnFriendsRequestListener(onFriendsRequestListener);
	getFriendsAction.execute();
    }

    /**
     * Get albums
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#USER_PHOTOS}
     */
    public void getAlbums(OnAlbumsRequestListener onAlbumsRequestListener) {
	GetAlbumsAction getAlbumsAction = new GetAlbumsAction(mSessionManager);
	getAlbumsAction.setOnAlbumsRequestListener(onAlbumsRequestListener);
	getAlbumsAction.execute();
    }

    /**
     * Get app requests
     * 
     * @param onAppRequestsListener
     */
    public void getAppRequests(OnAppRequestsListener onAppRequestsListener) {
	GetAppRequestsAction getAppRequestsAction = new GetAppRequestsAction(mSessionManager);
	getAppRequestsAction.setOnAppRequestsListener(onAppRequestsListener);
	getAppRequestsAction.execute();
    }

    /**
     * 
     * Gets scores using Scores API for games. <br>
     * <br>
     * 
     * 
     * @param onScoresRequestListener
     *            The listener for getting scores
     * @see https://developers.facebook.com/docs/games/scores/
     */
    public void getScores(OnScoresRequestListener onScoresRequestListener) {
	GetScoresAction getScoresAction = new GetScoresAction(mSessionManager);
	getScoresAction.setOnScoresRequestListener(onScoresRequestListener);
	getScoresAction.execute();
    }

    /**
     * 
     * Posts a score using Scores API for games. If missing publish_actions
     * permission, we do not ask again for it.<br>
     * <br>
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#PUBLISH_ACTION}
     * 
     * 
     * @param score
     *            Score to be posted. <code>int</code>
     * @param onPostScoreListener
     *            The listener for posting score
     * @see https://developers.facebook.com/docs/games/scores/
     */
    public void publish(Score score, OnPublishListener onPublishListener) {
	publish((Publishable) score, "me", onPublishListener);
    }

    /**
     * 
     * Publish {@link Feed} on the wall.<br>
     * <br>
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#PUBLISH_ACTION}
     * 
     * @param feed
     *            The feed to publish. Use {@link Feed.Builder} to create a new
     *            <code>Feed</code>
     * @param onPublishListener
     *            The listener for publishing action
     * @see https
     *      ://developers.facebook.com/docs/howtos/androidsdk/3.0/publish-to
     *      -feed/
     */
    public void publish(Feed feed, OnPublishListener onPublishListener) {
	publish((Publishable) feed, "me", onPublishListener);
    }

    /**
     * Share to feed by using dialog or do it silently without dialog. <br>
     * If you use dialog for sharing, you don't have to configure
     * {@link Permissions#PUBLISH_ACTION} since user does the share by himself.<br>
     * <br>
     * <b>Important:</b><br>
     * By setting <code>withDialog=true</code> the default implementation will
     * try to use a native facebook dialog. If option of native dialog will not
     * succeed, then a web facebook dialog will be used.<br>
     * <br>
     * 
     * For having the native dialog, you must add to your <b>manifest.xml</b>
     * 'app_id' meta value:
     * 
     * <pre>
     * {@code <}meta-data
     *      android:name="com.facebook.sdk.ApplicationId"
     *      android:value="@string/app_id" /{@code >}
     * </pre>
     * 
     * And in your <b>string.xml</b> add your app_id. For example:
     * 
     * <pre>
     * {@code <}string name="app_id"{@code >}625994234086470{@code <}/string{@code >}
     * </pre>
     * 
     * @param feed
     *            The feed to post
     * @param withDialog
     *            Set <code>True</code> if you want to use dialog.
     * @param onPublishListener
     */
    public void publish(Feed feed, boolean withDialog, OnPublishListener onPublishListener) {
	if (!withDialog) {
	    // make it silently
	    publish(feed, onPublishListener);
	}
	else {
	    PublishFeedDialogAction publishFeedDialogAction = new PublishFeedDialogAction(mSessionManager);
	    publishFeedDialogAction.setFeed(feed);
	    publishFeedDialogAction.setOnPublishListener(onPublishListener);
	    publishFeedDialogAction.execute();
	}
    }

    /**
     * Publish open graph story.<br>
     * <br>
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#PUBLISH_ACTION}
     * 
     * @param openGraph
     * @param onPublishListener
     */
    public void publish(Story story, OnPublishListener onPublishListener) {
	publish((Publishable) story, "me", onPublishListener);
    }

    /**
     * Publish photo to specific album. You can use
     * {@link #getAlbums(OnAlbumsRequestListener)} to retrieve all user's
     * albums.<br>
     * <br>
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#PUBLISH_STREAM}<br>
     * <br>
     * 
     * <b>Important:</b><br>
     * - The user must own the album<br>
     * - The album should not be full (Max: 200 photos). Check it by
     * {@link Album#getCount()}<br>
     * - The app can add photos to the album<br>
     * - The privacy setting of the app should be at minimum as the privacy
     * setting of the album ( {@link Album#getPrivacy()}
     * 
     * @param photo
     *            The photo to upload
     * @param albumId
     *            The album to which the photo should be uploaded
     * @param onPublishListener
     *            The callback listener
     */
    public void publish(Photo photo, String albumId, OnPublishListener onPublishListener) {
	publish((Publishable) photo, albumId, onPublishListener);
    }

    /**
     * Publish photo to application default album.<br>
     * <br>
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#PUBLISH_STREAM}<br>
     * <br>
     * 
     * <b>Important:</b><br>
     * - The album should not be full (Max: 200 photos). Check it by
     * {@link Album#getCount()}<br>
     * {@link Album#getPrivacy()}
     * 
     * @param photo
     *            The photo to upload
     * @param onPublishListener
     *            The callback listener
     */
    public void publish(Photo photo, OnPublishListener onPublishListener) {
	publish((Publishable) photo, "me", onPublishListener);
    }

    /**
     * Publish video to "Videos" album. <br>
     * 
     * <b>Permission:</b><br>
     * {@link Permissions#PUBLISH_STREAM}<br>
     * <br>
     * 
     * @param video
     *            The video to upload
     * @param onPublishListener
     *            The callback listener
     */
    public void publish(Video video, OnPublishListener onPublishListener) {
	publish((Publishable) video, "me", onPublishListener);
    }

    /**
     * Publish any publishable entity
     * 
     * @param publishable
     * @param onPublishListener
     */
    public void publish(Publishable publishable, String target, OnPublishListener onPublishListener) {
	PublishAction publishAction = new PublishAction(mSessionManager);
	publishAction.setPublishable(publishable);
	publishAction.setTarget(target);
	publishAction.setOnPublishListener(onPublishListener);
	publishAction.execute();
    }

    /**
     * Open invite dialog and can add multiple friends
     * 
     * @param message
     *            (Optional) The message inside the dialog. It could be
     *            <code>null</code>
     * @param data
     *            (Optional) The data you want to send within the request. It
     *            could be <code>null</code>
     * @param onInviteListener
     *            The listener. It could be <code>null</code>
     */
    public void invite(String message, final OnInviteListener onInviteListener, String data) {
	InviteAction inviteAction = new InviteAction(mSessionManager);
	inviteAction.setMessage(message);
	inviteAction.setData(data);
	inviteAction.setOnInviteListener(onInviteListener);
	inviteAction.execute();
    }

    /**
     * Open invite dialog and invite only specific friend
     * 
     * @param to
     *            The id of the friend profile
     * @param message
     *            The message inside the dialog. It could be <code>null</code>
     * @param data
     *            (Optional) The data you want to send within the request. It
     *            could be <code>null</code>
     * @param onInviteListener
     *            The listener. It could be <code>null</code>
     */
    public void invite(String to, String message, final OnInviteListener onInviteListener, String data) {
	InviteAction inviteAction = new InviteAction(mSessionManager);
	inviteAction.setTo(to);
	inviteAction.setMessage(message);
	inviteAction.setData(data);
	inviteAction.setOnInviteListener(onInviteListener);
	inviteAction.execute();
    }

    /**
     * Open invite dialog and invite several specific friends
     * 
     * @param suggestedFriends
     *            The ids of friends' profiles
     * @param message
     *            The message inside the dialog. It could be <code>null</code>
     * @param data
     *            (Optional) The data you want to send within the request. It
     *            could be <code>null</code>
     * @param onInviteListener
     *            The error listener. It could be <code>null</code>
     */
    public void invite(String[] suggestedFriends, String message, final OnInviteListener onInviteListener, String data) {
	InviteAction inviteAction = new InviteAction(mSessionManager);
	inviteAction.setSuggestions(suggestedFriends);
	inviteAction.setMessage(message);
	inviteAction.setData(data);
	inviteAction.setOnInviteListener(onInviteListener);
	inviteAction.execute();
    }

    /**
     * 
     * Deletes an apprequest.<br>
     * <br>
     * 
     * @param inRequestId
     *            Input request id to be deleted. Note that it should have the
     *            form {USERID}_{REQUESTID} <code>String</code>
     * @param onDeleteRequestListener
     *            The listener for deletion action
     * @see https
     *      ://developers.facebook.com/docs/android/app-link-requests/#step3
     */
    public void deleteRequest(String inRequestId, final OnDeleteRequestListener onDeleteRequestListener) {
	DeleteRequestAction deleteRequestAction = new DeleteRequestAction(mSessionManager);
	deleteRequestAction.setRequestId(inRequestId);
	deleteRequestAction.setOnDeleteRequestListener(onDeleteRequestListener);
	deleteRequestAction.execute();
    }

    /**
     * 
     * Requests any new permission in a runtime. <br>
     * <br>
     * Useful when you just want to request the action and won't be publishing
     * at the time, but still need the updated <b>access token</b> with the
     * permissions (possibly to pass back to your backend).
     * 
     * <br>
     * <b>Must be logged to use.</b>
     * 
     * @param permissions
     *            New permissions you want to have. This array can include READ
     *            and PUBLISH permissions in the same time. Just ask what you
     *            need.<br>
     * <br>
     * @param showPublish
     *            This flag is relevant only in cases when new permissions
     *            include PUBLISH permission. Then you can decide if you want
     *            the dialog of requesting publish permission to appear <b>right
     *            away</b> or <b>later</b>, at first time of real publish
     *            action.<br>
     * <br>
     * @param onNewPermissionsListener
     *            The listener for the requesting new permission action.
     */
    public void requestNewPermissions(Permission[] permissions, boolean showPublish, OnNewPermissionsListener onNewPermissionsListener) {
	mSessionManager.requestNewPermissions(permissions, showPublish, onNewPermissionsListener);
    }

    /**
     * Get the list of all granted permissions. <br>
     * Use {@link Permission#fromValue(String)} to get the {@link Permission}
     * object from string in this list.
     * 
     * @return List of granted permissions
     */
    public List<String> getGrantedPermissions() {
	return mSessionManager.getActiveSessionPermissions();
    }

    /**
     * @return <code>True</code> if all permissions were granted by the user,
     *         otherwise return <code>False</code>
     */
    public boolean isAllPermissionsGranted() {
	List<String> grantedPermissions = getGrantedPermissions();
	List<String> readPermissions = new ArrayList<String>(mConfiguration.getReadPermissions());
	List<String> publishPermissions = new ArrayList<String>(mConfiguration.getPublishPermissions());
	readPermissions.removeAll(grantedPermissions);
	publishPermissions.removeAll(grantedPermissions);
	if (readPermissions.size() > 0 || publishPermissions.size() > 0) {
	    return false;
	}
	return true;
    }

    /**
     * Notifies the events system that the app has launched & logs an
     * activatedApp event. Should be called whenever your app becomes active,
     * typically in the onResume() method of each long-running Activity of your
     * app.
     */
    public void eventAppLaunched() {
	AppEventsLogger.activateApp(mActivity.getApplicationContext(), mConfiguration.getAppId());
    }

    /**
     * Call this inside your activity in {@link Activity#onActivityResult}
     * method
     * 
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public boolean onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
	return mSessionManager.onActivityResult(activity, requestCode, resultCode, data);
    }

    /**
     * Clean all references like Activity to prevent memory leaks
     */
    public void clean() {
	mActivity = null;
	SessionManager.activity = null;
    }

}
