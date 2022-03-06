package com.socialnetwork.repository.db;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.validators.Validator;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.Paginator;
import com.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDbRepository implements PagingRepository<Long,User> {

    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    private Connection connection;

    /**
     * Constructor
     * @param url
     * @param username
     * @param password
     * @param validator
     */
    public UserDbRepository(String url, String username, String password, Validator<User> validator) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Identifica un utilizator dupa id
     * @param idu id
     * @return utilizatorul identificat
     */
    @Override
    public User findOne(Long idu) {
       return findUserBySQL(String.format("Select * from users where id=%s", idu));
    }

    public User findOne(String username) {
        return findUserBySQL(String.format("Select * from users where user_name = '%s'", username));
    }

    private User findUserBySQL(String sql){
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            resultSet.next();
            User radu = createUserFromResultSet(resultSet);
            loadFriends(radu);

            return radu;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String userName = rs.getString("user_name");
        User radu = new User(firstName, lastName, userName);
        radu.setId(id);
        return radu;
    }

    private void loadFriends(User user) {
        String sql = String.format("SELECT * from friendship WHERE id_first = %s or id_second = %s", user.getId(), user.getId());
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()){
            
            List<Long> temp = new ArrayList<>();
            while(rs.next()){
                long id_first = Long.parseLong(String.valueOf(rs.getInt("id_first")));
                long id_second = Long.parseLong(String.valueOf(rs.getInt("id_second")));
                if(id_first == user.getId())
                    temp.add(id_second);
                else
                    temp.add(id_first);
            }
            user.setFriendsID(temp);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     *
     * @return iteraotr pentru toi utilizatorii din baza de date
     */
    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next())
                users.add(createUserFromResultSet(resultSet));
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Salvare un utilizator in baza de date
     * @param entity
     *         entity must be not null
     * @return
     */
    @Override
    public User save(User entity) {
        String sql = String.format("insert into users (id, first_name, last_name, user_name ) values" +
                "                   (%s, '%s', '%s', '%s')",
        entity.getId().toString(), entity.getFirstName(), entity.getLastName(), entity.getUserName());
        validator.validate(entity);
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return entity; //"Creating user failed, no rows affected."
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getLong(1));
                return null;
            }
            else
                return entity; // "Creating user failed, no ID obtained."
        } catch (SQLException throwable) {
             throwable.printStackTrace();
        }
        return null;
    }

    /**
     * Stergere un utilizator din baza de date
     * @param aLong
     * @return
     */
    @Override
    public User delete(Long aLong) {

        User user =this.findOne(aLong);

        if(user == null)
            return null;

        String sql = String.format("delete from users where id=%s ", aLong);
        String messageUpdate = String.format("update messages set from_id = NULL where from_id = %s", aLong);
        String chatsUpdate = String.format("update chats set to_id = NULL where to_id = %s", aLong);
        try (PreparedStatement statement = connection.prepareStatement(sql);
             PreparedStatement messageStatement = connection.prepareStatement(messageUpdate);
             PreparedStatement chatsStatement = connection.prepareStatement(chatsUpdate);
        ){
            chatsStatement.executeUpdate();
            messageStatement.executeUpdate();
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    /**
     * Actualizare un utilizator in baza de date
     * @param entity
     *          entity must not be null
     * @return
     */
    @Override
    public User update(User entity) {
        String sql = String.format("update users set first_name = '%s', last_name = '%s' where id=%s or user_name = '%s'", entity.getFirstName(), entity.getLastName(), entity.getId(), entity.getUserName());

        if(entity == null) throw new IllegalArgumentException();
        validator.validate(entity);
        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Paginator<User> paginator = new Paginator<User>(pageable, this.findAll());
        return paginator.paginate();
    }
}