package au.com.icontacts.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import au.com.icontacts.R;
import au.com.icontacts.activities.LoginActivity;

/**
 * Implements AbstractAccountAuthenticator to connect to the IDashboard API.
 */
public class IDashboardAuthenticator extends AbstractAccountAuthenticator {
    private final Context mContext;

    public IDashboardAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    // Generates an intent with all the extras required to display the LoginActivity
    private Bundle getLoginIntent(
            AccountAuthenticatorResponse response,
            String accountType,
            String authTokenType,
            boolean isAddingNewAccount) {

        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        if (isAddingNewAccount) intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    // Editing properties is not supported (?)
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
        throw new UnsupportedOperationException();
    }

    // Redirects the user to the LoginActivity
    @Override
    public Bundle addAccount(
            AccountAuthenticatorResponse response,
            String accountType,
            String authTokenType,
            String[] requiredFeatures,
            Bundle options) throws NetworkErrorException {

        return getLoginIntent(response, accountType, authTokenType, true);
    }

    // Ignore attempts to confirm credentials - not sure what this is used for
    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response,
            Account account,
            Bundle options) {
        return null;
    }

    // Retrieves an AuthToken, including attempts to re-authenticate with a password if the
    // authtoken is expired or invalidated.
    @Override
    public Bundle getAuthToken(
            AccountAuthenticatorResponse response,
            Account account,
            String authTokenType,
            Bundle options) throws NetworkErrorException {

        // Extract the username and password from the AccountManager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        String authToken = am.peekAuthToken(account, authTokenType);

        // Give one more try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                // authToken = IDashApi.userSignIn(account.name, password, authTokenType);
            }
        }

        // If we get an authToken, we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // Else we need to re-prompt for credentials, so return the Intent.
        return getLoginIntent(response, account.type, authTokenType, false);
    }

    // Displayed when another application requests an authtoken from this authenticator
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return mContext.getResources().getString(R.string.full_access_authtoken_label);
    }

    // Updating user credentials is not supported (and probably doesn't need to be?)
    @Override
    public Bundle updateCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            String s,
            Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Checking features for the account is not supported (CLEVER!)
    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse r,
            Account account,
            String[] strings) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
