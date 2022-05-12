package javaapp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.sun.javacard.apduio.*;
/**
 *
 * @author laihi
 */
public class JavaCardHostApp {
    private Socket sock;
    private OutputStream os;
    private InputStream is;
    private Apdu apdu;
    private CadClientInterface cad;

    public JavaCardHostApp() {
        apdu = new Apdu();
    }
    
     //thiet lap ket noi voi applet thong qua java card runtime voi port 9025
    public void establishConnectionToSimulator() {
        try {
            //prgramm socket for the connection with simulator
            sock = new Socket("localhost", 9025);
            os = sock.getOutputStream();
            is = sock.getInputStream();
            //Initialize the instance card acceptance device
            cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void pwrUp() {
        try {
            if (cad != null) {
                //to power up the card
                cad.powerUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void pwrDown() {
        try {
            if (cad != null) {
                //power down the card
                cad.powerDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setTheAPDUCommands(byte[] cmnds) {
        if (cmnds.length > 4 || cmnds.length == 0) {
            System.err.println("inavlid commands");
        } else {
            //set the APDU header
            apdu.command = cmnds;
            System.out.println("CLA: " + atrToHex(cmnds[0]));
            System.out.println("INS: " + atrToHex(cmnds[1]));
            System.out.println("P1: " + atrToHex(cmnds[2]));
            System.out.println("P2: " + atrToHex(cmnds[3]));
        }
    }
    
     //set Lc
    public void setTheDataLength(byte len) {
        apdu.Lc = len;
        System.out.println("Lc: " + atrToHex(len));
    }
    public void setTheDataLengthShort(short len) {
        apdu.Lc = len;
        System.out.println("Lc: " + shorttoHex(len));
    }

    public void setTheDataIn(byte[] data) {
        if (data.length != apdu.Lc) {
            System.err.println("The number of data in the array are more than expected");
        } else {
            //set the data to be sent to the applets
            apdu.dataIn = data;
            for (int dataIndx = 0; dataIndx < data.length; dataIndx++) {
                System.out.println("dataIn" + dataIndx + ": " + atrToHex(data[dataIndx]));
            }

        }
    }
    
    //Le
    public void setExpctdByteLength(byte len) {
        apdu.Le = len;
        System.out.println("Le: " + atrToHex(len));
    }
    public void setExpctShortLength(short len) {
        apdu.Le = len;
        System.out.println("Le: " + shorttoHex(len));
    }
    
    //trao doi du lieu (apdu nhan du lieu tu applet)
    public void exchangeTheAPDUWithSimulator() {
        try {
            apdu.setDataIn(apdu.dataIn, apdu.Lc);
            cad.exchangeApdu(apdu);//cau lenh thuc hien trao doi du lieu giua apdu va applet
        } catch (IOException | CadTransportException e) {
            e.printStackTrace();
        }
    }
    
    
    public byte[] decodeDataOut() {

        byte[] dOut = apdu.dataOut;
        for (int dataIndx = 0; dataIndx < dOut.length; dataIndx++) {
            System.out.println("dataOut" + dataIndx + ": " + atrToHex(dOut[dataIndx]));
        }
        return dOut;

    }

    public byte[] decodeStatus() {
        byte[] statByte = apdu.getSw1Sw2();
        System.out.println("SW1: " + atrToHex(statByte[0]));
        System.out.println("SW2: " + atrToHex(statByte[1]));
        return statByte;
    }
    
    //convert byte to hex
    public String atrToHex(byte atCode) {
        StringBuilder result = new StringBuilder();
            result.append(String.format("%02x", atCode));
        return result.toString();
    }
    public String shorttoHex(short atCode) {
        StringBuilder result = new StringBuilder();
            result.append(String.format("%02x", atCode));
        return result.toString();
    }
    
}
