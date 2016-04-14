package kr.ac.kookmin.embedded.httptest;

import android.app.TabActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends TabActivity {

    // 전역변수 선언
    TabHost mTabHost = null;
    String myId, myPWord, myNick, mySubject, myResult;
    BackgroundTask asykTsk;
    Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tab 만들기
        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator("서버로 전송").setContent(R.id.page01));
        mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator("서버에서 받음").setContent(R.id.page02));

        findViewById(R.id.button_submit).setOnClickListener(buttonClick);
    }

    //------------------------------
    //    button Click
    //------------------------------
    Button.OnClickListener buttonClick = new Button.OnClickListener() {
        public void onClick(View v) {

            // 사용자가 입력한 내용을 전역변수에 저장한다
            myId = ((EditText)(findViewById(R.id.edit_id))).getText().toString();
            myPWord = ((EditText)(findViewById(R.id.edit_pword))).getText().toString();
            myNick = ((EditText)(findViewById(R.id.edit_nick))).getText().toString();
            mySubject = ((EditText)(findViewById(R.id.edit_subject))).getText().toString();

            handler = new Handler();//UI를 위한 핸들러
            asykTsk= new BackgroundTask();
            asykTsk.execute();

//            PostData();
        }
    };

    //------------------------------
    //    웹서버로 데이터 전송
    //------------------------------
    public void PostData() {
        // Server URL (필자의 개인 서버 주소임)
        String Server_URL = "http://ec2-52-79-168-35.ap-northeast-2.compute.amazonaws.com/test.php";

        // 전송할 데이터를 저장할 ArrayList 생성
        ArrayList<HttpQue> sBuffer = new ArrayList<HttpQue>();

        // ArrayList에 <변수=값> 형태로 저장
        sBuffer.add(new HttpQue("", Server_URL));                 // 서버 URL
        sBuffer.add(new HttpQue("user_id", myId));   // "" 안의 문자열은
        sBuffer.add(new HttpQue("user_pword", myPWord)); // 서버에 설정된 변수명이다
        sBuffer.add(new HttpQue("user_nick", myNick));
        sBuffer.add(new HttpQue("user_subject", mySubject));

        // HttpPost 생성
        HttpPost mHttp = new HttpPost(sBuffer);

        // Data 전송
        mHttp.HttpPostData();
        myResult = mHttp.rString; // 전송 결과

        // Tab2에 있는 TextEdit에 전송 결과 표시
        handler.post(new Runnable() {
            public void run() {
                ((TextView) (findViewById(R.id.text_result))).setText(myResult);
            }
        });
    } // PostData




    class BackgroundTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            PostData();

            return null;
        }
    }



} // Activity






















//--------------------------
//  HttpQue
//--------------------------
class HttpQue {
    public String var;   // 변수명
    public String value;  // 값

    public HttpQue(String _var, String _value) { // 생성자
        var = _var;
        value = _value;
    }
}

//--------------------------
//HttpPost
//--------------------------
class HttpPost {
    public  String    rString;  // receive String
    public  StringBuilder   rBuffer;
    private ArrayList<HttpQue>  sBuffer;  // sendBuffer

    //--------------------------
    // Constructor
    //--------------------------
    public HttpPost(ArrayList<HttpQue> _sBuffer) {
        sBuffer = _sBuffer;
        rBuffer = new StringBuilder(200000);  // receive 버퍼
        rString = "";      // receive 스트링
    }

    //--------------------------
    //   URL 설정하고 접속하기
    //--------------------------
    public void HttpPostData() {
        try {
            URL url = new URL(sBuffer.get(0).value);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속

            //--------------------------
            // 전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setConnectTimeout(5000);  // 5초
            http.setReadTimeout(10000) ;  // 10초
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                        // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");    // 전송 방식은 POST

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            //--------------------------
            // 서버로 값 전송
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            for (int i = 1; i < sBuffer.size(); i++) {
                buffer.append(sBuffer.get(i).var).append("=");
                buffer.append(sBuffer.get(i).value).append("&");
            }

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            // OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");

            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            // Log.v("Http post", buffer.toString());

            //--------------------------
            // 서버에서 전송받기
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            // InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);

            String str;
            while ((str = reader.readLine()) != null) {  // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                rBuffer.append(str + "\n");
            }
            rString = rBuffer.toString().trim();        // 전송결과를 문자열로
            // Log.v("Receive String", rString);

        } catch (MalformedURLException e) {
            Log.v("MalformedURLEx error","------------");
            rString = "N/A";
        } catch (IOException e) {
            Log.v("IOException", rString + "--------------");
            rString = "N/A";
        } // try
    } // HttpPostData
} // class