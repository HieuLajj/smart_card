package Nhom5;

//import javacard.framework.*;

public class Customer {
    private byte[] id;
	private byte[] name;
	private byte[] birth;
	private byte[] phone;
	private byte[] PIN;
    private byte[] wallet;
    private byte[] priKeyData;
    
    public Customer(byte[] id, byte[] name, byte[] birth, byte[] phone, byte[] PIN, byte[] wallet) {
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.PIN = PIN;
        this.wallet = wallet;
    }

    public Customer(byte[] id, byte[] name, byte[] birth, byte[] phone, byte[] PIN, byte[] wallet, byte[] priKeyData) {
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.PIN = PIN;
        this.wallet = wallet;
        this.priKeyData = priKeyData;
    }
    
    // public short getTotalLength(){
    //     return (short) (
    //             (short) id.length +
    //             (short) name.length +
    //             (short) birth.length +
    //             (short) phone.length +
    //             (short) PIN.length +
    //             (short) wallet.length +
    //             (short) 5
    //     );
    // }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public byte[] getBirth() {
        return birth;
    }

    public void setBirth(byte[] birth) {
        this.birth = birth;
    }

    public byte[] getPhone() {
        return phone;
    }

    public void setPhone(byte[] phone) {
        this.phone = phone;
    }

    public byte[] getPIN() {
        return PIN;
    }

    public void setPIN(byte[] PIN) {
        this.PIN = PIN;
    }
	
	public byte[] getWallet() {
        return wallet;
    }

    public void setWallet(byte[] wallet) {
        this.wallet = wallet;
    }

    public byte[] getPriKeyData() {
        return this.priKeyData;
    }

    public void setPriKeyData(byte[] priKeyData) {
        this.priKeyData = priKeyData;
    }


}