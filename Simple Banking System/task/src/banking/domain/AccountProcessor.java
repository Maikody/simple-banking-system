package banking.domain;

import java.util.Random;

public class AccountProcessor {
    private static final Random generator = new Random();

    String generateCardNumber() {
        StringBuilder sb = new StringBuilder("400000");
        for (int i = 0; i < 9; i++) {
            sb.append(generator.nextInt(10));
        }

        sb.append(getCheckSum(sb.toString()));

        return sb.toString();
    }

    int getCheckSum(String cardNum) {
        String[] cardNums = cardNum.split("");
        int[] array = new int[cardNums.length];
        for (int i = 0; i < cardNums.length; i++ ) {
            array[i] = Integer.parseInt(cardNums[i]);
        }

        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            if (i % 2 == 0) {
                array[i] *= 2;
            }
            if (array[i] > 9) {
                array[i] -= 9;
            }
            sum += array[i];
        }

        int checkSum;
        if (sum % 10 == 0) {
            checkSum = 0;
        } else {
            checkSum = 10 - sum % 10;
        }

        return checkSum;
    }

    public boolean checkCardWithLuhnAlgo(String cardNumber) {
        String[] cardNumbers = cardNumber.split("");
        int givenCheckSum = Integer.parseInt(cardNumbers[cardNumbers.length - 1]);

        String cardNumberWithoutLastDigit = cardNumber.substring(0, cardNumber.length() - 1);
        int calculatedCheckSum = getCheckSum(cardNumberWithoutLastDigit);

        return givenCheckSum == calculatedCheckSum;
    }

    String generatePIN() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(generator.nextInt(10));
        }
        return sb.toString();
    }
}
