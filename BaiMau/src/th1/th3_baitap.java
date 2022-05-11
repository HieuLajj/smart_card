package th1;

import javacard.framework.*;

public class th3_baitap extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new th3_baitap().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		
		short byteRead = (short)apdu.setIncomingAndReceive();
		byte a=0x15;
		short le = apdu.setOutgoing();
		Util.setShort(buf,(short)3,a);
		apdu.setOutgoingLength((short)10);
		apdu.sendBytes((short)0,(short)5);
		
	}

}
