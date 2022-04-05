package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message extends Entity<Long> {

    private Utilizator from;
    private ArrayList<Utilizator> to;
    private String message;
    private LocalDateTime date;

    public Message(Utilizator from,ArrayList<Utilizator> to,String message){
        this.from=from;
        this.to=to;
        this.message=message;
        date=LocalDateTime.now();
    }

    public Utilizator getFrom(){
        return from;
    }

    public ArrayList<Utilizator> getTo(){
        return to;
    }

    public String getMessage(){
        return message;
    }

    public LocalDateTime getDate(){
        return date;
    }

    public void setDate(LocalDateTime date){
        this.date=date;
    }

    @Override
    public String toString(){
        return date.format(Constants.DATE_TIME_FORMATTER_NICE)+
                "\n"+getFrom().getFirstName()+" "+getFrom().getLastName()+": "+message+"\n\n";
    }
}
