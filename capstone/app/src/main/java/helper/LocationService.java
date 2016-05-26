package helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Created by kesl on 2016-05-05.
 */

//locationService.startLocationService(); //위치 정보 받아옵니다! 그래서 토스트로 출력!
public class LocationService {

    private LocationManager manager;
    private GPSListener gpsListener;

    public LocationService(){ //생성자
        checkDangerousPermissions();
    }

    //GPS 서비스 실행!
    public void startLocationService() {
        manager = StaticManager.locationManager;

        gpsListener = new GPSListener();
        long minTime = 1000; //이 시간(1000ms= 1초)이 지나면 GPS를 업데이트 해주세요.
        float minDistance = 0; //내가 이만큼(0이면 항상) 움직이면 업데이트 해주세요.

        try {
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            android.location.Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {

                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                Log.i("GPSListener", "최근 위도 경도 : " + latitude + " " + longitude);

//                StaticManager.testToastMsg("최근 위도 경도 : "+latitude+" "+longitude);
                StaticManager.sendBroadcast("gps data", latitude + " " + longitude);
            }
        } catch(SecurityException ex) {
            ex.printStackTrace();
        }

        Log.d("GPS Location", "startLocationService() success");

    }//startLocationService


//    //GPS 현재 사용할 수 있는지 없는지 체크해주는 메소드였으나 manager.isProviderEnabled(LocationManager.GPS_PROVIDER)에서 NullPointException 오류로 구현 보류.
//    public void enableGPS(){
//        boolean test = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        StaticManager.testToastMsg(test+"");
////            if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //만약 사용중이지 않다면
////                buildAlertMessageNoGps(); //buildAlertMessageNoGps 메소드 실행.
////            }
//    }
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(StaticManager.applicationContext);
//        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        StaticManager.applicationContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        dialog.cancel();
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }
//    참고 : http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled









    //sdk 23부터 바뀐 권한 문제를 해결하는 메소드.
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(StaticManager.applicationContext, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d("GPS Location", "permissionCheck == PackageManager.PERMISSION_GRANTED");
        } else {
            Log.d("GPS Location", "permissionCheck != PackageManager.PERMISSION_GRANTED");

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) StaticManager.applicationContext, permissions[0])) {
                Log.d("GPS Location", "ActivityCompat.shouldShowRequestPermissionRationale((Activity) StaticManager.applicationContext, permissions[0])");
            } else {
                ActivityCompat.requestPermissions((Activity) StaticManager.applicationContext, permissions, 1);
            }
        }
    }//checkDangerousPermissions
}//LocationService

//매니저에게 리퀘스트를 보내기 위해 필요한 리스너 객체의 클래스
class GPSListener implements LocationListener {

    //여기서 실시간 위도 경도를 받습니당.
    public void onLocationChanged(android.location.Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        Log.i("GPS Location", "위도 경도 : " + latitude + " " + longitude);

//        StaticManager.testToastMsg("위도 경도 : "+latitude+" "+longitude);

        StaticManager.sendBroadcast("gps data", latitude + " " + longitude);
    }


    public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}//GPSListener
