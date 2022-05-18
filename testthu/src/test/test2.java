package test;

import javacard.framework.*;
import javacardx.apdu.ExtendedLength;

public class test2 extends Applet
{

    private static byte temp[];
	private final static short MAX_SIZE = (short)1024;
	private final static byte INS_NHAP = (byte)0x01;
	private final static byte INS_XUAT = (byte)0x02;
	private static short dataLen;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new test2().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	    temp =new byte[MAX_SIZE];
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		short recvLen = apdu.setIncomingAndReceive();
		short pointer = 0;
		
		
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case INS_NHAP:
			
			dataLen = apdu.getIncomingLength();
			if(dataLen>MAX_SIZE){
				ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);}
				short dataOffset = apdu.getOffsetCdata();
				pointer=0;
				while(recvLen>0){
					Util.arrayCopy(buf, dataOffset, temp, pointer, recvLen);
					pointer += recvLen;
					recvLen = apdu.receiveBytes(dataOffset);
				}
			break;
		case INS_XUAT:
			short toSend = dataLen;
			short le = apdu.setOutgoing();
			apdu.setOutgoingLength(toSend);
			short sendLen=0;
			pointer = 0;
			while (toSend >0){
				sendLen = (toSend > le)?le:toSend;
				apdu.sendBytesLong(temp,pointer,sendLen);
				toSend -=sendLen;
				pointer +=sendLen;
			}
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}


}
