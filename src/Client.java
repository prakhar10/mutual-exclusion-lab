// Prakhar Sapre
// 1001514586

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * This class implements the client functionality to send and receive data from server
 * @author Prakhar
 *
 */
public class Client extends JFrame implements ActionListener {

	//Declare global variables
	static JTextArea txtClientMessages;
	static JTextArea txtMessage;
	static JButton btnSendMessage;
	static JButton btnUploadNumber;
	static JButton btnKillClient;
	static Socket clientSocket;
	static DataInputStream dis;
	static DataOutputStream dos;
	static String clientName;
	int min = 3, max = 10;

	//Constructor will create the GUI for the client to send name, upload integer and kill client
	public Client() {
		// TODO Auto-generated constructor stub
		this.setTitle("Client");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		//Textarea for displaying the name of the client and other messages received from server
		txtClientMessages = new JTextArea();
		txtClientMessages.setBounds(50, 50, 650, 300);
		txtClientMessages.setEditable(false);
		add(txtClientMessages);

		//Textarea for taking client name as input from user
		txtMessage = new JTextArea();
		txtMessage.setBounds(50, 380, 530, 120);
		txtMessage.setEditable(true);
		add(txtMessage);

		//Send message button to send the client name to server 
		btnSendMessage = new JButton("Send");
		btnSendMessage.setBounds(600, 380, 100, 30);
		btnSendMessage.addActionListener(this);
		add(btnSendMessage);

		//Upload number button to send the post http message along with the integer to the server
		btnUploadNumber = new JButton("Upload Number");
		btnUploadNumber.setBounds(600, 420, 100, 30);
		btnUploadNumber.addActionListener(this);
		add(btnUploadNumber);

		//Kill client button to kill the client and notify the server which client is killed
		btnKillClient = new JButton("Kill Client");
		btnKillClient.setBounds(600, 460, 100, 30);
		btnKillClient.addActionListener(this);
		add(btnKillClient);
		
		//this will display the gui
		this.setVisible(true);
	}

	/**
	 * This is the action listener method which will listen to button click events and according to the 
	 * action command name will send name, upload number or kill the client
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		try {

			if (arg0.getActionCommand().equals("Send")) {
				sendMessage();
			} else if (arg0.getActionCommand().equals("Upload Number")) {
				uploadNumber();
			} else {
				killClient();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * The killClient method will send a message to the server with the client name that is killed and shutdown the client thread
	 * accordingly
	 */
	public void killClient() {

		try {
			dos.writeUTF("Kill " + clientName);
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * The sendMessage method will fetch the input given by the user from the textbox and store it as the client name.
	 * It will then send the name to server and will also display in the clients textarea.
	 */
	public void sendMessage() {
		clientName = txtMessage.getText().trim();
		try {
			dos.writeUTF(clientName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		txtClientMessages.append("Client name is " + clientName + "\n");
		txtMessage.setEnabled(false);
		btnSendMessage.setEnabled(false);
		txtMessage.setText("");

	}

	/**
	 * The uploadNumber method will generate a number between 5 and 15 and send it to server.
	 * Here the min is 5 and max is 15.
	 */
	public void uploadNumber() {
		Random rand = new Random();
		try {

			dos.writeUTF(
					getHttpMessage("Server will sleep for " + (rand.nextInt((max - min) + 1) + min) + " seconds.\n"));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * The main method will create an object of the client so that the gui can be created and then create a new socket
	 *  and connect to port 5000. 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		//Create a new Client object
		Client client = new Client();
		int port = 5000;
		try {
			clientSocket = new Socket("127.0.0.1", port);
			dos = new DataOutputStream(clientSocket.getOutputStream());

		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());

		}
		

		//The client will keep on running in this loop to fetch any incoming messages from the server. 
		// if the client is killed or the server is stopped then it will break from the loop and kill the 
		// connections
		while (true) {

			try {
				dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				String message = dis.readUTF();
				
				//if this message is received from the server then it will break from the loop
				if (message.contains("Killed")) {
					break;
					
				} else {
					//After waiting for the said number of time , the server sends the GET http message to client. Here the client parses the message and displays 
					// for how many seconds did the server wait.
					txtClientMessages.append("\n " + message.substring(message.indexOf("message:"), message.length()));
				}

			} catch (IOException e) {
				txtClientMessages.append("\n Server has stopped. Connection closed.");
				break;
			}
		}
	}

	/**
	 * This method will return the POST http message string with the message which client will send to server.
	 * @param msg
	 * @return
	 */
	public String getHttpMessage(String msg) {

		return "POST Http/1.1 \n" + "Host: www.uta.com \n" + "User-Agent: Mozilla/5.0 \n"
				+ "Content-type: application/x-www-form-urlencoded \n" + "Content-Length: " + msg.length() + " \n"
				+ "Date:" + new Date() + " \n" + "message: " + msg;
	}

}
