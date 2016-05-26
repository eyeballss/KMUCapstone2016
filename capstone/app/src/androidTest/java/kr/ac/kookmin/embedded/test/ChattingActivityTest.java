package kr.ac.kookmin.embedded.test;

/**
 * Created by kesl on 2016-05-17.
 */

//ActivityInstrumentationTestCase2<T>를 상속하고 있습니다.이는 안드로이드 액티비티를 테스트하기 위한 클래스로,<T>에는 테스트 대상 액티비티를 넣어줍니다.
//public class ChattingActivityTest extends ActivityInstrumentationTestCase2<ChattingActivity> {
//
//    //생성자에서는 부모 생성자에 테스트 대상 액티비티를 넘겨 주어 테스트할 액티비티를 지정합니다.
//    public ChattingActivityTest(){
//        super(ChattingActivity.class);
//    }
//
//    //getActivity() 를 호출하여 테스트 대상 액티비티(MainActivity)를 호출하고,
//    // 액티비티 내에 문자열이 표시되는 TextView의 인스턴스를 받은 후,
//    // 여기에 표시되어야 하는 문자열(R.string.hello_world)이 제대로 표시되었는 지 확인하고 있습니다.
//    public void testString(){
//        Activity activity = getActivity();
//        TextView testView = (TextView)activity.findViewById(R.id.testTxtView);
//        assertEquals(activity.getText(R.string.test), testView.getText().toString());
//    }
//}
