package th1;
// gui 1 chuoi co dinh
import javacard.framework.*;

public class th2 extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new th2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		short byteRead = (short)(apdu.setIncomingAndReceive());
		
		
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			byte[] name ={'L','A','I','H','I','E','U'};
			byte[] birthday = {10,10,90};
			
			short nameLen = (short)name.length;
			short birthdayLen = (short)birthday.length;
			short totalBytes = (short)(nameLen + birthdayLen);
			short le = apdu.setOutgoing();
			apdu.setOutgoingLength((short)20);
			//Util.arrayCopy(name,(short)2, buf,(short)4,nameLen);
			Util.arrayCopy(name,(short)2, buf,(short)4,(short)4);
			
			// copy du lieu cua thang name vao buf
			// du lieu copy se duoc luu tu short 4 ( tuc bat dau in tu thang short 5)cua thang buf
			// nameLen do dai chuoi nhan vao
			apdu.sendBytes((short)0,(short)20);
			
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
