package banking.domain;

import java.math.BigDecimal;

public class AccountDto {
    private String cardNumber;
    private String pin;
    private BigDecimal balance;

    public AccountDto(String cardNumber, String pin, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
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

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
