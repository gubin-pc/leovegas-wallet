package org.leovegas.wallet.repositories;

import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.domains.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class TransactionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Transaction insert(final Transaction transaction) {
        jdbcTemplate.update(
                "INSERT INTO transaction(id, wallet_id, amount, operation, created_at)\n" +
                    "VALUES (:id, :wallet_id, :amount, :operation, :created_at)",
                new MapSqlParameterSource()
                        .addValue("id", transaction.id())
                        .addValue("wallet_id", transaction.walletId())
                        .addValue("amount", transaction.amount())
                        .addValue("operation", transaction.operation().name())
                        .addValue("created_at", Timestamp.from(transaction.createdAt()))
        );
        return transaction;
    }

    public List<Transaction> findAll(final int playerId, final LocalDate from, final LocalDate to) {

        String whereClause = "";
        if (from != null) {
            whereClause = whereClause + " AND created_at >= :from";
        }
        if (to != null) {
            whereClause = whereClause + " AND created_at <=:to";
        }

        return jdbcTemplate.query(
                "SELECT t.* FROM transaction t " +
                        "JOIN wallet w on w.id = t.wallet_id " +
                        "WHERE player_id = :player_id " + whereClause +
                        "ORDER BY created_at",
                new MapSqlParameterSource()
                        .addValue("player_id", playerId)
                        .addValue("from", from)
                        .addValue("to", to),
                (rs, rowNum) -> new Transaction(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("wallet_id")),
                        rs.getBigDecimal("amount").toBigInteger(),
                        Operation.valueOf(rs.getString("operation")),
                        rs.getTimestamp("created_at").toInstant()
                )
        );
    }
}
