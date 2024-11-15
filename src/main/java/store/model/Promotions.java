package store.model;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import store.util.FileDataLoader;

public class Promotions {

    private final List<Promotion> promotions;
    private FileDataLoader fileDataLoader;


    public Promotions(){
        this.fileDataLoader = new FileDataLoader();
        this.promotions = loadPromotionsFromFile();
    }

    public void display(){
        promotions.forEach(Promotion::display);
    }

    public List<Promotion> loadPromotionsFromFile(){
        List<Promotion> promotions = new LinkedList<>();
        try {
            promotions = fileDataLoader.loadDataFromFile("promotions.md", Promotion.class);
        } catch (FileNotFoundException | IllegalArgumentException e) {
            System.err.println("[Error] " + e.getMessage());
        }
        return promotions;
    }

    public Promotion getMatchedPromotion(String value, String field){
        for(Promotion promotion: promotions){
            String extractedValue = promotion.getValueOfTheField(field);
            if(value.equals(extractedValue)){
                return promotion;
            }
        }
        return null;
    }

}
