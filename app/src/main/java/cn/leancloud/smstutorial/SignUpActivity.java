package cn.leancloud.smstutorial;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wujun on 10/27/15.
 */
public class SignUpActivity extends AppCompatActivity {

    EditText mUsername;
    EditText mEmail;
    EditText mMobileNumber;
    EditText mPassword;
    Button mSignUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsername =(EditText)findViewById(R.id.sign_up_username);
        mEmail =(EditText)findViewById(R.id.sign_up_email);
        mMobileNumber= (EditText)findViewById(R.id.sign_up_mobileNumber);
        mPassword= (EditText)findViewById(R.id.sign_up_password);

        mSignUpButton = (Button)findViewById(R.id.sign_up);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                String mobileNumber = mMobileNumber.getText().toString();
//                AVUser nUser =new AVUser();
//                nUser.setUsername(mUsername.getText().toString());
//                nUser.setEmail(mEmail.getText().toString());
//                nUser.setPassword(mPassword.getText().toString());
//                nUser.setMobilePhoneNumber(mobileNumber);
                attemptSignUp();
            }
        });
    }
    private void attemptSignUp() {

        // Reset errors.
        mEmail.setError(null);
        mMobileNumber.setError(null);

        boolean cancel = false;
        boolean should_go_to_home_activity = false;
        boolean has_email = false;
        View focusView = null;

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        final String mobileNumber = mMobileNumber.getText().toString();
        String email = mEmail.getText().toString();

        AVUser nUser =new AVUser();

        //nUser.setEmail(null);

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            has_email = false;
        }else if(emailValidation(email)){
            has_email =true;
        } else {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }
        if(TextUtils.isEmpty(mobileNumber)){
            should_go_to_home_activity =true;
        } else {
            if(!isMobileNumberValid(mobileNumber)) {
                mMobileNumber.setError(getString(R.string.error_invalid_mobile_phone));
                focusView = mPassword;
                cancel = true;
                should_go_to_home_activity =false;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            nUser.setUsername(username);
            nUser.setPassword(password);
            if(has_email){
                nUser.setEmail(email);
            }
            if(!TextUtils.isEmpty(mobileNumber)) {
                nUser.setMobilePhoneNumber(mobileNumber);
            }
            final boolean finalShould_go_to_home_activity = should_go_to_home_activity;
            nUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        if (finalShould_go_to_home_activity) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            Intent intent = new Intent(getApplicationContext(), VerifyMoblieNumber.class);
                            Bundle b = new Bundle();
                            b.putString("mobile_number", mobileNumber);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    } else {
                        Log.e("SignUp", e.getMessage());
                    }
                }
            });

        }
    }
    /**
     * 验证手机号是否符合大陆的标准格式
     * @param mobiles
     * @return
     */
    public static boolean isMobileNumberValid(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证手机号是否标准格式
     * @param email
     * @return
     */
    public static boolean emailValidation(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    /**
     * 推荐密码至少长度为 6 位
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }
}
