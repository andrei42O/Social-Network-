package com.socialnetwork.repository.db;

import com.socialnetwork.domain.Message;
import com.socialnetwork.domain.User;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.Paginator;
import com.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class MessageDbRepository implements PagingRepository<Long, Message> {

    private final String url;
    private final String user;
    private final String password;
    Connection connection;

    /**
     * Constructor
     * @param url
     * @param user
     * @param password
     */
    public MessageDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a user from a result set
     * @param rs result set
     * @return the newly created user
     * @throws SQLException
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        Long id = Long.parseLong(String.valueOf(rs.getInt("id")));
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String userName = rs.getString("user_name");
        List<Long> radusFriends = new ArrayList<>();
        String sql = String.format("select * from friendship where id_first = %s or id_second = %s", id, id);
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet tempRS = statement.executeQuery(); // check if the message exist
        //Closing the connection
        
        while(tempRS.next()){
            Long tempID1 = Long.parseLong(String.valueOf(tempRS.getInt("id_first")));
            Long tempID2 = Long.parseLong(String.valueOf(tempRS.getInt("id_second")));
            if(tempID1.equals(id))
                radusFriends.add(tempID2);
            if(tempID2.equals(id))
                radusFriends.add(tempID1);
        }
        User radu = new User(firstName, lastName);
        radu.setUserName(userName);
        radu.setId(id);
        radu.setFriendsID(radusFriends);
        return radu;
    }

    /**
     * create a message from a result set
     * @param rs result set
     * @return the newly created message
     * @throws SQLException
     */
    private Message createMessageFromResultSet(ResultSet rs) throws SQLException {
        long aLong = rs.getLong("id");
        String usersSQL = String.format("select * from chats where msg_id = %s", aLong);
        String userDetailsSQL = "select * from users where id = ?";
        PreparedStatement usersStatement = connection.prepareStatement(usersSQL); // search all users from this chat
        PreparedStatement userSearch = connection.prepareStatement(userDetailsSQL); // search all users by id
        ResultSet userRS = usersStatement.executeQuery(); // check the message receivers
        int fromID = rs.getInt("from_id");
        ResultSet tempRS = null;
        User from = verifyUnknownUser(userSearch, fromID);
        Set<User> to = new HashSet<>();
        while(userRS.next()) { // while there are persons to the message to be sent
            int tempID = userRS.getInt("to_id"); // get their id
            User radu = null;
            radu = verifyUnknownUser(userSearch, tempID);
            to.add(radu);
        }
        // Closing the connection to the database
        
        long reply = rs.getLong("reply"); // get the reply message id
        LocalDate date = LocalDate.parse(rs.getDate("date").toString());
        LocalTime time = LocalTime.parse(rs.getTime("time").toString());
        LocalDateTime actualDateTime = LocalDateTime.of(date, time);
        Message msg = new Message(from, to, rs.getString("content"), actualDateTime);
        msg.setId(aLong);
        if(reply != -1) // if the message is a reply
            msg.setReply(findOne(reply)); // find the message that it was replied to
        long groupID = rs.getLong("group_id");
        if(groupID != -1)
            msg.setGroupID(groupID);
        return msg;
    }

    /**
     * The function checks if the user we are going to query has an existing account
     * @param userSearch - PreparedStatement (SQL)
     * @param tempID - integer
     * @return User entity
     * @throws SQLException if the query fails
     */
    private User verifyUnknownUser(PreparedStatement userSearch, int tempID) throws SQLException {
        ResultSet tempRS;
        User radu;
        if(tempID != 0) {
            userSearch.setInt(1, tempID); // prepare sql statement with the id
            tempRS = userSearch.executeQuery(); // find their information
            tempRS.next();
            radu = createUserFromResultSet(tempRS); // create the user
        }
        else {
            radu = new User("Unknown", "User");
            radu.setId(-1L);
        }
        return radu;
    }

    @Override
    public Message findOne(Long aLong) {
        String sql = String.format("select * from messages where id = %s", aLong);
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery(); // check if the message exists
        ) {
            rs.next();
            
            return createMessageFromResultSet(rs);
        } catch (SQLException throwable) {
           //throwable.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return iterable on all the messages
     */
    @Override
    public Iterable<Message> findAll() {
        List<Message> ret = new ArrayList<>();
        String sql = "select * from messages";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery(); // check if the message exists
        ) {
            while(rs.next())
                ret.add(createMessageFromResultSet(rs));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

    /**
     * save a message
     * @param entity message
     * @return
     * @throws Exception
     */
    private long saveToMessages(Message entity) throws Exception {
        String messageSave = "insert into messages (from_id, content, reply, date, time, group_id) values (?, ?, ?, ?, ?, ?)";
        PreparedStatement msgSave = connection.prepareStatement(messageSave, Statement.RETURN_GENERATED_KEYS);
        msgSave.setInt(1, Integer.parseInt(String.valueOf(entity.getFrom().getId())));
        msgSave.setString(2, entity.getMessage());
        if (entity.getReply() == null)
            msgSave.setLong(3, -1L);
        else
            msgSave.setLong(3, entity.getReply().getId());
        msgSave.setDate(4, Date.valueOf(entity.getDate().toLocalDate()));
        msgSave.setTime(5, Time.valueOf(entity.getDate().toLocalTime()));
        msgSave.setLong(6, entity.getGroupID());
        if(msgSave.executeUpdate() == 0){
            
            throw new SQLException("Creating user failed, no rows affected.");
        }
        ResultSet generatedKey = msgSave.getGeneratedKeys();
        if(generatedKey.next()) {
            
            return generatedKey.getLong(1);
        }
        
        throw new Exception("The message couldn't be saved!\n");
    }

    /**
     * save a message in a chat
     * @param entity
     * @throws Exception
     */
    private void saveToChats(Message entity) throws Exception {
        if(entity.getTo() == null)
            return;
        String chatsSave = "insert into chats (msg_id, to_id) values (?, ?)";
        PreparedStatement chtSave = connection.prepareStatement(chatsSave);
        chtSave.setLong(1, entity.getId());
        AtomicReference<String> safeRadu = new AtomicReference<>("");
        entity.getTo().forEach(x -> {
            try {
                chtSave.setInt(2, Integer.parseInt(String.valueOf(x.getId())));
                if(chtSave.executeUpdate() == 0)
                    safeRadu.set(" ");
            } catch (SQLException e) {
                e.printStackTrace();
                safeRadu.set(" ");
            }
        });
        
        if(safeRadu.getPlain().length() > 0)
            throw new Exception("Something happened!");
    }

    /**
     * save a message in Db
     * @param entity
     *         entity must be not null
     * @return
     */
    @Override
    public Message save(Message entity) {
        try {
            if(entity.getGroupID() != -1){
                saveToMessages(entity);
                return null;
            }
            if(!checkAllParticipantsExist(entity.getParticipants()))
                return entity;
            Long id = saveToMessages(entity);
            entity.setId(id);
            saveToChats(entity);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * The function checks if all the participants to the conversation have an existing account
     * @param users - Set<User>
     * @return true / false
     */
    private boolean checkAllParticipantsExist(Set<User> users){
        String sql = "select * from users where id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            for(User user: users){
                statement.setInt(1, Integer.parseInt(String.valueOf(user.getId())));
                ResultSet rs = statement.executeQuery();
                if(!rs.next()) {
                    
                    return false;
                }
            }
        } catch (SQLException throwable) {
            throwable.getStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Message delete(Long aLong) {
        Message ret = findOne(aLong);
        if(ret == null)
            return null;
        String messageSQL = "delete from messages where id = ?";
        String chatsSQL = "delete from chats where msg_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(messageSQL);
            PreparedStatement statement1 = connection.prepareStatement(chatsSQL)
        ) {
            // deletes from chats (foreign keys safe)
            statement1.setLong(1, aLong);
            statement1.executeUpdate();
            // delete the message (from the message database)
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

    /**
     * Update a message
     * @param entity
     *          entity must not be null
     * @return
     */
    @Override
    public Message update(Message entity) {
        String sql = "UPDATE messages SET content = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, entity.getMessage());
            statement.setLong(2, entity.getId());
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        Paginator<Message> paginator = new Paginator<Message>(pageable, this.findAll());
        return paginator.paginate();
    }
}
