package Nhom5;

//import java.security.interfaces.RSAPublicKey;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;
//import javacard.security.KeyBuilder;

public class initCard extends Applet
{	
	public static Customer customer;
	
	public static final short OFFSET_ID     = 0;
	public static final short OFFSET_NAME   = 1;
	public static final short OFFSET_BIRTH  = 2;
	public static final short OFFSET_PHONE  = 3;
	public static final short OFFSET_PIN    = 4;
	public static final short OFFSET_WALLET = 5;
	public static final short OFFSET_PRIKEY = 6;

	public static final short LENGTH_BLOCK_AES = 16;

	public static final byte INS_INIT_0x00 = 0x00;
	public static final byte INS_GET_SINGLE_INFO_0x01 = 0x01;
	public static final byte INS_GET_ALL_INFO_0x02 = 0x02;
	public static final byte INS_CHANGE_INFO_0x03 = 0x03; 	// thay doi thong tin, bao gom thanh toan
	public static final byte INS_AUTHENTICATE_PIN_0x04 = 0x04;
	public static final byte INS_AUTHENTICATE_CARD_0x05 = 0x05;
	
	private static short[] wrong_PIN_count;

	public static final byte[] RUN_OUT_OF_TRIES_CODE = new byte[] {(byte)0x30};
	public static final byte[] WRONG_PIN_CODE = new byte[] {(byte)0x31};

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new initCard().register(bArray, (short) (bOffset + 1), bArray[bOffset]);

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

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();

		switch (buf[ISO7816.OFFSET_INS])
		{
		// khoi tao the
		//                 |<-------------------------------data----------------------------------------------------->|
		// /send 000000002E3031036869656E033034303731393939033033333433313134333503313432393330323030033132333435360330
		case (byte) INS_INIT_0x00:
			
			byte[] data = new byte[(short)buf[ISO7816.OFFSET_LC]];
			short dataLen = (short)data.length;
			Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, data, (short)0, (short)buf[ISO7816.OFFSET_LC]);
			
			short idLen, nameLen, birthLen, phoneLen, pinLen, walletLen;

			// tao mang luu vi tri bat dau cua tung thong tin trong mang data nhan duoc
			byte[] infoOFFSET = new byte[(short)6];
			// ID bat dau tu vi tri 0
			infoOFFSET[OFFSET_ID] = (short)0;
			
			short j = (short)1;
			short i;
			//lay ra vi tri cua tung thong tin dua vao ky tu phan cach la @
			for(i = (short)0; i < (short)buf[ISO7816.OFFSET_LC]; i = (short)(i+1) ){
				if( data[(short)i] == (byte)'@'){
					infoOFFSET[(short)j] = (byte)(i + 1);
					j = (short)(j+1);
				}
			}			
			
			idLen     = (short)(infoOFFSET[OFFSET_NAME]   - 1);
			nameLen   = (short)((short)(infoOFFSET[OFFSET_BIRTH]  - infoOFFSET[OFFSET_NAME])  - 1);
			birthLen  = (short)((short)(infoOFFSET[OFFSET_PHONE]  - infoOFFSET[OFFSET_BIRTH]) - 1);
			phoneLen  = (short)((short)(infoOFFSET[OFFSET_PIN]    - infoOFFSET[OFFSET_PHONE]) - 1);
			pinLen    = (short)((short)(infoOFFSET[OFFSET_WALLET] - infoOFFSET[OFFSET_PIN])   - 1);
			walletLen = (short)(buf[ISO7816.OFFSET_LC]            - infoOFFSET[OFFSET_WALLET]    );
			
