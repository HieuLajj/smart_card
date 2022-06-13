package helloworld;
public class Customer {
	private byte[] cccd;
	private byte[] hoten;
	private byte[] ngaysinh;
	private byte[] sdt;
	private byte[] phong;
	private byte[] ngaydk;
	private byte[] mapin;
	private byte[] tien;
	private byte[] anhdaidien;
	
	public Customer(byte[] cccd, byte[] hoten, byte[] ngaysinh, byte[] sdt, byte[] phong, byte[] ngaydk, byte[] mapin, byte[] tien, byte[] anhdaidien) {
        this.cccd = cccd;
        this.hoten = hoten;
        this.ngaysinh = ngaysinh;
        this.sdt = sdt;
        this.phong = phong;
        this.ngaydk = ngaydk;
        this.mapin = mapin;
        this.tien = tien;
        this.anhdaidien = anhdaidien;
    }
    public Customer(){}
    public byte[] getCccd() {
        return cccd;
    }

    public void setCccd(byte[] cccd) {
        this.cccd = cccd;
    }

    public byte[] getHoten() {
        return hoten;
    }

    public void setHoten(byte[] hoten) {
        this.hoten = hoten;
    }

    public byte[] getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(byte[] ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public byte[] getSdt() {
        return sdt;
    }

    public void setSdt(byte[] sdt) {
        this.sdt = sdt;
    }

    public byte[] getPhong() {
        return phong;
    }

    public void setPhong(byte[] phong) {
        this.phong = phong;
    }

    public byte[] getNgaydk() {
        return ngaydk;
    }

    public void setNgaydk(byte[] ngaydk) {
        this.ngaydk = ngaydk;
    }

    public byte[] getMapin() {
        return mapin;
    }

    public void setMapin(byte[] mapin) {
        this.mapin = mapin;
    }  
    public byte[] getTien(){
	    return tien;
    }
    public void setTien(byte[] tien){
	    this.tien=tien;
    }
    public byte[] getAnhdaidien() {
        return anhdaidien;
    }

    public void setAnhdaidien(byte[] anhdaidien) {
        this.anhdaidien = anhdaidien;
    }	
}
