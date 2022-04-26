/*
Send: A0 01 02 02
Recv: 00 00 90 00
Time used: 11.000 ms
Send: A0 02 02 02
Recv: 00 04 90 00
Time used: 12.000 ms
Send: A0 03 02 02
Recv: 00 01 90 00
Time used: 13.000 ms
*/
package Buoi2nay;

import javacard.framework.*;

public class Maytinhbotui extends Applet
{
	final static byte Calculator_CLA = (byte)0xA0;
	final static byte INS_CONG = (byte)0X00;
	final static byte INS_TRU = (byte)0X01;
	final static byte INS_NHAN = (byte)0X02;
	final static byte INS_CHIA = (byte)0X03;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Maytinhbotui().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		
		apdu.setIncomingAndReceive();
		if(buf[ISO7816.OFFSET_CLA]!= Calculator_CLA){
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}
		short res = 0;
		short tmp1 = buf[ISO7816.OFFSET_P1];
		short tmp2 = buf[ISO7816.OFFSET_P2];
		switch (buf[ISO7816.OFFSET_INS]){
		case INS_CONG:
			res = (short)(tmp1+tmp2);
			break;
		case INS_TRU:
			res = (short)(tmp1-tmp2);
			break;
		case INS_NHAN:
			res=(short)(tmp1*tmp2);
			break;
		case INS_CHIA:
			res=(short)(tmp1/tmp2);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			
		}
		Util.setShort(buf,(short)0,res);
		short le = apdu.setOutgoing();
		if(le<(short)2){
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);}
		apdu.setOutgoingLength((short)2);
		apdu.sendBytes((short)0,(short)2);
		
	}

}
