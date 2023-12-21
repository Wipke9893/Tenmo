package com.techelevator.tenmo.model;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {
    private boolean pending;
    private boolean denied;
    private int sender_id;
    private int receiver_id;
    @Positive(message = "The amount_transferred field must be greater than 0.")
    private BigDecimal amount_transferred;
    private LocalDateTime date_time;
    private int transfer_id;

    public Transfer() {
        this.date_time = LocalDateTime.now();
    }


//    boolean pending, boolean denied
    public Transfer(int transfer_id, boolean pending, int senderId, int receiverId, BigDecimal amountTransferred) {
        this.transfer_id = transfer_id;
        this.denied = false;
        this.pending = pending;
        sender_id = senderId; //TODO make instead of account id into user id
        receiver_id = receiverId;
        amount_transferred = amountTransferred;
        this.date_time = LocalDateTime.now();

    }
    public int getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(int transfer_id) {
        this.transfer_id = transfer_id;
    }
    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public boolean isDenied() {
        return denied;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public BigDecimal getAmount_transferred() {
        return amount_transferred;
    }

    public void setAmount_transferred(BigDecimal amount_transferred) {
        this.amount_transferred = amount_transferred;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }


}
