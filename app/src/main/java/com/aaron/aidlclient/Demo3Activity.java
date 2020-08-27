package com.aaron.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.aidlserver.IComputerManagerObserver;
import com.aaron.aidlserver.IOnComputerArrivedListener;
import com.aaron.aidlserver.entity.ComputerEntity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class Demo3Activity extends AppCompatActivity implements View.OnClickListener {

    private Button bind_demo3_btn;
    private Button unbind_demo3_btn;
    private Button test_demo3_btn;
    private Button clear_demo3_btn;
    private LinearLayout mShowLinear;

    private boolean mIsBindService;
    private static final int MESSAGE_COMPUTER_ARRIVED = 1;
    private IComputerManagerObserver mRemoteComputerManager;

    private IOnComputerArrivedListener mOnComputerArrivedListener = new IOnComputerArrivedListener.Stub(){

        @Override
        public void onComputerArrived(ComputerEntity computer) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_COMPUTER_ARRIVED,computer).sendToTarget();
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_COMPUTER_ARRIVED:
                    ComputerEntity computer = (ComputerEntity)msg.obj;
                    String str = "computerId:" + String.valueOf(computer.computerId) +
                            " brand:" + computer.brand +
                            " model:" + computer.model ;
                    TextView textView = new TextView(Demo3Activity.this);
                    textView.setText(str);
                    mShowLinear.addView(textView);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo3);
        initViews();
        mIsBindService = false;
    }

    private void initViews() {
        bind_demo3_btn = findViewById(R.id.bind_demo3_btn);
        unbind_demo3_btn = findViewById(R.id.unbind_demo3_btn);
        test_demo3_btn = findViewById(R.id.test_demo3_btn);
        clear_demo3_btn = findViewById(R.id.clear_demo3_btn);

        bind_demo3_btn.setOnClickListener(this);
        unbind_demo3_btn.setOnClickListener(this);
        test_demo3_btn.setOnClickListener(this);
        clear_demo3_btn.setOnClickListener(this);

        bind_demo3_btn.setEnabled(true);
        unbind_demo3_btn.setEnabled(true);
        test_demo3_btn.setEnabled(true);
        clear_demo3_btn.setEnabled(true);

        mShowLinear = findViewById(R.id.show_demo3_linear);
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bind_demo3_btn:
                bindService();
                break;
            case R.id.unbind_demo3_btn:
                Toast.makeText(this,"unbind service success",Toast.LENGTH_SHORT).show();
                unbindService();
                break;
            case R.id.test_demo3_btn:
                if (!mIsBindService || mRemoteComputerManager == null){
                    Toast.makeText(this,"not bind service",Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ComputerEntity computer = new ComputerEntity(3,"hp","envy13");
                    mRemoteComputerManager.addComputer(computer);
                    List<ComputerEntity> computerList = mRemoteComputerManager.getComputerList();
                    for (int i =0;i<computerList.size();i++){
                        String str = "computerId:" + String.valueOf(computerList.get(i).computerId) +
                                " brand:" + computerList.get(i).brand +
                                " model:" + computerList.get(i).model ;
                        TextView textView = new TextView(this);
                        textView.setText(str);
                        mShowLinear.addView(textView);
                    }
                    mRemoteComputerManager.registerUser(mOnComputerArrivedListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.clear_demo3_btn:
                mShowLinear.removeAllViews();
                break;
        }
    }

    private void bindService(){
        Intent intent = new Intent();
        intent.setAction("com.aaron.aidlserver.action.COMPUTER_OBSERVER_SERVICE");
        intent.setPackage("com.aaron.aidlserver");//这里你需要设置你应用的包名
        mIsBindService = bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(){
        if(!mIsBindService){
            return;
        }
        if (mRemoteComputerManager != null && mRemoteComputerManager.asBinder().isBinderAlive()){
            try {
                mRemoteComputerManager.unRegisterUser(mOnComputerArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        mIsBindService = false;
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(mRemoteComputerManager != null){
                mRemoteComputerManager.asBinder().unlinkToDeath(mDeathRecipient,0);
                mRemoteComputerManager = null;
                bindService();
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsBindService = true;
            Toast.makeText(Demo3Activity.this,"bind service success",Toast.LENGTH_SHORT).show();
            mRemoteComputerManager = IComputerManagerObserver.Stub.asInterface(service);
            try {
                mRemoteComputerManager.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteComputerManager = null;
        }
    };
}
