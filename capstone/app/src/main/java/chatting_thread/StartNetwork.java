package chatting_thread;

/**
 * Created by kesl on 2016-05-19.
 */
//public     class StartNetwork extends Thread{
//    private Socket socket;
//    private String serverIp;
//
//    public StartNetwork(String ip){
//        serverIp=ip;
//    }
//
//    public void run(){
//        try {
//            socket = new Socket(serverIp, 5011);
//            System.out.println("success to connect");
//
//            myId = "samsung";
//
//            ClientReceiver clientReceiver = new ClientReceiver(socket);
//            ClientSender clientSender = new ClientSender(socket);
//
//            clientReceiver.start();
//            clientSender.start();
//        } catch (IOException e) {
////            System.out.println("!!");
//            StaticManager.testToastMsg("error!!!");
//        }
//    }
//}