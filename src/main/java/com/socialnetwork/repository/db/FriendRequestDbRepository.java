package com.socialnetwork.repository.db;

import com.socialnetwork.domain.FriendRequest;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.Paginator;
import com.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendRequestDbRepository implements PagingRepository<Long, FriendRequest> {

    private String url;
    private String username;
    private String password;

    /**
     * Constructor
     * @param url
     * @param username
     * @param password
     */
    public FriendRequestDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * find one friend request
     * @param aLong id of searched request
     * @return
     */
    @Override
    public FriendRequest findOne(Long aLong) {
        String sql = String.format("Select * from friendrequest where id=%s", aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            resultSet.next();
            Integer ID = resultSet.getInt("id");
            Long id = Long.parseLong(ID.toString());

            Integer ID_FROM  = resultSet.getInt("id_from");
            Long idFrom = Long.parseLong(ID_FROM.toString());

            Integer ID_TO = resultSet.getInt("id_to");
            Long idTo = Long.parseLong(ID_TO.toString());

            Integer STATE = resultSet.getInt("state");
            Long state = Long.parseLong(STATE.toString());

            String date = resultSet.getString("date");

            FriendRequest fr = new FriendRequest(idFrom, idTo, LocalDate.parse(date));
            fr.setId(id);
            fr.setState(state);
            return fr;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return all friend requests
     */
    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> frs = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendrequest");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer ID = resultSet.getInt("id");
                Long id = Long.parseLong(ID.toString());

                Integer ID_FROM  = resultSet.getInt("id_from");
                Long idFrom = Long.parseLong(ID_FROM.toString());

                Integer ID_TO = resultSet.getInt("id_to");
                Long idTo = Long.parseLong(ID_TO.toString());

                Integer STATE = resultSet.getInt("state");
                Long state = Long.parseLong(STATE.toString());

                String date = resultSet.getString("date");

                FriendRequest fr = new FriendRequest(idFrom, idTo, LocalDate.parse(date));
                fr.setId(id);
                fr.setState(state);
                frs.add(fr);
            }
            return frs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frs;
    }

    /**
     * save a friend request
     * @param fr friend request
     * @return
     */
    @Override
    public FriendRequest save(FriendRequest fr) {
        String sql = String.format("insert into friendrequest (id_from, id_to, state, date) values (%s, %s, 0, '%s')", fr.getIdFrom().toString(), fr.getIdTo().toString(), fr.getDate());

        if(fr == null) throw new IllegalArgumentException();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        } catch (SQLException e) {
            return fr;
        }
        return null;
    }

    /**
     * delete a friend request
     * @param aLong id of friend request
     * @return
     */
    @Override
    public FriendRequest delete(Long aLong) {
        FriendRequest fr =this.findOne(aLong);
        if(fr == null)
            return null;
        String sql = String.format("delete from friendrequest where id = %s ", aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return fr;
    }

    /**
     * Update a friend request
     * @param entity
     *          entity must not be null
     * @return
     */
    @Override
    public FriendRequest update(FriendRequest entity) {
        String sql = String.format("update friendrequest set state = '%s' where id=%s and state=0", entity.getState().toString(), entity.getId().toString());

        if(entity == null) throw new IllegalArgumentException();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
        return null;
    }

    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        Paginator<FriendRequest> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
