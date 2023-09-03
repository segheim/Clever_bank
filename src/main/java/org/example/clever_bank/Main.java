package org.example.clever_bank;

import org.example.clever_bank.view.Menu;

public class Main {
    public static void main(String[] args) {
        Menu menu = BeanRegistration.registrationMenu();
        menu.getMenu();
    }
}