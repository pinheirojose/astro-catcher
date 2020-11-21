package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MainWindowController {

	private Main main;
	private Stage primaryStage;
	@FXML private TextArea nasaapi, type_desc, credit_desc;
	@FXML private DatePicker date;
	@FXML private Button getimage_btn;
	@FXML private ImageView imageview;
	
	public void setMain(Main main, Stage primaryStage) {
		this.main = main;
		this.primaryStage = primaryStage;
	}
	
	/*
	 * initialize() 
	 * 
	 * Sets default values: 
	 * Nasa API to connect
	 * Todays date
	 * 
	 */
	public void initialize() {
		String nasaApi = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
		nasaapi.setText(nasaApi);
		date.setValue(LocalDate.now());
	}
	
	/*
	 * handleAction(Event event)
	 * 
	 * param: Event event
	 * 
	 * Evaluates which button was pressed and triggers actions
	 * 
	 */
	@FXML
	public void handleAction(Event event) throws Exception {
		Button btn = (Button)event.getSource();
		try {
			if (btn.getId().equals("getimage_btn")) {
				downloadImage(getImageURL(getImageProcess()));
			} else if (btn.getId().equals("loadimage_btn")) {
				// TODO:loadImageProcess()
			} else if (btn.getId().equals("exit_btn")){
				primaryStage.close();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * getImageProcess()
	 * 
	 * Triggers process to get the image on a certain day
	 * 
	 */
	private String getImageProcess() throws Exception {
		
		try {
			URL nasaApi = new URL("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY");
	        URLConnection nasa = nasaApi.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(nasa.getInputStream()));
	        String jsonMessage = in.readLine();
	        if(getMediaType(jsonMessage)){
	        	type_desc.setText("Image");
	        	return jsonMessage;
	        }
	       
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return "";
	}
	
	/*
	 * getMediaType(String inputLine)
	 * 
	 * param String jsonMessage 
	 * 
	 * jsonMessage contains the message received from Nasa API request
	 * Evaluates the message looking for the media type
	 */
	private boolean getMediaType(String jsonMessage) {
        String []message = jsonMessage.split(",(?!\\s)");
        String target = "\"media_type\":\"image\"";

        for(int i=0;i<message.length;i++){
            if(message[i].equals(target)){
                return true;
            }
        }
        return false;
	}
	
	/*
	 * getImageURL(String jsonMessage)
	 * 
	 * param: String jsonMessage
	 * Gets image URL to be used to download and display
	 * 
	 */
	public String getImageURL(String jsonMessage){
		String []message = jsonMessage.split(",(?!\\s)");
		String target = "\"hdurl\"";
		
		for(int i=0;i<message.length;i++){
		    String []url = message[i].split(":");
		    if(url[0].equals(target)){
		        String urlClean = url[1]+":"+url[2];
		        urlClean = urlClean.replace("\"","");
		        return urlClean;
		    }
		}
		// Not a URL 
		return "not url";
	}
	 
	/*
	 * downloadImage(String url)
	 * 
	 * param: String url
	 * Receives image URL to download, store and display
	 * 
	 */
	public void downloadImage(String url){
		try(InputStream in = new URL(url).openStream()){
		    String imagePath = "C:\\temp\\" + getTodaysDate() + ".jpg";
		    String changedPath = "file:///C:/temp/" + getTodaysDate() + ".jpg";
		    File f = new File(imagePath);
		    if(f.exists()) {
			    Image image = new Image(changedPath);
			    imageview.setImage(image);
		    }
		    else {
		    	Files.copy(in, Paths.get(imagePath));
			    Image image = new Image(changedPath);
			    imageview.setImage(image);
		    }
		}
		 catch (IOException e) {
			 e.printStackTrace();
		}
	 }
	    
	public String getTodaysDate(){
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
	    LocalDateTime now = LocalDateTime.now();  
	    return dtf.format(now);      
	}
}
