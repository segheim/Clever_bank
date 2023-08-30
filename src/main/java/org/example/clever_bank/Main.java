package org.example.clever_bank;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.service.BankAccountService;
import org.example.clever_bank.service.impl.BankAccountServiceImpl;

import java.math.BigDecimal;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ValidationException {

//        System.out.println(ConfigurationManager.getProperty("db.url"));

        ConnectionPool.lockingPool().init();
        BankAccountServiceImpl.getInstance().transferMoney(5l, 3l,"lo", BigDecimal.valueOf(500));
//        System.out.println(BankAccountServiceImpl.getInstance().add(BankAccount.builder()
//                        .banks(List.of(Bank.builder().id(1l).build()))
//                        .account(Account.builder().id(5l).build())
//                        .balance(BigDecimal.ONE)
//
//                .build()));
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