package Buoi2nay;

import javacard.framework.*;

public class Buoi2nay extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Buoi2nay().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}
byte []ten = {'h','i','e','u'};
byte []ngaysinh = {1,2,1,1,2,0,0,0};
short len;
short len2;
short len3;

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			len= (short)ten.length;
			Util.arrayCopy(ten,(short)0, buf, (short)0,len);
			apdu.setOutgoingAndSend((short)0,len);
			break;
		case (byte)0x11:
			len2= (short)ngaysinh.length;
			Util.arrayCopy(ngaysinh,(short)0, buf, (short)0,len2);
			apdu.setOutgoingAndSend((short)0,len2);
			break;
		case (byte)0x22:
			short nameLen=(short)ten.length;
			short birthdayLen = (short)ngaysinh.length;
			short total= (short)(nameLen+birthdayLen);
			short le = apdu.setOutgoing();
			apdu.setOutgoingLength(total);
			Util.arrayCopy(ten,(short)0,buf,(short)0,nameLen);
			apdu.sendBytes((short)0,nameLen);
			Util.arrayCopy(ngaysinh,(short)0,buf,(short)0,birthdayLen);
			apdu.sendBytes((short)0,birthdayLen);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
