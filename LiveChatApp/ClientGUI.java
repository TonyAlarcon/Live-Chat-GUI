package org.openjfx.LiveChatApp;





import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ClientGUI extends Application{
	
	private TextArea textArea = new TextArea();
	private Client connection =  createClient();
	private TextField input = new TextField();
	private Button sendBt = new Button("Send");
	private Button endChatBt = new Button("End Chat");
	private Button saveBt = new Button("Save Chat");
	
	
	private Parent createGUI() {
		
		textArea.setPrefHeight(400);
		textArea.setDisable(true);
		input.setPrefWidth(350);
		sendBt.setPrefWidth(90);
		
		HBox topHbox = new HBox(5, endChatBt, saveBt);
		HBox hBox = new HBox(5, input, sendBt);
		VBox vBox = new VBox(5, topHbox, textArea, hBox);
		topHbox.setAlignment(Pos.CENTER);
		vBox.setPrefSize(450, 450);
		
		sendBt.setOnAction(new TransmitMessageHandler());
		input.setOnAction(new TransmitMessageHandler());
		endChatBt.setOnAction(new EndChatHandler());
		saveBt.setOnAction(new SaveToFileHandler());
		
		
		return vBox;
	}
	
	class SaveToFileHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			FileChooser filechooser = new FileChooser();
			filechooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
			Window primaryStage = null;
			File file = filechooser.showSaveDialog(primaryStage);
	        String content = textArea.getText();
	          try {
	              FileWriter fileWriter;
	               
	              fileWriter = new FileWriter(file);
	              fileWriter.write(content);
	              fileWriter.close();
	          } catch (IOException ex) {
	        	  ex.printStackTrace();
	          }
		}
	}
	
	class EndChatHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			try {
				connection.close();
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}
	
	class TransmitMessageHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			String message = "Client: "; 
			message += input.getText(); //append text in TextField to String variable
			input.clear(); //clears the TextField
			
			textArea.appendText(message + "\n"); //Displays String variable to TextArea of Client
			
			//invokes send() method which send String Variable to server
			try {
				connection.send(message); 
			} catch (Exception ex) {
				
				textArea.appendText("Exception: Failed To Send\n"); 
			}
		}
	}
	
	//When application is initialized, start the client thread
	@Override 
	public void init() throws Exception{
		connection.start();
	}
	
	//Main entry point for javaFX applications
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Client Chat"); //Sets Title
		primaryStage.setScene( new Scene( createGUI() ) ); //Sets Scene onto Stage
		primaryStage.show();
		
		String connected = "Connection Established. Client IP: "+ connection.getIP()+ "\n";
		textArea.appendText(connected);
		try {
			connection.send(connected);
		} catch (Exception e) {
			
			textArea.appendText("Exception: Failed To Connect\n");
		}
	}
	
	//when Client Chat Application is closed, close the client connection
	@Override
	public void stop() throws Exception{
		connection.close();
	}
	
	
	private Client createClient() {
		return new Client("192.168.1.206", 8000, data -> {
			Platform.runLater(()-> {
				textArea.appendText(data.toString() + "\n");
			});
		});
	}
	
	   public static void main(String[] args) {
	        launch(args);
	    }

}