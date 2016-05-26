package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.StringTokenizer;

import helper.HttpConnection;
import helper.StaticManager;

public class ChattingActivity extends AppCompatActivity {

    Button sendBtn, chattingRefreshBtn;
    EditText editxtForChat;
    String oppoNickname; //상대방 닉네임.
    Intent intent;
    Handler handler;

    static String getMyChatCheck;

    //아래는 채팅 리스트
    ListView mChattingList;
    ArrayAdapter<String> mChattingAdapter; //이걸로 조종하면 됨.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바 없애기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chatting);



        //상대방 아이디를 저장하는 곳
        intent = getIntent();
        oppoNickname = intent.getStringExtra("oppoNickname").trim();

        //채팅에 필요한 뷰와 핸들러
        sendBtn = (Button) findViewById(R.id.sendBtn);
        chattingRefreshBtn = (Button) findViewById(R.id.chattingRefreshBtn);
        editxtForChat = (EditText) findViewById(R.id.editxtForChat);
        handler = new Handler(Looper.getMainLooper());


        //채팅 리스트 객체 만들고 어댑터 적용
        mChattingList = (ListView) findViewById(R.id.chattingList);
        mChattingAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_items);
        mChattingList.setAdapter(mChattingAdapter);



        init(); //초기화

    }


    private void init() {
        String[] key = {"NICKNAME", "FILTER"};
        String[] val = {
                StaticManager.nickname,
                oppoNickname.trim()
        };
        Log.d("Chatting Activity", "내 닉:"+StaticManager.nickname+" 상대 닉:"+oppoNickname);
        //db_login.php에 로그인 요청을 보냄. 결과는 브로드캐스트 리비서에서 받을 것임.
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.connect("http://" + StaticManager.ipAddress + "/getMyChatRoom.php", "getMyChatRoom", key, val);
        Log.d("Chatting Activity",  "init에서 getMyChatRoom.php으로 요청 보냄");
    }







    //send 버튼 누르면
    public void chattingSendOnClick(View v){
        String[] key = {"MYNICK", "YOURNICK", "MESSAGE"};
        String[] val = {
                StaticManager.nickname,
                oppoNickname.trim(),
                editxtForChat.getText().toString()
        };
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.connect("http://" + StaticManager.ipAddress + "/sendChat.php", "chattingSend", key, val);
        Log.d("ChattingActivity", "메세지 "+editxtForChat.getText().toString()+" 그리고 chattingSend버튼 눌러서 요청함");
        handler.post(new Runnable() { //VIEW 들을 만져줌.
            public void run() {
                editxtForChat.setText(""); //비움.
            }
        });
        refresh();
    }

    //새로고침 버튼 누르면
    public void chattingRefreshOnClick(View v){
        refresh();

    }

    private void refresh(){
        Log.d("ChattingActivity", "refresh call");
        String[] key = {"NICKNAME"};
        String[] val = {
                StaticManager.nickname
        };
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.connect("http://" + StaticManager.ipAddress + "/getMyCheck.php", "chattingRefresh", key, val);
        Log.d("ChattingActivity", "refresh 요청함");
    }



    //서버에서 가져온 값을 알려주는 브로드캐스트 리시버
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // db_login.php로 보낸 결과값을 여기서 받음.
            final String message = intent.getStringExtra("getMyChatRoom");
            if(message!=null){

                StringTokenizer token = new StringTokenizer(message, "*");
                Log.d("Chatting Activity", message + "를 파싱합니다.");
                while (token.hasMoreTokens()) {
                    String opponentId = token.nextToken();
                    String msg = token.nextToken();
                    String who = token.nextToken();
                    if(who.equals("1")) who = "from";
                    else who = "to";

                    mChattingAdapter.add(who+" "+opponentId+": "+msg);


                }//while
            }
            final String message2 = intent.getStringExtra("chattingRefresh");
            if(message2!=null){
                getMyChatCheck=message2;
                String[] key = {"NICKNAME", "HOW", "FILTER"};
                String[] val = {
                        StaticManager.nickname,
                        getMyChatCheck,
                        oppoNickname
                };
                HttpConnection httpConnection = new HttpConnection();
                httpConnection.connect("http://" + StaticManager.ipAddress + "/getMyChatRoom.php", "getMyChatRoom", key, val);
                Log.d("Chatting Activity", getMyChatCheck+"으로 받고 getMyChatRoom 요청함");
            }



            Log.d("Chatting Activity", "local broadcast receiver works");
        }
    };













    public void onResume() {
        super.onResume();
        //이것도 나중에 스태틱으로 바꿔주자. 여기서 특별히 다르게 처리해야 할 것은 없으니까.
        // Register mMessageReceiver to receive messages. 브로드캐스트 리시버 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalBroadcastReceiver, new IntentFilter("localBroadCast"));
    }

    protected void onPause() {
        //이것도 나중에 스태틱으로 바꿔주자. 여기서 특별히 다르게 처리해야 할 것은 없으니까.
        // Unregister since the activity is not visible 브로드캐스트 리비서 해제
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver);
        super.onPause();
    }
}
