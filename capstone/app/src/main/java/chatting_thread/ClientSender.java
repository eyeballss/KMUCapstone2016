package chatting_thread;

/**
 * Created by kesl on 2016-05-19.
 */
//public class ClientSender extends Thread {
//    Socket socket;
//    DataOutputStream output;
//
//    public ClientSender(Socket socket) {
//        this.socket = socket;
//        try {
//            output = new DataOutputStream(socket.getOutputStream());
//            output.writeUTF(myId);
//            Log.d("ChattingTab2Activity", "send myId");
//        } catch (Exception e) {
//        }
//    }
//
//    @Override
//    public void run() {
//        Scanner sc = new Scanner(System.in);
////            final String msg = {""};
//
//        while (output != null) {
//
//            //                    msg = sc.nextLine();
//
//            //send 버튼을 누르면
//            chatSendBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Log.d("ChattingTab2Activity", "chatSendBtn click");
//
//                    msg ="xaomi*"+chatEdit.getText().toString();
//
//                    if (msg.equals("exit"))
//                        System.exit(0);
//                    try {
//                        Log.d("ChattingTab2Activity", "ready to send");
//                        output.writeUTF(msg);
//                        Log.d("ChattingTab2Activity", "success to send");
//                    } catch (IOException e) {
//                    }
//                }
//            });
//
//
//
//
//
//        }
//    }
//}