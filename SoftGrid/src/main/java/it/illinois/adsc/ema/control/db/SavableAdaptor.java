package it.illinois.adsc.ema.control.db;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public abstract class SavableAdaptor implements Savable {
    public void save(Connection connection, SavableStatus status) {
        switch (status)
        {
            case INSERT:
                insert(connection);
                break;
            case UPDATE:
                update(connection);
                break;
            case DELETE:
                delete(connection);
                break;
            default:
        }
    }
    public abstract void insert(Connection connection);
    public abstract void update(Connection connection);
    public abstract void delete(Connection connection);
    public abstract void load(Connection connection, ResultSet rs);
}
