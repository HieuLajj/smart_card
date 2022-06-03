package helloworld;

import javacard.framework.*;
import javacardx.apdu.ExtendedLength;
import javacard.security.*;
import javacardx.crypto.*;

public class hi extends Applet implements ExtendedLength
{
	public static Customer customer;
	
	public static final byte INS_INIT_0x00 = 0x00;
	public static final byte UNLOCK_PIN = 0x01;
	public static final byte INS_GET_ALL_INFO_0x02 = 0x02;
	public static final byte INS_CHANGE_INFO_0x03 = 0x03; 
	public static final byte INS_AUTHENTICATE_PIN_0x04 = 0x04;
	
    private final static byte INS_CREATE_IMAGE = (byte)0x53;
	private final static byte INS_CREATE_SIZEIMAGE = (byte)0x54;//countanh
	private final static byte INS_OUT_SIZEIMAGE = (byte)0x55;
	private final static byte INS_OUT_IMAGE = (byte)0x56;
	
	
	private static short dataLen2;
	
	
	public static final short OFFSET_CCCD         = 0;
	public static final short OFFSET_HOTEN        = 1;
	public static final short OFFSET_DICHVUYEUCAU = 2;
	public static final short OFFSET_NGAYSINH     = 3;
	public static final short OFFSET_SDT          = 4;
	public static final short OFFSET_PHONG        = 5;
	public static final short OFFSET_NGAYDK       = 6;
	public static final short OFFSET_MAPIN        = 7;
	public static final short OFFSET_TIEN         = 8;
	
	public static final short LENGTH_BLOCK_AES = 16;
	
	
    short len;
    short len2;
    short len3;
    
