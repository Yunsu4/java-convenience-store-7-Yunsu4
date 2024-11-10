package store.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Promotion {

    private final Map<String, Object> promotionDetails;

    public Promotion(Map<String, Object> promotionDetails){
        this.promotionDetails = promotionDetails;
    }

    public void display(){
        for(String key: promotionDetails.keySet()){
            String value = (String) promotionDetails.get(key);
            System.out.print(value+" ");
        }
        System.out.println();
    }

    public String getValueOfTheField(String field) {
        return (String) promotionDetails.get(field);
    }

    public boolean isInPromotionPeriod(LocalDateTime currentDateTime){
        LocalDate startDate = parseLocalDateTime("start_date");
        LocalDate endDate = parseLocalDateTime("end_date");

        LocalDate currentDate = currentDateTime.toLocalDate();

        return !startDate.isAfter(currentDate) && !endDate.isBefore(currentDate);
    }

    private LocalDate parseLocalDateTime(String field){
        String value = getValueOfTheField(field);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(value, formatter);
    }
}
