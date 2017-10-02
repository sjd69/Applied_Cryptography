import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) {
        FileClient fileClient = new FileClient();
        GroupClient groupClient = new GroupClient();
        UserToken userToken = null;
        String username = null;

        MyFileClient myFileClient = new MyFileClient();
        MyGroupClient myGroupClient = new MyGroupClient();

        Scanner scanIn = new Scanner(System.in);

        boolean exit = true;
        boolean auth = false;
        int nav = -1;

        do {
            while (!auth) {
                System.out.println("MENU\n " +
                        "1: LOGIN\n " +
                        "2: EXIT");
                nav = scanIn.nextInt();
                if (nav == 1) {
                    exit = false;

                    System.out.println("Enter username");
                    username = scanIn.nextLine();

                    //Connect to group client to auth
                    //Not entirely sure what to use for port
                    groupClient.connect("localhost", 8765 );

                    if (groupClient.isConnected()) {
                        userToken = groupClient.getToken(username);

                        if (userToken == null) {
                            System.out.println("Authentication error.");
                        } else {
                            auth = true;
                            System.out.println("Authentication successful.");
                        }
                        groupClient.disconnect();

                    } else {
                        System.out.println("Connection error.");
                        exit = true;
                        break;
                    }
                } else if (nav == 2) {
                    exit = true;
                    break;
                }
            }

            while (auth) {
                System.out.println("MENU:\n" +
                        "1: Connect to File Server\n" +
                        "2: Connect to Group Server\n" +
                        "3: Exit\n");


                switch(nav) {
                    case 1:
                        myFileClient.startMyFileClient("localhost", 8765, (Token)userToken);
                        break;

                    case 2:
                        myGroupClient.startMyGroupClient("localhost", 8765, (Token)userToken);
                        break;

                    case 3:
                        System.out.println("Exiting...");
                        exit = true;
                        break;

                    default:
                        System.out.println("Invalid input.");
                        break;
                }

                if (exit) {
                    break;
                }
            }

        } while (!exit);

    }
}
