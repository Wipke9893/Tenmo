package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransfersDao {

    List<Transfer> getTransfersByAccountId(int account_id);

    List<Transfer> getPendingTransfersByUserId(int id);

    Transfer createTransfer(Transfer transfer);

    Transfer getTransferByTransferId(int id);

    Transfer approveTransfer(int transferId);

    Transfer rejectTransfer(int transferId);
    void performTransaction(Transfer transfer);
}
