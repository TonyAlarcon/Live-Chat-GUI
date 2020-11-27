package org.openjfx.LiveChatApp;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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

public class ServerGUI extends Application{
	
	private TextArea textArea = new TextArea();
	private Server connection =  createServer();
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
			String message = "Server: "; 
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
	

	
	@Override 
	public void init() throws Exception{
		connection.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Server Chat");
		primaryStage.setScene( new Scene( createGUI() ) );
		primaryStage.show();
		textArea.appendText("Server started at " + new Date() + "\nWaiting on client request to connect\n");
		
	}
	
	//Close the connection when we exit the application
	@Override
	public void stop() throws Exception{
		connection.close();
	}
	
	private Server createServer() {
		return new Server(8000, data -> {
			Platform.runLater(()-> {
				textArea.appendText(data.toString() + "\n");
			});
		});
				
	}
	

	
	   public static void main(String[] args) {
	        launch(args);
	    }

}