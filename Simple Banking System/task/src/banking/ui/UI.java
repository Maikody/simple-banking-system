package banking.ui;

import banking.domain.AccountDto;
import banking.service.Service;

import java.util.Scanner;

public class UI {

    private final Scanner scanner = new Scanner(System.in);
    private final Service service = new Service();

    private static final UI INSTANCE = new UI();

    private UI() {
    }

    public static UI getInstance() {
        return INSTANCE;
    }

    public void start() {
        printMainMenu();
        getUserInputMainMenu();
    }

    private void getUserInputMainMenu() {
        int userChoice = scanner.nextInt();

        switch (userChoice) {
            case 1:
                service.createAccountInDB();
                break;
            case 2:
                AccountDto accountDto = service.logIntoAccount();
                if (accountDto != null) {
                    while (getUserInputLoggedMenu(accountDto));
                    break;
                }
                break;
            default:
                shutdown();
        }
    }

    private boolean getUserInputLoggedMenu(AccountDto accountDto) {
        printLoggedMenu();

        int userChoice = scanner.nextInt();

        switch (userChoice) {
            case 1:
                service.printAccountBalance(accountDto);
                return true;
            case 2:
                service.addIncome(accountDto);
                return true;
            case 3:
                service.doTransfer(accountDto);
                return true;
            case 4:
                service.closeAccount(accountDto);
                return false;
            case 5:
                service.logOut();
                return false;
            default:
                shutdown();
                return false;
        }
    }

    void printMainMenu() {
        System.out.println("\n1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    void printLoggedMenu() {
        System.out.println("\n1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    private void shutdown() {
        System.out.println("\nBye!");
        System.exit(0);
    }

}
