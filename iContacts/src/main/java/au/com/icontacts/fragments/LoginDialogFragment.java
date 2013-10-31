package au.com.icontacts.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import au.com.icontacts.R;

/**
 * Presents the user with login fields for use in a Dialog.
 */
public class LoginDialogFragment extends DialogFragment
        implements TextView.OnEditorActionListener, DialogInterface.OnShowListener {

    private AlertDialog mLoginDialog;
    private boolean mDialogReady = false;
    public EditText usernameField;
    public EditText passwordField;
    public ProgressBar loginActivityIndicator;

    /**
     * The activity that creates and instance of this dialog fragment must implement
     * this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface LoginDialogListener {
        public void onLoginClick(LoginDialogFragment dialog);
    }

    private LoginDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LoginDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View loginView = inflater.inflate(R.layout.fragment_login_dialog, null);

        usernameField = (EditText) loginView.findViewById(R.id.username);
        passwordField = (EditText) loginView.findViewById(R.id.password);
        loginActivityIndicator = (ProgressBar) loginView.findViewById(R.id.login_activity_indicator);

        usernameField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordField.setOnEditorActionListener(this);

        // TODO: Use ButterKnife to replace these onClickListeners
        builder.setTitle(R.string.login)
                .setView(loginView)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        mLoginDialog = builder.create();

        mLoginDialog.setOnShowListener(this);
        return mLoginDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        getActivity().finish();
    }

    /** Sets the "GO" button on the soft keyboard to trigger a login event. */
    public boolean onEditorAction(TextView view, int action, KeyEvent event) {
        if (action == EditorInfo.IME_ACTION_DONE) {
            validateLoginFields();
        }
        return false;
    }

    /** Validates the form fields before submitting back to LoginActivity */
    public void validateLoginFields() {
        if (usernameField.getText().length() > 0 && passwordField.getText().length() > 0) {
            mListener.onLoginClick(LoginDialogFragment.this);
        } else {
            Toast.makeText(
                    getActivity(),
                    getActivity().getString(R.string.login_cannot_be_blank),
                    Toast.LENGTH_LONG).show();
        }
    }

    /** Replaces the positive onClick listeners with one that doesn't dismiss the dialog. */
    @Override
    public void onShow(DialogInterface dialog) {
        if (!mDialogReady) {
            Button button = mLoginDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            // TODO: ButterKnife it up.
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateLoginFields();
                }
            });
            mDialogReady = true;
        }
    }
}
