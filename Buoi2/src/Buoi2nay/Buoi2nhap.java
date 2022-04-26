package Buoi2nay;

import javacard.framework.*;

public class Buoi2nhap extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Buoi2nhap().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
			short le = apdu.setOutgoing();
			if(le <(short)2) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
			apdu.setOutgoingLength((short)(3));
			apdu.sendBytes((short)0,(short)(3));
			break;
		
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
