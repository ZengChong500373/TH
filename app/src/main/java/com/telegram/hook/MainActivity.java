package com.telegram.hook;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.service.BridgeService;
import com.telegram.hook.utils.LaunUtils;
import com.telegram.hook.utils.SpUtils;


public class MainActivity extends AppCompatActivity {
    //        BridgeService.controllerTelegram(ConstantAction.loginNum,"13438155680");
    TextView tv_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_mode=findViewById(R.id.tv_mode);
        checkMode();
    }



    public void sendSearch(View view) {
        SpUtils.setMode(0);
        checkMode();
    }


    public void sendMsg(View view) {
        SpUtils.setMode(1);
        checkMode();
    }

    public void checkMode(){
        tv_mode.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SpUtils.getMode()==0){
                    tv_mode.setText("筛号码模式");
                }else {
                    tv_mode.setText("发消息模式模式");
                }
            }
        },500);
    }
}
