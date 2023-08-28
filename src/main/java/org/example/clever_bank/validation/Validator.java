package org.example.clever_bank.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private Validator() {
    }

    public static Validator getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final Validator INSTANCE = new Validator();
    }

    public boolean validateLogin(String login) {
        if (login == null) {
            return false;
        }
        Pattern patternLogin = Pattern.compile("[a-zA-Z0-9.\\\\-_\\\\+]{3,60}");
        Matcher matcherLogin = patternLogin.matcher(login);

        return matcherLogin.matches();
    }

    public boolean validatePassword(String login, String password) {
        if (password == null) {
            return false;
        }

        Pattern patternPassword = Pattern.compile("^(?=\\S+$).{3,60}$");
        Matcher matcherPassword = patternPassword.matcher(password);

        return matcherPassword.matches();
    }
}