    public static byte[] OpImage,size;
    private static short[] wrong_PIN_count;
    public static final byte[] RUN_OUT_OF_TRIES_CODE = new byte[] {(byte)0x30};
	public static final byte[] WRONG_PIN_CODE = new byte[] {(byte)0x31};
    
    

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new hi().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		
		OpImage = new byte[10000];
		size = new byte[7];
		wrong_PIN_count = JCSystem.makeTransientShortArray ((short)1, JCSystem.CLEAR_ON_RESET);
		wrong_PIN_count[(short)0] = (short)0;
		 
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}
		byte[] inputPIN;
		byte[] inputPHONG;

		byte[] buf = apdu.getBuffer();
		short recvLen =apdu.setIncomingAndReceive();
		
		short pointer = 0;
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)INS_INIT_0x00:
			// //------------------------//
			
			// // // //byte[] data = new byte[[short]]
			byte[] data = new byte[(short)buf[ISO7816.OFFSET_LC]];
			short dataLen = (short)data.length;
			Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, data, (short)0, (short)buf[ISO7816.OFFSET_LC]);
			short cccd_len, hoten_len, ngaysinh_len, sdt_len, phong_len, ngaydk_len,mapin_len,tien_len,dichvuyeucau_len,cccd2_len;
			// tao mang luu vi tri bat dau cua tung thong tin trong mang data nhan duoc
			
			byte[] infoOFFSET = new byte[(short)9];
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
			 
			 
			 cccd_len         = (short)(infoOFFSET[OFFSET_HOTEN]   - 1);
			 hoten_len        = (short)((short)(infoOFFSET[OFFSET_DICHVUYEUCAU]  - infoOFFSET[OFFSET_HOTEN])  - 1);
			 dichvuyeucau_len = (short)((short)(infoOFFSET[OFFSET_NGAYSINH]  - infoOFFSET[OFFSET_DICHVUYEUCAU]) - 1);
			 ngaysinh_len     = (short)((short)(infoOFFSET[OFFSET_SDT]  - infoOFFSET[OFFSET_NGAYSINH]) - 1);
			 sdt_len          = (short)((short)(infoOFFSET[OFFSET_PHONG]    - infoOFFSET[OFFSET_SDT]) - 1);
			 phong_len        = (short)((short)(infoOFFSET[OFFSET_NGAYDK] - infoOFFSET[OFFSET_PHONG]-1));	
			 ngaydk_len       = (short)((short)(infoOFFSET[OFFSET_MAPIN] - infoOFFSET[OFFSET_NGAYDK]-1));	
			 mapin_len        = (short)((short)(infoOFFSET[OFFSET_TIEN]  - infoOFFSET[OFFSET_MAPIN]-1));
			 tien_len =          (short)(buf[ISO7816.OFFSET_LC] - infoOFFSET[OFFSET_TIEN]);
			 //tien_len         = (short)((short)(infoOFFSET[OFFSET_DICHVUYEUCAU]) - infoOFFSET[OFFSET_TIEN-1]);
			 //dichvuyeucau_len = (short)(buf[ISO7816.OFFSET_LC] - infoOFFSET[OFFSET_DICHVUYEUCAU]);
			 
			 //anh_len      = (short)OpImage.length;
			 
			 byte[] cccd = new byte[cccd_len];
			 byte[] hoten = new byte[hoten_len];
			 byte[] ngaysinh = new byte[ngaysinh_len];
			 byte[] sdt = new byte[sdt_len];
			 byte[] phong = new byte[phong_len];
			 byte[] ngay_dk = new byte[ngaydk_len];
			 byte[] mapin = new byte[mapin_len];
			 byte[] tien  = new byte[tien_len];
			 byte[] dichvuyeucau = new byte[dichvuyeucau_len];
			 
			 //byte[] anhdaidien = new byte[anh_len];
			
			
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_CCCD]     , cccd     , (short)0, cccd_len);			
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_HOTEN]    , hoten    , (short)0, hoten_len);		
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_DICHVUYEUCAU], dichvuyeucau, (short)0, dichvuyeucau_len);	
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NGAYSINH] , ngaysinh , (short)0, ngaysinh_len);			
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_SDT]      , sdt      , (short)0, sdt_len);			
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_PHONG]    , phong    , (short)0, phong_len);			
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NGAYDK]   , ngay_dk  , (short)0, ngaydk_len);
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_MAPIN]    , mapin    , (short)0, mapin_len);
			 Util.arrayCopy(data, (short)infoOFFSET[OFFSET_TIEN]     , tien     , (short)0, tien_len);
			 
			 
			 //Util.arrayCopy(OpImage, (short)0, anhdaidien, (short)0, anh_len);
			 // //--------------------------//
			 
			 mapin = hashMD5(mapin);
			 
			 cccd         =  encryptAES(cccd, mapin);
			 hoten        =  encryptAES(hoten, mapin);
			 ngaysinh     =  encryptAES(ngaysinh, mapin);
			 sdt          =  encryptAES(sdt, mapin);
			 phong        =  encryptAES(phong, mapin);
			 ngay_dk      =  encryptAES(ngay_dk, mapin);
			 tien         =  encryptAES(tien, mapin);
			 dichvuyeucau =  encryptAES(dichvuyeucau, mapin);
			 
			// JCSystem.beginTransaction();
			 customer = new Customer(cccd,hoten,ngaysinh,sdt,phong,ngay_dk,mapin,OpImage,tien,dichvuyeucau);
			// JCSystem.commitTransaction();
			 // len3= (short)mapin.length;
			 // Util.arrayCopy(mapin,(short)0, buf, (short)0,len3);
			 // apdu.setOutgoingAndSend((short)0,len3);
			 break;
	    case (byte) UNLOCK_PIN:
	    	wrong_PIN_count[(short)0] = (short)0;
	    	break;
		case (byte)INS_GET_ALL_INFO_0x02:
			byte[] flag = new byte[] {0x40};
			
			 
			cccd = decryptAES(customer.getCccd(),customer.getMapin());
			cccd_len = removePaddingM2(cccd, (short)cccd.length); 
			 
			hoten =decryptAES(customer.getHoten(),customer.getMapin());
			hoten_len = removePaddingM2(hoten, (short)hoten.length);
			
			dichvuyeucau =decryptAES(customer.getDichvuyeucau(),customer.getMapin());
			dichvuyeucau_len = removePaddingM2(dichvuyeucau, (short)dichvuyeucau.length);
			
			ngaysinh =decryptAES(customer.getNgaysinh(),customer.getMapin());
			ngaysinh_len = removePaddingM2(ngaysinh, (short)ngaysinh.length);
			
			sdt =decryptAES(customer.getSdt(),customer.getMapin());
			sdt_len = removePaddingM2(sdt, (short)sdt.length);
			
			phong =decryptAES(customer.getPhong(),customer.getMapin());
		    phong_len = removePaddingM2(phong, (short)phong.length);
			
			ngay_dk =decryptAES(customer.getNgaydk(),customer.getMapin());
			ngaydk_len = removePaddingM2(ngay_dk, (short)ngay_dk.length);
			 
			tien =decryptAES(customer.getTien(),customer.getMapin());
			tien_len = removePaddingM2(tien, (short)tien.length);
			 
			short totalSendLen = (short)(cccd_len+hoten_len+ngaysinh_len+sdt_len+phong_len+ngaydk_len+tien_len+dichvuyeucau_len+(short)7);
			apdu.setOutgoing();
			apdu.setOutgoingLength(totalSendLen);
			
			apdu.sendBytesLong(cccd,(short)0,cccd_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			
			apdu.sendBytesLong(hoten,(short)0,hoten_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(dichvuyeucau,(short)0, dichvuyeucau_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(ngaysinh,(short)0, ngaysinh_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(sdt,(short)0, sdt_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(phong,(short)0, phong_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(ngay_dk,(short)0, ngaydk_len);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(tien,(short)0, tien_len);
			
			break;
			
		case (byte) INS_CHANGE_INFO_0x03:
			
			// thay doi ten 0x01
			if( buf[ISO7816.OFFSET_P1] == (byte)0x01){
				hoten = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, hoten, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				hoten = encryptAES(hoten, customer.getMapin());		
				//bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setHoten(hoten);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			
			// thay doi ngay sinh
			if( buf[ISO7816.OFFSET_P1] == (byte)0x02){
				ngaysinh = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, ngaysinh, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa ngay sinh moi truoc khi luu
				ngaysinh = encryptAES(ngaysinh,customer.getMapin());

				JCSystem.beginTransaction();
				customer.setNgaysinh(ngaysinh);
				JCSystem.commitTransaction(); 
			}
			
			//thay doi so dien thoai
			if( buf[ISO7816.OFFSET_P1] == (byte)0x03){
				sdt = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, sdt, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa phone moi truoc khi luu
				sdt = encryptAES(sdt,customer.getMapin());

				JCSystem.beginTransaction();
				customer.setSdt(sdt);
				JCSystem.commitTransaction(); 
			}
			//thay doi so phong
			if( buf[ISO7816.OFFSET_P1] == (byte)0x04){
				phong = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, phong, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa phone moi truoc khi luu
				phong = encryptAES(phong,customer.getMapin());

				//bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setPhong(phong);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			
	        if( buf[ISO7816.OFFSET_P1] == (byte)0x05){
		        mapin = new byte[(short)buf[ISO7816.OFFSET_LC]];
		        Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, mapin, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				mapin = hashMD5(mapin);
				
				cccd         =  decryptAES(customer.getCccd(), customer.getMapin());
			    hoten        =  decryptAES(customer.getHoten(), customer.getMapin());
			    ngaysinh     =  decryptAES(customer.getNgaysinh(), customer.getMapin());
			    sdt          =  decryptAES(customer.getSdt(), customer.getMapin());
			    phong        =  decryptAES(customer.getPhong(), customer.getMapin());
			    ngay_dk      =  decryptAES(customer.getNgaydk(), customer.getMapin());
			    tien         =  decryptAES(customer.getTien(), customer.getMapin());
			    dichvuyeucau =  decryptAES(customer.getDichvuyeucau(), customer.getMapin());
			    
			    cccd         =  encryptAES_wontPadding(cccd, mapin);
			    hoten        =  encryptAES_wontPadding(hoten, mapin);
			    ngaysinh     =  encryptAES_wontPadding(ngaysinh, mapin);
			    sdt          =  encryptAES_wontPadding(sdt, mapin);
			    phong        =  encryptAES_wontPadding(phong, mapin);
			    ngay_dk      =  encryptAES_wontPadding(ngay_dk, mapin);
			    tien         =  encryptAES_wontPadding(tien, mapin);
			    dichvuyeucau =  encryptAES_wontPadding(dichvuyeucau, mapin);
			    
			    customer.setCccd(cccd);
			    customer.setHoten(hoten);
			    customer.setNgaysinh(ngaysinh);
			    customer.setSdt(sdt);
			    customer.setPhong(phong);
			    customer.setNgaydk(ngay_dk);
			    customer.setTien(tien);
			    customer.setDichvuyeucau(dichvuyeucau);
			    
				JCSystem.beginTransaction();
				customer.setMapin(mapin);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			if( buf[ISO7816.OFFSET_P1] == (byte)0x06){
				tien = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, tien, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				// //ma hoa so du moi truoc khi luu
				tien = encryptAES(tien,customer.getMapin());
				// //bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setTien(tien);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			if( buf[ISO7816.OFFSET_P1] == (byte)0x07){
				dichvuyeucau = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, dichvuyeucau, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				// //ma hoa so du moi truoc khi luu
				dichvuyeucau = encryptAES (dichvuyeucau,customer.getMapin());

				// //bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setDichvuyeucau(dichvuyeucau);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			       
			break;
		
		case (byte) INS_AUTHENTICATE_PIN_0x04:
			 if( wrong_PIN_count[(short)0] == (short)3){
			 	 apdu.setOutgoing();
		         apdu.setOutgoingLength((short)1);
				 apdu.sendBytesLong(RUN_OUT_OF_TRIES_CODE, (short)0, (short)1);
				 return;
			 }
			 short a = (short)buf[ISO7816.OFFSET_LC];
			 inputPIN = new byte[(short)a];
			 Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, inputPIN, (short)0, (short)buf[ISO7816.OFFSET_LC]);
			 
			 inputPIN = hashMD5(inputPIN);	 
			 if( Util.arrayCompare(inputPIN, (short)0, customer.getMapin(), (short)0, (short)inputPIN.length) != (byte)0){
				 wrong_PIN_count[(short)0] = (short)(wrong_PIN_count[(short)0] + 1);
				 apdu.setOutgoing();
		         apdu.setOutgoingLength((short)1);
				 apdu.sendBytesLong(WRONG_PIN_CODE, (short)0, (short)1);
			 }
			
			break;
		// case (byte) 0x06:
			// //len3= (short)wrong_PIN_count.length;
			// //Util.arrayCopy(wrong_PIN_count,(short)0, buf, (short)0,len3);
			// //apdu.setOutgoingAndSend((short)0,len3);
			// Util.setShort(buf,(short)0,wrong_PIN_count[(short)0]);
			// short le =apdu.setOutgoing();
			// apdu.setOutgoingLength((short)2);
			// apdu.sendBytes((short)0,(short)2);
		case (byte)0x05:
			 short b = (short)buf[ISO7816.OFFSET_LC];
			 inputPHONG = new byte[(short)b];
			 Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, inputPHONG, (short)0, (short)buf[ISO7816.OFFSET_LC]);	 
			 inputPHONG = encryptAES (inputPHONG,customer.getMapin());
			 		 
			 if( Util.arrayCompare(inputPHONG, (short)0, customer.getPhong(), (short)0, (short)inputPHONG.length) != (byte)0){
				 apdu.setOutgoing();
		         apdu.setOutgoingLength((short)1);
				 apdu.sendBytesLong(WRONG_PIN_CODE, (short)0, (short)1);
			 }		
			break;
	    case INS_CREATE_IMAGE:		 
		    short p1 = (short)(buf[ISO7816.OFFSET_P1]&0xff);
		    short count1 = (short)(249 * p1);
		    Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, OpImage, count1, (short)249);
			break;
		case INS_CREATE_SIZEIMAGE:
			Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, size, (short)0, (short)7);
			break;
		case INS_OUT_SIZEIMAGE:
			Util.arrayCopy(size, (short)0, buf, (short)0, (short)(size.length));
			apdu.setOutgoingAndSend((short)0,(short)7);
			break;
		case INS_OUT_IMAGE:
			apdu.setOutgoing();
		    short p = (short)(buf[ISO7816.OFFSET_P1]&0xff);
		    short count = (short)(249 * p);
		    apdu.setOutgoingLength((short)249);
		    apdu.sendBytesLong(customer.getAnhdaidien(), count, (short)249);
		   // apdu.sendBytesLong(OpImage, count, (short)249);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	// ham bam
	public byte[] hashMD5(byte[] data){
		byte[] hashData = new byte[(short)16];
		MessageDigest md5 = MessageDigest.getInstance(
			MessageDigest.ALG_MD5,
			false
		);
		md5.doFinal(data, (short)0, (short)(data.length), hashData, (short)0);

		return hashData;
	}
	// ham ma hoa AES co them padding
	private byte[] encryptAES(byte[] data, byte[] keyData){
		AESKey aesKey= (AESKey)KeyBuilder.buildKey(
			KeyBuilder.TYPE_AES,
			KeyBuilder.LENGTH_AES_128, 
			false
		);
		aesKey.setKey(keyData, (short)0);

		Cipher cipher = Cipher.getInstance(
			Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, 
			false
		);
		
		data = addPaddingM2(data, (short)data.length);

		cipher.init(aesKey, Cipher.MODE_ENCRYPT);
		cipher.doFinal(data, (short)0, (short)data.length, data, (short)0);
		return data;
	}
	// ham ma hoa AES khong them padding, ap dung voi du lieu vao da duoc padding tu truoc
	private byte[] encryptAES_wontPadding(byte[] data, byte[] keyData){
		AESKey aesKey= (AESKey)KeyBuilder.buildKey(
			KeyBuilder.TYPE_AES,
			KeyBuilder.LENGTH_AES_128, 
			false
		);
		aesKey.setKey(keyData, (short)0);

		Cipher cipher = Cipher.getInstance(
			Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, 
			false
		);

		cipher.init(aesKey, Cipher.MODE_ENCRYPT);
		cipher.doFinal(data, (short)0, (short)data.length, data, (short)0);
		return data;
	}
	// ham giai ma AES
	//du lieu sau khi giai ma van con padding
	private byte[] decryptAES(byte[] data, byte[] keyData){
		short data3_len = (short)data.length;
		byte[] data3 =  new byte[data3_len];
		AESKey aesKey= (AESKey)KeyBuilder.buildKey(
			KeyBuilder.TYPE_AES,
			KeyBuilder.LENGTH_AES_128, 
			false
		);
		aesKey.setKey(keyData, (short)0);
		Cipher cipher = Cipher.getInstance(
			Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, 
			false
		);
		cipher.init(aesKey, Cipher.MODE_DECRYPT);
		cipher.doFinal(data, (short)0, (short)data.length, data3, (short)0);
		return data3;
	}

	// ISO 9797 Padding Method 2.
	private byte[] addPaddingM2(byte[] data, short length) {
		//vi tri padding flag 0x80 
		short newLen = (short)(length+1);

		// neu do dai mang moi chua bang do dai 1 block hoac chua phai boi so cua 1 block
		// thi tiep tuc tang do dai
		while (newLen < LENGTH_BLOCK_AES || (newLen % LENGTH_BLOCK_AES != 0)) {
			newLen = (short)(newLen+1);
		}

		byte[] buffer  = new byte[newLen];
		Util.arrayCopy(data, (short)0, buffer, (short)0, length);

		// them padding flag
		buffer[length] = (byte)0x80;
		
		// them 0x00 toi het mang moi
		while ( length < (short)(newLen - 1) ) {
			length = (short)(length+1);
		   	buffer[length] = 0x00;
		}
		
		return buffer; 
	 }
	 //ham xoa padding
	 //tra ve do dai moi khong bao gom padding
	 private short removePaddingM2(byte[] buffer, short length) {
		
		// xoa cac so 0x00
		while ( (length != 0) && buffer[(short)(length - 1)] == (byte)0x00) 
			length = (short)(length-1);
		
		// ktra padding flag
		if (buffer[(short)(length - 1)] != (byte)0x80 ) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		
		length = (short)(length-1); // xoa not byte padding 0x80    
  
		return length; //tra ve do dai moi khong bao gom padding
	 }
	 


}
