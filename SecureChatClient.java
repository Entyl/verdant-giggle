import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

	public static final int PORT = 8765;

	ObjectOutputStream myWriter;
	ObjectInputStream myReader;
	JTextArea outputArea;
	JLabel prompt;
	JTextField inputField;
	String myName, serverName;
	Socket connection;
	SymCipher cipher = null;	//cipher
	
	public SecureChatClient ()	throws IOException
	{
		try 
		{
			//NUMBER 1
			serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
			InetAddress addr = InetAddress.getByName(serverName);
			connection = new Socket(addr, PORT);   // Connect to server with, socket
			//NUMBER 2
			myWriter = new ObjectOutputStream( connection.getOutputStream() );
			myWriter.flush();
			//NUMBER 3
			myReader = new ObjectInputStream( connection.getInputStream() );	//check
			//NUMBER 4
			BigInteger e = (BigInteger) myReader.readObject();	//read public key e
			//NUMBER 5
			BigInteger n = (BigInteger) myReader.readObject();	//read mod value n
			//NUMBER 6 & 7
			String cipherType = (String) myReader.readObject();	//reads cipher type as string
			if( cipherType.equals("Sub") )
			{
				System.out.print("Substitute: ");
				cipher = new Substitute();
			}
			else
			{
				System.out.print("Add128: ");
				cipher = new Add128();
			}
			//Number 8
			BigInteger key = new BigInteger(1, cipher.getKey());	//makes positive BigInteger representation
			System.out.print(key + "\n");
			//Number 9
			BigInteger encryptedKey = key.modPow(e, n);	//encrypts key
			myWriter.writeObject( encryptedKey );
			myWriter.flush();
			//Number 10
			myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
			sendToServer( myName );	//COMMENTING OUT LINE 60 and UNCOMMENTING LINES 61-62 WILL STOP THE OUTPUT OF STEP 2(OUTPUT OF NOMRLA MESSAGE, OUTPUT OF BYTES AND OUTPUT OF ENCRYPTION)
			/*myWriter.writeObject( cipher.encode( myName ) );
			myWriter.flush();*/
			//Number 11
			this.setTitle(myName);	  // Set title to identify chatter
		
			Box b = Box.createHorizontalBox();  // Set up graphical environment for
			outputArea = new JTextArea(8, 30);  // user
			outputArea.setEditable(false);
			b.add(new JScrollPane(outputArea));
			
			outputArea.append("Welcome to the Chat Group, " + myName + "\n");	//NOTE: THIS IS NOT SENT TO THE SERVER BECAUSE IT IS ONLY FOR THE USER THAT JUST LOGGED IN TO SEE

			inputField = new JTextField("");  // This is where user will type input
			inputField.addActionListener(this);

			prompt = new JLabel("Type your messages below:");
			Container c = getContentPane();

			c.add(b, BorderLayout.NORTH);
			c.add(prompt, BorderLayout.CENTER);
			c.add(inputField, BorderLayout.SOUTH);

			Thread outputThread = new Thread(this);  // Thread is to receive strings
			outputThread.start();					// from Server
			
			//NUMBER 14
			addWindowListener(	
				new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)	
					{ 
						sendToServer("CLIENT CLOSING");	//COMMENTING OUT LINE 92 AND UNCOMMENTING LINES 93-94 WILL STOP THE OUTPUT OF STEP 2(OUTPUT OF NOMRLA MESSAGE, OUTPUT OF BYTES AND OUTPUT OF ENCRYPTION)
						/*myWriter.writeObject( cipher.encode("CLIENT CLOSING") );
						myWriter.flush();*/	
						System.exit(0);
					}
				}
			);	
			
			setSize(500, 200);
			setVisible(true);
			
		}
		catch (Exception e)
		{
			System.out.println("Problem starting client!");
		}
	}

	public void run()	
	{
		while (true)
		{
			try 
			{
				//NUMBER 13 & DECRYPTION
				byte[] byteMessage = (byte[]) myReader.readObject();
				
				BigInteger intMessage = new BigInteger(byteMessage);	//3.1
				
				String stringMessage = cipher.decode( byteMessage );	//3.3
				BigInteger decryptedMessage = new BigInteger( stringMessage.getBytes() );	//3.2
				
				System.out.println("\nArray of Bytes: " + intMessage + "\tDecrypted Bytes: " + decryptedMessage + "\tOriginal String: " + stringMessage);	//for requirement 3
				outputArea.append(stringMessage +"\n");	//to print to actual chat
			 }
			 catch (Exception e)
			 {
				System.exit(0);	//falls into this code whenever the sentinel value has been sent
			 }
		}
	}
	
	public void actionPerformed(ActionEvent e)	
	{
		String fullMessage = myName + ":" + e.getActionCommand();	//2.1, NOTE: the new message that will be encrypted includes the myName and colon to tell everyone who the message is coming from
		inputField.setText("");
		sendToServer(fullMessage);
	}											 	

	public void sendToServer(String fullMessage)
	{
		//NUMBER 12 & ENCRYPTION
		byte[] byteMessage = fullMessage.getBytes();	
		BigInteger intMessage = new BigInteger(byteMessage);	//2.2
		
		byte[] encryptedBytes = cipher.encode( fullMessage );	
		BigInteger encryptedMessage = new BigInteger(encryptedBytes);	//2.3
		
		System.out.println("\nOriginal Message: " + fullMessage + "\tArray of Bytes: " + intMessage + "\tEncrypted Bytes: " + encryptedMessage);	//for requirement 2
		try
		{	
			myWriter.writeObject( cipher.encode( fullMessage ) );
			myWriter.flush();
		}
		catch( IOException ex )
		{
			System.out.println("ERROR sending message");
		}
	}
	
	/*public void sentToServer(BigInteger key)
	{
		String fullMessage = key.toString();	//to print out, but key is to send
		
		byte[] byteMessage = fullMessage.getBytes();	
		BigInteger intMessage = new BigInteger(byteMessage);	//2.2
		
		byte[] encryptedBytes = cipher.encode( fullMessage );	
		BigInteger encryptedMessage = new BigInteger(encryptedBytes);	//2.3
		
		inputField.setText("");
		System.out.println("\nOriginal Message: " + fullMessage + "\tArray of Bytes: " + intMessage + "\tEncrypted Bytes: " + encryptedMessage);	//for requirement 2
			
		try
		{	
			myWriter.writeObject( cipher.encode( fullMessage ) );
			myWriter.flush();
		}
		catch( IOException ex )
		{
			System.out.println("ERROR sending message");
		}
	}*/
	
	public static void main(String [] args)	throws IOException
	{
		 SecureChatClient JR = new SecureChatClient();
		 JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}


