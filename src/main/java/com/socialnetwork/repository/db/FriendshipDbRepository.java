package com.socialnetwork.repository.db;

import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.Pair;
import com.socialnetwork.domain.validators.ValidationException;
import com.socialnetwork.domain.validators.Validator;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.Paginator;
import com.socialnetwork.repository.paging.PagingRepository;
//import org.postgresql.util.PSQLException;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements PagingRepository<Pair<Long, Long>, Friendship> {

    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;
    private Connection connection;

    /**
     * Constructor
     * @param url
     * @param username
     * @param password
     * @param validator
     */
    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) throws Exception {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        try{
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ignored) {
            throw new Exception("The friendship SQL connection couldn't be created");
        }
    }

    /**
     * Selectarea unei prietenii in functie de id
     * @param longLongPair -> pereche de id-uri a celor 2 utilizatori
     * @return prietenia identificate
     */
    @Override
    public Friendship findOne(Pair<Long, Long> longLongPair)
    {
        String sql = String.format("Select * from friendship where (id_first = %s and id_second= %s) or (id_second = %s and id_first = %s)", longLongPair.getFirst(), longLongPair.getSecond(), longLongPair.getFirst(), longLongPair.getSecond());
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            resultSet.next();
            Long id_first = resultSet.getLong("id_first");
            Long id_second = resultSet.getLong("id_second");
            String date = resultSet.getString("date");
            Friendship friendship = new Friendship(id_first, id_second, LocalDate.parse(date));
            friendship.setId( new Pair<>(id_first, id_second));

            return friendship;
        } catch (SQLException e)
        {
           // e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return Toate prieteniile
     */
    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from friendship");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id_first = resultSet.getLong("id_first");
                Long id_second = resultSet.getLong("id_second");
                String date = resultSet.getString("date");
                Friendship friendship = new Friendship(id_first, id_second, LocalDate.parse(date));
                friendship.setId( new Pair<>(id_first, id_second));
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    /**
     * Salveaza o entitate de prietenie in basa de date
     * @param entity
     *         entity must be not null
     * @return
     */
    @Override
    public Friendship save(Friendship entity) {
        Friendship frnd = new Friendship();
        try {
            frnd = this.findOne(new Pair<>(entity.getId2(), entity.getId1()));
        }catch (Exception psqlException) {}
        if (frnd != null) throw new ValidationException("Prietenie existenta!");

        String sql = String.format("insert into friendship(id_first, id_second, date) values (%s, %s, '%s')", entity.getId1(), entity.getId2(), entity.getDate());

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

    /**
     * Sterge o entitate de prietenie din baza de date
     * @param longLongPair -> pereche de id-uri a celor 2 utilizatori
     * @return
     */
    @Override
    public Friendship delete(Pair<Long, Long> longLongPair) {
        Friendship friendship = new Friendship();
        Friendship friendship2 = new Friendship();
        try{
            friendship = this.findOne(longLongPair);
            friendship2 =this.findOne(new Pair<>(longLongPair.getSecond(), longLongPair.getFirst()));
        }
            catch (Exception e){}

        if(friendship == null && friendship2 == null)
            return null;

        String sql = String.format("delete from friendship where (id_first = %s and id_second= %s) or (id_first = %s and id_second = %s)", longLongPair.getSecond(), longLongPair.getFirst(), longLongPair.getFirst(), longLongPair.getSecond());

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return friendship;
    }

    @Override
    public Friendship update(Friendship entity) {
        return null;
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        Paginator<Friendship> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
