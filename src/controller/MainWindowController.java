package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

public class MainWindowController {

	private Main main;
	@FXML private TextArea nasaapi, type_desc;
	@FXML private DatePicker date;
	@FXML private Button getimage_btn;
	
	
	public void setMain(Main main) {
		this.main = main;
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
				getImageProcess();
			} else if (btn.getId().equals("loadimage_btn")) {
				// loadImageProcess()
			} else {
				// exitProcess() 
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
	private void getImageProcess() throws Exception {
		
		try {
			URL nasaApi = new URL("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY");
	        URLConnection nasa = nasaApi.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(nasa.getInputStream()));
	        String inputLine = in.readLine();
	        if(getMediaType(inputLine)){
	        	type_desc.setText("Image");
	        }
        
		} catch (Exception e) {
			e.printStackTrace();
		}
        
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
}
