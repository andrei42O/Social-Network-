package com.socialnetwork.repository.db;

import com.socialnetwork.domain.Credentials;
import com.socialnetwork.domain.Group;
import com.socialnetwork.repository.CrudRepository;

import java.sql.*;

public class SecurityRepository implements CrudRepository<Long, Credentials> {
    private String url;
    private String username;
    private String password;
    private Connection connection;

    public SecurityRepository(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
            throw new SQLException("The connection to the GROUP database failed!\n");
        }
    }
    @Override
    public Credentials findOne(Long aLong) throws IllegalArgumentException {
        return findOneBySQL(String.format("select * from credentials where id = %s", aLong));
    }

    public Credentials findOne(String username) throws IllegalArgumentException {
        return findOneBySQL(String.format("select * from credentials where username = '%s'", username));
    }

    private Credentials findOneBySQL(String sql){
        try(PreparedStatement ps = this.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                return new Credentials(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getLong("id")
                );
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Credentials> findAll() {
        return null;
    }

    @Override
    public Credentials save(Credentials entity) {
        String sql = String.format("insert into credentials (username, password, id) values ('%s', '%s', %s)", entity.getUsername(), entity.getHashedPassword(), entity.getId());
        try(PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);){
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating credentials failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return null;
                }
                else {
                    throw new SQLException("Creating credentials failed, no ID obtained.");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Credentials delete(Long aLong) {
        Credentials ret = findOne(aLong);
        if(ret == null)
            return null;
        String sql = String.format("delete from credentials where username = '%s' and id = %s", ret.getUsername(), aLong);
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.executeUpdate();
            return ret;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Credentials update(Credentials entity) {
        // for the moment, no forgot password possibility
        return null;
    }
}
