package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts") //@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }


    // Creates Account
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountDao.createAccount(account);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @GetMapping("/{user_id}")
    public Account getAccount(@PathVariable int user_id, Principal principal) {
        if (principal.getName().equals((userDao.getUserById(user_id)).getUsername())) {
            return accountDao.getAccountByUserId(user_id);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
    }

}
