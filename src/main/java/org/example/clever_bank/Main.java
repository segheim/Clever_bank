package org.example.clever_bank;

import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.BankDao;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.dao.impl.BankDaoImpl;
import org.example.clever_bank.dao.impl.TransactionDaoImpl;
import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.util.ConfigurationManager;
import org.example.clever_bank.view.Menu;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.SortedMap;


public class Main {
    public static void main(String[] args) {

//        System.out.println(ConfigurationManager.getProperty("db.url"));

        ConnectionPool.lockingPool().init();
//        Optional cleverBank = BankDaoImpl.getInstance().create(new Bank(null, "bet-bank"));
//        System.out.println(BankDaoImpl.getInstance().read(1L).get());
//        List<Bank> banks = BankDaoImpl.getInstance().readAll();
//        System.out.println(BankDaoImpl.getInstance().update(Bank.builder().id(2L).name("good_bank").build()));
//        System.out.println(BankDaoImpl.getInstance().delete(2L));
//        System.out.println(banks);

//        System.out.println(AccountDaoImpl.getInstance().create(new Account(null, "so", "3")).get());
//        System.out.println(AccountDaoImpl.getInstance().read(2L));
//        System.out.println(AccountDaoImpl.getInstance().update(new Account(2L, "No", "2")));
//        System.out.println(AccountDaoImpl.getInstance().delete(3L));
//        TransactionDaoImpl.getInstance().create(Transaction.builder().bankAccountFrom(new BankAccount(1L, null, null, null)).build())
//
        Menu menu = new Menu();
        menu.getMenu();
//        Scanner scanner = new Scanner(System.in);
//        int choice;
//        System.out.println("Select a bank:");
//        System.out.println("1. Good Bank.");
//        System.out.println("2. Best Bank.");
//        System.out.println("3. Full  Bank.");
//        System.out.println("4. Big Bank.");
//        System.out.println("5. Top Bank.");
//        System.out.println("Enter your choice: ");
//        while (true) {
//            choice = scanner.nextInt();
//            scanner.nextLine();
//            if (choice > 5 || choice < 1) {
//                System.out.println("Enter correct number of action! Try again: ");
//            } else {
//                break;
//            }
//        }
//        System.out.println("Otside!!!!");
    }

}