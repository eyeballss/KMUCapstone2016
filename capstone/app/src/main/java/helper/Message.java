package helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by kesl on 2016-04-22.
 */
public class Message {

    public static void okayMsgShow(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(StaticManager.applicationContext);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//닫기
            }
        });
        alert.setMessage(msg);
        alert.show();
    }

    //메세지, 브로드캐스트 할 key(intent에서 받을 key), yes msg, no msg, 해당 context(대개는 SM에 있는걸 사용)
    public static void yesNoMsgShow(String msg, final String key, final String yesVal, final String noVal, Context context){

        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
        alert_confirm.setMessage(msg);
        alert_confirm.setCancelable(false);
        alert_confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 'YES'누르면 yesKey, yesVal로 로컬브로드캐스트 함.
                StaticManager.sendBroadcast(key, yesVal);
            }
        });
        alert_confirm.setNegativeButton("Cancle",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No' 누르면 noKey, noVal로 로컬브로드캐스트 함.
                        StaticManager.sendBroadcast(key, noVal);
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }


}
