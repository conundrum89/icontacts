package au.com.icontacts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
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

import au.com.icontacts.R;

/**
 * Provides access to the iDashboard API.
 */
public final class IDashApi {
    private static Context mContext;
    private static AccountManager mAccountManager;
    private static String mAuthToken;
    private static Account mConnectedAccount;

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
                            Log.i("LoginActivity", "checking Auth token");
                            if (mAuthToken != null) {
                                Log.i("LoginActivity", mAuthToken);
                                String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                                Log.i("LoginActivity", accountName);
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
                return response.getString("access_token");
            }
        } catch (IOException e) {
            // TODO: Handle.
        } catch (JSONException e) {
            // TODO: Handle.
        } finally {
            if (connection != null) { connection.disconnect(); }
        }
        return null;
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
            // TODO: Handle.
        } catch (JSONException e) {
            // TODO: Handle.
        }
        return null;
    }

    private static String generateLoginParameters(String username, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=password&username=")
                .append(username)
                .append("&password=")
                .append(password)
                .append("&client_id=")
                .append(mContext.getString(R.string.client_id))
                .append("&client_secret=")
                .append(mContext.getString(R.string.client_secret));

        return sb.toString();
    }
}
