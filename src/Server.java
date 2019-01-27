// Prakhar Sapre
// 1001514586

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The class implements the Server functionality to send and receive data from
 * client
 * 
 * @author Prakhar
 *
 */
public class Server extends JFrame implements ActionListener {

	// Declare global variables
	static JTextArea txtMessageBox;
	static Socket socket = new Socket();
	static DataInputStream dis = null;
	DataOutputStream dos;
	JPanel panel;
	JScrollPane scroll;
	JButton btnStopServer;
	Queue<QueuePojo> queue;
	static List<ClientHandler> clientList = new ArrayList<ClientHandler>();
	 
	// Server constructor will create the GUI for server to display the messages
	// received from client
	public Server() {
		this.setTitle("Server");
		this.setSize(800, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		queue=new ConcurrentLinkedQueue<QueuePojo>();
		// Textarea to display the messages received from the client
		txtMessageBox = new JTextArea();
		txtMessageBox.setBounds(50, 50, 750, 400);
		txtMessageBox.setEditable(false);

		// Button to stop the server
		btnStopServer = new JButton("Stop Server");
		btnStopServer.setBounds(100, 600, 80, 50);
		btnStopServer.addActionListener(this);
		add(btnStopServer, BorderLayout.SOUTH);

		// This will give the scroll functionality to the textarea if the messages go
		// beyond the textarea limit.
		scroll = new JScrollPane(txtMessageBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.CENTER);

		this.setVisible(true);
	}

	/**
	 * This action listener method will listen to the button clicks and stop the
	 * server when the stop server button is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			//Close all connections for all clients
			for (int i = 0; i < clientList.size(); i++) {
				socket.close();
				dis.close();
			}
			System.exit(0);
		} catch (IOException e1) {
		}

	}

	/**
	 * The main method will create a new server object and once the client is
	 * connected to the server it will spawn a new client thread to perform the
	 * transfer of messages.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// create server object
		Server server = new Server();
		txtMessageBox.append("Server started.\n");
		ServerSocket serverSocket = new ServerSocket(5000);
		String clientName;

		// This loop will keep listening for new client connections and create a thread
		// for every client that connects to the server
		while (true) {
			try {
				// this will keep listening for client connections and accept any incoming
				// connections
				socket = serverSocket.accept();

				// check if socket is closed or not
				if (!socket.isClosed()) {
					// read the client name received from client and display in the textarea.
					dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					clientName = dis.readUTF();
					txtMessageBox.append("\nClient name " + clientName + " is connected.\n");

					// create a new thread and initialize it to the clienthandler class
					ClientHandler clientHandler = new ClientHandler(socket, dis, txtMessageBox, clientName,server.queue);
					clientList.add(clientHandler);
					Thread t = new Thread(clientHandler);
					t.setName(clientName);
					t.start();
				}

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

		}

	}
	

}
