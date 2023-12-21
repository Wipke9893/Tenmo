package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransfersDao implements TransfersDao{
    private final JdbcTemplate jdbcTemplate;
    public JdbcTransfersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }
    @Override
    public List<Transfer> getTransfersByAccountId(int account_id) {
        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, pending, denied, amount_transferred, sender_id, receiver_id, date_time\n" +
                "\tFROM transfers WHERE sender_id = ? OR receiver_id = ?; ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id, account_id);
            while (results.next()) {
                transfers.add(mapRowToTransfers(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransfersByUserId(int user_id) {
        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, pending, denied, amount_transferred, sender_id, receiver_id, date_time\n" +
                "\tFROM transfers WHERE sender_id = (SELECT account_id FROM accounts WHERE user_id = ?) AND pending = true; ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);
            while (results.next()) {
                transfers.add(mapRowToTransfers(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {

        Transfer newTransfer = null;

        String sql =
                "INSERT INTO transfers(pending, denied, amount_transferred, sender_id, receiver_id, date_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING transfer_id;";

        try {
            int transferId = jdbcTemplate.queryForObject(sql, int.class,
                    transfer.isPending(),
                    transfer.isDenied(),
                    transfer.getAmount_transferred(),
                    transfer.getSender_id(),
                    transfer.getReceiver_id(),
                    transfer.getDate_time());

            newTransfer = getTransferByTransferId(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return newTransfer;
    }

    public void performTransaction(Transfer transfer) {
        // Update sender's account balance
        jdbcTemplate.update("UPDATE accounts SET balance = balance - ? WHERE account_id = ?;",
                transfer.getAmount_transferred(), transfer.getSender_id());

        // Update receiver's account balance
        jdbcTemplate.update("UPDATE accounts SET balance = balance + ? WHERE account_id = ?;",
                transfer.getAmount_transferred(), transfer.getReceiver_id());
    }


    @Override
    public Transfer getTransferByTransferId(int id) {
        String sql = "SELECT transfer_id, pending, denied, amount_transferred, sender_id, receiver_id, date_time\n" +
                "\tFROM transfers WHERE transfer_id = ?; ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                return mapRowToTransfers(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    @Override
    public Transfer approveTransfer(int transferId) {
        Transfer transfer = null;
        String sql = "UPDATE transfers SET pending = false WHERE transfer_id = ?";
        try {
           int numRowsAffected = jdbcTemplate.update(sql, transferId);
           if (numRowsAffected == 0){
               throw new DaoException("Zero rows affected, expected at least one.");
           }
           else{
               transfer = getTransferByTransferId(transferId);
               performTransaction(getTransferByTransferId(transferId));
           }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation occurred while updating employee", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error updating employee in the database", e);
        }
        return transfer;
    }

    @Override
    public Transfer rejectTransfer(int transferId) {
        Transfer transfer = null;
        String sql = "UPDATE transfers SET pending = false, denied = true WHERE transfer_id = ?";
        try {
            int numRowsAffected = jdbcTemplate.update(sql, transferId);
            if (numRowsAffected == 0){
                throw new DaoException("Zero rows affected, expected at least one.");
            }
            else{
                transfer = getTransferByTransferId(transferId);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation occurred while updating employee", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error updating employee in the database", e);
        }
        return transfer;
    }

    private Transfer mapRowToTransfers(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(results.getInt("transfer_id"));
        transfer.setAmount_transferred(results.getBigDecimal("amount_transferred"));
        transfer.setSender_id(results.getInt("sender_id"));
        transfer.setReceiver_id(results.getInt("receiver_id"));
        transfer.setPending(results.getBoolean("pending"));
        transfer.setDenied(results.getBoolean("denied"));

        // Check if date_time column is NULL
        Timestamp timestamp = results.getTimestamp("date_time");
        LocalDateTime dateTime = (timestamp != null) ? timestamp.toLocalDateTime() : null;
        transfer.setDate_time(dateTime);

        return transfer;
    }

}
