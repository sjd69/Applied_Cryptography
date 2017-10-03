import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) {
        //FileClient fileClient = new FileClient();
        GroupClient groupClient = new GroupClient();
        GroupServer groupServer = new GroupServer();
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
                do {
                    System.out.println("MENU\n " +
                            "1: LOGIN\n " +
                            "2: EXIT");

                    while (!scanIn.hasNextInt()) {
                        System.out.println("MENU\n " +
                                "1: LOGIN\n " +
                                "2: EXIT");
                        scanIn.next();
                    }
                    nav = scanIn.nextInt();
                } while ((nav != 1 && nav != 2));

                if (nav == 1) {
                    exit = false;

                    System.out.println("Enter username");
                    scanIn.nextLine();
                    username = scanIn.nextLine();

                    //Connect to group client to auth
                    // Used this for the port. I think this should work. 
                    // It's 8765 just in case this is not right.
                    try {
                        groupClient.connect("localhost", GroupServer.SERVER_PORT);
                        userToken = groupClient.getToken(username);

                        if (userToken == null) {
                            System.out.println("Authentication error.");
                        } else {
                            auth = true;
                            System.out.println("Authentication successful.");
                        }
                        groupClient.disconnect();
                    } catch (Exception e) {
                        System.out.println("Connection error.");
                        exit = true;
                        break;
                    }

                } else {
                    exit = true;
                    break;
                }
            }

            while (auth) {
                do {
                    System.out.println("MENU:\n" +
                            "1: Connect to File Server\n" +
                            "2: Connect to Group Server\n" +
                            "3: Logout\n" +
                            "4: Exit\n");

                    while (!scanIn.hasNextInt()) {
                        System.out.println("MENU:\n" +
                                "1: Connect to File Server\n" +
                                "2: Connect to Group Server\n" +
                                "3: Logout\n" +
                                "4: Exit\n");
                        scanIn.next();
                    }
                    nav = scanIn.nextInt();
                } while (!(nav > 0 && nav < 5));

                switch(nav) {
                    case 1:
                        System.out.println("Enter IP Address of File Server.");
                        String fs_ip = scanIn.nextLine();

                        System.out.println("Enter the port number for the File Server");
                        int fs_port = scanIn.nextInt();
                        myFileClient.startMyFileClient(fs_ip, fs_port, (Token)userToken);
                        break;

                    case 2:
                        System.out.println("Enter IP Address of File Server.");
                        String gc_ip = scanIn.nextLine();

                        System.out.println("Enter the port number for the File Server");
                        int gc_port = scanIn.nextInt();
                        myGroupClient.startMyGroupClient(gc_ip, gc_port);
                        break;

                    case 3:
                        System.out.println("Logging out");
                        auth = false;
                        break;

                    case 4:
                        System.out.println("Exiting");
                        auth = false;
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
