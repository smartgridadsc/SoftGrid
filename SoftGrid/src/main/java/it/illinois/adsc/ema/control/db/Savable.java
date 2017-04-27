package it.illinois.adsc.ema.control.db;

import java.sql.Connection;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public interface Savable {
    public void save(Connection connection, SavableStatus status);
    public void insert(Connection connection);
    public void update(Connection connection);
    public void delete(Connection connection);
    public void load(Connection connection, ResultSet rs);
}
