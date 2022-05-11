package th1;

import javacard.framework.*;

public class th2_baitapj extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new th2_baitapj().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		short byteRead = (short)apdu.setIncomingAndReceive();
		short a = buf[ISO7816.OFFSET_P1];
		short b = buf[ISO7816.OFFSET_P2];
		short res = 0;
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			res = (short)(a+b);
			break;
		case (byte) 0x01:
			res = (short) (a*b);
			break;
		case (byte) 0x02:
			res = (short) (a-b);
			break;
		case (byte) 0x03:
			res = (short) (a/b);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
		short le = apdu.setOutgoing();
		
		Util.setShort(buf,(short)0,res);
		apdu.setOutgoingLength((short)2);
		apdu.sendBytes((short)0,(short)2);
		
		
	}

}
