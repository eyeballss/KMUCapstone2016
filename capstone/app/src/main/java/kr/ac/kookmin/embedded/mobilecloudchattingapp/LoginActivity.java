package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.util.StringTokenizer;

import helper.DataSaver;
import helper.HttpConnection;
import helper.StaticManager;

/**
 * Don't forget:
 *  - makeing Log at method
 *  - TDD
 *  - makeing flow chart
 *  - naming rule
 *  - access modifier
 *  - exception
 *  - comments (//)
 *
 */


/**
 * Main screen that client sees first
 * A login screen that offers login.
 *
 */
public class LoginActivity extends AppCompatActivity {


    EditText idEditTxt, pwEditTxt;
    Button loginBtn;
    private static int idpwHashCode;
    static HttpConnection httpConnection ;

    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바 없애기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);


        StaticManager.applicationContext=getApplicationContext(); //어플리케이션 콘텍스트 넘김.
        StaticManager.locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);//위치매니저 넘김.

        idEditTxt=(EditText)findViewById(R.id.idEditTxt);
        pwEditTxt=(EditText)findViewById(R.id.pwEditTxt);
        loginBtn=(Button)findViewById(R.id.loginBtn);
        httpConnection=new HttpConnection(); //http 컨넥터 만들기

    }//onCreate


    //로그인 버튼 클릭하면
    public void loginBtnOnClick(View v){


        String idTxt = idEditTxt.getText().toString().trim();
        String pwTxt = pwEditTxt.getText().toString().trim();

        if(idTxt.length()==0) {
            StaticManager.testToastMsg("ID is empty!");
            return;
        }
        if(pwTxt.length()==0) {
            StaticManager.testToastMsg("PW is empty!");
            return;
        }
        //연결을 시도함.
        //아이디+비밀번호 문자열을 해쉬코드로 넘김.
        idpwHashCode = (idTxt+""+pwTxt).hashCode();
        //SM에 저장함.
        StaticManager.id = idTxt;
        StaticManager.pw = pwTxt;

        //key-value를 String[]으로 만듦.
        String[] key= {"ID", "PASSWD"};
        String[] val= {
                idTxt,
                pwTxt
        };
        //db_login.php에 로그인 요청을 보냄. 결과는 브로드캐스트 리비서에서 받을 것임.
        httpConnection.connect("http://"+StaticManager.ipAddress+"/loginPage.php", "loginPage.php",key, val);

    }//loginBtnOnClick






    //서버에서 가져온 값을 알려주는 브로드캐스트 리시버
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // db_login.php로 보낸 결과값을 여기서 받음.
            final String message = intent.getStringExtra("loginPage.php");
            Log.d("LoginActivity", "broadcst receiver call");

            Intent in;
            if(message.substring(0, 9).equals("Not match")) { //로그인에 실패하면 바로 가입을 위해 EidtProfileActivity로 이동
                in = new Intent(LoginActivity.this, EditProfileActivity.class);

                in.putExtra("path", "loginFail");
                startActivityForResult(in, 1);
                Log.d("LoginActivity", "login fail");


            }
            else{ //로그인에 성공하면 MainActivity로 이동.
                in = new Intent(LoginActivity.this, MainActivity.class);
                saveProfileToStaticManager(message); //로그인 성공이므로 profile 데이터를 핸드폰에 저장함.
                startActivity(in);
                finish(); //로그인 하고 나면 로그인창은 닫습니다.
            }



            Log.d("LoginActivity", "local broadcast receiver works");
        }
    };


    //profile 만든 후에 제대로 저장하면 OK 아니면 FALSE
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("LoginActivity", "onActivityResult call");
        //저장이 되어있다면, 즉 프로필을 제대로 생성했다면
        if(StaticManager.checkIfSMHasProfile){
            Log.d("LoginActivity", "result OK!");
            Intent in = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(in);
            finish(); //로그인 하고 나면 로그인창은 닫습니다.

        }

        //저장이 안되어있다면, 즉 프로필을 제대로 생성하지 않았다면
        else{
            //do nothing.
            Log.d("LoginActivity", "result CANCELED");
        }

        Log.d("LoginActivity", "onActivityResult end");


    }



    //msg를 파싱해서 원하는 것만
    static private void saveProfileToStaticManager(String msg){
        DataSaver dataSaver = new DataSaver();

        StringTokenizer token = new StringTokenizer(msg, "*");

        //StaticManager에 저장하여 다른 곳에서도 이용할 수 있도록 함.
        StaticManager.uniqeNum=token.nextToken();
        StaticManager.nickname=token.nextToken();
        if(token.nextToken().equals("F")) StaticManager.sex=false;
        else StaticManager.sex=true;
        StaticManager.comment=token.nextToken();
        StaticManager.checkIfSMHasProfile=true; //저장했으므로 true;

    }




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