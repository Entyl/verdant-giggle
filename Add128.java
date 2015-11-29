import java.util.*;

public class Add128 implements SymCipher 
{
	byte[] key;	//128 byte key
	
	public Add128() //parameterless constructor
	{
		Random keyGenerator = new Random();
		key = new byte[128];
		keyGenerator.nextBytes(key);
	}
	
	public Add128(byte[] givenKey)	//parameter constructor
	{
		key = givenKey;	//sets key to the givenKey
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
			encodedMessage[i] = (byte)(byteString[i] + key[i % key.length]);	//adds the key to each index of the string
		}
		
		return encodedMessage;	
	}
	
	public String decode(byte[] bytes)
	{
		byte[] decodedMessage = new byte[bytes.length];	//decoded message array
		
		for(int i = 0; i < decodedMessage.length; i++)
		{
			decodedMessage[i] = (byte)(bytes[i] - key[i % key.length]);	//subtracts the key from each index of the byte array
		}
		
		return new String(decodedMessage);	//creates a string from the byte array and returns
	}
}
