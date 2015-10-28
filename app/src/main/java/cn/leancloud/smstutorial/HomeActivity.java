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
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    EditText mMobileNumber;
    TextView mUsername;
    EditText mSMSCode;

    Button active;
    Button sendNotice;
    Button operationVerify;
    Button doVerify;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mUsername = (TextView)findViewById(R.id.value_username);
        mMobileNumber = (EditText)findViewById(R.id.value_mobile_number);
        mSMSCode = (EditText)findViewById(R.id.home_operation_verify_sms_code);

        active = (Button)findViewById(R.id.home_active);
        sendNotice=(Button)findViewById(R.id.home_send_notice);
        operationVerify=(Button)findViewById(R.id.home_operation_verify);
        doVerify = (Button)findViewById(R.id.home_operation_verify_sms_code_do_verify);
        logout = (Button)findViewById(R.id.logout);

        mUsername.setText(AVUser.getCurrentUser().getUsername());
        mMobileNumber.setText(AVUser.getCurrentUser().getMobilePhoneNumber());

        active.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobileNumber = mMobileNumber.getText().toString().trim();
                if (!SignUpActivity.isMobileNumberValid(mobileNumber)) {
                    mMobileNumber.setError(getString(R.string.error_invalid_mobile_phone));
                    return;
                }
                Log.d("1",mobileNumber);
                Log.d("2", AVUser.getCurrentUser().getMobilePhoneNumber());
                Log.d("3",Boolean.toString(AVUser.getCurrentUser().isMobilePhoneVerified()));
                if (AVUser.getCurrentUser().getMobilePhoneNumber().equals(mobileNumber) && AVUser.getCurrentUser().isMobilePhoneVerified()) {
                    mMobileNumber.setError(getString(R.string.error_diplicate_active));
                    return;
                }
                AVUser.requestMobilePhoneVerifyInBackground(mobileNumber, new RequestMobileCodeCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Intent intent = new Intent(getApplicationContext(), VerifyMoblieNumber.class);
                            Bundle b = new Bundle();
                            b.putString("mobile_number", mobileNumber);
                            intent.putExtras(b);
                            startActivity(intent);
                        } else {
                            Log.e("Home", e.getMessage());
                        }
                    }
                });
            }
        });

        /**
         * 调试真实结果，需要对应的 LeanCloud 账户至少拥有 200 人民币余额，方可创建模板。
         */
        sendNotice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object>  parameters = new HashMap<String, Object>();
                parameters.put("service_name","月度周刊");
                parameters.put("order_id","7623432424540");
                AVOSCloud.requestSMSCodeInBackground(AVUser.getCurrentUser().getMobilePhoneNumber(), "MyNoticeTemplate", parameters, new RequestMobileCodeCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(getBaseContext(), getString(R.string.msg_notice_sent), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Home.SendNotice", e.getMessage());
                        }
                    }
                });
            }
        });

        /**
         *
         */
        operationVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AVOSCloud.requestSMSCodeInBackground(AVUser.getCurrentUser().getMobilePhoneNumber(), "LCSMS", "敏感操作", 10, new RequestMobileCodeCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            mSMSCode.requestFocus();
                        } else {
                            Log.e("Home.OperationVerify", e.getMessage());
                        }
                    }
                });
            }
        });

        doVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsCode = mSMSCode.getText().toString();
                if(!VerifyMoblieNumber.isSMSCodeValid(smsCode)){
                    mSMSCode.setError(getString(R.string.error_invalid_sms_code_format));
                    return;
                }
                AVOSCloud.verifyCodeInBackground(smsCode, AVUser.getCurrentUser().getMobilePhoneNumber(), new AVMobilePhoneVerifyCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(getBaseContext(), getString(R.string.msg_operation_valid), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Home.DoOperationVerify", e.getMessage());
                        }
                    }
                });
            }
        });

        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}
