package org.example.clever_bank.service.accrual;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.service.BankAccountService;

import java.time.LocalDate;

public class ManagerAccrualOfInterest implements Runnable {

    private static final Logger logger = LogManager.getLogger(ManagerAccrualOfInterest.class);

    private final BankAccountService bankAccountService;

    public ManagerAccrualOfInterest(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                LocalDate now = LocalDate.now();
                if (now.lengthOfMonth() == now.getDayOfMonth()) {
                    bankAccountService.interestOnBalance();
                    logger.info("Percent was accrued on account balances");
                }
                Thread.sleep(30000);
                logger.info("ACCRUED CHECKED");
            } catch (InterruptedException e) {
                logger.error("Thread has been interrupted in ManagerAccrualOfInterest");
            }

        }
    }
}
