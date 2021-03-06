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
	@FXML private TextArea nasaapi, type_desc, title_desc, log_desc;
	@FXML private DatePicker date;
	@FXML private Button getimage_btn, exit_btn, loadimage_btn;
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
		nasaapi.setEditable(false); 
		type_desc.setEditable(false); 
		title_desc.setEditable(false); 
		log_desc.setEditable(false);
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
				log_desc.setText("");
				log_desc.setText("Getting Today's Astronomy Picture of the Day");
				String jsonMessage = getImageProcess();
				downloadImage(getImageURL(jsonMessage));
				getImageTitle(jsonMessage);
			} else if (btn.getId().equals("loadimage_btn")) {
				// TODO:loadImageProcess()
				String log = log_desc.getText();
				log = log + "\nTODO: Image Loader still in progress";
				log_desc.setText(log);
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
			URL nasaApi = new URL(formatApiUrl());
	        URLConnection nasa = nasaApi.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(nasa.getInputStream()));
	        String jsonMessage = in.readLine();
	        if(getMediaType(jsonMessage)){
	        	String log = log_desc.getText();
	        	log = log + "\nWe got an Image!";
	        	log_desc.setText(log);
	        	type_desc.setText("Image");
	        	return jsonMessage;
	        }
	       
		} catch (Exception e) {
			e.printStackTrace();
		}
		String log = log_desc.getText();
    	log = log + "\nTo bad... Today we got a video!";
    	log_desc.setText(log);
    	type_desc.setText("Video: Not Supported");
		return "";
	}
	
	/*
	 * formatApiUrl()
	 * 
	 * Takes date entered by user and formats the API String to 
	 * do the request
	 */
	private String formatApiUrl() {
		
		String api = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
		String localdate = dateEntered();
		
		return api + "&date=" + localdate;
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
		    String imagePath = "C:\\temp\\" + dateEntered() + ".jpg";
		    String changedPath = "file:///C:/temp/" + dateEntered() + ".jpg";
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
		    
		    String log = log_desc.getText();
        	log = log + "\nImage Loaded!";
        	log = log + "\nYou can find your image in the path: C:/temp/";
        	log_desc.setText(log);
		}
		 catch (IOException e) {
			 e.printStackTrace();
		}
	 }
	
	/*
	 * getTodaysDate()
	 * 
	 * Get todays date using LocalDateTime
	 */
	public String getTodaysDate(){
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
	    LocalDateTime now = LocalDateTime.now();  
	    return dtf.format(now);      
	}
	
	/*
	 * dateEntered()
	 * 
	 * Get the date entered by user
	 */
	public String dateEntered(){
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
	    LocalDate localdate = date.getValue(); 
	    
	    return dtf.format(localdate);  
	}
	
	/*
	 * getImageTitle(String jsonMessage)
	 * 
	 * Get image title 
	 */
	public void getImageTitle(String jsonMessage){
		String []message = jsonMessage.split(",(?!\\s)");
		
		String target = "\"title\"";
		
		for(int i=0;i<message.length;i++){
		    String []title = message[i].split(":");
		    if(title[0].equals(target) && title.length > 2){
		        String titleClean = title[1]+":"+title[2];
		        titleClean = titleClean.replace("\"","");
		        title_desc.setText(titleClean);
		        break;
		    }
		    if(title[0].equals(target)) {
		    	String titleClean = title[1];
		    	titleClean = titleClean.replace("\"","");
		    	title_desc.setText(titleClean);
		    }
		    
		}
	}
}
