package cn.leancloud.smstutorial;

import android.app.Application;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVOSCloud;

/**
 * Created by wujun on 10/27/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.setDebugLogEnabled(true);
        AVOSCloud.initialize(this,"krl29y0N0wU3dMel6k8Kd2Hq","aiyTuVQwSb8uTI0NpYhmVGVV");
    }
}
