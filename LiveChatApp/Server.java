package org.openjfx.LiveChatApp;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;


public class Server {
	
	 //function that allows a call to function when message received from other end.
	private Consumer<Serializable> onReceiveCallback;
	private ServerThread serverThread = new ServerThread(); //creates a thread
	private int port;

	
	
	public Server(int port, Consumer<Serializable> onReceiveCallback) {
		this.onReceiveCallback = onReceiveCallback;
		serverThread.setDaemon(true);
		this.port = port;
	}
	
	//starts the thread and causes the run() method to be invoked by JVM
	public void start() throws Exception{
		serverThread.start(); 
	}
	
	public void close() throws Exception{
		serverThread.socket.close();
	}
	
	//send data 
	public void send(Serializable data) throws Exception {
		serverThread.out.writeObject(data);
	}
	
	
	protected int getPort() {
		return port;
	}
	
	private class ServerThread extends Thread{
		
		private Socket socket;
		private ObjectOutputStream out; 
		
		@Override
		public void run() {
			try(
				ServerSocket server = new ServerSocket(getPort()); //Create a server socket on a port
				Socket socket = server.accept(); //listens for connections
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream() );//Create Output Stream to Client
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); //Input Stream from socket
					){
				
				this.socket = socket;
				this.out = out;
				socket.setTcpNoDelay(true);
				
				while(true) {
					
					Serializable data = (Serializable) in.readObject(); //read from input stream
					onReceiveCallback.accept(data);
				}
				
				
			}
			catch (Exception ex) {
				onReceiveCallback.accept("Connection closed");
			}
		}

		
	}

}