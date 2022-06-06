/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paymentapp;

/**
 *
 * @author duong
 */
public class  Customer {
    private static String id;
    private static String name;
    private static String birth;
    private static String phone;
    private static String pin;
    private static String wallet;

    public Customer(String id, String name, String birth, String phone, String pin, String wallet) {
        Customer.id = id;
        Customer.name = name;
        Customer.birth = birth;
        Customer.phone = phone;
        Customer.pin = pin;
        Customer.wallet = wallet;
    }

    public Customer() {
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Customer.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Customer.name = name;
    }

    public static String getBirth() {
        return birth;
    }

    public static void setBirth(String birth) {
        Customer.birth = birth;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        Customer.phone = phone;
    }

    public static String getPin() {
        return pin;
    }

    public static void setPin(String pin) {
        Customer.pin = pin;
    }

    public static String getWallet() {
        return wallet;
    }

    public static void setWallet(String wallet) {
        Customer.wallet = wallet;
    }
    
    
}
