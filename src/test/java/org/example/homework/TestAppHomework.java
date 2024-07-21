package org.example.homework;

import org.example.entities.Bank;
import org.example.entities.User;
import org.example.entities.UserBuilder;
import org.example.exception.CentralBankException;
import org.example.service.CentralBank;
import org.example.service.TimeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAppHomework {
    private User userWithAllPersonalFields;
    private User userWithNotAllPersonalFields;
    private User userSasha;
    private User userIvan;

    private CentralBank centralBank;
    private Bank sber;

    private TimeManager timeManager;
    private LocalDateTime dateFirst;

    @BeforeEach
    public void setUp() throws Exception {        System.out.println("BeforeEach");
        userWithAllPersonalFields = new UserBuilder("Anna", "Smith", 100.0).withAddress("Street 1").withPassportId(123123).build();
        userWithNotAllPersonalFields = new UserBuilder("Peter", "Johnson", 999.99).build();

        userSasha = new UserBuilder("Sasha", "Ivanov", 100000).withAddress("Green Street").withPassportId(124).build();
        userIvan = new UserBuilder("Ivan", "Petrov", 10000).withAddress("Green Street").withPassportId(123).build();

        centralBank = new CentralBank();
        sber = new Bank("SberBank", 1, 2, 5, 5000, 10000, 2, -1000000, 1000, 999999999);
        centralBank.addBank(sber);

        timeManager = new TimeManager(LocalDateTime.of(2022, 9, 1, 0, 0, 0));
        dateFirst = LocalDateTime.of(2022, 9, 1, 0, 0, 0);
    }

    @Test
    void testVerificationPersonalDataAllFieldsFilled() {
        assertTrue(userWithAllPersonalFields.verificationPersonalData());
    }

    @Test
    void testVerificationPersonalDataNotAllFieldsFilled() {
        assertFalse(userWithNotAllPersonalFields.verificationPersonalData());
    }

    @Test
    public void testAddNullBank() {
        CentralBankException exception = assertThrows(CentralBankException.class, () -> centralBank.addBank(null));
        assertEquals("Unable to add bank due to null object", exception.getMessage());
    }

    @Test
    public void testDebitCardBalance() throws Exception {
        timeManager.addObserver(sber);
        sber.addUser(userSasha);
        sber.addDebitCard(dateFirst, 50000, userSasha.getUserId());
        timeManager.addMonth();

        assertEquals(80000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testCreditCardUntrustedLimit() throws Exception {
        sber.addUser(userSasha);
        sber.addCreditCard(dateFirst, 100, userSasha.getUserId());

        assertEquals(0, sber.getListCreditCards().get(0).getUntrustedUserLimit(), 0.001);
    }

    @Test
    public void testCreditCardWithdrawMoney() throws Exception {
        timeManager.addObserver(sber);
        sber.addUser(userSasha);
        sber.addCreditCard(dateFirst, 100, userSasha.getUserId());
        sber.getListCreditCards().get(0).withdrawMoney(2000);

        assertEquals(-1900, sber.getListCreditCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testDepositCardBalance() throws Exception {
        timeManager.addObserver(sber);
        sber.addUser(userSasha);
        sber.addDepositCard(dateFirst, LocalDateTime.of(2022, 9, 2, 0, 0, 0), 15000, userSasha.getUserId());
        timeManager.addMonth();

        assertEquals(37500, sber.getListDepositCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testTransferDebitCardBalance() throws Exception {
        timeManager.addObserver(sber);
        sber.addUser(userSasha);
        sber.addUser(userIvan);
        sber.addDebitCard(dateFirst, 50000, userSasha.getUserId());
        sber.addDebitCard(dateFirst, 50000, userIvan.getUserId());

        centralBank.transferMoney(25000, sber.getListDebitCards().get(0).getCardId(), sber.getListDebitCards().get(1).getCardId());

        assertAll(
                "Transfer money. Initial card balance: card0 = 50000, card1 = 50000, transfer = 25000",
                () -> assertEquals(25000, sber.getListDebitCards().get(0).getBalance(), 0.001),
                () -> assertEquals(75000, sber.getListDebitCards().get(1).getBalance(), 0.001)
        );
    }

    @Test
    public void testCancelTransaction() throws Exception {
        timeManager.addObserver(sber);
        sber.addUser(userSasha);
        sber.addUser(userIvan);
        sber.addDebitCard(dateFirst, 40000, userSasha.getUserId());
        sber.addDebitCard(dateFirst, 50000, userIvan.getUserId());

        centralBank.transferMoney(25000, sber.getListDebitCards().get(0).getCardId(), sber.getListDebitCards().get(1).getCardId());

        centralBank.transactionCancellation(sber.getListDebitCards().get(0).getCardId(), 0);

        assertAll(
                "Canceling the transaction. Initial card balance: card0 = 40000, card1 = 50000",
                () -> assertEquals(40000, sber.getListDebitCards().get(0).getBalance(), 0.001),
                () -> assertEquals(50000, sber.getListDebitCards().get(1).getBalance(), 0.001)
        );
    }

    @Test
    public void testTopUpCard() throws Exception {
        sber.addUser(userSasha);
        sber.addDebitCard(dateFirst, 50000, userSasha.getUserId());

        sber.getListDebitCards().get(0).topUpCard(10000);

        assertEquals(60000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testWithdrawMoney() throws Exception {
        sber.addUser(userSasha);
        sber.addDebitCard(dateFirst, 50000, userSasha.getUserId());

        sber.getListDebitCards().get(0).withdrawMoney(5000);

        assertEquals(45000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }
}