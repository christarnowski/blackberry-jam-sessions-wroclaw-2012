package pl.itraff.TestApi.ItraffApi;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

/**
 * Example usage:
 * 
 * <p>
 * Call api:
 * 
 * <pre>
 * {@code
 * 	ItraffApi api = new ItraffApi(CLIENT_API_ID, CLIENT_API_KEY, TAG, true);
 * 
 * 	api.sendPhoto(photo, itraffApiHandler);
 * }
 * </pre>
 * <p>
 * Get response:
 * 
 * <pre>
 * {@code
 * 	private Handler itraffApiHandler = new Handler() {
 * 		// callback from api
 * 		&#064;Override
 * 		public void handleMessage(Message msg) {
 * 			Bundle data = msg.getData();
 * 			if (data != null) {
 * 				Integer status = data.getInt(ItraffApi.STATUS, -1);
 * 				String response = data.getString(ItraffApi.RESPONSE);
 * 
 * 				if (status == 0) { // status ok
 * 					// TODO response contains json with your data
 * 				} else if (status == -1) { // application error (for example
 * 											// timeout)
 * 					// TODO show application error
 * 				} else { // error from api
 * 					// TODO get "message" from response json - it contains api error
 * 				}
 * 			}
 * 		}
 * 	};
 * }
 * </pre>
 */
public class ItraffApi {

    private String TAG = "TestApi";
    public static final String API_URL = "http://recognize.im/recognize/";
    private Integer clientId;
    private String clientKey;
    private String customUrl;
    Boolean debug = false;

    public static final String RESPONSE = "response";
    public static final String STATUS = "status";
    public static final String HASH_HEADER = "x-itraff-hash";
    public static final String ID = "UNIQUE_IDENTIFIER";

    public final static String ACCEPT = "Accept";
    public final static String APPLICATION_JSON = "application/json";

    /**
     * ItraffApi public constructor
     * 
     * @param clientId client api id
     * @param clientKey client api key
     * @param debugTag custom debug TAG
     * @param debug enable Log if true
     */
    public ItraffApi(Integer clientId, String clientKey, String debugTag, Boolean debug) {
        this.clientId = clientId;
        this.debug = debug;
        this.TAG = debugTag;
        this.clientKey = clientKey;
    }

    /**
     * ItraffApi public constructor
     * 
     * @param clientId client api id
     * @param clientKey client api key
     * @param customUrl custom url; default is: "http://recognize.im/recognize/"
     * @param debugTag custom debug TAG
     * @param debug enable Log if true
     */
    public ItraffApi(Integer clientId, String clientKey, String customUrl, String debugTag,
            Boolean debug) {
        this.clientId = clientId;
        this.debug = debug;
        this.customUrl = customUrl;
        this.TAG = debugTag;
        this.clientKey = clientKey;
    }

    /**
     * Sends photo to recognition server
     * 
     * @param photo Bitmap image that is send through api
     * @param itraffApiHandler Handler used to return message from api
     */
    public void sendPhoto(byte[] photo, int uniqueId, Handler itraffApiHandler) {
        try {
            HttpPost postPhoto = getPostPhotoRequest(photo);
            ItraffApiPostPhoto postPhotoAsyncTask =
                    new ItraffApiPostPhoto(itraffApiHandler, postPhoto, uniqueId, TAG, debug);
            postPhotoAsyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpPost getPostPhotoRequest(byte[] photo) throws UnsupportedEncodingException {
        String requestUrl = customUrl;
        if (requestUrl != null) {
            if (clientId != null) {
                requestUrl += clientId;
            }

        } else {
            if (clientId != null) {
                requestUrl = API_URL + clientId;
            } else {
                requestUrl = API_URL;
            }
        }

        HttpPost postRequest = new HttpPost(requestUrl);
        log("Request url: " + requestUrl);

        HttpParams params = new BasicHttpParams();
        postRequest.setParams(params);

        // create entity
        HttpEntity entity = new ByteArrayEntity(photo);
        postRequest.setEntity(entity);

        // get hash MD5(clientKey+image)
        String hash = ItraffApi.getMD5FromKeyAndImage(clientKey, photo);

        log("hash MD5(clientKey+image):");
        log(hash);

        Header header = new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg");
        postRequest.addHeader(header);
        header = new BasicHeader(HASH_HEADER, hash);
        postRequest.addHeader(header);
        header = new BasicHeader(ACCEPT, APPLICATION_JSON);
        postRequest.addHeader(header);

        return postRequest;
    }

    /**
     * Generates MD5 from client api key and image bytes
     * 
     * @param clientKey client api key
     * @param image byte[] image
     * @return md5 string
     */
    public static String getMD5FromKeyAndImage(String clientKey, byte[] image) {
        String hash = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.reset();
            md.update(clientKey.getBytes("UTF-8"));
            md.update(image);
            byte[] array = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            hash = sb.toString();
        } catch (Exception e) {
            hash = null;
        }
        return hash;
    }

    /**
     * Check if user is connected to internet
     * 
     * @param appContext application context
     * @return true if is connected or connecting
     */
    public static boolean isOnline(Context appContext) {
        ConnectivityManager cm =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Logs message if debug == true using Log.v(TAG, message)
     * 
     * @param message message to log
     */
    public void log(String message) {
        if (debug) {
            Log.v(TAG, message);
        }
    }
}
