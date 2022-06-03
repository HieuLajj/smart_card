package helloworld;
public class Customer {
	private byte[] cccd;
	private byte[] hoten;
	private byte[] ngaysinh;
	private byte[] sdt;
	private byte[] phong;
	private byte[] ngaydk;
	private byte[] mapin;
	private byte[] anhdaidien;
	private byte[] tien;
	private byte[] dichvuyeucau;
	private byte[] priKeyData;
	// public Customer(byte[] cccd, byte[] hoten, byte[] ngaysinh, byte[] sdt, byte[] phong, byte[] ngaydk, byte[] mapin, byte[] anhdaidien, byte[] tien) {
        // this.cccd = cccd;
        // this.hoten = hoten;
        // this.ngaysinh = ngaysinh;
        // this.sdt = sdt;
        // this.phong = phong;
        // this.ngaydk = ngaydk;
        // this.mapin = mapin;
        // this.anhdaidien = anhdaidien;
        // this.tien = tien;
    // }
    //customer = new Customer(cccd,hoten,ngaysinh,sdt,phong,ngay_dk,mapin,OpImage,tien,dichvuyeucau);
    public Customer(byte[] cccd, byte[] hoten, byte[] ngaysinh, byte[] sdt, byte[] phong, byte[] ngaydk, byte[] mapin, byte[] anhdaidien, byte[] tien,byte[] dichvuyeucau) {
        this.cccd = cccd;
        this.hoten = hoten;
        this.ngaysinh = ngaysinh;
        this.sdt = sdt;
        this.phong = phong;
        this.ngaydk = ngaydk;
        this.mapin = mapin;
        this.anhdaidien = anhdaidien;
        this.tien = tien;
        this.dichvuyeucau = dichvuyeucau;
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
    
    public byte[] getAnhdaidien() {
        return anhdaidien;
    }

    public void setAnhdaidien(byte[] anhdaidien) {
        this.anhdaidien = anhdaidien;
    }
    
    public byte[] getTien(){
	    return tien;
    }
    public void setTien(byte[] tien){
	    this.tien=tien;
    }
    public byte[] getDichvuyeucau(){
	    return dichvuyeucau;
    }
    public void setDichvuyeucau(byte[] dichvuyeucau){
	    this.dichvuyeucau = dichvuyeucau;
    }
    	
}
