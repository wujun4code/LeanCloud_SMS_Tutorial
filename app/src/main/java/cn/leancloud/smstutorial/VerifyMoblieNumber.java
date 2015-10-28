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
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

import java.util.ArrayList;
import java.util.List;

public class VerifyMoblieNumber extends AppCompatActivity {

    EditText mMobileNumber;
    Button resend;
    EditText mSMSCode;
    Button verify;
    Button skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        String mobile_number = b.getString("mobile_number");
        setContentView(R.layout.activity_verify_mobile_number);

        mMobileNumber = (EditText)findViewById(R.id.verify_mobileNumber);
        mMobileNumber.setText(mobile_number);

        mSMSCode=(EditText)findViewById(R.id.sms_code);
        mSMSCode.requestFocus();

        verify = (Button)findViewById(R.id.verify);
        verify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSMSCode.setError(null);
                String smsCode = mSMSCode.getText().toString();
                if(isSMSCodeValid(smsCode)) {
                    AVUser.verifyMobilePhoneInBackground(smsCode, new AVMobilePhoneVerifyCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                mSMSCode.setError(getString(R.string.error_wrong_sms_code));
                            } else {
                                AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    mSMSCode.setError(getString(R.string.error_invalid_sms_code_format));
                }

            }
        });

        skip=(Button)findViewById(R.id.verify_skip);
        skip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
    }

    /**
     * 判断验证码是否为 6 位纯数字，LeanCloud 统一的验证码均为 6  位纯数字。
     * @param smsCode
     * @return
     */
    public static boolean isSMSCodeValid(String smsCode) {
        String regex = "^\\d{6}$";
        return smsCode.matches(regex);
    }
}
