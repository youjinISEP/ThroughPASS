package com.world.magicline;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.world.magicline.obj.Prop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.world.magicline.obj.Prop.TAG;
public class App extends Application {

    /*private static App instance;

    static App getApplictaion() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
    }*/

    @Override
    public void onCreate(){
        super.onCreate();
        setFCMTokenID();
    }

    // FCM Token ID 전역 변수에 저장
    private void setFCMTokenID() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        Prop.INSTANCE.setFcmTokenId(task.getResult().getToken());

                        // Log and toast
                        Log.d(TAG, Prop.INSTANCE.getFcmTokenId());
//                        Toast.makeText(getApplicationContext(), TAG, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
