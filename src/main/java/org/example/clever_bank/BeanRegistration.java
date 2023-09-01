package org.example.clever_bank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.connection.ConnectionPool;
import org.example.clever_bank.dao.impl.AccountDaoImpl;
import org.example.clever_bank.dao.impl.BankAccountDaoImpl;
import org.example.clever_bank.dao.impl.BankDaoImpl;
import org.example.clever_bank.dao.impl.TransactionDaoImpl;
import org.example.clever_bank.service.TransactionService;
import org.example.clever_bank.service.impl.AccountServiceImpl;
import org.example.clever_bank.service.impl.BankAccountServiceImpl;
import org.example.clever_bank.service.impl.TransactionServiceImpl;
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

        BankAccountDaoImpl bankAccountDao = new BankAccountDaoImpl(connectionPool, logger);
        AccountDaoImpl accountDao = new AccountDaoImpl(connectionPool, logger);
        PaperWorkerPdf paperWorker = new PaperWorkerPdf();

        BankAccountServiceImpl bankAccountService = new BankAccountServiceImpl(
                bankAccountDao, new TransactionDaoImpl(connectionPool, logger),
                accountDao, new BankDaoImpl(connectionPool, logger), paperWorker);

        BankActivityMenu bankActivityMenu = new BankActivityMenu(scanner, bankAccountService,
                new TransactionServiceImpl(new TransactionDaoImpl(connectionPool, logger), bankAccountDao, accountDao, paperWorker));

        new ManagerAccrualOfInterest(bankAccountService);

        return new Menu(authMenu, bankActivityMenu, scanner);
    }
}
