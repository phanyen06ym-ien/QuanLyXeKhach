package model;

/**
 * Model tuong ung bang TuyenDuong trong SQL Server:
 *   MaTuyen INT IDENTITY(1,1) PRIMARY KEY,
 *   DiemDi NVARCHAR(100) NOT NULL,
 *   DiemDen NVARCHAR(100) NOT NULL,
 *   QuangDuong FLOAT CHECK(QuangDuong > 0),
 *   GiaCoBan DECIMAL(18,2) CHECK(GiaCoBan > 0)
 */
public class TuyenDuong {

    private int maTuyen;
    private String diemDi;
    private String diemDen;
    private double quangDuong;   // don vi: km
    private double giaCoBan;     // don vi: VND

    public TuyenDuong() {
    }

    public TuyenDuong(int maTuyen, String diemDi, String diemDen, double quangDuong, double giaCoBan) {
        this.maTuyen = maTuyen;
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.quangDuong = quangDuong;
        this.giaCoBan = giaCoBan;
    }

    // Constructor dung khi them moi (chua co MaTuyen)
    public TuyenDuong(String diemDi, String diemDen, double quangDuong, double giaCoBan) {
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.quangDuong = quangDuong;
        this.giaCoBan = giaCoBan;
    }

    public int getMaTuyen() {
        return maTuyen;
    }

    public void setMaTuyen(int maTuyen) {
        this.maTuyen = maTuyen;
    }

    public String getDiemDi() {
        return diemDi;
    }

    public void setDiemDi(String diemDi) {
        this.diemDi = diemDi;
    }

    public String getDiemDen() {
        return diemDen;
    }

    public void setDiemDen(String diemDen) {
        this.diemDen = diemDen;
    }

    public double getQuangDuong() {
        return quangDuong;
    }

    public void setQuangDuong(double quangDuong) {
        this.quangDuong = quangDuong;
    }

    public double getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(double giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    @Override
    public String toString() {
        return diemDi + " - " + diemDen;
    }
}