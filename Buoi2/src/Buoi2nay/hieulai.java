package Buoi2nay;

import javacard.framework.*;

public class hieulai extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new hieulai().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu){
		if(selectingApplet()){
			return;
		}
		byte[] buf = apdu.getBuffer();
		//byte[] buf_temp = new byte[256];
		// nhan du lieu dai
		short dataLen=(short)(buf[ISO7816.OFFSET_LC]&0xff);
		short byteRead = (short)(apdu.setIncomingAndReceive());
		byte[] buf_temp = new byte[dataLen];
		//short pointer = 0;
		Util.arrayCopy(buf,ISO7816.OFFSET_CDATA,buf_temp,(short)0,byteRead);
		// while (dataLen>0){
			// Util.arrayCopy(buf,ISO7816.OFFSET_CDATA,buf_temp,pointer,byteRead);
			// pointer += byteRead;
			// dataLen -=byteRead;
			// byteRead =apdu.receiveBytes(ISO7816.OFFSET_CDATA);
		// }
		short len2= (short)dataLen;
		short le = apdu.setOutgoing();
		apdu.setOutgoingLength(len2);
		Util.arrayCopy(buf_temp,(short)0,buf,(short)0,len2);
		apdu.sendBytes((short)0,len2);
		
		
	}
 

}
