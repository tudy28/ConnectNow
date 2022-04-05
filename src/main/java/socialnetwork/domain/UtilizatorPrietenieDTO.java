package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class UtilizatorPrietenieDTO extends Entity<Long> {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime date;

    public UtilizatorPrietenieDTO(String email,String firstName,String lastName,LocalDateTime date){
        this.email=email;
        this.firstName=firstName;
        this.lastName=lastName;
        this.date=date;
    }

    public String getEmail(){
        return email;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public LocalDateTime getDate(){
        return date;
    }

    @Override
    public String toString(){
        return firstName+" "+lastName+"\n         Friendship created on: "+ date.format(Constants.DATE_TIME_FORMATTER_NICE);
    }

}
