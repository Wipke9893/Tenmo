package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransfersDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()") //JUST makes it so that anonymous users cant access anything
// You HAVE to log in every time you restart the server
public class TransferController { //principle


    private TransfersDao transfersDao;
    private UserDao userDao;
    private AccountDao accountDao;

    public TransferController(TransfersDao transfersDao, UserDao userDao, AccountDao accountDao) {
        this.transfersDao = transfersDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }


//Gets history by id
    @GetMapping("/{user_id}")
    public List<Transfer> getTransfers(@PathVariable int user_id, Principal principal){
        final int loggedInUserId = userDao.getUserByUsername(principal.getName()).getId();
        if(loggedInUserId==user_id){
            return transfersDao.getTransfersByAccountId(accountDao.getAccountByUserId(user_id).getAccount_id());
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the logged in account");
    }

// Creates either a request or a send
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer, Principal principal){

        final User loggedInUser = userDao.getUserByUsername(principal.getName());
        final User sender = userDao.getUserById(accountDao.getAccountByAccountId(transfer.getSender_id()).getUser_id());
        final User receiver = userDao.getUserById(accountDao.getAccountByAccountId(transfer.getReceiver_id()).getUser_id());
        //makes sure neither account is null if yes, throw BAD REQUEST
        checkIfValidAccounts(sender, receiver);
        // sets pending to correct value, OR throws FORBIDDEN if user is not sender/receiver
        transfer.setPending(ensurePendingIsCorrect(loggedInUser, sender, receiver));
        // Checks to make sure you're not stealing money from the bank
        if(transfer.getAmount_transferred().compareTo(BigDecimal.valueOf(0))<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve a negative number");
        }
        // Checks for sending money to yourself
        if(transfer.getSender_id()==transfer.getReceiver_id()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send money to yourself");
        }
        // Only performs transaction if the user is the sender
        if(!transfer.isPending()){
            transfersDao.performTransaction(transfer);
        }
        return transfersDao.createTransfer(transfer);
    }

    @GetMapping("/pending/{sender_user_Id}")
    public List<Transfer> getPendingTransfers(@PathVariable int sender_user_Id, Principal principal) {
        final User loggedInUser = userDao.getUserByUsername(principal.getName());
        if(loggedInUser.getId()==sender_user_Id) {
            return transfersDao.getPendingTransfersByUserId(sender_user_Id);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access other accounts, OR accounts don't exist");
    }
// Approves a pending transfer
    @PutMapping("/approve/{transferId}")
    public Transfer approveTransfer(@PathVariable int transferId, Principal principal) {
        final User loggedInUser = userDao.getUserByUsername(principal.getName());
        Transfer transfer = transfersDao.getTransferByTransferId(transferId);
        if(accountDao.getAccountByUserId(loggedInUser.getId()).getAccount_id()==transfer.getSender_id()) {
            //either returns the approved transfer or just returns the given transfer
            return transfer.isPending() ? transfersDao.approveTransfer(transferId) : transfer;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access other accounts transfers, OR transfer don't exist");

    }
// Rejects a pending transfer
    @PutMapping("/reject/{transferId}")
    public Transfer rejectTransfer(@PathVariable int transferId, Principal principal) {
        final User loggedInUser = userDao.getUserByUsername(principal.getName());
        Transfer transfer = transfersDao.getTransferByTransferId(transferId);
        if(accountDao.getAccountByUserId(loggedInUser.getId()).getAccount_id()==transfer.getSender_id()) {
            return transfer.isPending() ? transfersDao.rejectTransfer(transferId) : transfer;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access other accounts transfers, OR transfer don't exist");

    }

    private void checkIfValidAccounts(User sender, User receiver){
        if(sender==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown sender");
        }
        if(receiver==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown receiver");
        }
    }
    private static boolean ensurePendingIsCorrect( User loggedInUser, User sender, User receiver) {
        if(loggedInUser.getId() == sender.getId()) {
            return false;
        }
        if(loggedInUser.getId() == receiver.getId()) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access other accounts, OR accounts don't exist");
    }


}
