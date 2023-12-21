package com.techelevator.dao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransfersDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class JdbcTransfersDaoIntegrationTest extends BaseDaoTests {

    private Transfer transfer1 = new Transfer(1, true, 1, 2, BigDecimal.valueOf(100));
    private Transfer transfer2 = new Transfer(2, true, 2, 1, BigDecimal.valueOf(200));
    private Account account1 = new Account(1, 1001);
    private Account account2 = new Account(2, 1002);

    private JdbcTransfersDao transfersDao;

    private JdbcAccountDao accountDao;

    @Before
    public void setup() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accountDao = new JdbcAccountDao(jdbcTemplate); // Initialize the class-level accountDao variable
        transfersDao = new JdbcTransfersDao(jdbcTemplate);
    }



    @Test
    public void getTransfersByAccountId_ShouldReturnCorrectTransfers() {
        List<Transfer> transfers = transfersDao.getTransfersByAccountId(accountDao.getAccountByUserId(1001).getAccount_id());
        assertNotNull(transfers);
        assertEquals("expected size of 2",2, transfers.size());
        Transfer transfer = transfers.get(0);
        // Add assertions to match the expected data
        assertEquals("Transfer_Id expected to be 1 for transfer 1",1, transfer.getTransfer_id());
        assertEquals("Sender_Id expected to be 1 for transfer 1",1, transfer.getSender_id());
        assertEquals("Receiver_Id expected to be 2 for transfer 1",2, transfer.getReceiver_id());
        assertEquals("expected transfer of 100",0, transfer.getAmount_transferred().compareTo(BigDecimal.valueOf(100)));
        // Add more assertions as needed based on your test data
        Transfer transfer2 = transfers.get(1);
        assertEquals("Transfer_Id expected to be 2 for transfer 2",2, transfer2.getTransfer_id());
        assertEquals("Sender_Id expected to be 2 for transfer 2",2, transfer2.getSender_id());
        assertEquals("Receiver_Id expected to be 1 for transfer 2",1, transfer2.getReceiver_id());
    }

    @Test
    public void createTransfer_ShouldCreateTransferCorrectly() {
        Transfer transfer = new Transfer();
        // Set transfer properties
        transfer.setPending(true);
        transfer.setDenied(false);
        transfer.setAmount_transferred(BigDecimal.valueOf(100));
        transfer.setSender_id(1);
        transfer.setReceiver_id(2);

        Transfer createdTransfer = transfersDao.createTransfer(transfer);
        assertNotNull(createdTransfer);
        assertEquals(transfer.getSender_id(), createdTransfer.getSender_id());
        assertEquals(transfer.getReceiver_id(), createdTransfer.getReceiver_id());
    }


    @Test
    public void performTransaction_transfers_100() {
        transfersDao.performTransaction(transfer1);

        BigDecimal updatedAccount1Balance = accountDao.getBalanceByAccountId(account1.getAccount_id());
        BigDecimal updatedAccount2Balance = accountDao.getBalanceByAccountId(account2.getAccount_id());

        BigDecimal expectedUser1Balance = BigDecimal.valueOf(900.00);
        BigDecimal expectedUser2Balance = BigDecimal.valueOf(1100.00);

        assertEquals("expected account 1 balance 900", 0, expectedUser1Balance.compareTo(updatedAccount1Balance));
        assertEquals("expected account 2 balance 1100", 0, expectedUser2Balance.compareTo(updatedAccount2Balance));
    }
    @Test
    public void pendingTransfers_retrieves_all_pending_transfers(){
        List<Transfer> expected = new ArrayList<>();
        expected.add(transfer2);
        List<Transfer> actual = transfersDao.getPendingTransfersByUserId(1);

        Assert.assertEquals("expected list size of 1", expected.size(), actual.size());
    }
   @Test
   public void  approveTransfer_approves(){
        transfersDao.approveTransfer(1);
        assertFalse(transfersDao.getTransferByTransferId(1).isPending());
        assertFalse(transfersDao.getTransferByTransferId(1).isDenied());
   }
   @Test
    public void rejectTransfer_rejects(){
        transfersDao.rejectTransfer(2);
        assertTrue(transfersDao.getTransferByTransferId(2).isDenied());
       assertFalse(transfersDao.getTransferByTransferId(2).isPending());
   }
}

