/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signatureverification;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;

import java.util.Scanner;

public class SignatureVerification {
   public static void main(String args[]) throws Exception{
      //Creating KeyPair generator object
      KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
	      
      //Initializing the key pair generator
      keyPairGen.initialize(1024);
	      
      //Generate the pair of keys
      KeyPair pair = keyPairGen.generateKeyPair();
      
      //Getting the privatekey from the key pair
      PrivateKey privKey = pair.getPrivate();

      //Creating a Signature object
      Signature sign = Signature.getInstance("SHA1withRSA");
       // Signature signature = Signature.getInstance("SHA1withRSA");

      //Initializing the signature
      sign.initSign(privKey);
      byte[] bytes = new byte[]{0x01, 0x02, 0x03};
      
      //Adding data to the signature
      sign.update(bytes);
      
      //Calculating the signature
      byte[] signature = sign.sign();      
      
      //Initializing the signature
      sign.initVerify(pair.getPublic());
      sign.update(bytes);
      
      //Verifying the signature
      boolean bool = sign.verify(signature);
      
      if(bool) {
         System.out.println("Signature verified");   
      } else {
         System.out.println("Signature failed");
      }
   }
}