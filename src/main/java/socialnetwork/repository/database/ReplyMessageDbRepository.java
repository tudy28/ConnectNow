package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class ReplyMessageDbRepository extends AbstractDbRepository<Long,ReplyMessage>{
    private UserDbRepository repoUsers;
    private MessageDbRepository repoMessages;
    String dbName;
    String url;
    String username;
    String password;
    Map<Long,Utilizator> cachedUsers;
    Map<Long,Message> cachedMessages;

    public ReplyMessageDbRepository(String dbName, String url, String username, String password, Validator<ReplyMessage> validator,UserDbRepository repoUsers,MessageDbRepository repoMessages) {
        super(dbName, url, username, password, validator);
        this.repoUsers=repoUsers;
        this.repoMessages=repoMessages;
        this.dbName=dbName;
        this.url=url;
        this.username=username;
        this.password=password;
        this.cachedUsers=new HashMap<>();
        this.cachedMessages=new HashMap<>();
    }

    @Override
    public ReplyMessage extractEntity(ResultSet resultSet) throws SQLException {
        ArrayList<Utilizator> toUsers=new ArrayList<>();
        Utilizator fromUser=null;
        Message repliedMessage=null;
        Long id = resultSet.getLong("id");
        Long fromID = resultSet.getLong("from_id");
        Array toID_list = resultSet.getArray("to_list");
        String message = resultSet.getString("message");
        String createdDateString = resultSet.getString("delivered_date");
        Long repliedID= resultSet.getLong("replied_id");
        LocalDateTime createdDate = LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);
        Long[] listTo=(Long[])toID_list.getArray();
        for(Long idTo:listTo)
            repoUsers.findOne(idTo).ifPresent(toUsers::add);
        if(repoUsers.findOne(fromID).isPresent())
            fromUser=repoUsers.findOne(fromID).get();
        if(repoMessages.findOne(repliedID).isPresent())
            repliedMessage=repoMessages.findOne(repliedID).get();
        ReplyMessage remess=new ReplyMessage(fromUser,toUsers,message,repliedMessage);
        remess.setDate(createdDate);
        remess.setId(id);

        return remess;
    }

    @Override
    public String createEntityAsQueryInsert(ReplyMessage replyMessage) {
        long lastID_reply=generateNewID();
        String messageSent="'"+replyMessage.getMessage()+"'";
        String createdDate="'"+replyMessage.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
        String repliedID="'"+replyMessage.getRepliedMessage().getId()+"'";
        String query="INSERT INTO messages(\n" +
                "id, from_id, to_id, message, delivered_date,replied_id)\n" +
                "VALUES ";
        for(Utilizator user:replyMessage.getTo()){
            query+="("+lastID_reply+","+replyMessage.getFrom().getId()+","+user.getId()+","+messageSent+","+createdDate+","+repliedID+"),";
        }
        query = query.substring(0, query.length()-1) + ";";
        return query;
    }

    @Override
    public String createEntityAsQueryDelete(Long replyMessageID) {
        return "DELETE FROM messages WHERE id="+replyMessageID;
    }

    @Override
    public String createEntityAsQueryFindOne(Long replyMessageID) {
        return "SELECT id, from_id,ARRAY_AGG(to_id) to_list, message,delivered_date,replied_id\n" +
                "from     messages\n" +
                "where id="+replyMessageID+" and replied_id is not null\n"+
                "group by id,from_id,message,delivered_date,replied_id\n" +
                "order by id";
    }

    @Override
    protected Iterable<ReplyMessage> gettAllFromDatabase() {
        ArrayList<ReplyMessage> list = new ArrayList<ReplyMessage>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id, from_id,ARRAY_AGG(to_id) to_list, message,delivered_date,replied_id\n" +
                     "from     messages\n" +
                     "where replied_id is not null\n"+
                     "group by id,from_id,message,delivered_date,replied_id\n" +
                     "order by id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ReplyMessage replyMessage = extractEntity(resultSet);
                list.add(replyMessage);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<ReplyMessage> gettConvo(long id_a,long id_b) {
        ArrayList<ReplyMessage> list = new ArrayList<ReplyMessage>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select *\n" +
                     "from messages\n" +
                     "where ((from_id="+id_a +"and to_id="+id_b+") or (from_id="+id_b+" and to_id="+id_a+"))\n");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ReplyMessage entity = extractEntityGeneral(resultSet);
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Iterable<ReplyMessage> gettConvoPaged(long id_a,long id_b,long lastID,int pageSize) {
        ArrayList<ReplyMessage> list = new ArrayList<ReplyMessage>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select *\n" +
                     "from messages\n" +
                     "where ((from_id="+id_a +"and to_id="+id_b+") or (from_id="+id_b+" and to_id="+id_a+"))\n" +
                     "and id<"+lastID+
                     "\norder by delivered_date desc limit "+pageSize);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ReplyMessage entity = extractEntityGeneral(resultSet);
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<ReplyMessage> gettConvoBetweenDates(long id_a, long id_b, LocalDate date1, LocalDate date2) {
        ArrayList<ReplyMessage> list = new ArrayList<ReplyMessage>();
        String date1String="'"+date1.toString()+" 00:00:00"+"'";
        String date2String="'"+date2.toString()+" 23:59:59"+"'";

        try (Connection connection = DriverManager.getConnection(url, username, password);

             PreparedStatement statement = connection.prepareStatement("select *\n" +
                     "from messages\n" +
                     "where (from_id="+id_b+" and to_id="+id_a+") and (delivered_date>="+date1String+" and delivered_date<="+date2String+")\n" +
                     "order by delivered_date");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ReplyMessage entity = extractEntityGeneral(resultSet);
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ReplyMessage extractEntityGeneral(ResultSet resultSet) throws SQLException {
        ArrayList<Utilizator> toUsers=new ArrayList<>();
        Utilizator fromUser=null;
        Long id = resultSet.getLong("id");
        Long fromID = resultSet.getLong("from_id");
        Long toID = resultSet.getLong("to_id");
        String message = resultSet.getString("message");
        String createdDateString = resultSet.getString("delivered_date");
        LocalDateTime createdDate = LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);
        Utilizator toCachedUser=cachedUsers.get(toID);
        if(toCachedUser==null) {
            toCachedUser=repoUsers.findOne(toID).get();
            cachedUsers.put(toCachedUser.getId(),toCachedUser);
            toUsers.add(toCachedUser);
        }
        else{
            toUsers.add(toCachedUser);
        }
        Utilizator fromCachedUser=cachedUsers.get(fromID);
        if(fromCachedUser==null){
            fromCachedUser=repoUsers.findOne(fromID).get();
            cachedUsers.put(fromCachedUser.getId(),fromCachedUser);
            fromUser=fromCachedUser;
        }
        else{
            fromUser=fromCachedUser;
        }

        ReplyMessage mess;
        Message repliedMessage=null;
        if(resultSet.getObject("replied_id")!=null){
            long repliedID= resultSet.getLong("replied_id");
            Message cachedRepliedMessage=cachedMessages.get(repliedID);
            if(cachedRepliedMessage==null){
                cachedRepliedMessage=repoMessages.findOne(repliedID).get();
                cachedMessages.put(cachedRepliedMessage.getId(),cachedRepliedMessage);
                repliedMessage=cachedRepliedMessage;
            }
            else{
                repliedMessage=cachedRepliedMessage;
            }
            mess=new ReplyMessage(fromUser,toUsers,message,repliedMessage);
            mess.setDate(createdDate);
            mess.setId(id);
        }
        else{
            mess=new ReplyMessage(fromUser,toUsers,message,null);
            mess.setDate(createdDate);
            mess.setId(id);
        }
        return mess;
    }


    public ReplyMessage getLastMessage(long id_a,long id_b) {
        ArrayList<ReplyMessage> list = new ArrayList<ReplyMessage>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select *\n" +
                     "from messages\n" +
                     "where ((from_id="+id_a +"and to_id="+id_b+") or (from_id="+id_b+" and to_id="+id_a+"))\n" +
                     "order by delivered_date desc\n"+
                     "limit 1");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                return extractEntityGeneral(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Page<ReplyMessage> findAllConversationPaged(Pageable pageable, long userA,long userB,long lastID){
        return new PageImplementation<ReplyMessage>(pageable, StreamSupport.stream(this.gettConvoPaged(userA,userB,lastID,pageable.getPageSize()).spliterator(),false));
    }

}
