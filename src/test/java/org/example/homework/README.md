### Домашняя работа по Занятию 3

Привести тесты из файла AppTest_1 к концепции "One Assertion Per Test".

### Сделано
src/test/java/org/example/homework/TestAppHomework.java

_Тесты:_
* проверка клиента на надежность:
  * testVerificationPersonalDataAllFieldsFilled()
  * testVerificationPersonalDataNotAllFieldsFilled()
* проверка баланса дебетовой карты через месяц (+ проценты на остаток) 
  * testDebitCardBalance() 
* проверка лимита кредитной карты ненадежного клиента
  * testCreditCardUntrustedLimit() 
* проверка баланса после снятия денег с кредитной карты
  * testCreditCardWithdrawMoney() 
* проверка баланса депозитной карты (учитываются проценты за месяц)
  * testDepositCardBalance()
* проверка перевода средств между двумя дебитовыми картами
  * testTransferDebitCardBalance() 
* проверка отмены транзакции перевода средств между двумя картами
  * testCancelTransaction() 
* проверка пополнения карты
  * testTopUpCard()
* проверка баланса карты после снятия средств
  * testWithdrawMoney() 