/*
Bai nhap ho ten ngay sinh hoc sinh
/select
/send
Send: 00 00 01 02 07 12 12 32 32 32 12 32
Recv: 90 00
Time used: 12.000 ms
Send: 00 00 02 02 04 12 11 20 00
Recv: 90 00
Time used: 12.000 ms
Send: 00 01 01 02 04 12 11 20 00
Recv: 12 12 32 32 32 12 32 00 00 00 90 00
Time used: 12.000 ms
Send: 00 01 02 02 04 12 11 20 00
Recv: 12 11 20 00 00 00 00 00 90 00
Time used: 14.000 ms

Phn xut c h và tên do  dài mng c nh là 256 lên em ã làm nhng cha thc thi

*/
package Buoi2nay;

import javacard.framework.*;

public class Buoi2yy extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Buoi2yy().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	short b;
	short c;
	short dataLen;
	byte[] ho_ten= new byte[256];
	byte[] ngay_sinh = new byte[256];
	
	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		
		
		short byteRead = (short)(apdu.setIncomingAndReceive());
		//dataLen=(short)(buf[ISO7816.OFFSET_LC]&0xff);
 		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			switch (buf[ISO7816.OFFSET_P1]){
				case (byte)0x01:
					b=(short)(byteRead);
					Util.arrayCopy(buf,ISO7816.OFFSET_CDATA,ho_ten,(short)0,byteRead);
					break;
				case (byte)0x02:
					c=(short)(byteRead);
					Util.arrayCopy(buf,ISO7816.OFFSET_CDATA,ngay_sinh,(short)0,byteRead);
					break;
			    default:
				ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			}
			
			break;
		case (byte)0x01:
			switch (buf[ISO7816.OFFSET_P1]){
				case (byte)0x01:
					short len2= (short)b;
					short le = apdu.setOutgoing();
					apdu.setOutgoingLength(len2);
					Util.arrayCopy(ho_ten,(short)0,buf,(short)0,len2);
					apdu.sendBytes((short)0,len2);
					break;
				case (byte)0x02:
					short len3=(short)c;
					short le3= apdu.setOutgoing();
					apdu.setOutgoingLength(len3);
					Util.arrayCopy(ngay_sinh,(short)0,buf,(short)0,len3);
					apdu.sendBytes((short)0,len3);
					break;
				case (byte)0x03:
					short nameLen=(short)b;
					short birthdayLen = (short)c;
					short total= (short)(nameLen+birthdayLen);
					apdu.setOutgoing();
					apdu.setOutgoingLength(total);
					Util.arrayCopy(ho_ten,(short)0,buf,(short)0,nameLen);
					apdu.sendBytes((short)0,nameLen);
					Util.arrayCopy(ngay_sinh,(short)0,buf,(short)0,birthdayLen);
					apdu.sendBytes((short)0,birthdayLen);
			    default:
				ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			}
		
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
