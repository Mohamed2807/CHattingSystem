package Chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	  final ServerSocket serverSocket;
	 
	 public Server(ServerSocket serverSocket) {
	        this.serverSocket = serverSocket;
	    }
	 public static void main(String[] args) throws IOException {
			// TODO Auto-generated method stub
		
			 ServerSocket serverSocket = new ServerSocket(1234);
			 System.out.println("Server ready");
		        Server server = new Server(serverSocket);
		       
		        server.startServer();
		       
		        

		}
	  public void startServer() {
	        try {
	        	  while (!serverSocket.isClosed()) {
	        		  Socket socket = serverSocket.accept();
	                  System.out.println("A new User has connected!");
	                  ClientHandler clientHandler = new ClientHandler(socket);
	                  Thread thread = new Thread(clientHandler);	        	  	        
	        	      thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }}
	  
        public void closeServerSocket() {
            try {              
                    serverSocket.close();                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public class ClientHandler implements Runnable {
     	    ArrayList<ClientHandler> clientHandlers = new ArrayList<>();   	   
     	    Socket socket;
     	    BufferedReader bufferedReader;
     	    BufferedWriter bufferedWriter;
     	    String clientUsername; 	    
     	    public ClientHandler(Socket socket) {
     	        try {
     	            this.socket = socket;
     	            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     	            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
     	            this.clientUsername = bufferedReader.readLine();
     	            clientHandlers.add(this);
     	            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
     } catch (IOException e) {
         // Close everything more gracefully.
         closeEverything(socket, bufferedReader, bufferedWriter);
     }
     }
     	    @Override
     	    public void run() {
     	        String messageFromClient; 
     	        while (socket.isConnected()) {
     	            try {
     	                messageFromClient = bufferedReader.readLine();
     	                broadcastMessage(messageFromClient);
     	            } catch (IOException e) {
     	            	 closeEverything(socket, bufferedReader, bufferedWriter);
     	                 break;
     	             }
     	         }
     	     }
     	    
     	    public void broadcastMessage(String messageToSend) {
     	        for (ClientHandler clientHandler : clientHandlers) {
     	            try {
     	        
     	                if (!clientHandler.clientUsername.equals(clientUsername)) {
     	                    clientHandler.bufferedWriter.write(messageToSend);
     	                    clientHandler.bufferedWriter.newLine();
     	                    clientHandler.bufferedWriter.flush();
     	                }
     	            } catch (IOException e) {
     	                closeEverything(socket, bufferedReader, bufferedWriter);
     	            }
     	        }
     	    }
     	    
     	    
     	    public void removeClientHandler() {
     	        clientHandlers.remove(this);
     	        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
     	    }
     	    
     	    
     	    
     	    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
     	    	 removeClientHandler();
     	         try {    	             
     	                 bufferedReader.close();    	                	             
     	                 bufferedWriter.close();     	                  	             
     	                 socket.close();
     	             
     	         } catch (IOException e) {
     	             e.printStackTrace();
     	         }
     	     }
     	            }

}
