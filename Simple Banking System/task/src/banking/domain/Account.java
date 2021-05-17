package banking.domain;

import java.math.BigDecimal;

public class Account {
    private final String cardNumber;
    private final String pin;
    private BigDecimal balance;
    private final AccountProcessor accountProcessor = new AccountProcessor();

    public Account() {
        this.cardNumber = accountProcessor.generateCardNumber();
        this.pin = accountProcessor.generatePIN();
        this.balance = BigDecimal.ZERO;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
