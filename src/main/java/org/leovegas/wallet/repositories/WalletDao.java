package org.leovegas.wallet.repositories;

import org.leovegas.wallet.models.domains.Wallet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class WalletDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WalletDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Wallet insert(final Wallet wallet) {
        jdbcTemplate.update(
                "INSERT INTO wallet(id, player_id, debit_balance, credit_balance) " +
                        "VALUES (:id, :player_id, :debit_balance, :credit_balance)",
                new MapSqlParameterSource()
                        .addValue("id", wallet.id())
                        .addValue("player_id", wallet.playerId())
                        .addValue("debit_balance", wallet.debitBalance())
                        .addValue("credit_balance", wallet.creditBalance())
        );
        return wallet;
    }

    public Optional<Wallet> findByPlayerId(final int playerId) {
        return jdbcTemplate.query(
                "SELECT * FROM wallet WHERE player_id = :player_id",
                new MapSqlParameterSource().addValue("player_id", playerId),
                resultSetExtractor
        ).stream().findAny();
    }

    public void updateCreditBalance(final Wallet wallet, final BigInteger amount) {
        jdbcTemplate.update(
                "UPDATE wallet SET credit_balance = credit_balance + :amount WHERE id = :id",
                new MapSqlParameterSource()
                        .addValue("amount", amount)
                        .addValue("id", wallet.id())
        );
    }

    public boolean updateDebitBalance(final Wallet wallet, final BigInteger amount) {
        return jdbcTemplate.update(
                "UPDATE wallet SET " +
                        "debit_balance = debit_balance + :amount, " +
                        "version = version + 1 " +
                        "WHERE id = :id AND version = :version",
                new MapSqlParameterSource()
                        .addValue("amount", amount)
                        .addValue("id", wallet.id())
                        .addValue("version", wallet.version())
        ) == 1;
    }

    private final RowMapper<Wallet> resultSetExtractor = (rs, rowNum) -> new Wallet(
            UUID.fromString(rs.getString("id")),
            rs.getInt("player_id"),
            rs.getBigDecimal("debit_balance").toBigInteger(),
            rs.getBigDecimal("credit_balance").toBigInteger(),
            rs.getLong("version")
    );


}
