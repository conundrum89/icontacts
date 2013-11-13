package au.com.icontacts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import au.com.icontacts.R;

/**
 * Provides access to the iDashboard API.
 */
public final class IDashApi {
    private static Context mContext;
    private static AccountManager mAccountManager;
    private static String mAuthToken;
    private static Account mConnectedAccount;

    private static final String BASE_URL = "http://www.idashboard.com.au/api/";
    private static final String CONTACTS = "contacts/";

    public static void connect(Activity activity, final String accountType, final String authTokenType) {
        mContext = activity;
        mAccountManager = AccountManager.get(mContext);
        mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, activity, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.i("connect", mAuthToken);
                            if (mAuthToken != null) {
                                String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                                mConnectedAccount = new Account(accountName, accountType);
                            }
                        } catch (OperationCanceledException e) {
                            // TODO: Handle exception
                        } catch (IOException e) {
                            // TODO: Handle exception
                        } catch (AuthenticatorException e) {
                            // TODO: Handle exception
                        }
                    }
                }
                , null);
    }

    public static String userLogin(String username, String password, String authTokenType) {
        String parameters = generateLoginParameters(username, password);
        try {
            return performLoginRequest(new URL(mContext.getString(R.string.auth_url)), parameters);
        } catch (MalformedURLException e) {
            // TODO: handle
        }
        // Failed to obtain an authToken
        return null;
    }

    /** Performs a login request to the API. */
    private static String performLoginRequest(URL url, String parameters) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();

            if (connection.getResponseCode() != 200) { return null; }

            JSONObject response = readResponse(connection);

            if (response.has("access_token")) {
                Log.i("accessToken", response.getString("access_token"));
                return response.getString("access_token");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        } finally {
            if (connection != null) { connection.disconnect(); }
        }
        return null;
    }

    /** Gets the JSON for a single page of contacts */
    public static JSONObject getContactPage(int page, int perPage) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));

        HttpURLConnection connection = getApiConnection(BASE_URL + CONTACTS, params);
        return readResponse(connection).getJSONObject("results");
    }

    /** Gets full details for a single contact */
    public static JSONObject getContact(int id) throws JSONException {
        HttpURLConnection connection = getApiConnection(BASE_URL + CONTACTS + id);
        return readResponse(connection).getJSONObject("contact");
    }

    /** Gets an API connection for the desired URL */
    private static HttpURLConnection getApiConnection(String urlString) {
        return getApiConnection(urlString, null);
    }

    /** Gets an API connection for the desired URL with the desired parameters */
    @SuppressWarnings("deprecation")
    private static HttpURLConnection getApiConnection(String urlString, HashMap<String, String> params) {
        HttpURLConnection connection;
        Bundle future;
        try {
            if (params != null) { urlString += createParamString(params); }
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // TODO: Either mAccountManager or mConnectedAccount is null here. WHYYY.
                future = mAccountManager.getAuthToken(mConnectedAccount, "Full Access", null, true, null, null).getResult();
            } else {
                future = mAccountManager.getAuthToken(mConnectedAccount, "Full Access", true, null, null).getResult();
            }
            mAuthToken = future.getString(AccountManager.KEY_AUTHTOKEN);
            Log.i("getApiConnection", mAuthToken);
            connection.setRequestProperty("Authorization", "Bearer " + mAuthToken);
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        } catch (OperationCanceledException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        } catch (AuthenticatorException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        }
    }

    /**
     * Turns an API response into a usable JSONObject
     * @param connection the connection from which to read the response
     * @return JSONObject parsed from the API response
     */
    private static JSONObject readResponse(HttpURLConnection connection) {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line).append('\r');
            }
            rd.close();
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // TODO: Handle properly.
        } finally {
            if (connection != null) { connection.disconnect(); }
        }
    }

    /** Creates an HTML parameter string from a HashMap, eg: ?param1=value1&param2=value2 */
    private static String createParamString(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder("?");
        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    private static String generateLoginParameters(String username, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=password&username=").append(username)
                .append("&password=").append(password)
                .append("&client_id=").append(mContext.getString(R.string.client_id))
                .append("&client_secret=").append(mContext.getString(R.string.client_secret));

        return sb.toString();
    }
}
