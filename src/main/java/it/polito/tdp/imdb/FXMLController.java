/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.imdb;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimili"
    private Button btnSimili; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimulazione"
    private Button btnSimulazione; // Value injected by FXMLLoader

    @FXML // fx:id="boxGenere"
    private ComboBox<String> boxGenere; // Value injected by FXMLLoader

    @FXML // fx:id="boxAttore"
    private ComboBox<Actor> boxAttore; // Value injected by FXMLLoader

    @FXML // fx:id="txtGiorni"
    private TextField txtGiorni; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doAttoriSimili(ActionEvent event) {
    	this.txtResult.clear();
    	Actor partenza = this.boxAttore.getValue();
    	
    	if(partenza.equals(null)) {
    		this.txtResult.appendText("Devi prima selezionare un attore");
    		return;
    	}
    	
    	List<Actor> res = model.getAttoriSimili(partenza);
    	this.txtResult.appendText("Hai selezionato l'attore: " + partenza +" e gli attori raggiungibili sono: \n\n");
    	
    	for(Actor a: res) {
    		this.txtResult.appendText(a +"\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	String genere = this.boxGenere.getValue();
    	if(genere == null) {
    		this.txtResult.setText("Prima seleziona un genere");
    		return;
    	}
    	
    	this.model.creaGrafo(genere);
    	this.txtResult.appendText("GRAFO CREATO \n\n");
    	this.txtResult.appendText("#vertici: " + this.model.getNVertici() +"\n");
    	this.txtResult.appendText("#archi: " + this.model.getNArchi());
    	
    	List<Actor> vx = new LinkedList<>();
    	for(Actor a: model.getVertici()) {
    		vx.add(a);
    	}
    	
    	Collections.sort(vx, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {

				return o1.getLastName().compareTo(o2.getLastName());
			}
    		
    	});
    	
    	this.boxAttore.getItems().addAll(vx);
    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	this.txtResult.clear();
    	if(this.boxGenere.getValue() == null) {
    		this.txtResult.setText("Prima crea il grafo");
    		return;
    	}
    	
    	if(model.getGrafo() == null) {
    		this.txtResult.setText("Prima crea il grafo");
    		return;
    	}
    	
    	int n ;
    	try {
    		n = Integer.parseInt(this.txtGiorni.getText());
    	}catch(NumberFormatException nfe) {
    		this.txtResult.setText("Inserire prima un numero intero positivo");
    		return;
    	}
    	
    	model.setAttori();
    	model.init();
    	model.run(n);
    	
    	this.txtResult.appendText("Simulazione effettuata\n\n");
    	this.txtResult.appendText("Attori intervistati: " + model.getNumeroIntervistati() +"\n");
    	this.txtResult.appendText("Giornate di riposo: " +model.getGiornateRiposo());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimili != null : "fx:id=\"btnSimili\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGenere != null : "fx:id=\"boxGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxAttore != null : "fx:id=\"boxAttore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGiorni != null : "fx:id=\"txtGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.boxGenere.getItems().addAll(this.model.getGeneri());
    }
}
