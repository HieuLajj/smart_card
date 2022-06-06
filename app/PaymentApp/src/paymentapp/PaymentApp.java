/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paymentapp;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author duong
 */
public class PaymentApp {
    

    /**
     */
    
    public  static HashMap<String, String> idAndPubkey = new HashMap<String, String>();
    public static final byte[] AID_APPLET = {0x11,0x22,0x33,0x44,0x55,0x05,0x00};
    private static Card card;
    private static TerminalFactory factory;
    private static CardChannel channel;
    private static CardTerminal terminal;
    private static List<CardTerminal> terminals;
    private static ResponseAPDU response;

    public PaymentApp(){}
    
    public boolean connectCard(){
        try {
            factory = TerminalFactory.getDefault();
            terminals = factory.terminals().list();
            terminal = terminals.get(0);
            card = terminal.connect("T=0");
            channel = card.getBasicChannel();

            if (channel == null) {
                return false;
            }
            response = channel.transmit(new CommandAPDU(0x00,(byte) 0xA4,0x04,0x00,AID_APPLET));
            String check = Integer.toHexString(response.getSW());
            if(check.equals("9000")){
                return true;
            }else if(check.equals("6400")){
                JOptionPane.showMessageDialog(null, "Thẻ đã bị vô hiệu hóa");
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
        return false;
    }

    public boolean disconnect(){
        try {
            card.disconnect(false);
            return true;
        } catch (CardException e) {
            System.out.println("Error :" + e);
        }
        return false;
    }
    
    /**
     *
     * @param ba
     * @return
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
}
    
    
    public boolean transmissionData(String id, String name, String birth, String phone,String pin, String wallet){
        String data = id + "@" + name + "@" + birth + "@" + phone + "@" + pin + "@" + wallet ;
        byte[] dataTrans = data.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, dataTrans));
            String check = Integer.toHexString(response.getSW());
            String pub = bytesToHex(response.getData());
            idAndPubkey.put(id, pub);
           
            System.out.println(idAndPubkey);
            System.out.println(check);
            if (check.equals("9000")) {
                return true;
            }else{
                return  false;
            }
        } catch (CardException e) {
            System.out.println("Error :" + e);
            return false;
        }
        
    }
    
    public  String getData(){
        try {
            response = channel.transmit(new CommandAPDU((byte) 0x00, (byte)0x02, (byte) 0x00,(byte) 0x00));
            return new String(response.getData(), StandardCharsets.UTF_8);
        } catch (CardException e) {
            System.out.println("Error :" + e);
            return "Lỗi";
        }
        
    }

    public boolean changeName(String name){
        byte[] nameTrans = name.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x03, (byte)0x01, (byte)0x00, nameTrans));
            String check = Integer.toHexString(response.getSW());
            
            if (check.equals("9000")) {
                return true;
            }
                return false;
        } catch (CardException ex) {
            System.out.println("Error :" + ex);
        }
            return false;
    }
    
    public boolean changeBirth(String birth){
        byte[] birthTrans = birth.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x03, (byte)0x02, (byte)0x00, birthTrans));
            String check = Integer.toHexString(response.getSW());
            
            if (check.equals("9000")) {
                return true;
            }
                return false;
        } catch (CardException ex) {
            System.out.println("Error :" + ex);
        }
            return false;
    }
    
    
    public boolean changePhoneNumber(String phoneNumber){
        byte[] phoneNumberTrans = phoneNumber.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x03, (byte)0x03, (byte)0x00, phoneNumberTrans));
            String check = Integer.toHexString(response.getSW());
            
            if (check.equals("9000")) {
                return true;
            }
                return false;
        } catch (CardException ex) {
            System.out.println("Error :" + ex);
            return false;
        }
            
    }
    
    public static boolean changeWallet(String wallet){
        byte[] walletTrans = wallet.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x03, (byte)0x04, (byte)0x00, walletTrans));
            String check = Integer.toHexString(response.getSW());
            
            if (check.equals("9000")) {
                return true;
            }else{return false;}
                
        } catch (CardException ex) {
            System.out.println("Error :" + ex);
            return false;
        }
            
    }
    
    public boolean changePIN(String pin){
        byte[] pinTrans = pin.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x03, (byte)0x05, (byte)0x00, pinTrans));
            String check = Integer.toHexString(response.getSW());
            
            if (check.equals("9000")) {
                return true;
            }else{
                return false;
            }
        } catch (CardException ex) {
            System.out.println("Error :" + ex);
            return false;
        }
            
    }
    
    public String authCard(String ranString){
        byte[] ranStringTrans = ranString.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte) 0x00, (byte)0x05, (byte) 0x00,(byte) 0x00, ranStringTrans));
            String enCodeRanString = bytesToHex(response.getData());
            System.out.println(enCodeRanString);
            String check = Integer.toHexString(response.getSW());
            if (check.equals("9000")) {
                return enCodeRanString;
            }else{
                return "Lỗi";
            }
        } catch (CardException e) {
            System.out.println("Error :" + e);
            return "Lỗi";
        }
    }
    
    public  String authPIN(String pin){
        byte[] pinTrans = pin.getBytes();
        try {
            response = channel.transmit(new CommandAPDU((byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00, pinTrans));
            String test = new String(response.getData(), StandardCharsets.UTF_8) + Integer.toHexString(response.getSW());
            System.out.println(test);
            return  test;
        } catch (CardException e) {
            System.out.println("Error :" + e);
            return "Lỗi";
        }
        
    }
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        KetNoiThe ketNoiThe = new KetNoiThe();
        ketNoiThe.setVisible(true);
    }
}


