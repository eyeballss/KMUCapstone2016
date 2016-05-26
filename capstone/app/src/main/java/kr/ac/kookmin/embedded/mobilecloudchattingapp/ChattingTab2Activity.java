package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import helper.StaticManager;

/**
 * Created by kesl on 2016-05-05.
 *
 * 고쳐야 하는 점 : 하드코딩한 아이디 값을 디비에서 가져와서 써야 함.
 *
 *
 */
public class ChattingTab2Activity extends LinearLayout {
    View rootView;

    EditText chatEdit;
    Button chatSendBtn;

    public static Handler handler;

    private String myId;
    private Socket socket;
    public String inputStr, msg;
    private static String opponentID=null;

    //여기서 이 레이아웃이 할 일을 지정함.
    private void work() {
        TextView text = (TextView)findViewById(R.id.section_label2);
        text.setText("222222");

        chatEdit = (EditText)findViewById(R.id.tab2EditTxt);
        chatSendBtn = (Button)findViewById(R.id.tab2SendBtn);

        StartNetwork startNetwork = new StartNetwork();
        Log.d("ChattingTab2Activity", "new StartNetwork");
        startNetwork.start();
        Log.d("ChattingTab2Activity", "StartNetwork start");

        LocalBroadcastManager.getInstance(StaticManager.applicationContext).registerReceiver(mLocalBroadcastReceiver, new IntentFilter("localBroadCast"));




    }//work


    //브로드캐스트 리시버
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            final String message = intent.getStringExtra("dataFromChat");

            //토스트 메세지로 테스트
            StaticManager.testToastMsg(message);

            Log.d("ChattingTab2Activity", "local broadcast receiver works");
        }
    };



    class StartNetwork extends Thread{
        public void run(){
            try {
                socket = new Socket(StaticManager.ipAddress, 5011);
                System.out.println("success to connect");

                myId = "samsung";

                ClientReceiver clientReceiver = new ClientReceiver(socket);
                ClientSender clientSender = new ClientSender(socket);

                clientReceiver.start();
                clientSender.start();
            } catch (IOException e) {
//            System.out.println("!!");
                StaticManager.testToastMsg("error!!!");
            }
        }
    }

    class ClientReceiver extends Thread {
        Socket socket;
        DataInputStream input;
        DataOutputStream output;

        public ClientReceiver(Socket socket) {
            this.socket = socket;
            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            inputStr=null;
            while (input != null) {
                try {
                    inputStr = input.readUTF();

                    StaticManager.sendBroadcast("dataFromChat", inputStr);


//                    System.out.println(inputStr);
                } catch (IOException e) {
                }
            }
        }
    }







    class ClientSender extends Thread {
        Socket socket;
        DataOutputStream output;

        public ClientSender(Socket socket) {
            this.socket = socket;
            try {
                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(myId);
                Log.d("ChattingTab2Activity", "send myId");
            } catch (Exception e) {
            }
        }

        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);
//            final String msg = {""};

            while (output != null) {

                //                    msg = sc.nextLine();

                //send 버튼을 누르면
                chatSendBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("ChattingTab2Activity", "chatSendBtn click");

                        msg ="xaomi*"+chatEdit.getText().toString();

                        if (msg.equals("exit"))
                            System.exit(0);
                        try {
                            Log.d("ChattingTab2Activity", "ready to send");
                            output.writeUTF(msg);
                            Log.d("ChattingTab2Activity", "success to send");
                        } catch (IOException e) {
                        }
                    }
                });





            }
        }
    }


















    public ChattingTab2Activity(Context context) { //생성자
        super(context);
        init(context);
    }

    public ChattingTab2Activity(Context context, AttributeSet attrs) { //생성자
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //첫번째 : xml 파일, 두번째: 가서 붙을 곳, 세번째 : t면 바로 붙고 f면 필요할 때 붙음.
        rootView = inflater.inflate(R.layout.fragment_main_tab2, this, true);

//        work();
//        Log.d("ChattingTab2Activity", "call work method");
    }

    public View getView(){
        return rootView;
    }
}
