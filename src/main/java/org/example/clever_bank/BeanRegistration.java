package org.example.clever_bank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.dao.impl.BankDaoImpl;
import org.example.clever_bank.dao.impl.TransactionDaoImpl;
import org.example.clever_bank.service.impl.AccountServiceImpl;
import org.example.clever_bank.service.impl.BankAccountServiceImpl;
import org.example.clever_bank.service.text.impl.PaperWorkerPdf;
import org.example.clever_bank.service.accrual.ManagerAccrualOfInterest;
import org.example.clever_bank.view.AuthMenu;
import org.example.clever_bank.view.BankActivityMenu;
import org.example.clever_bank.view.Menu;

import java.util.Scanner;

public class BeanRegistration {

    private static final Logger logger = LogManager.getLogger(BeanRegistration.class);

    public static Menu refgistrationMenu() {
        ConnectionPool connectionPool = ConnectionPool.lockingPool();
        connectionPool.init();
        Scanner scanner = new Scanner(System.in);
        AuthMenu authMenu = new AuthMenu(scanner, new AccountServiceImpl(new AccountDaoImpl(connectionPool, logger)));
        BankAccountServiceImpl bankAccountService = new BankAccountServiceImpl(
                new BankAccountDaoImpl(connectionPool, logger), new TransactionDaoImpl(connectionPool, logger),
                new AccountDaoImpl(connectionPool, logger), new BankDaoImpl(connectionPool, logger), new PaperWorkerPdf());
        BankActivityMenu bankActivityMenu = new BankActivityMenu(scanner, bankAccountService);

        new ManagerAccrualOfInterest(bankAccountService);

        return new Menu(authMenu, bankActivityMenu, scanner);
    }
}
