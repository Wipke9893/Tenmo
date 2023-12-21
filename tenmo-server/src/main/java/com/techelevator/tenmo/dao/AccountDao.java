package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    BigDecimal getBalanceByAccountId(int id);

     Account createAccount(Account account);

     Account getAccountByUserId(int user_id);
    Account getAccountByAccountId(int accountId);
//  TODO  User getUserByAccountId(int account_id);

}