			byte[] id         = new byte[idLen];
			byte[] name       = new byte[nameLen];
			byte[] birth      = new byte[birthLen];
			byte[] phone      = new byte[phoneLen];		
			byte[] pin        = new byte[pinLen];
			byte[] wallet     = new byte[walletLen];
			byte[] priKeyData = gen_RSA_key(apdu); //tao khoa bi mat dong thoi gui khoa cong khai len server
							
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_ID]    , id        , (short)0, idLen);	// 30 31		
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_NAME]  , name      , (short)0, nameLen);	// 68 69 65 6E		
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_BIRTH] , birth     , (short)0, birthLen);	// 30 34 30 37 31 39 39 39		
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_PHONE] , phone     , (short)0, phoneLen);	// 30 33 33 34 33 31 31 34 33 35		
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_PIN]   , pin       , (short)0, pinLen);	// 31 32 33 34 35 36		
			Util.arrayCopy(data, (short)infoOFFSET[OFFSET_WALLET], wallet    , (short)0, walletLen);// 30
			
			//bam ma pin de lam khoa cho AES
			pin = hashMD5(pin);
			//ma hoa thong tin
			id         = encryptAES(id        , pin);
			name       = encryptAES(name      , pin);
			birth      = encryptAES(birth     , pin);
			phone      = encryptAES(phone     , pin);
			wallet     = encryptAES(wallet    , pin);
			priKeyData = encryptAES(priKeyData, pin);
			
			//bat dau thao tac nguyen tu
			JCSystem.beginTransaction();
			customer = new Customer(id, name, birth, phone, pin, wallet, priKeyData);		
			JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			break;

		//lay ra tung` thong tin
		// chi dung de test
		case (byte)INS_GET_SINGLE_INFO_0x01:

			// lay ten chua giai ma
			// /send 00010202		
			if( buf[ISO7816.OFFSET_P1] == (byte)0x02 && buf[ISO7816.OFFSET_P2] == (byte)0x02){
				
				apdu.setOutgoing();
				apdu.setOutgoingLength((short)customer.getName().length);
				apdu.sendBytesLong(customer.getName(), (short)0, (short)customer.getName().length);
			}
			// lay ten da giai ma
			// /send 00010200			
			else if( buf[ISO7816.OFFSET_P1] == (byte)0x02 && buf[ISO7816.OFFSET_P2] == (byte)0x00){
				
				byte[] buffer  = decryptAES(customer.getName(), customer.getPIN());

				short withoutPaddingLen = removePaddingM2(buffer, (short)buffer.length);

				apdu.setOutgoing();
				apdu.setOutgoingLength(withoutPaddingLen);
				apdu.sendBytesLong(buffer, (short)0, withoutPaddingLen);
			}
			
			break;
		
		// lay tat ca thong tin
		// /send 00020000
		case (byte)INS_GET_ALL_INFO_0x02:
			byte[] flag = new byte[] {0x40};

			id  = decryptAES(customer.getId(), customer.getPIN());
			idLen = removePaddingM2(id, (short)id.length);

			name  = decryptAES(customer.getName(), customer.getPIN());
			nameLen = removePaddingM2(name, (short)name.length);

			birth  = decryptAES(customer.getBirth(), customer.getPIN());
			birthLen = removePaddingM2(birth, (short)birth.length);

			phone  = decryptAES(customer.getPhone(), customer.getPIN());
			phoneLen = removePaddingM2(phone, (short)phone.length);

			wallet  = decryptAES(customer.getWallet(), customer.getPIN());
			walletLen = removePaddingM2(wallet, (short)wallet.length);
	
			short totalSendLen = (short) ( idLen + nameLen + birthLen + phoneLen +  walletLen + (short)4 );

			apdu.setOutgoing();
			apdu.setOutgoingLength(totalSendLen);
			
			apdu.sendBytesLong(id, (short)0, idLen);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(name, (short)0, nameLen);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(birth, (short)0, birthLen);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(phone, (short)0, phoneLen);
			apdu.sendBytesLong(flag, (short)0, (short)1);
			
			apdu.sendBytesLong(wallet, (short)0, walletLen);
			
			break;

		// thay doi thong tin, bao gom thanh toan
		case (byte) INS_CHANGE_INFO_0x03:
			// thay doi ten P1 = 0x01
			// /send 00030100<do dai ten><ten moi>
			if( buf[ISO7816.OFFSET_P1] == (byte)0x01){
				name = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, name, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa ten moi truoc khi luu
				name = encryptAES(name, customer.getPIN());
				
				//bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setName(name);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			// thay doi ngay sinh P1 = 0x02
			// /send 00030100<do dai ngay sinh><ngay sinh>
			if( buf[ISO7816.OFFSET_P1] == (byte)0x02){
				birth = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, birth, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa ngay sinh moi truoc khi luu
				birth = encryptAES(birth, customer.getPIN());

				//bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setBirth(birth);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			// thay doi sdt P1 = 0x03
			// /send 00030100<do dai sdt><sdt moi>
			if( buf[ISO7816.OFFSET_P1] == (byte)0x03){
				phone = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, phone, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa phone moi truoc khi luu
				phone = encryptAES(phone, customer.getPIN());

				//bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setPhone(phone);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			// thay doi so du P1 = 0x04
			// /send 00030100<do dai so du><so du moi>
			if( buf[ISO7816.OFFSET_P1] == (byte)0x04){
				wallet = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, wallet, (short)0, (short)buf[ISO7816.OFFSET_LC]);
				//ma hoa so du moi truoc khi luu
				wallet = encryptAES(wallet, customer.getPIN());

				//bat dau thao tac nguyen tu
				JCSystem.beginTransaction();
				customer.setWallet(wallet);
				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}
			

			// thay doi ma PIN
			// /send 00030000<do dai ma PIN><ma PIN moi>
			// /send 0003000006363534333231
			if( buf[ISO7816.OFFSET_P1] == (byte)0x02){
				//lay ma pin nhap vao tu apdu buffer
				inputPIN = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, inputPIN, (short)0, (short)buf[ISO7816.OFFSET_LC]);

				//bam ma pin moi truoc khi luu
				inputPIN = hashMD5(inputPIN);

				//giai ma toan bo thong tin
				//du lieu sau khi giai ma van con padding
				id         = decryptAES(customer.getId()        , customer.getPIN());
				name       = decryptAES(customer.getName()      , customer.getPIN());
				birth      = decryptAES(customer.getBirth()     , customer.getPIN());
				phone      = decryptAES(customer.getPhone()     , customer.getPIN());	
				wallet     = decryptAES(customer.getWallet()    , customer.getPIN());
				priKeyData = decryptAES(customer.getPriKeyData(), customer.getPIN());

				//ma hoa lai voi khoa' la ma pin moi
				id         = encryptAES_wontPadding(id        , inputPIN);
				name       = encryptAES_wontPadding(name      , inputPIN);
				birth      = encryptAES_wontPadding(birth     , inputPIN);
				phone      = encryptAES_wontPadding(phone     , inputPIN);
				wallet     = encryptAES_wontPadding(wallet    , inputPIN);
				priKeyData = encryptAES_wontPadding(priKeyData, inputPIN);

				//luu thong tin moi vao the
				JCSystem.beginTransaction();//bat dau thao tac nguyen tu

				customer.setPIN(inputPIN);
				customer.setId(id);
				customer.setName(name);
				customer.setBirth(birth);
				customer.setPhone(phone);
				customer.setWallet(wallet);
				customer.setPriKeyData(priKeyData);

				JCSystem.commitTransaction(); //xac nhan ket thuc thao tac nguyen tu
			}

			break;
		
		// Xac thuc ma PIN
		// /send 00040000<do dai ma PIN><ma PIN>
		// truong hop sai
		// /send 0004000006112233445566
		// truong hop dung (neu da thay doi ma PIN thi phai nhap ma PIN moi)
		// /send 0004000006313233343536
		case (byte) INS_AUTHENTICATE_PIN_0x04:
			
			//kiem tra so lan nhap sai
			if( wrong_PIN_count[(short)0] == (short)3){
				apdu.setOutgoing();
				apdu.setOutgoingLength((short)1);
				apdu.sendBytesLong(RUN_OUT_OF_TRIES_CODE, (short)0, (short)1);
				return;
			}

			inputPIN = new byte[(short)buf[ISO7816.OFFSET_LC]];
			Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, inputPIN, (short)0, (short)buf[ISO7816.OFFSET_LC]);
			//bam ma pin nhap vao
			inputPIN = hashMD5(inputPIN);

			//so sanh ma pin nhap vao da duoc bam voi ma pin da duoc bam luu trong the
			if( Util.arrayCompare(inputPIN, (short)0, customer.getPIN(), (short)0, (short)inputPIN.length) != (byte)0){
				wrong_PIN_count[(short)0] = (short)(wrong_PIN_count[(short)0] + 1);
				apdu.setOutgoing();
				apdu.setOutgoingLength((short)1);
				apdu.sendBytesLong(WRONG_PIN_CODE, (short)0, (short)1);
			}
			break;

		// Xac thuc the
		//00050000054685923458
		case (byte) INS_AUTHENTICATE_CARD_0x05:
				//PC gui chuoi ngau nhien abc
				//card doc ra chuoi do' tu apdu buffer
				byte[] authString = new byte[(short)buf[ISO7816.OFFSET_LC]];
				Util.arrayCopy(buf, (short)ISO7816.OFFSET_CDATA, authString, (short)0, (short)buf[ISO7816.OFFSET_LC]); // vi du chuoi nhan duoc =abc
				
				//ky vao cuoi xac thuc bang khoa bi mat
				authString = rsaSign(authString); //vi du sau khi ky' abc nhan duoc def
				//gui chuoi da ky len server de xac thuc
				apdu.setOutgoing();
				apdu.setOutgoingLength((short)authString.length);
				apdu.sendBytesLong(authString, (short)0, (short)authString.length); //gui di def 
				// PC doc ra def. lay khoa cong khai, giai ma cai def ra , neu ra abc ban dau, thi xac thuc thanh cong.
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
	//ham ky
	private byte[] rsaSign(byte[] data){
		Signature rsaSig = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1,false);
		
		RSAPrivateKey rsaPrivKey = (RSAPrivateKey) KeyBuilder.buildKey(
			KeyBuilder.TYPE_RSA_PRIVATE, 
			KeyBuilder.LENGTH_RSA_1024, 
			false
		);
		//giai ma khoa bi mat luu trong the
		byte[] priKeyData = decryptAES(customer.getPriKeyData(), customer.getPIN());

		//thiet lap khoa bang cach cung cap so mu va modulus
		rsaPrivKey.setExponent(priKeyData, (short) 0, (short)128);//do dai cua so mu =128, bat dau tu vi tri 0
		rsaPrivKey.setModulus(priKeyData, (short)128, (short)128);//do dai cua modulus =128, bat dau tu vi tri 128(ngay sau so mu)
		
		byte[] sig_buffer = new byte[(short)(KeyBuilder.LENGTH_RSA_1024/8)];
		//ky vao chuoi xac thuc bang khoa bi mat
		rsaSig.init(rsaPrivKey, Signature.MODE_SIGN);
		rsaSig.sign(data, (short)0, (short)(data.length), sig_buffer, (short)0);
		//tra ve chuoi xac thuc da duoc ky
		return sig_buffer;
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
		//them padding vao du lieu vao truoc khi ma hoa
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
		cipher.doFinal(data, (short)0, (short)data.length, data, (short)0);
		return data;
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

	 public byte[] gen_RSA_key(APDU apdu){
		//tao doi tuong khoa bi mat
        RSAPrivateKey rsaPrivKey = (RSAPrivateKey)KeyBuilder.buildKey(
            KeyBuilder.TYPE_RSA_PRIVATE,
            KeyBuilder.LENGTH_RSA_1024, //do dai khoa la 1024 bit
            false
        );
		//tao doi tuong khoa cong khai
        RSAPublicKey rsaPubKey = (RSAPublicKey)KeyBuilder.buildKey(
            KeyBuilder.TYPE_RSA_PUBLIC,
            KeyBuilder.LENGTH_RSA_1024, 
            false
        );
		//tao doi tuong dung de sinh cap khoa
        KeyPair keyPair = new KeyPair(KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024);
		//sinh cap khoa
        keyPair.genKeyPair();
		//lay ra khoa bi mat va cong khai
        rsaPrivKey = (RSAPrivateKey)keyPair.getPrivate();
        rsaPubKey = (RSAPublicKey)keyPair.getPublic();
        //lay ra apdu buffer
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

		//thuc hien cac buoc tuong tu de lay ra data cua khoa cong khai
		len    = (short)0;
        totalLen = (short)0;
		//copy so mu cua khoa cong khai vao mang buf tu vi tri offset, tra ve do dai cua so mu
		len = rsaPubKey.getExponent(buf, (short)0);// =3

        totalLen += len; // =3

		//copy module cua khoa cong khai vao mang buf tu vi tri len =3, tra ve do dai cua module
        len = rsaPubKey.getModulus(buf, (short)len);// =128

        totalLen += len; // 3 + 128 = 131
		//gui khoa cong khai da copy vao mang buf len server
		apdu.setOutgoing();
        apdu.setOutgoingLength(totalLen);
        apdu.sendBytes((short)0, totalLen);		

		return priKeyData;
    }
}
