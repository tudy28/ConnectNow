package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReplyMessage extends Message {

    LocalDateTime date;
    Message repliedMessage;
    public ReplyMessage(Utilizator from, ArrayList<Utilizator> to, String message,Message repliedMessage) {
        super(from, to, message);
        date=LocalDateTime.now();
        this.repliedMessage=repliedMessage;
    }

    public Message getRepliedMessage() {
        return repliedMessage;
    }

    @Override
    public String toString(){
        if(repliedMessage==null)
            return super.toString();
        else
            return "    Replied at: "+repliedMessage.getMessage()+"     From: "+repliedMessage.getFrom().getLastName()+" "+repliedMessage.getFrom().getFirstName()+"\n"+
                    super.toString();

    }
}
