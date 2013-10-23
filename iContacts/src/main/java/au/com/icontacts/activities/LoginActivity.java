package au.com.icontacts.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Displays a pretty login page with fields for the username and password.
 * @author Matthew Rowland
 */

public class LoginActivity extends Activity
        implements  View.OnClickListener, TextView.OnEditorActionListener {
    private Button mLoginButton;
    private EditText mUsernameField;
    private EditText mPasswordField;

    /** Sets the "GO" button on the soft keyboard to trigger a login event. */
    public boolean onEditorAction(TextView view, int action, KeyEvent event) {
        if (action == EditorInfo.IME_ACTION_GO) {
            loginWithFormFields();
        }
        return false;
    }

    /** Sets the Go button of the form to trigger a login event. */
    public void onClick(View v) {
        loginWithFormFields();
    }

    /**
     * Gets the string values from the username and password form fields, and uses them
     * to attempt a login with the API.
     */
    private void loginWithFormFields() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        // new LoginTask(this).execute(username, password);
    }

    /** Disables the title bar for the Login page. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        // setContentView(R.layout.activity_login_page);

        // set the listeners on button/password/username.
        // Skip this page entirely if the user is already logged in.
    }

}
