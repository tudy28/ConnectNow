package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MessageDbRepository extends AbstractDbRepository<Long,Message> {
    private UserDbRepository repoUsers;
    String dbName;
    String url;
    String username;
    String password;
    public MessageDbRepository(String dbName, String url, String username, String password, Validator<Message> validator,UserDbRepository repoUsers) {
        super(dbName, url, username, password, validator);
        this.repoUsers=repoUsers;
        this.dbName=dbName;
        this.url=url;
        this.username=username;
        this.password=password;
    }


    @Override
    public Message extractEntity(ResultSet resultSet) throws SQLException {
        ArrayList<Utilizator> toUsers=new ArrayList<>();
        Utilizator fromUser=null;
        Long id = resultSet.getLong("id");
        Long fromID = resultSet.getLong("from_id");
        Array toID_list = resultSet.getArray("to_list");
        String message = resultSet.getString("message");
        String createdDateString = resultSet.getString("delivered_date");
        LocalDateTime createdDate = LocalDateTime.parse(createdDateString, Constants.DATE_TIME_FORMATTER);
        Long[] listTo=(Long[])toID_list.getArray();
        for(Long idTo:listTo)
            repoUsers.findOne(idTo).ifPresent(toUsers::add);
        if(repoUsers.findOne(fromID).isPresent())
            fromUser=repoUsers.findOne(fromID).get();
        Message mess=new Message(fromUser,toUsers,message);
        mess.setDate(createdDate);
        mess.setId(id);

        return mess;
    }


    @Override
    public String createEntityAsQueryInsert(Message message) {
        long lastID=generateNewID();
        String messageSent="'"+message.getMessage()+"'";
        String createdDate="'"+message.getDate().format(Constants.DATE_TIME_FORMATTER)+"'";
        String query="INSERT INTO messages(\n" +
                "id, from_id, to_id, message, delivered_date)\n" +
                "VALUES ";
        for(Utilizator user:message.getTo()){
            query+="("+lastID+","+message.getFrom().getId()+","+user.getId()+","+messageSent+","+createdDate+"),";
        }
        query = query.substring(0, query.length()-1) + ";";
        return query;
    }

    @Override
    public String createEntityAsQueryDelete(Long messageID) {
        return "DELETE FROM messages WHERE id="+messageID;
    }

    @Override
    public String createEntityAsQueryFindOne(Long messageID) {
        return "SELECT id, from_id,ARRAY_AGG(to_id) to_list, message,delivered_date\n" +
                "from     messages\n" +
                "where id="+messageID+"\n"+
                "group by id,from_id,message,delivered_date\n" +
                "order by id";

    }

    @Override
    protected Iterable<Message> gettAllFromDatabase() {
        ArrayList<Message> list = new ArrayList<Message>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id, from_id,ARRAY_AGG(to_id) to_list, message,delivered_date\n" +
                     "from     messages\n" +
                     "where replied_id is null\n"+
                     "group by id,from_id,message,delivered_date\n" +
                     "order by id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Message message = extractEntity(resultSet);
                list.add(message);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }




}
