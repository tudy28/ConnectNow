package socialnetwork.domain;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class Event extends Entity<Long>{
    private Utilizator owner;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String name;
    private String description;

    public Event(Utilizator owner, LocalDateTime startDate, LocalDateTime endDate, String name, String description){
        this.owner=owner;
        this.startDate=startDate;
        this.endDate=endDate;
        this.name = name;
        this.description=description;
    }

    public Utilizator getOwner(){
        return owner;
    }

    public void setOwner(Utilizator owner) {
        this.owner = owner;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    @Override
    public String toString(){
        return "\tEvent: "+ name +"\n\tStarting on "+startDate.format(Constants.DATE_TIME_FORMATTER_HM)+"\n\tOwner: "+owner.getFirstName()+" "+owner.getLastName()+"\n\n";
    }






}
