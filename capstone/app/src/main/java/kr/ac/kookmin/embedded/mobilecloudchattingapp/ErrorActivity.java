package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import helper.Message;

public class ErrorActivity extends AppCompatActivity {

    Message message = new Message();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_error);


        message.okayMsgShow("에러가 났으니 처음 화면으로 돌아갑니다.");
    }
}
