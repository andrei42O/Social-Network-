package com.socialnetwork.repository.db;

import com.socialnetwork.AppGUI.utils.EventType;
import com.socialnetwork.domain.Event;
import com.socialnetwork.domain.Group;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.Paginator;
import com.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventRepository implements PagingRepository<Long, Event> {

    private String url;
    private String username;
    private String password;
    private Connection connection;

    public EventRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event findOne(Long aLong) {
        String sql = String.format("select * from events where id = %s", aLong);
        try(PreparedStatement ps = this.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            if(rs.next())
                return createEvent(rs);
        } catch (SQLException ignored) {
        }
        return null;
    }

    private Event createEvent(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String eventName = rs.getString("name");
        EventType type = Enum.valueOf(EventType.class, rs.getString("event_type"));
        String description = rs.getString("description");
        LocalDate date = rs.getDate("date").toLocalDate();
        String sql = String.format("select * from usertoevent where event_id = %s", id);
        Event event = new Event(eventName, type, date, description);
        event.setId(id);
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet result = ps.executeQuery();
        while(result.next())
            event.addParticipant(result.getLong("user_id"));
        return event;
    }

    @Override
    public Iterable<Event> findAll() {
        String sql = "select * from events";
        List<Event> arr = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next())
                arr.add(createEvent(rs));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return arr;
    }

    @Override
    public Event save(Event entity) {
        String sql = String.format("insert into events (event_type, description, date, name) values ('%s', '%s', '%s', '%s')", entity.getType(), entity.getDescription(), entity.getDate(), entity.getName());
        String userToGroupSQL = "insert into usertoevent (event_id, user_id) values (?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insert = connection.prepareStatement(userToGroupSQL);){
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating events failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating events failed, no ID obtained.");
                }
            }

            for(Long userID : entity.getParticipants()){
                insert.setLong(1, entity.getId());
                insert.setLong(2, userID);
                int rez = insert.executeUpdate();
                if (rez == 0)
                    throw new SQLException("Assigning users to group failed, no rows affected.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Event delete(Long aLong) {
        Event ret = findOne(aLong);
        if(ret == null)
            return null;
        String sql = String.format("delete from events where id = %s", aLong);
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.executeUpdate();
            return ret;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Event update(Event entity) {
        Event oldEntity = findOne(entity.getId());
        if(oldEntity == null)
            return entity;
        List<Long> going = entity.getParticipants().stream().filter(id -> {return entity.going(id) && !oldEntity.getParticipants().contains(id);}).collect(Collectors.toList());
        List<Long> notGoing = oldEntity.getParticipants().stream().filter(id -> {return !entity.getParticipants().contains(id);}).collect(Collectors.toList());
        if(notGoing.size() > 0) {
            String del = "delete from usertoevent where event_id = ? and user_id = ?";
            try(PreparedStatement delete = connection.prepareStatement(del);){
                for(Long userID : notGoing){
                    delete.setLong(1, entity.getId());
                    delete.setLong(2, userID);
                    int rez = delete.executeUpdate();
                    if (rez == 0)
                        throw new SQLException("Assigning users to group failed, no rows affected.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if(going.size() == 0)
            return null;

        String userToGroupSQL = "insert into usertoevent (event_id, user_id) values (?, ?)";
        try(PreparedStatement insert = connection.prepareStatement(userToGroupSQL);){
            for(Long userID : going){
                insert.setLong(1, entity.getId());
                insert.setLong(2, userID);
                int rez = insert.executeUpdate();
                if (rez == 0)
                    throw new SQLException("Assigning users to group failed, no rows affected.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        Paginator<Event> paginator = new Paginator<Event>(pageable, this.findAll());
        return paginator.paginate();
    }

    public int getNoOfEvents() {
        int count = 0;
        String sql = "select * from events";
        List<Event> arr = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                count++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
        return count;
    }
}
