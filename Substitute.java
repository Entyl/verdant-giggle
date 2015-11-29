import java.util.*;

public class Substitute implements SymCipher 
{
	byte[] key;	//256 byte key
	byte[] reverseKey;	//reverse key to decode
	
	public Substitute() //parameterless constructor
	{
		key = new byte[256];
		for(int i = 0; i < key.length; i++)
		{
			key[i] = (byte)(i - 128);	//inserts each element into the array and subtracts 128 to get the negative numbers
		}
		
		//shuffle the key so that it is now randomized substitutions
		Random randomIndex = new Random();
		for(int i = 0; i < key.length; i++)
		{
			byte temp = key[i];
			int index = randomIndex.nextInt(255);
			key[i] = key[index];
			key[index] = temp;
		}
		
		createReverseKey();	
	}
	
	public Substitute(byte[] givenKey)	//parameter constructor
	{
		key = givenKey;	//sets key to the givenKey
		createReverseKey();
	}

	private void createReverseKey()	//creates the reverse key 
	{
		reverseKey = new byte[256];
		for(int i = 0; i < reverseKey.length; i++)
		{			
			reverseKey[key[i] + 128] = (byte)(i-128);	//gets the reverse array
		}
	}
	
	public byte[] getKey()	//returns the key
	{
		return key;	
	}
	
	public byte[] encode(String S)
	{
		byte[] byteString = S.getBytes();	//transforms string into byte array
		byte[] encodedMessage = new byte[byteString.length];	//encoded message array
		
		for(int i = 0; i < encodedMessage.length; i++)
		{
			encodedMessage[i] = (byte)key[byteString[i] + 128];	//sets the index to the substituted byte that key says it is
		}
		
		return encodedMessage;	
	}
	
	public String decode(byte[] bytes)
	{
		byte[] decodedMessage = new byte[bytes.length];	//decoded message array
		
		for(int i = 0; i < decodedMessage.length; i++)
		{
			decodedMessage[i] = (byte)reverseKey[bytes[i] + 128];	//sets the index to the reverse substituted byte that reverse key says it is
		}
		
		return new String(decodedMessage);	//creates a string from the byte array and returns
	}
}