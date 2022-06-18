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
	
	private static final byte INS_SIGN = (byte)0x31;
    private static final byte INS_VERIFY = (byte)0x32;
    private static final byte EXPONETT = (byte)0x33;
    private static final byte MODULUSS = (byte)0x34;
	
	private static short dataLen2;
	
	public static final short OFFSET_CCCD         = 0;
	public static final short OFFSET_HOTEN        = 1;
	public static final short OFFSET_NGAYSINH     = 2;
	public static final short OFFSET_SDT          = 3;
	public static final short OFFSET_PHONG        = 4;
	public static final short OFFSET_NGAYDK       = 5;
	public static final short OFFSET_TIEN         = 6;
	public static final short OFFSET_MAPIN        = 7;
	
	public static final short LENGTH_BLOCK_AES = 16;
    
    public static byte[] OpImage,size;
    private static short[] wrong_PIN_count;
    public static final byte[] RUN_OUT_OF_TRIES_CODE = new byte[] {(byte)0x30};
	public static final byte[] WRONG_PIN_CODE = new byte[] {(byte)0x31};
    
    private static RSAPrivateKey rsaPrivKey;
    private static RSAPublicKey rsaPubKey;
    private static RSAPublicKey rsaPubKey2;
    
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
				
        case (byte)0x06: 	
        	JCSystem.beginTransaction();
         	byte[] data5 = new byte[(short)buf[ISO7816.OFFSET_LC]];   
		    Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, data5, (short)0, (short)buf[ISO7816.OFFSET_LC]);        	
			JCSystem.commitTransaction();
			// short a1 = (short)((rsaSign(data5,apdu)).length);
			// byte[] authString = new byte[(short)a1];
			 
			 byte[] authString = rsaSign(data5,apdu);
			//authString = rsaSign(data5,apdu);
			//JCSystem.beginTransaction();
			apdu.setOutgoing();
			apdu.setOutgoingLength((short)authString.length);
		    apdu.sendBytesLong(authString, (short)0, (short)authString.length); //gui di def 
		    //JCSystem.commitTransaction();
			break;
        case EXPONETT:
          sendPublicExponent(apdu);
          break;
        case MODULUSS:
          sendPublicModulus(apdu);
          break;
		case (byte)INS_INIT_0x00:
			byte[] data = new byte[(short)buf[ISO7816.OFFSET_LC]];
			short dataLen = (short)data.length;
			Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, data, (short)0, (short)buf[ISO7816.OFFSET_LC]);
			short cccd_len, hoten_len, ngaysinh_len, sdt_len, phong_len, ngaydk_len,mapin_len,tien_len;			
			byte[] infoOFFSET = new byte[(short)8];
			infoOFFSET[OFFSET_CCCD] = (short)0;
			short j = (short)1;
			short i;
			
			//lay ra vi tri cua tung thong tin dua vao ky tu phan cach la @
			for(i = (short)0; i < (short)buf[ISO7816.OFFSET_LC]; i = (short)(i+1)){
				if( data[(short)i] == (byte)'@'){
					infoOFFSET[(short)j] = (byte)(i + 1);
					j = (short)(j+1);
				}
			}
			 
			cccd_len         = (short)(infoOFFSET[OFFSET_HOTEN]   - 1);
			hoten_len        = (short)((short)(infoOFFSET[OFFSET_NGAYSINH]  - infoOFFSET[OFFSET_HOTEN])  - 1);
			ngaysinh_len     = (short)((short)(infoOFFSET[OFFSET_SDT]  - infoOFFSET[OFFSET_NGAYSINH]) - 1);
			sdt_len          = (short)((short)(infoOFFSET[OFFSET_PHONG]    - infoOFFSET[OFFSET_SDT]) - 1);
			phong_len        = (short)((short)(infoOFFSET[OFFSET_NGAYDK] - infoOFFSET[OFFSET_PHONG]-1));	
			ngaydk_len       = (short)((short)(infoOFFSET[OFFSET_TIEN] - infoOFFSET[OFFSET_NGAYDK]-1));	
			tien_len         = (short)((short)(infoOFFSET[OFFSET_MAPIN]  - infoOFFSET[OFFSET_TIEN]-1));
			mapin_len        = (short)(buf[ISO7816.OFFSET_LC] - infoOFFSET[OFFSET_MAPIN]);
			 
			byte[] cccd         = new byte[cccd_len];
			byte[] hoten        = new byte[hoten_len];
			byte[] ngaysinh     = new byte[ngaysinh_len];
			byte[] sdt          = new byte[sdt_len];
			byte[] phong        = new byte[phong_len];
			byte[] ngay_dk      = new byte[ngaydk_len];
			byte[] tien         = new byte[tien_len];
			byte[] mapin        = new byte[mapin_len];
		    byte[] priKeyData = gen_RSA_key(apdu);
		    
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_CCCD]     , cccd     , (short)0, cccd_len);			
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_HOTEN]    , hoten    , (short)0, hoten_len);			
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NGAYSINH] , ngaysinh , (short)0, ngaysinh_len);			
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_SDT]      , sdt      , (short)0, sdt_len);			
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_PHONG]    , phong    , (short)0, phong_len);			
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NGAYDK]   , ngay_dk  , (short)0, ngaydk_len);
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_TIEN]     , tien     , (short)0, tien_len);
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_MAPIN]    , mapin    , (short)0, mapin_len);
			 
			mapin = hashMD5(mapin);
			 
		    JCSystem.beginTransaction(); 
			cccd         =  encryptAES(cccd, mapin);
			hoten        =  encryptAES(hoten, mapin);
			ngaysinh     =  encryptAES(ngaysinh, mapin);
			sdt          =  encryptAES(sdt, mapin);
			phong        =  encryptAES(phong, mapin);
			ngay_dk      =  encryptAES(ngay_dk, mapin);
			tien         =  encryptAES(tien, mapin);
			//priKeyData   =  encryptAES(priKeyData, mapin);
			JCSystem.commitTransaction();
			
			
			JCSystem.beginTransaction();
			//customer = new Customer(cccd,hoten,ngaysinh,sdt,phong,ngay_dk,mapin,tien,OpImage);
			customer = new Customer(cccd,hoten,ngaysinh,sdt,phong,ngay_dk,mapin,tien,priKeyData,OpImage);
			JCSystem.commitTransaction();
			break;
			 
	    case (byte) UNLOCK_PIN:
	    	wrong_PIN_count[(short)0] = (short)0;
	    	break;
	    	
		case (byte)INS_GET_ALL_INFO_0x02:
			byte[] flag = new byte[] {0x40};
			byte[] flag1 = new byte[] {0x3C};
			byte[] flag2 = new byte[] {0x3E};
			
			
			
			cccd = decryptAES(customer.getCccd(),customer.getMapin());
			cccd_len = removePaddingM2(cccd,(short)cccd.length); 
			 
			hoten =decryptAES(customer.getHoten(),customer.getMapin());
			hoten_len = removePaddingM2(hoten,(short)hoten.length);		
			
			ngaysinh =decryptAES(customer.getNgaysinh(),customer.getMapin());
		    ngaysinh_len = removePaddingM2(ngaysinh,(short)ngaysinh.length);
			
			sdt =decryptAES(customer.getSdt(),customer.getMapin());
			sdt_len = removePaddingM2(sdt,(short)sdt.length);
			
			phong =decryptAES(customer.getPhong(),customer.getMapin());
		    phong_len = removePaddingM2(phong,(short)phong.length);
			
			ngay_dk =decryptAES(customer.getNgaydk(),customer.getMapin());
			ngaydk_len = removePaddingM2(ngay_dk,(short)ngay_dk.length);
			 
			tien =decryptAES(customer.getTien(),customer.getMapin());
			tien_len = removePaddingM2(tien,(short)tien.length);
			
			
			short totalSendLen = (short)(cccd_len+hoten_len+ngaysinh_len+sdt_len+phong_len+ngaydk_len+tien_len+(short)12);
			
			apdu.setOutgoing();
			apdu.setOutgoingLength(totalSendLen);
			
			JCSystem.beginTransaction();
			apdu.sendBytesLong(cccd,(short)0,cccd_len);
			//apdu.sendBytesLong(flag,(short)0,(short)1);
			apdu.sendBytesLong(flag1,(short)0,(short)1);
			apdu.sendBytesLong(flag2,(short)0,(short)1);
			JCSystem.commitTransaction();	
			
			JCSystem.beginTransaction();			
			apdu.sendBytesLong(hoten,(short)0,hoten_len);
			//apdu.sendBytesLong(flag,(short)0,(short)1);	
			apdu.sendBytesLong(flag1,(short)0,(short)1);
			apdu.sendBytesLong(flag2,(short)0,(short)1);		
			JCSystem.commitTransaction();
			
			JCSystem.beginTransaction();
			apdu.sendBytesLong(ngaysinh,(short)0, ngaysinh_len);
			//apdu.sendBytesLong(flag,(short)0,(short)1);
			apdu.sendBytesLong(flag1,(short)0,(short)1);
			apdu.sendBytesLong(flag2,(short)0,(short)1);
			JCSystem.commitTransaction();
			
			JCSystem.beginTransaction();
			apdu.sendBytesLong(sdt,(short)0, sdt_len);
			//apdu.sendBytesLong(flag,(short)0,(short)1);
			apdu.sendBytesLong(flag1,(short)0,(short)1);
			apdu.sendBytesLong(flag2,(short)0,(short)1);
			JCSystem.commitTransaction();
			
			JCSystem.beginTransaction();
			apdu.sendBytesLong(phong,(short)0, phong_len);
			//apdu.sendBytesLong(flag,(short)0,(short)1);
			apdu.sendBytesLong(flag1,(short)0,(short)1);
			apdu.sendBytesLong(flag2,(short)0,(short)1);
			JCSystem.commitTransaction();
			
			JCSystem.beginTransaction();
			apdu.sendBytesLong(ngay_dk,(short)0, ngaydk_len);
			//apdu.sendBytesLong(flag,(short)0,(short)1);
			apdu.sendBytesLong(flag1,(short)0,(short)1);
			apdu.sendBytesLong(flag2,(short)0,(short)1);
			JCSystem.commitTransaction();
			
			JCSystem.beginTransaction();
			apdu.sendBytesLong(tien,(short)0,tien_len);
			JCSystem.commitTransaction();
			
			break;
			
		case (byte)0x22:
			cccd = decryptAES(customer.getCccd(),customer.getMapin());
			cccd_len = removePaddingM2(cccd,(short)cccd.length);
			apdu.setOutgoing();
			apdu.setOutgoingLength(cccd_len);
			Util.arrayCopy(cccd, (short)0, buf, (short)0,cccd_len);
			apdu.sendBytes((short)0, cccd_len);
            break;
        case (byte)0x23:
			hoten =decryptAES(customer.getHoten(),customer.getMapin());
			hoten_len = removePaddingM2(hoten,(short)hoten.length);	
			apdu.setOutgoing();
			apdu.setOutgoingLength(hoten_len);
			Util.arrayCopy(hoten, (short)0, buf, (short)0,hoten_len);
			apdu.sendBytes((short)0, hoten_len);
            break;
        case (byte)0x24:
        	ngaysinh =decryptAES(customer.getNgaysinh(),customer.getMapin());
		    ngaysinh_len = removePaddingM2(ngaysinh,(short)ngaysinh.length);
		    apdu.setOutgoing();
			apdu.setOutgoingLength(ngaysinh_len);
			Util.arrayCopy(ngaysinh, (short)0, buf, (short)0,ngaysinh_len);
			apdu.sendBytes((short)0, ngaysinh_len);
            break;
        case (byte)0x25:
        	sdt =decryptAES(customer.getSdt(),customer.getMapin());
			sdt_len = removePaddingM2(sdt,(short)sdt.length);
		    apdu.setOutgoing();
			apdu.setOutgoingLength(sdt_len);
			Util.arrayCopy(sdt, (short)0, buf, (short)0,sdt_len);
			apdu.sendBytes((short)0, sdt_len);
            break;
        case (byte)0x26:
        	phong =decryptAES(customer.getPhong(),customer.getMapin());
		    phong_len = removePaddingM2(phong,(short)phong.length);
		    apdu.setOutgoing();
			apdu.setOutgoingLength(phong_len);
			Util.arrayCopy(phong, (short)0, buf, (short)0,phong_len);
			apdu.sendBytes((short)0, phong_len);
            break;
         case (byte)0x27:
        	ngay_dk =decryptAES(customer.getNgaydk(),customer.getMapin());
			ngaydk_len = removePaddingM2(ngay_dk,(short)ngay_dk.length);
		    apdu.setOutgoing();
			apdu.setOutgoingLength(ngaydk_len);
			Util.arrayCopy(ngay_dk, (short)0, buf, (short)0,ngaydk_len);
			apdu.sendBytes((short)0, ngaydk_len);
            break;
         case (byte)0x28:
        	tien =decryptAES(customer.getTien(),customer.getMapin());
			tien_len = removePaddingM2(tien,(short)tien.length);
		    apdu.setOutgoing();
			apdu.setOutgoingLength(tien_len);
			Util.arrayCopy(tien, (short)0, buf, (short)0,tien_len);
			apdu.sendBytes((short)0, tien_len);
            break;
		case (byte) INS_CHANGE_INFO_0x03:
		
			// thay doi ten 0x01
			if( buf[ISO7816.OFFSET_P1] == (byte)0x01){
				hoten = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, hoten, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				hoten = encryptAES(hoten, customer.getMapin());	
						
				JCSystem.beginTransaction();
				customer.setHoten(hoten);
				JCSystem.commitTransaction();
			}
			
			// thay doi ngay sinh
			if( buf[ISO7816.OFFSET_P1] == (byte)0x02){
				ngaysinh = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, ngaysinh, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				ngaysinh = encryptAES(ngaysinh,customer.getMapin());
				JCSystem.beginTransaction();
				customer.setNgaysinh(ngaysinh);
				JCSystem.commitTransaction(); 
			}
			
			//thay doi so dien thoai
			if( buf[ISO7816.OFFSET_P1] == (byte)0x03){
				sdt = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, sdt, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				sdt = encryptAES(sdt,customer.getMapin());
				JCSystem.beginTransaction();
				customer.setSdt(sdt);
				JCSystem.commitTransaction(); 
			}
			
			//thay doi so phong
			if( buf[ISO7816.OFFSET_P1] == (byte)0x04){
				phong = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, phong, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				phong = encryptAES(phong,customer.getMapin());
				JCSystem.beginTransaction();
				customer.setPhong(phong);
				JCSystem.commitTransaction();
			}
			
	        if( buf[ISO7816.OFFSET_P1] == (byte)0x05){
		        mapin = new byte[(short)buf[ISO7816.OFFSET_LC]];
		        Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, mapin, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				
				mapin = hashMD5(mapin);
				
				JCSystem.beginTransaction();
				cccd         =  decryptAES(customer.getCccd(), customer.getMapin());
			    hoten        =  decryptAES(customer.getHoten(), customer.getMapin());
			    ngaysinh     =  decryptAES(customer.getNgaysinh(), customer.getMapin());
			    sdt          =  decryptAES(customer.getSdt(), customer.getMapin());
			    phong        =  decryptAES(customer.getPhong(), customer.getMapin());
			    ngay_dk      =  decryptAES(customer.getNgaydk(), customer.getMapin());
			    tien         =  decryptAES(customer.getTien(), customer.getMapin());
			   // priKeyData   =  decryptAES(customer.getPriKeyData(), customer.getMapin());
			    
			    cccd         =  encryptAES_wontPadding(cccd, mapin);
			    hoten        =  encryptAES_wontPadding(hoten, mapin);
			    ngaysinh     =  encryptAES_wontPadding(ngaysinh, mapin);
			    sdt          =  encryptAES_wontPadding(sdt, mapin);
			    phong        =  encryptAES_wontPadding(phong, mapin);
			    ngay_dk      =  encryptAES_wontPadding(ngay_dk, mapin);
			    tien         =  encryptAES_wontPadding(tien, mapin);
			   // priKeyData   =  encryptAES_wontPadding(priKeyData, mapin);
			    JCSystem.commitTransaction();
			    
				JCSystem.beginTransaction();
				customer.setCccd(cccd);
			    customer.setHoten(hoten);
			    customer.setNgaysinh(ngaysinh);
			    customer.setSdt(sdt);
			    customer.setPhong(phong);
			    customer.setNgaydk(ngay_dk);
			    customer.setTien(tien);
			   // customer.setPriKeyData(priKeyData);
				customer.setMapin(mapin);
				JCSystem.commitTransaction();
			}
			
			if( buf[ISO7816.OFFSET_P1] == (byte)0x06){
				
				// phong = new byte[(short)buf[ISO7816.OFFSET_LC]];
				// Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, phong, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				// phong = encryptAES(phong,customer.getMapin());
				// JCSystem.beginTransaction();
				// customer.setPhong(phong);
				// JCSystem.commitTransaction();
				
				
				tien = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, tien, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				tien = encryptAES(tien,customer.getMapin());
				JCSystem.beginTransaction();
				customer.setTien(tien);
				JCSystem.commitTransaction();
			}	       
			break;
		
		case (byte) INS_AUTHENTICATE_PIN_0x04:
			 if( wrong_PIN_count[(short)0] == (short)3){
			 	
			 	
			 	 // apdu.setOutgoing();
		         // apdu.setOutgoingLength((short)1);
				 // apdu.sendBytesLong(RUN_OUT_OF_TRIES_CODE, (short)0, (short)1);
				 // return;
							 
				  apdu.setOutgoing();
			      apdu.setOutgoingLength((short)1);
			      Util.arrayCopy(RUN_OUT_OF_TRIES_CODE, (short)0, buf, (short)0,(short)1);
			      apdu.sendBytes((short)0, (short)1);
			      return;
			 }
			 short a = (short)buf[ISO7816.OFFSET_LC];
			 inputPIN = new byte[(short)a];
			 Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, inputPIN, (short)0, (short)buf[ISO7816.OFFSET_LC]);		 
			 inputPIN = hashMD5(inputPIN);	 
			 
			 if( Util.arrayCompare(inputPIN, (short)0, customer.getMapin(), (short)0, (short)inputPIN.length) != (byte)0){
				 wrong_PIN_count[(short)0] = (short)(wrong_PIN_count[(short)0] + 1);
							 
				 // apdu.setOutgoing();
		         // apdu.setOutgoingLength((short)1);
				 // apdu.sendBytesLong(WRONG_PIN_CODE, (short)0, (short)1);
				 apdu.setOutgoing();
			     apdu.setOutgoingLength((short)1);
			     Util.arrayCopy(WRONG_PIN_CODE, (short)0, buf, (short)0,(short)1);
			     apdu.sendBytes((short)0, (short)1);
			      return;
			 }			
			 break;
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
		   // apdu.sendBytesLong(decryptAES(customer.getAnhdaidien(),customer.getMapin()), count, (short)249);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	
   private byte[] rsaSign(byte[] data4,APDU apdu)
    {
     Signature rsaSig = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1,false);	
     byte[] sig_buffer = new byte[(short)(KeyBuilder.LENGTH_RSA_1024/8)];
    	
     RSAPrivateKey rsaPrivKey3 = (RSAPrivateKey) KeyBuilder.buildKey(
			KeyBuilder.TYPE_RSA_PRIVATE, 
			KeyBuilder.LENGTH_RSA_1024, 
			false
		);	
	 JCSystem.beginTransaction();
	 byte[] priKeyData = customer.getPriKeyData();
	// byte[] priKeyData = decryptAES(customer.getPriKeyData(), customer.getMapin());
     rsaPrivKey3.setExponent(priKeyData, (short) 0, (short)128);//do dai cua so mu =128, bat dau tu vi tri 0
	 rsaPrivKey3.setModulus(priKeyData, (short)128, (short)128);//do dai cua modulus =128, bat dau tu vi tri 128(ngay sau so mu)   	
    	
	 rsaSig.init(rsaPrivKey3, Signature.MODE_SIGN);
	 rsaSig.sign(data4, (short)0, (short)(data4.length), sig_buffer, (short)0);
	 JCSystem.commitTransaction();
     return sig_buffer;
    }
   
    private void sendPublicExponent(APDU apdu){
	    byte[] buf = apdu.getBuffer();
	    short lenExponent = rsaPubKey.getExponent(buf,(short)0);
	    apdu.setOutgoingAndSend((short)0,lenExponent);
    }
    private void sendPublicModulus(APDU apdu){
	    byte[] buf = apdu.getBuffer();
	    short lenModulus = rsaPubKey.getModulus(buf,(short)0);
	    apdu.setOutgoingAndSend((short)0,lenModulus);
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
		//JCSystem.beginTransaction();
		cipher.init(aesKey, Cipher.MODE_DECRYPT);
		cipher.doFinal(data, (short)0, (short)data.length, data3, (short)0);
		return data3;
		//JCSystem.commitTransaction();
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
	 private short removePaddingM2(byte[] buffer, short length){	
		// xoa cac so 0x00
		while ((length != 0) && buffer[(short)(length - 1)] == (byte)0x00) 
			length = (short)(length-1);
		// ktra padding flag
		if (buffer[(short)(length - 1)] != (byte)0x80 ) ISOException.throwIt(ISO7816.SW_DATA_INVALID);	
		length = (short)(length-1); // xoa not byte padding 0x80    
		return length; //tra ve do dai moi khong bao gom padding
	 }
	 
	 public byte[] gen_RSA_key(APDU apdu){
		 short sigLen = (short)(KeyBuilder.LENGTH_RSA_1024/8);
         byte[] sig_buffer = new byte[sigLen];
	     Signature rsaSig =Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1,false);
		 rsaPrivKey = (RSAPrivateKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE,(short)(8*sigLen),false);
		 rsaPubKey = (RSAPublicKey)KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,(short)(8*sigLen),false);
		 KeyPair keyPair = new KeyPair(KeyPair.ALG_RSA,(short)(8*sigLen));
		 keyPair.genKeyPair();
		 rsaPrivKey = (RSAPrivateKey)keyPair.getPrivate();
		 rsaPubKey =  (RSAPublicKey)keyPair.getPublic();
		 byte[] buf = apdu.getBuffer();

         short len    = (short)0;
         short totalLen = (short)0;
		 //copy so mu cua khoa bi mat vao mang buf tu vi tri 0, tra ve do dai cua so mu =128
		 len = rsaPrivKey.getExponent(buf, (short)0);

         totalLen += len;

		 //copy module cua khoa bi mat vao mang buf tu vi tri len =128, tra ve do dai cua module =128
         len = rsaPrivKey.getModulus(buf, (short)len);

         totalLen += len; //=256
		
		 //tao mang luu du lieu cua khoa bi mat voi do dai chinh xac vua tinh duoc
		 byte[] priKeyData = new byte[totalLen];
		 //copy khoa bi mat trong mang buf vao mang vua tao
		 Util.arrayCopy(buf, (short)0, priKeyData, (short)0, totalLen); 

		 return priKeyData;	 	 
	 }
}
