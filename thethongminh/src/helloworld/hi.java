package helloworld;

import javacard.framework.*;

public class hi extends Applet
{
	public static Customer customer;
	
	public static final byte INS_INIT_0x00 = 0x00;
	public static final byte INS_GET_ALL_INFO_0x02 = 0x02;
	
	
	public static final short OFFSET_CCCD     = 0;
	public static final short OFFSET_HOTEN   = 1;
	public static final short OFFSET_NGAYSINH  = 2;
	public static final short OFFSET_SDT  = 3;
	public static final short OFFSET_PHONG    = 4;
	public static final short OFFSET_NGAYDK = 5;
	public static final short OFFSET_MAPIN =6;
	
	
	
	
	byte []ten = {'h','i','e','u'};
    byte []ngaysinh4 = {1,2,1,1,2,0,0,0};
    byte []ngaysinh2= {1,2,3,4,5,6,7,7};
    short len;
    short len2;
    short len3;

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new hi().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
		case (byte)INS_INIT_0x00:
			// //------------------------//
			
			// // // //byte[] data = new byte[[short]]
			byte[] data = new byte[(short)buf[ISO7816.OFFSET_LC]];
			short dataLen = (short)data.length;
			Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, data, (short)0, (short)buf[ISO7816.OFFSET_LC]);
			short cccd_len, hoten_len, ngaysinh_len, sdt_len, phong_len, ngaydk_len,mapin_len;
			// tao mang luu vi tri bat dau cua tung thong tin trong mang data nhan duoc
			
			byte[] infoOFFSET = new byte[(short)7];
			// // ID bat dau tu vi tri 0
			infoOFFSET[OFFSET_CCCD] = (short)0;
			short j = (short)1;
			short i;
			// //lay ra vi tri cua tung thong tin dua vao ky tu phan cach la @
			 for(i = (short)0; i < (short)buf[ISO7816.OFFSET_LC]; i = (short)(i+1) ){
				  if( data[(short)i] == (byte)'@'){
					  infoOFFSET[(short)j] = (byte)(i + 1);
					  j = (short)(j+1);
				  }
			 }
			 cccd_len     = (short)(infoOFFSET[OFFSET_HOTEN]   - 1);
			 hoten_len   = (short)((short)(infoOFFSET[OFFSET_NGAYSINH]  - infoOFFSET[OFFSET_HOTEN])  - 1);
			 ngaysinh_len  = (short)((short)(infoOFFSET[OFFSET_SDT]  - infoOFFSET[OFFSET_NGAYSINH]) - 1);
			 sdt_len  = (short)((short)(infoOFFSET[OFFSET_PHONG]    - infoOFFSET[OFFSET_SDT]) - 1);
			 phong_len    = (short)((short)(infoOFFSET[OFFSET_NGAYDK] - infoOFFSET[OFFSET_PHONG]-1));	
			 ngaydk_len = (short)((short)(infoOFFSET[OFFSET_MAPIN] - infoOFFSET[OFFSET_NGAYDK]-1));	
			 mapin_len = (short)(buf[ISO7816.OFFSET_LC] - infoOFFSET[OFFSET_MAPIN]);
			 byte[] cccd = new byte[cccd_len];
			 byte[] hoten = new byte[hoten_len];
			 byte[] ngaysinh = new byte[ngaysinh_len];
			 byte[] sdt = new byte[sdt_len];
			 byte[] phong = new byte[phong_len];
			 byte[] ngay_dk = new byte[ngaydk_len];
			 byte[] mapin = new byte[mapin_len];
			
			
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_CCCD]    , cccd      , (short)0, cccd_len);	// 30 31		
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_HOTEN]  , hoten      , (short)0, hoten_len);	// 68 69 65 6E		
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NGAYSINH] , ngaysinh     , (short)0, ngaysinh_len);	// 30 34 30 37 31 39 39 39		
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_SDT] , sdt     , (short)0, sdt_len);	// 30 33 33 34 33 31 31 34 33 35		
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_PHONG]   , phong       , (short)0, phong_len);	// 31 32 33 34 35 36		
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NGAYDK], ngay_dk    , (short)0, ngaydk_len);// 30
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_MAPIN], mapin  , (short)0, mapin_len);// 30
			 // //--------------------------//
			 
			customer = new Customer(cccd,hoten,ngaysinh,sdt,phong,ngay_dk,mapin);
			len3= (short)mapin.length;
			Util.arrayCopy(mapin,(short)0, buf, (short)0,len3);
			apdu.setOutgoingAndSend((short)0,len3);
			break;
		case (byte)INS_GET_ALL_INFO_0x02:
			// len3= (short)customer.getHoten().length;
			// Util.arrayCopy(customer.getHoten(),(short)0, buf, (short)0,len3);
			// apdu.setOutgoingAndSend((short)0,len3);
			byte[] flag = new byte[] {0x40};
			
			cccd = customer.getCccd();
			hoten = customer.getHoten();
			ngaysinh = customer.getNgaysinh();
			sdt = customer.getSdt();
			phong = customer.getPhong();
			ngay_dk = customer.getNgaydk();
			mapin = customer.getMapin();
			
			cccd_len = (short)cccd.length;
			hoten_len = (short)hoten.length;
			ngaysinh_len = (short)ngaysinh.length;
			sdt_len = (short)sdt.length;
			phong_len = (short)phong.length;
			ngaydk_len = (short)ngay_dk.length;
			mapin_len = (short)mapin.length;
			
			short totalSendLen = (short)(cccd_len+hoten_len+ngaysinh_len+sdt_len+phong_len+ngaydk_len+mapin_len+(short)6);
			apdu.setOutgoing();
			apdu.setOutgoingLength(totalSendLen);
			apdu.sendBytesLong(cccd,(short)0,cccd_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(hoten,(short)0,hoten_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(ngaysinh,(short)0, ngaysinh_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(sdt,(short)0, sdt_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(phong,(short)0, phong_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(ngay_dk,(short)0, ngaydk_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(mapin,(short)0, mapin_len);
			
			
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
