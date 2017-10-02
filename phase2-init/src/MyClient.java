import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) {
        FileClient fileClient = new FileClient();
        GroupClient groupClient = new GroupClient();
        UserToken userToken = null;

        Scanner scanIn = new Scanner(System.in);

        boolean exit = true;
        boolean auth = false;
        int nav = -1;

        do {
            while (!auth) {
                System.out.println("MENU\n 1: LOGIN\n 2: EXIT");
                nav = scanIn.nextInt();
                if (nav == 1) {
                    exit = false;

                    //Connect to group client to auth
                    groupClient.connect();
                } else if (nav == 2) {
                    exit = true;
                    break;
                }
            }

        } while (!exit);

    }
}
