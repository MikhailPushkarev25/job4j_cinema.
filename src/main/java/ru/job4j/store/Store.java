package ru.job4j.store;

import ru.job4j.model.Account;
import ru.job4j.model.Ticket;

import java.util.Collection;

public interface Store {

    Collection<Account> findAccountAll();

    Account createAccount(Account account);

    Account updateAccount(Account account);

    Account saveAccount(Account account);

    Account findByIdAccount(String email);

    Collection<Ticket> findTicketAll();

    void saveTicket(Ticket ticket);

    Ticket createTicket(Ticket ticket);

    Ticket updateTicket(Ticket ticket);
}
