package com.aaron.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.aidlserver.IComputerManager;
import com.aaron.aidlserver.entity.ComputerEntity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class Demo2Activity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "DEMO2";

    private Button bind_demo2_btn;
    private Button unbind_demo2_btn;
    private Button test_demo2_btn;
    private Button clear_demo2_btn;

    private LinearLayout mShowLinear;

    private boolean mIsBindService;
    private IComputerManager mRemoteComputerManager;

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e("DEMO2", "binderDied");
            Toast.makeText(Demo2Activity.this,"binderDied",Toast.LENGTH_SHORT).show();
            if(mRemoteComputerManager != null){
                mRemoteComputerManager.asBinder().unlinkToDeath(mDeathRecipient,0);
                mRemoteComputerManager = null;
                bindService();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        initViews();
        mIsBindService = false;
    }

    private void initViews() {
        bind_demo2_btn = findViewById(R.id.bind_demo2_btn);
        unbind_demo2_btn = findViewById(R.id.unbind_demo2_btn);
        test_demo2_btn = findViewById(R.id.test_demo2_btn);
        clear_demo2_btn = findViewById(R.id.clear_demo2_btn);

        bind_demo2_btn.setOnClickListener(this);
        unbind_demo2_btn.setOnClickListener(this);
        test_demo2_btn.setOnClickListener(this);
        clear_demo2_btn.setOnClickListener(this);

        bind_demo2_btn.setEnabled(true);
        unbind_demo2_btn.setEnabled(true);
        test_demo2_btn.setEnabled(true);
        clear_demo2_btn.setEnabled(true);

        mShowLinear = findViewById(R.id.show_linear);
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bind_demo2_btn:
                bindService();
                break;
            case R.id.unbind_demo2_btn:
                Toast.makeText(this,"unbind service success",Toast.LENGTH_SHORT).show();
                unbindService();
                break;
            case R.id.test_demo2_btn:
                if (!mIsBindService || mRemoteComputerManager == null){
                    Log.e(TAG, "not bind to any service");
                    Toast.makeText(this,"not bind service",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    List<ComputerEntity> computerList = mRemoteComputerManager.getComputerList();
                    for (int i =0;i<computerList.size();i++){
                        String str = "computerId:" + String.valueOf(computerList.get(i).computerId) +
                                " brand:" + computerList.get(i).brand +
                                " model:" + computerList.get(i).model ;
                        TextView textView = new TextView(this);
                        textView.setText(str);
                        mShowLinear.addView(textView);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.clear_demo2_btn:
                mShowLinear.removeAllViews();
                break;
        }
    }

    private void bindService(){
        Intent intent = new Intent();
        intent.setAction("com.aaron.aidlserver.action.COMPUTER_SERVICE");
        intent.setPackage("com.aaron.aidlserver");//这里你需要设置你应用的包名
        mIsBindService = bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(){
        if(!mIsBindService){
            return;
        }
        mIsBindService = false;
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsBindService = true;
            Toast.makeText(Demo2Activity.this,"bind service success",Toast.LENGTH_SHORT).show();
            mRemoteComputerManager = IComputerManager.Stub.asInterface(service);
            try {
                mRemoteComputerManager.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
            mRemoteComputerManager = null;
        }
    };
}
