package chatting_thread;

/**
 * Created by kesl on 2016-05-19.
 */
//public class ClientReceiver extends Thread {
//    Socket socket;
//    DataInputStream input;
//    DataOutputStream output;
//
//    public ClientReceiver(Socket socket) {
//        this.socket = socket;
//        try {
//            input = new DataInputStream(socket.getInputStream());
//            output = new DataOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//        }
//    }
//
//    @Override
//    public void run() {
//        inputStr=null;
//        while (input != null) {
//            try {
//                inputStr = input.readUTF();
//
//                StaticManager.sendBroadcast("dataFromChat", inputStr);
//
//
////                    System.out.println(inputStr);
//            } catch (IOException e) {
//            }
//        }
//    }
//}