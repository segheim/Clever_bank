package org.example.clever_bank.service.validation;

import org.example.clever_bank.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {

    @Test
    public void test_validateLogin_shouldReturnTrue_whenCorrectLogin() {
        String login = "llloginfdrfff";

        assertTrue(Validator.getInstance().validateLogin(login));
    }

    @Test
    public void test_validateLogin_shouldReturnFalse_whenIncorrectLogin() {
        String login = "ll";

        assertFalse(Validator.getInstance().validateLogin(login));
    }

    @Test
    public void test_validatePassword_shouldReturnFalse_whenLoginNull() {
        String login = null;

        assertFalse(Validator.getInstance().validateLogin(login));
    }

    @Test
    public void test_validatePassword_shouldReturnTrue_whenCorrectPassword() {
        String password = "llas0!~?dee.-?d=";

        assertTrue(Validator.getInstance().validatePassword(password));
    }

    @Test
    public void test_validatePassword_shouldReturnFalse_whenIncorrectPassword() {
        String password = "op";

        assertFalse(Validator.getInstance().validateLogin(password));
    }

    @Test
    public void test_validateLogin_shouldReturnFalse_whenPasswordNull() {
        String password = null;

        assertFalse(Validator.getInstance().validateLogin(password));
    }

    @Test
    public void test_validateAmount_shouldReturnFalse_whenIncorrectLogin() {
        assertTrue(Validator.getInstance().validateAmount(BigDecimal.ZERO));
    }

    @Test
    public void test_validateAmount_shouldReturnFalse_whenLoginNull() {
        assertFalse(Validator.getInstance().validateAmount(BigDecimal.TEN.negate()));
    }

    @Test
    public void test_validateType_shouldReturnFalse_whenLoginNull() {
        String type = null;

        assertFalse(Validator.getInstance().validateLogin(type));
    }

    @Test
    public void test_validateType_shouldReturnTrue_whenCorrectLogin() {
        String type = "llas0!~?dee.-?d=";

        assertTrue(Validator.getInstance().validatePassword(type));
    }

    @Test
    public void test_validateType_shouldReturnFalse_whenIncorrectLogin() {
        String type = "op";

        assertFalse(Validator.getInstance().validateLogin(type));
    }
}
