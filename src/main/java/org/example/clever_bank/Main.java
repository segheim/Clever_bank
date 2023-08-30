package org.example.clever_bank;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.BankAccount;

import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) {

//        System.out.println(ConfigurationManager.getProperty("db.url"));

        ConnectionPool.lockingPool().init();
        System.out.println(BankAccountDaoImpl.getInstance().update(BankAccount.builder()
                        .id(16L)
                        .account(Account.builder().id(5L).build())
                        .balance(BigDecimal.ONE)

                .build()));
//        List<BankAccount> bankAccounts = BankAccountDaoImpl.getInstance().readAll();
//        bankAccounts.stream().forEach(System.out::println);

//
//        Menu menu = new Menu();
//        menu.getMenu();
//        System.out.println(BankActivityMenu.getInstance().replenishmentAccount(1L));
//
//        System.out.println(BankAccountServiceImpl.getInstance().transferMoney(1l, 1l, "no", BigDecimal.valueOf(20)));
//        System.out.println(TransactionDaoImpl.getInstance().read(2L));
//        try {
//            CreatorBill.getInstance   ().createBill("1", "type", "bank1", "bank2", "ba1", "ba2", BigDecimal.TEN);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(BankAccountServiceImpl.getInstance().replenishmentAccount(1L, BigDecimal.valueOf(300)));

//        System.out.println(BankAccountServiceImpl.getInstance().withdrawal(1L, BigDecimal.valueOf(300)));
    }

}