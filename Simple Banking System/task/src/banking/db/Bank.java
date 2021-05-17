package banking.db;

import banking.Application;
import banking.domain.Account;
import banking.domain.AccountDto;
import banking.domain.AccountProcessor;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Bank {

    private static final Bank INSTANCE = new Bank();
    private final Map<String, Account> accountRepository = new HashMap<>();

    private Bank() {
    }

    public static Bank getInstance() {
        return INSTANCE;
    }

    public Account createAccountInMemory() {
        Account account = new Account();
        accountRepository.put(account.getCardNumber(), account);
        return account;
    }

    public Account logIntoAccountFromMemory(String cardNumber) {
        return accountRepository.get(cardNumber);
    }

    public AccountDto logIntoAccountFromDB(String cardNumber) {
        return getAccountFromDB(cardNumber);
    }

    public AccountDto getAccountFromDB(String cardNumber) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(Application.dbURL);

        AccountDto accountDto = null;
        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                        "SELECT * FROM " + Application.tableName +
                            " WHERE number = " + cardNumber)) {
                if (resultSet.next()) {
                    accountDto = new AccountDto(resultSet.getString("number"),
                                                resultSet.getString("pin"),
                                                resultSet.getBigDecimal("balance"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountDto;
    }

    public boolean authenticateFromMemory(String cardNumber, String pin) {
        if (accountRepository.containsKey(cardNumber)) {
            Account account = accountRepository.get(cardNumber);
            return account.getPin().equals(pin);
        }
        else
            return false;
    }

    public boolean authenticateFromDB(String cardNumber, String pin) {
        AccountDto accountFromDB = getAccountFromDB(cardNumber);
        if (accountFromDB != null) {
            return accountFromDB.getPin().equals(pin);
        }
        else {
            return false;
        }
    }

    public Account createAccountInDB() {
        Account account = new Account();

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(Application.dbURL);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate(
                        "INSERT INTO " + Application.tableName + " (number, pin) " +
                            " VALUES ('" + account.getCardNumber() + "', '" + account.getPin() + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    public boolean checkCardWithLuhnAlgo(String cardNumber) {
        AccountProcessor accountProcessor = new AccountProcessor();
        return accountProcessor.checkCardWithLuhnAlgo(cardNumber);
    }

}
