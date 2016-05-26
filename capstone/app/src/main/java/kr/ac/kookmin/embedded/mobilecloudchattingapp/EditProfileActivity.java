package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;

import helper.DataSaver;
import helper.HttpConnection;
import helper.StaticManager;

public class EditProfileActivity extends AppCompatActivity {

    EditText nicknameEditTxt, commentEditTxt;
    RadioButton radioManBtn, radioWomanBtn;
    DataSaver dataSaver;
    Intent intent;
    boolean whereicame= false; //false면 로그인 실패로 오는 경우. true면 edit으로 오는 경우

    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바 없애기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_profile);

        intent = getIntent();

        nicknameEditTxt = (EditText)findViewById(R.id.nicknameEditTxt);
        commentEditTxt = (EditText)findViewById(R.id.commentEditTxt);
        radioManBtn = (RadioButton)findViewById(R.id.radioManBtn);
        radioWomanBtn = (RadioButton)findViewById(R.id.radioWomanBtn);

        //로그인 실패로 온 경우
        if(intent.getStringExtra("path").equals("loginFail")){
            StaticManager.checkIfSMHasProfile=false;
            whereicame=false;
        }
        //수정하기 위해 온 경우
        if(intent.getStringExtra("path").equals("editProfile")){
            whereicame=true;
            setViews();
        }

        dataSaver = new DataSaver();

    }

    //뷰들을 StaticManager에서 따와서 세팅함.
    private void setViews() {
        nicknameEditTxt.setText(StaticManager.nickname);
        commentEditTxt.setText(StaticManager.comment);
        if(!StaticManager.sex) radioWomanBtn.setChecked(true);
        else radioManBtn.setChecked(true);
    }

    //닉네임과 sex 여부 체크하고 코멘트는 괜찮음.
    public void saveProfileBtnOnClick(View v){
        if(nicknameEditTxt.getText().toString().matches("")){ //닉네임이 정해져 있지 않으면
            StaticManager.testToastMsg("fill your nickname");
            return;
        }
        StaticManager.nickname=nicknameEditTxt.getText().toString();//닉네임 저장

        if(!(radioManBtn.isChecked() || radioWomanBtn.isChecked())){//둘 다 체크가 안 되어 있다면
            StaticManager.testToastMsg("choose sex");
            return;
        }
        if(radioWomanBtn.isChecked()) {
            StaticManager.sex=false; //여자에 체크면 false
        }
        else {
            StaticManager.sex=true; //남자에 체크면 true. 둘 중 하나가 안 되어 있다면 여기까지 올 수 없음.
        }
        if(commentEditTxt.getText().toString().matches("")){ // 코멘트가 비어있다면 디폴트 값으로 hi!
            StaticManager.comment="hi!";
        }
        else{ //코멘트가 있다면 그걸로 저장
            StaticManager.comment=commentEditTxt.getText().toString();
        }

        StaticManager.checkIfSMHasProfile=true; //저장했으므로 true;
        StaticManager.testToastMsg("You make a profile!");

    }//saveProfileBtnOnClick


    //취소 버튼이 눌린다면
    public void onBackPressed(){
        super.onBackPressed();

        Log.d("EditProfileActivity", "push Back Btn");

        //makeProfile ==true && whereicame로그인 실패로 옴 : 다 만들고 Back 눌러서 나가면
        if(StaticManager.checkIfSMHasProfile && whereicame==false){
            saveProfileIntoServer();//디비를 저장하고
            setResult(RESULT_OK, intent); //OK라고 말해줌.
            Log.d("EditProfileActivity", "login fail result OK");
        }
        //makeProfile ==true && whereicame edit을 하기 위해 옴 : 어찌저찌 채우고 Back 눌러 나가면
        else if(StaticManager.checkIfSMHasProfile && whereicame==true){
            editProfileInServer(); //디비에 수정.
//            setResult(RESULT_OK, intent);
            Log.d("EditProfileActivity", "edit profile result OK");
        }
        //makeProfiel==false : 만들던 중간에 Back 눌러서 나가면
        else if(!StaticManager.checkIfSMHasProfile){
            setResult(RESULT_CANCELED, intent); //취소가 되었다고 말해줌.
            Log.d("EditProfileActivity", "result CANCLE");
        }

    }



    //데이터베이스에 저장. 완전 처음으로 저장하는 거.
    static private void saveProfileIntoServer(){
        Log.d("LoginActivity", "save profile In server call");

        String sex;
        if(StaticManager.sex){ sex="M";}
        else {sex="F";}

        String[] key= {"ID", "PASSWD","NICKNAME", "GENDER", "COMMENT"};
        String[] val= {
                StaticManager.id,
                StaticManager.pw,
                StaticManager.nickname,
                sex,
                StaticManager.comment
        };

        //db_login.php에 로그인 요청을 보냄. 결과는 브로드캐스트 리비서에서 받을 것임.
        Log.d("LoginActivity", val[0] + " " + val[1] + " " + val[2]+" "+val[3]+" "+val[4]+" are sent");
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.connect("http://"+StaticManager.ipAddress+"/createAccount.php", "createAccount.php", key, val);

        Log.d("LoginActivity", "send http msg to db_save.php");
    }

    //데이터베이스에 저장. 내 프로필 바꾸는 거. idpw 값을 기준으로 저장할꺼임.
    static private void editProfileInServer(){
        Log.d("EditProfileActivity", "edit profile In server call");

        String[] key= {"ID", "NICKNAME", "COMMENT"};
        String[] val= {
                StaticManager.uniqeNum,
                StaticManager.nickname,
                StaticManager.comment
        };

        //db_editProfile.php에 업데이트 해달라고 요청함.
        Log.d("EditProfileActivity", val[0] + " " + val[1] + " " + val[2] +" are sent");
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.connect("http://"+StaticManager.ipAddress+"/updateProfile.php", "db_editProfile.php", key, val);

        Log.d("EditProfileActivity", "send http msg to db_editProfile.php");
    }
}