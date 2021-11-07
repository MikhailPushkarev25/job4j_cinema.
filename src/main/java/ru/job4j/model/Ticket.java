package ru.job4j.model;

import java.util.Objects;

public class Ticket {

    private int id;
    private int session_id;
    private int row;
    private int cell;
    private Account account;

    public Ticket(int id, int session_id, int row, int cell, Account account) {
        this.id = id;
        this.session_id = session_id;
        this.row = row;
        this.cell = cell;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id &&
                session_id == ticket.session_id &&
                row == ticket.row &&
                cell == ticket.cell &&
                Objects.equals(account, ticket.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, session_id, row, cell, account);
    }
}
