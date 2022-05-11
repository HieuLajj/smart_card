package th1;

import javacard.framework.*;

public class th1_baitap extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new th1_baitap().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		
		short byteRead = (short)apdu.setIncomingAndReceive();
		
		short le = (short)apdu.setOutgoing();
		Util.setShort(buf,(short)0,buf[ISO7816.OFFSET_INS]);
		apdu.setOutgoingLength((short)2);
		apdu.sendBytes((short)0,(short)2);
	}

}
