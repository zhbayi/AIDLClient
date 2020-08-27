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
import android.widget.Toast;

import com.aaron.aidlserver.ICalculate;

import androidx.appcompat.app.AppCompatActivity;

public class Demo1Activity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "DEMO1";
    //是否已经绑定service
    private boolean mIsBindService;
    private ICalculate mCalculate;
    private View v;
    private Button bind_demo1_btn;
    private Button unbind_demo1_btn;
    private Button calculate_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        initViews();
        mIsBindService = false;
    }

    private void initViews() {
        bind_demo1_btn = findViewById(R.id.bind_demo1_btn);
        unbind_demo1_btn = findViewById(R.id.unbind_demo1_btn);
        calculate_btn = findViewById(R.id.calculate_btn);

        bind_demo1_btn.setOnClickListener(this);
        unbind_demo1_btn.setOnClickListener(this);
        calculate_btn.setOnClickListener(this);

        bind_demo1_btn.setEnabled(true);
        unbind_demo1_btn.setEnabled(false);
        calculate_btn.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    public void onClick(View v) {
        this.v = v;
        switch (v.getId()){
            case R.id.bind_demo1_btn:
                Log.e(TAG,"try to invoke bindService");
                bindService();
                break;
            case R.id.unbind_demo1_btn:
                Toast.makeText(this,"unbind service success",Toast.LENGTH_SHORT).show();
                unbindService();
                break;
            case R.id.calculate_btn:
                if (mIsBindService && mCalculate != null ){
                    try {
                        int result = mCalculate.add(2,4);
                        Log.d(TAG,String.valueOf(result));
                        Toast.makeText(this,String.valueOf(result),Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this,"not bind service",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void bindService(){
        Intent intent = new Intent();
        intent.setAction("com.aaron.aidlserver.action.CALCULATE_SERVICE");
        intent.setPackage("com.aaron.aidlserver");//这里你需要设置你应用的包名
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(){
        if(mIsBindService){
            mIsBindService = false;
            unbindService(mConnection);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"bind success");
            Toast.makeText(Demo1Activity.this,"bind service success",Toast.LENGTH_SHORT).show();
            mCalculate = ICalculate.Stub.asInterface(service);
            mIsBindService = true;
            bind_demo1_btn.setEnabled(false);
            unbind_demo1_btn.setEnabled(true);
            calculate_btn.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //重新绑定Service防止系统将服务进程杀死而产生的调用错误。
            bindService();
        }
    };
}
