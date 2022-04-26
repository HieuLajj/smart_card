package Buoi2nay;

import javacard.framework.*;

public class hieu extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new hieu().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	
	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		short byteRead = (short)(apdu.setIncomingAndReceive());
		short res = (short)(buf[ISO7816.OFFSET_LC]&0xff);
		//short res = (short)(buf[ISO7816.OFFSET_INS]);
		//ISO7816.OFFSET_INS
		Util.setShort(buf,(short)0,byteRead);
		short le = apdu.setOutgoing();
		
		apdu.setOutgoingLength((short)2);
		apdu.sendBytes((short)0,(short)2);
		
	}

}
