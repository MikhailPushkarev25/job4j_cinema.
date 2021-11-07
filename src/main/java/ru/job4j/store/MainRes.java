package ru.job4j.store;

import ru.job4j.model.Account;
import ru.job4j.model.Ticket;

public class MainRes {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.saveAccount(new Account(2, "Mikhail", "Hall", "900"));
        for (Account account : store.findAccountAll()) {
            System.out.println(account.getId() + " " + account.getUsername());
        }

        store.saveTicket(new Ticket(1, 1, 1, 2, new Account(0, "Mikhail", "Hall", "900")));
        for (Ticket ticket : store.findTicketAll()) {
            System.out.println(ticket.getId() + " " + ticket.getRow());
        }
    }
}
