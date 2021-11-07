package ru.job4j.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.Account;
import ru.job4j.model.Ticket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(PsqlStore.class.getClassLoader()
                .getResourceAsStream("db.properties")))) {
            cfg.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Account> findAccountAll() {
        List<Account> accounts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    accounts.add(
                            new Account(
                                    it.getInt("id"),
                                    it.getString("username"),
                                    it.getString("email"),
                                    it.getString("phone")
                            )
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return accounts;
    }

    @Override
    public Account createAccount(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO account(username, email, phone) VALUES(?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            try (ResultSet it = ps.getGeneratedKeys()) {
                if (it.next()) {
                    account.setId(it.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return account;
    }

    @Override
    public Account updateAccount(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE account SET username = ?, email = ?, phone = ? WHERE id = ?")) {
            ps.setInt(1, account.getId());
            ps.setString(2, account.getUsername());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getPhone());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return account;
    }

    @Override
    public Account saveAccount(Account account) {
        Account result = null;
        Account search = null;
        search = PsqlStore.instOf().findByIdAccount(account.getEmail());
        if (search == null) {
            createAccount(account);
            result = account;
        } else {
            result = updateAccount(account);

        }
        return result;
    }

    @Override
    public Account findByIdAccount(String email) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                   account = new Account(
                           rs.getInt("id"),
                           rs.getString("username"),
                           rs.getString("email"),
                           rs.getString("phone"));
                }
            }
        } catch(Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return account;
    }

    @Override
    public Collection<Ticket> findTicketAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            try (ResultSet it = ps.getGeneratedKeys()) {
                while (it.next()) {
                    tickets.add(
                            new Ticket(
                                    it.getInt("id"),
                                    it.getInt("session_id"),
                                    it.getInt("row"),
                                    it.getInt("cell"),
                                    null
                            )
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return tickets;
    }

    @Override
    public void saveTicket(Ticket ticket) {
        if (ticket.getId() == 0) {
            createTicket(ticket);
        } else {
            updateTicket(ticket);
        }
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(session_id, row, cell, account) VALUES(?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,ticket.getSession_id());
            ps.setInt(2,ticket.getRow());
            ps.setInt(3,ticket.getCell());
            ps.setInt(4, ticket.getAccount().getId());
            try (ResultSet it = ps.getGeneratedKeys()) {
                if (it.next()) {
                    ticket.setId(it.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return ticket;
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE ticket SET session_id = ?, row = ?, cell = ? WHERE id = ?")) {
            ps.setInt(1, ticket.getId());
            ps.setInt(2, ticket.getSession_id());
            ps.setInt(3, ticket.getRow());
            ps.setInt(4, ticket.getCell());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception in log example ", e);
        }
        return ticket;
    }
}
