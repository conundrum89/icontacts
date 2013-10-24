package au.com.icontacts.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * A bound service that instantiates the authenticator when started.
 */
public class AuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private IDashboardAuthenticator mIDashboardAuthenticator;

    @Override
    public void onCreate() {
        mIDashboardAuthenticator = new IDashboardAuthenticator(this);
    }

    /**
     * Returns the authenticator's IBinder when the system binds to this Service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mIDashboardAuthenticator.getIBinder();
    }
}
