package org.openjfx.LiveChatApp;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;


public class Client {
	
	 //function that allows a call to function when message received from other end.
	private Consumer<Serializable> displayMsgFunction;
	private ClientThread clientThread = new ClientThread(); //creates a thread
	
	private int port;
	private String ip;


	public Client(String ip, int port, Consumer<Serializable> function) {
		this.displayMsgFunction = function;
		clientThread.setDaemon(true);
		this.port = port;
		this.ip = ip;
		
	}


	protected String getIP() {
		return ip;
	}


	protected int getPort() {
		// TODO Auto-generated method stub
		return port;
	}
	
	//starts the thread and causes the run() method to be invoked by JVM
	public void start() throws Exception{
		clientThread.start(); 
	}
	
	//when invoked, socket is closed.
	public void close() throws Exception{
		clientThread.socket.close();
	}
	
	//write the String Object to ObjectOutputStream
	public void send(Serializable data) throws Exception {
		clientThread.out.writeObject(data);
	}
	

	
	private class ClientThread extends Thread{
		
		private Socket socket; 
		private ObjectOutputStream out;
		
		@Override
		public void run() {
			try(
				ServerSocket server = null;
				Socket socket =  new Socket(getIP(), getPort() ); //connect to the server
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream() );
				//reads input stream object from specified socket
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()); 
					){
				
				this.socket = socket;
				this.out = outputStream;
				socket.setTcpNoDelay(true);
				
				while(true) {
					
					//Reads Object from ObjectInputStream and assign to the data reference of type Serializable
					//Then uses variable as parameter to invoke displayMsgFunction which will append
					// String object received from Server to Client's TextArea
					Serializable data = (Serializable) inputStream.readObject(); 
					displayMsgFunction.accept(data);
				}
				
				
			}
			catch (Exception ex) {
				displayMsgFunction.accept("Connection closed");
			}
		}

		
	}

}
