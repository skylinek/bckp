/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldmultos;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 *
 * @author DzurendaPetr(106420)
 */
public class HelloWorldMultOS {

    protected List<CardTerminal> terminals;
    protected TerminalFactory factory;
    private CardTerminal terminal;
    private Card card;
    private CardChannel channel;
    private ResponseAPDU rAPDU;
    private byte[] baCommandAPDU;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        HelloWorldMultOS helloWorld = new HelloWorldMultOS();
        try {
            helloWorld.run();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HelloWorldMultOS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() throws NoSuchAlgorithmException {

        try {
            factory = TerminalFactory.getDefault();
            terminals = factory.terminals().list();
            terminal = terminals.get(0);

            System.out.println("Terminals: " + terminals);
            System.out.println("Selected Terminal: " + terminal);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            while (!terminal.isCardPresent()) {
            };

            card = terminal.connect("*");
            System.out.println("ATR: " + bytesToHex(card.getATR().getBytes()));
            channel = card.getBasicChannel();

            System.out.println("\nCard info: " + card);

        } catch (CardException ce) {
            ce.printStackTrace();
        }

        byte[] AID = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00,
            (byte) 0x04,
            (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00};

        byte[] rData = sendAPDU(AID);

        byte[] vstupniText = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16};

        int counter = 1;

        byte[] opp1 = new byte[]{(byte) 0x09,(byte) 0xDD,(byte) 0x12,(byte) 0xCE};
        byte[] opp2 = new byte[]{0x01};
        byte[] modulus = new byte[]{(byte) 0x03};
     //   Test_MOD_ADD(counter, opp1, opp2, modulus);
        
        
        Test_MOD_INV(counter, opp1, modulus);
        

        

        /* byte[] DES_ASM = new byte[]{(byte) 0x80, (byte) 0x20, (byte) 0x00, (byte) 0x00, 
                                (byte) 0x08, 
                                (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
                                (byte) 0x80};*/
        // byte[] rDES_ASM = sendAPDU(DES_ASM);
        // Test
        Cipher ecipher;
        byte key[] = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08};
        byte openText[] = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08};
        byte cypherText[];

        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = skf.generateSecret(dks);

            ecipher = Cipher.getInstance("DES/ECB/NoPadding");
            ecipher.init(Cipher.ENCRYPT_MODE, desKey);
            cypherText = ecipher.doFinal(vstupniText);
            bytesToHex(cypherText);
            System.err.println(bytesToHex(cypherText));
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(HelloWorldMultOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(HelloWorldMultOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(HelloWorldMultOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(HelloWorldMultOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(HelloWorldMultOS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

   

    /**
     * Call des funciton on card
     *
     * @param data dsfafd
     *
     */
    public void DES(byte[] data) {

        /* byte[] DES = new byte[]{(byte) 0x80, (byte) 0x10, (byte) 0x00, (byte) 0x00, 
                                (byte) 0x08, 
                                (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
                                (byte) 0x80};*/
        byte[] DES = createAPDUComm((byte) 0x80, (byte) 0x31, (byte) 0x00, (byte) 0x00,
                (byte) data.length,
                data,
                (byte) data.length);
        byte[] rDES = sendAPDU(DES);
    }
    /**
     * TESTOVACI metoda pro des funciton on card
     *
     */
      private void Test_DES(int counter, byte[] vstup) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                DES(vstup);  
        }
        count_time(timeStart, counter);
    }
    

        /**
     * Call triple des funciton on card
     *
     *
     */
    public void tripleDES(byte[] data) {

        byte[] TRIPLEDES = createAPDUComm((byte) 0x80, (byte) 0x11, (byte) 0x00, (byte) 0x00,
                (byte) 0x08,
                data,
                (byte) 0x80);
        byte[] rDES = sendAPDU(TRIPLEDES);
    }
/**
     * TESTOVACI metoda pro triple des funciton on card
     *
     */
     private void Test_tripelDES(int counter, byte[] vstup) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                tripleDES(vstup);  
        }
        count_time(timeStart, counter);
    }

    
    
    
    /**
     * Call SHA1 funciton on card
     *
     *
     */
   /* public void SHA1(byte[] data) {

        byte[] SHA1 = createAPDUComm((byte) 0x80, (byte) 0x31, (byte) 0x00, (byte) 0x00,
                (byte) data.length,
                data,
                (byte) data.length);
        byte[] rSHA1 = sendAPDU(SHA1);
    }*/

    /**
     * Call SHA
     *
     *
     */
    public void SHA(int velikost, byte[] data) {

        byte[] SHA2 = createAPDUComm((byte) 0x80, (byte) 0x31, (byte) velikost, (byte) 0x00,
                (byte) data.length,
                data,
                (byte) data.length);
        byte[] rSHA2 = sendAPDU(SHA2);
    }
    /**
     * TESTOVACI metoda pro SHA
     *
     */
     private void Test_SHA(int counter, int velikost, byte[] vstup) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                SHA(velikost,  vstup);
        }
        count_time(timeStart, counter);
    }
    
    
    /**
     * Call generate random numbers funciton on card
     *
     *
     */
    // pouze do 128 bytes kvůli velikost APDU 
    public void generateRandomNumbers(int howMuchBytes) {

        byte[] RNG = createAPDUComm((byte) 0x80, (byte) 0x40, (byte) howMuchBytes, (byte) 0x00,
                (byte) 0x00,
                null,
                (byte) howMuchBytes);
        byte[] rRNG = sendAPDU(RNG);
    }
     /**
     * TESTOVACI metoda pro  random numbers funciton on card
     *
     */
     private void Test_generateRandomNumbers(int counter, int howMuchBytes) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                generateRandomNumbers(howMuchBytes);  
        }
        count_time(timeStart, counter);
    }
    
    
    
    
    

    /**
     * Modularni scitani Modullar Addition
     *
     */
    public void Modular_ADD(byte[] opp1, byte[] opp2, byte[] n) {

        int priznak = 0x50; //scitani

        int count_of_return = 255;
        byte[] ADD1 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x01, (byte) 0x00,
                (byte) opp1.length,
                opp1,
                (byte) 0x01);
        byte[] rMUL1 = sendAPDU(ADD1);

        byte[] ADD2 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x02, (byte) 0x00,
                (byte) opp2.length,
                opp2,
                (byte) 0x01);
        byte[] rMUL2 = sendAPDU(ADD2);

        byte[] ADD3 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x03, (byte) 0x00,
                (byte) n.length,
                n,
                (byte) count_of_return);
        byte[] rMUL3 = sendAPDU(ADD3);
    }
    /**
     * TESTOVACI metoda pro Modularni scitani Modullar Addition
     *
     */
     private void Test_MOD_ADD(int counter, byte[] opp1, byte[] opp2,byte[]  modulus) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                Modular_ADD(opp1,opp2,modulus);  // N can not be ODD  (sude)
        }
        count_time(timeStart, counter);
    }

    private void count_time(long timeStart, int counter) {
        long timeIssueAtt = (System.currentTimeMillis() - timeStart) / counter;
        System.out.println("TIME START:"+timeStart);
        System.out.println("TIME ISSUE:"+timeIssueAtt);
    }
    
    

    /**
     * Modularni nasobeni Modullar multiplication
     *
     */
    public void Modular_MUL(byte[] opp1, byte[] opp2, byte[] n) {

        byte[] MUL1 = createAPDUComm((byte) 0x80, (byte) 0x51, (byte) 0x01, (byte) 0x00,
                (byte) opp1.length,
                opp1,
                (byte) 0x01);
        byte[] rMUL1 = sendAPDU(MUL1);

        byte[] MUL2 = createAPDUComm((byte) 0x80, (byte) 0x51, (byte) 0x02, (byte) 0x00,
                (byte) opp2.length,
                opp2,
                (byte) 0x01);
        byte[] rMUL2 = sendAPDU(MUL2);

        byte[] MUL3 = createAPDUComm((byte) 0x80, (byte) 0x51, (byte) 0x03, (byte) 0x00,
                (byte) n.length,
                n,
                (byte) 0x01);
        byte[] rMUL3 = sendAPDU(MUL3);
    }
 /**
     * TESTOVACI metoda pro Modularni nasobeni Modullar multiplication
     *
     */
     private void Test_MOD_MUL(int counter, byte[] opp1, byte[] opp2,byte[]  modulus) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                Modular_MUL(opp1,opp2,modulus);  // N can not be ODD  (sude)
        }
        count_time(timeStart, counter);
    }
    
      /**
     * Modularni mocneni exponantion
     *
     */
    public void Modular_EXP(byte[] opp1, byte[] opp2, byte[] n) {

        int priznak = 0x52; //scitani

        int count_of_return = 255;
        byte[] ADD1 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x01, (byte) 0x00,
                (byte) opp1.length,
                opp1,
                (byte) 0x01);
        byte[] rMUL1 = sendAPDU(ADD1);

        byte[] ADD2 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x02, (byte) 0x00,
                (byte) opp2.length,
                opp2,
                (byte) 0x01);
        byte[] rMUL2 = sendAPDU(ADD2);

        byte[] ADD3 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x03, (byte) 0x00,
                (byte) n.length,
                n,
                (byte) count_of_return);
        byte[] rMUL3 = sendAPDU(ADD3);
    }
    /**
     * TESTOVACI metoda pro Modularni  mocneni exponantion
     *
     */
     private void Test_MOD_EXP(int counter, byte[] opp1, byte[] opp2,byte[]  modulus) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                Modular_EXP(opp1,opp2,modulus);  // N can not be ODD  (sude)
        }
        count_time(timeStart, counter);
    }

     /**
     * Modularni redukce reduction
     *
     */
    public void Modular_Redcution(byte[] opp1,  byte[] n) {

        int priznak = 0x53; //scitani

        int count_of_return = 255;
        byte[] ADD1 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x01, (byte) 0x00,
                (byte) opp1.length,
                opp1,
                (byte) 0x01);
        byte[] rMUL1 = sendAPDU(ADD1);

        byte[] ADD2 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x02, (byte) 0x00,
                (byte) n.length,
                 n,
                (byte) count_of_return);
        byte[] rMUL2 = sendAPDU(ADD2);

    }
    /**
     * TESTOVACI metoda pro Modularni  redukci
     *
     */
     private void Test_MOD_RED(int counter, byte[] opp1,byte[]  modulus) {
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                Modular_Redcution(opp1,modulus);  // N can not be ODD  (sude)
        }
        count_time(timeStart, counter);
    }

     
     /**
     * Modularni inverze inversion
     *
     */
    public void Modular_Inversion(byte[] opp1,  byte[] n) {

        int priznak = 0x54; //scitani

        int count_of_return = 255;
        byte[] ADD1 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x01, (byte) 0x00,
                (byte) opp1.length,
                opp1,
                (byte) opp1.length);
        byte[] rMUL1 = sendAPDU(ADD1);

        byte[] ADD2 = createAPDUComm((byte) 0x80, (byte) priznak, (byte) 0x02, (byte) 0x00,
                (byte) n.length,
                 n,
                (byte) n.length);
        byte[] rMUL2 = sendAPDU(ADD2);

    }
    /**
     * TESTOVACI metoda pro Modularni  inverzi
     *
     */
     private void Test_MOD_INV(int counter, byte[] opp1,byte[]  modulus) {
         System.out.println("TEST MODULAR INVERSE");
         System.out.println("|a|=|n|:"+modulus.length*8 + "bit");
         
         long timeStart = System.currentTimeMillis();
        for (int i = 0; i < counter; i++) {
                Modular_Inversion(opp1,modulus);  // N can not be ODD  (sude)
        }
        count_time(timeStart, counter);
    }
     
     
     
     
     
    
    /**
     * Create APDU command
     *
     * @param CLA
     * @param INS
     * @param P1
     * @param P2
     * @param Lc
     * @param data
     * @param Le
     * @return
     */
    public byte[] createAPDUComm(byte CLA, byte INS, byte P1, byte P2, int Lc, byte[] data, byte Le) {

        byte[] apduComm;
        int LcSize = 1;
        int LeSize = 1;

        if (Lc > 255) {
            LcSize = 3;
        }

        if (Lc > 255) {
            LeSize = 3;
        }

        if (Le == 0 && Lc == 0) {                                         //Case 1
            apduComm = new byte[4];
        } else if (Le != 0 && Lc == 0) {                                   //Case 2
            apduComm = new byte[5];
            apduComm[apduComm.length - 1] = Le;

        } else if (Le == 0 && Lc != 0) //Case 3
        {
            apduComm = new byte[4 + LcSize + data.length];
        } else if (Le != 0 && Lc != 0) {                                   //Case 4
            apduComm = new byte[4 + LcSize + data.length + LeSize];
            apduComm[apduComm.length - 1] = Le;
        } else {
            return null;
        }

        apduComm[0] = CLA;
        apduComm[1] = INS;
        apduComm[2] = P1;
        apduComm[3] = P2;

        if (Lc != 0) {
            apduComm[4] = (byte) data.length;
            System.arraycopy(data, 0, apduComm, 5, data.length);
        }

        return apduComm;
    }

    /**
     * Convert bytes to hexadecimal string
     *
     * @param bytes
     * @return
     */
    public String bytesToHex(byte[] bytes) {

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Send APDU command to the card
     */
    public byte[] sendAPDU(byte[] data) {

        byte[] baResponceAPDU = null;

        baCommandAPDU = data;

        System.out.println("APDU >>>: " + bytesToHex(baCommandAPDU));

        try {

            ResponseAPDU r = channel.transmit(new CommandAPDU(baCommandAPDU));

            baResponceAPDU = r.getBytes();

            System.out.println("APDU <<<: " + bytesToHex(baResponceAPDU) + " SW =" + bytesToHex(new byte[]{(byte) r.getSW1(), (byte) r.getSW2()}));
            return r.getData();

        } catch (CardException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

}
