package socialnetwork.domain.validators;

import socialnetwork.domain.Event;
import socialnetwork.domain.exceptions.EventValidationException;

import java.time.LocalDateTime;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws EventValidationException {
        String errors = "";
        if(entity.getEndDate().isBefore(entity.getStartDate())){
            errors+="Event cannot have the end date earlier than the start date!\n";
        }
        if(entity.getStartDate().isBefore(LocalDateTime.now())){
            errors+="Event cannot start because the start date has already passed!\n";
        }
        if(errors.length()>0)
            throw new EventValidationException(errors);
    }
}
