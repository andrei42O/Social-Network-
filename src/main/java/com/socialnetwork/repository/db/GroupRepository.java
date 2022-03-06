package com.socialnetwork.repository.db;

import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.Group;
import com.socialnetwork.domain.validators.Validator;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.Paginator;
import com.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository implements PagingRepository<Long, Group> {

    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;
    private Connection connection;

    public GroupRepository(String url, String username, String password) throws SQLException {
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
    public Group findOne(Long aLong) {
        String sql = String.format("select * from groups where id = %s", aLong);
        try( PreparedStatement ps = this.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                String groupName = rs.getString("group_name");
                Group radu = new Group(groupName);
                radu.setUsers(getUsersIDS(aLong));
                radu.setId(aLong);
                return radu;
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    private List<Long> getUsersIDS(Long groupID) throws SQLException {
        List<Long> arr  = new ArrayList<>();
        String sql = "select user_id from groupToUsers where group_id = " + groupID;
        try( PreparedStatement ps = this.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next())
                arr.add(rs.getLong("user_id"));
        } catch (SQLException t){
            t.printStackTrace();
        }
       return arr;
    }

    @Override
    public Iterable<Group> findAll() {
        String sql = "select id from groups";
        List<Group> arr = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()) {
                arr.add(findOne(
                        rs.getLong("id")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return arr;
    }

    @Override
    public Group save(Group entity) {
        String sql = String.format("insert into groups (group_name) values ('%s')", entity.getGroupName());
        String userToGroupSQL = "insert into grouptousers (group_id, user_id) values (?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insert = connection.prepareStatement(userToGroupSQL);){
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating group failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating group failed, no ID obtained.");
                }
            }

            for(Long userID : entity.getUsers()){
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
    public Group delete(Long aLong) {
        Group ret = findOne(aLong);
        if(ret == null)
            return null;
        String sql = String.format("delete from groups where id = %s", aLong);
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.executeUpdate();
            return ret;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Group update(Group entity) {
        Group oldEntity = findOne(entity.getId());
        if(delete(entity.getId()) == null)
            return entity;
        if(save(entity) == null){
            save(oldEntity);
        }
        return null;
    }

    @Override
    public Page<Group> findAll(Pageable pageable) {
        Paginator<Group> paginator = new Paginator<Group>(pageable, this.findAll());
        return paginator.paginate();
    }
}
