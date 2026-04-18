package pobj.TVA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.UnaryOperator;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Calcul le montant de la TVA, du TTC et du HT en fonction du prix saisi 
 */
public class TVA extends Application{

	private static final int width = 800;
	private static final int height = 600;
	
	// le montant du prix TVA
	private final StringProperty TVA = new SimpleStringProperty();
	// le montant du prix HT 
	private final StringProperty HT = new SimpleStringProperty();
	// le montant du prix TTC 
	private final StringProperty TTC = new SimpleStringProperty();
	// le montant saisi
	private Property<Double> input = new SimpleObjectProperty(0.0);
	// HT ou TTC
	private final StringProperty nameOption = new SimpleStringProperty("HT :");
	// representation du input : HT ou TTC 
	private boolean option = true;  // par defaut HT
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Calcul TVA");
		primaryStage.getIcons().add(new Image("file:resources/ICON.pnj"));
		Scene scene = new Scene(createContent(),width,height);   // ARG A REMPLACER 
        primaryStage.setScene(scene);
        primaryStage.show();
		
	}
	
	private Region createContent() {
		
        VBox results = new VBox(20, createInputRow(), createResultsColumn()); 
        results.setAlignment(Pos.CENTER);
        results.getStylesheets().add(this.getClass().getResource("resources/TVAStylesheet.css").toExternalForm());
        results.getStyleClass().add("border-box");
        return results;
    }
	
	private Node createInputRow() {
		TextField tf = new TextField("");
		tf.getStyleClass().add("input-textfield");
		tf.setOnKeyPressed(evt->{
			if(evt.getCode() == KeyCode.ENTER){
				triggerCalculation();
			}
		});
		TextFormatter<Double> textFormatter = new TextFormatter(new PositiveDoubleStringConverter(), 0, new PositiveDoubleFilter());
        textFormatter.valueProperty().bindBidirectional(input);
        tf.setTextFormatter(textFormatter);
		 
		
		HBox results = new HBox(6, createOptionButton(),createNameOptionLabel(),tf,createCalculationButton());
		results.getStyleClass().add("hbox");
		triggerCalculation();
		results.setAlignment(Pos.CENTER);
		return results;
	}

	/**
	 * bouton de calcul
	 * @return renvoie le bouton 
	 */
	private Node createCalculationButton() {
		Button results = styledButton("Calculer","calculation-button");;
		results.setOnAction(evt->triggerCalculation());
		return results;
	}
	
	/**
	 * bouton pour changer d'option HT ou TTC
	 * @return
	 */
	private Node createOptionButton() {
		Button results = styledButton("<=>","option-button");
		results.setOnAction(evt->setOption());
		return results;
	}
	
	/**
	 * declenche les calculs
	 */
	private void triggerCalculation() {
			setHT();
			setTVA();
			setTTC();
	}
	
	private Node createNameOptionLabel() {
		Label results = styledLabel("","nameOption-label");
		results.textProperty().bind(nameOption);
		return results;
	}
	
	private void setOption() {
		if(option) {
			option = false;
			nameOption.set("TTC :");
		}
		else {
			option = true;
			nameOption.set("HT :");
		}
		triggerCalculation();
	}
	
	
	private Node createResultsColumn() {
		VBox results = new VBox(createHTLabel(),createTVALabel(),createTTCLabel());
		results.getStyleClass().add("results-box");
		results.setAlignment(Pos.CENTER);
		return results;
	}
	
	/**
	 * resultats HT
	 * @return les resultats
	 */
	private Node createHTLabel() {
		Label results = styledLabel("","HT-label");
		results.textProperty().bind(HT);
		return results;
	}
	
	/**
	 * resultats TVA
	 * @return les resultats
	 */
	private Node createTVALabel() {
		Label results = styledLabel("","TVA-label");
		results.textProperty().bind(TVA);
		return results;
	}
	
	/**
	 * resultats TTC
	 * @return les resultats
	 */
	private Node createTTCLabel() {
		Label results = styledLabel("","TTC-label");
		results.textProperty().bind(TTC);
		return results;
	}
	
	
	
	/**
	 * Calcule la valeur HT
	 */
	private void setHT() {
		 // selon option calcul ou valeur de input
		double val = input.getValue();
		if(!option) {
			val /=1.2;
		}
		
		HT.set("HT : " + round(val,2) + " €");
	}
	
	/**
	 * Calcule la valeur TTC
	 */
	private void setTTC() {
		Double val = input.getValue();
		if(option) {
			val *=1.2;
		}
		
		TTC.set("TTC : " + round(val,2) + " €");
	}
	 
	/**
	 * Calcule la valeur TVA 
	 */
	private void setTVA() {
		Double val = input.getValue();
		if(option) {
			val = (val*1.2) - val;
		}
		else {
			val = val - (val/1.2);
		}
		
		
		TVA.set("TVA (20%) : " + round(val,2) + " €");
	}
	
	
	
	
	private Label styledLabel(String contents, String classSelector) {
        Label results = new Label(contents);
        results.getStyleClass().add(classSelector);
        return results;
    }
	
	private Button styledButton(String contents, String classSelector) {
        Button results = new Button(contents);
        results.getStyleClass().add(classSelector);
        return results;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	
	
	/**
	 * Arrondit le nombre a i chiffres apres la virgule 
	 * @param value le nombre a arrondir 
	 * @param places le nombre de chiffres apres la virgule a garder
	 * @return la valeur donnee arrondie
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Convertisseur n acceptant que des valeurs positives
	 */
	public class PositiveDoubleStringConverter extends DoubleStringConverter {

        @Override
        public Double fromString(String value) {
            Double result = super.fromString(value);
            if (result < 0) {
                throw new RuntimeException("Negative number");
            }
            return result;
        }

        @Override
        public String toString(Double value) {
            if (value < 0) {
                return "0";
            }
            return super.toString(value);
        }

    }
    
	/**
	 * Filtre pour valeurs décimales 
	 */
	public class PositiveDoubleFilter implements UnaryOperator<TextFormatter.Change> {

        @Override
        public TextFormatter.Change apply(TextFormatter.Change change) {
            if (change.getControlNewText().matches("([0-9]*)?([.]?[0-9]*)?")) {
                return change;
            }
            return null;
        }
    }

	
}