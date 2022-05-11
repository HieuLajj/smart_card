package th4_1;

import javacard.framework.*;

public class Applet2 extends Applet
{
	final static byte SV_ID_LENGTH = (byte)0x04;
	private static byte [] diemThi, sinhVien;
	private static byte soLuongMonThi;
	byte cLen,aLen;
	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Applet2(bArray,bOffset,bLength);
	}
	
	private Applet2(byte[] bArray, short bOffset, byte bLength){
		byte iLen = bArray[bOffset];
		if(iLen ==0){
			register();
		}else{
			register(bArray, (short)(bOffset+1), iLen);
		}
		bOffset = (short)(bOffset+iLen+1);
		cLen = bArray[bOffset];
		bOffset = (short)(bOffset+cLen+1);
		aLen = bArray[bOffset];
		bOffset = (short)(bOffset +1);
		if(aLen !=0){
			sinhVien = new byte[SV_ID_LENGTH];
			Util.arrayCopy(bArray,bOffset,sinhVien,(byte)0,SV_ID_LENGTH);
			bOffset +=SV_ID_LENGTH;
			soLuongMonThi = bArray[bOffset];
		}else{
			sinhVien = new byte[]{'S','V','0','1'};
			soLuongMonThi=(byte)0x08;
		}
	}

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
				apdu.setOutgoing();
				apdu.setOutgoingLength((short)5);
				apdu.sendBytesLong(sinhVien,(short)0,SV_ID_LENGTH);
				buf[0] = soLuongMonThi;
				apdu.sendBytes((short)0,(short)1);
				break;
			default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	

		// byte[] buf = apdu.getBuffer();
		// byte a=0x12;
		// short byteRead = (short)apdu.setIncomingAndReceive();
		// short le = apdu.setOutgoing();
		// Util.setShort(buf,(short)3,cLen);
		// apdu.setOutgoingLength((short)10);
		// apdu.sendBytes((short)0,(short)5);
	}

}
