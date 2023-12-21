package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalanceByAccountId(int id) {

        String sql = "SELECT balance\n" +
                "\tFROM accounts WHERE account_id = ? ;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                return results.getBigDecimal("balance");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    @Override
    public Account createAccount(Account account) {
        Account newAccount = null;
        String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";

        try {
            int results = jdbcTemplate.queryForObject(sql, int.class, account.getUser_id(), 1000);
            newAccount = getAccountByAccountId(results);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation occurred while updating employee", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error updating employee in the database", e);
        }
         return newAccount;
    }

    @Override
    public Account getAccountByUserId(int user_id) {

        Account account = new Account();
        account.setUser_id(0);
        String sql = "SELECT account_id, balance FROM accounts WHERE  user_id = ?";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);
            if (results.next()){
                account.setBalance(results.getBigDecimal("balance"));
                account.setAccount_id(results.getInt("account_id"));
                account.setUser_id(user_id);
            }
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation occurred while updating employee", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error updating employee in the database", e);
        }
        if(account.getUser_id()==0){
            throw new DaoException("Non-existent account");
        }
        return account;
    }

    public Account getAccountByAccountId(int accountId) {
        Account account = new Account();
        String sql = "SELECT balance, user_id FROM accounts WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
            if (results.next()) {
                account.setBalance(results.getBigDecimal("balance"));
                account.setAccount_id(accountId);
                account.setUser_id(results.getInt("user_id"));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation occurred while updating employee", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error updating employee in the database", e);
        }
        return account;
    }

}
