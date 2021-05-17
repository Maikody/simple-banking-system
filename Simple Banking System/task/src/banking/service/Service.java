package banking.service;

import banking.Application;
import banking.domain.Account;
import banking.db.Bank;
import banking.domain.AccountDto;
import org.sqlite.SQLiteDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Scanner;

public class Service {

    private final Scanner scanner = new Scanner(System.in);
    private final Bank bank = Bank.getInstance();

    public void createAccountInMemory() {
        Account account = bank.createAccountInMemory();
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:\n" + account.getCardNumber());
        System.out.println("Your card PIN:\n" + account.getPin());
    }

    public void createAccountInDB() {
        Account account = bank.createAccountInDB();
        System.out.println("\nYour card has been created");
        System.out.println("Your card number:\n" + account.getCardNumber());
        System.out.println("Your card PIN:\n" + account.getPin());
    }

    public AccountDto logIntoAccount() {
        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        if (!bank.checkCardWithLuhnAlgo(cardNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return null;
        }

        if (bank.authenticateFromDB(cardNumber, pin)) {
            System.out.println("\nYou have successfully logged in!");
            return bank.logIntoAccountFromDB(cardNumber);
        } else {
            System.out.println("\nWrong card number or PIN!");
            return null;
        }
    }

    public void printAccountBalance(AccountDto accountDto) {
        System.out.println("\nBalance: " + accountDto.getBalance());
    }

    public void logOut() {
        System.out.println("\nYou have successfully logged out!");
    }

    public void addIncome(AccountDto accountDto) {
        System.out.println("Enter income:");
        BigDecimal income = scanner.nextBigDecimal();
        scanner.nextLine();

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(Application.dbURL);

        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()) {
                accountDto.setBalance(accountDto.getBalance().add(income));
                statement.executeUpdate(
                        "UPDATE card " +
                            " SET balance = " + accountDto.getBalance() +
                            " WHERE number = " + accountDto.getCardNumber());
                System.out.println("Income was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doTransfer(AccountDto senderAccountDto) {
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        String receiverCardNumber = scanner.nextLine();

        if (receiverCardNumber.equals(senderAccountDto.getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        if (!bank.checkCardWithLuhnAlgo(receiverCardNumber)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }

        AccountDto receiverAccountDto = bank.getAccountFromDB(receiverCardNumber);
        if (receiverAccountDto == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        BigDecimal sumToTransfer = scanner.nextBigDecimal();
        scanner.nextLine();

        if (sumToTransfer.subtract(senderAccountDto.getBalance()).compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Not enough money!");
            return;
        }

        senderAccountDto.setBalance(senderAccountDto.getBalance().subtract(sumToTransfer));
        receiverAccountDto.setBalance(receiverAccountDto.getBalance().add(sumToTransfer));

        transferMoney(senderAccountDto, receiverAccountDto);
    }


    private void transferMoney(AccountDto sender, AccountDto receiver) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(Application.dbURL);

        try (Connection con = dataSource.getConnection()) {
            Savepoint savepoint = con.setSavepoint();
            try (Statement statement = con.createStatement()) {
                if (con.getAutoCommit()) {
                    con.setAutoCommit(false);
                }
                statement.executeUpdate(
                        "UPDATE card " +
                                " SET balance = " + sender.getBalance() +
                                " WHERE number = " + sender.getCardNumber());
                statement.executeUpdate(
                        "UPDATE card " +
                                " SET balance = " + receiver.getBalance() +
                                " WHERE number = " + receiver.getCardNumber());

                con.commit();
                System.out.println("Success!");
            } catch (SQLException e) {
                con.rollback(savepoint);
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void closeAccount(AccountDto accountDto) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(Application.dbURL);

        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()) {
                statement.executeUpdate(
                        "DELETE FROM card " +
                            " WHERE number = " + accountDto.getCardNumber());
                System.out.println("The account has been closed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
