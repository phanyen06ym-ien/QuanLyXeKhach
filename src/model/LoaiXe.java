package model;

/**
 * Model tuong ung bang LoaiXe trong SQL Server:
 *   MaLoaiXe INT IDENTITY(1,1) PRIMARY KEY,
 *   TenLoaiXe NVARCHAR(50) NOT NULL UNIQUE,
 *   SoGheMacDinh INT CHECK(SoGheMacDinh > 0)
 */
public class LoaiXe {

    private int maLoaiXe;
    private String tenLoaiXe;
    private int soGheMacDinh;

    public LoaiXe() {
    }

    public LoaiXe(int maLoaiXe, String tenLoaiXe, int soGheMacDinh) {
        this.maLoaiXe = maLoaiXe;
        this.tenLoaiXe = tenLoaiXe;
        this.soGheMacDinh = soGheMacDinh;
    }

    // Constructor khong co MaLoaiXe, dung khi them moi (MaLoaiXe la IDENTITY, DB tu sinh)
    public LoaiXe(String tenLoaiXe, int soGheMacDinh) {
        this.tenLoaiXe = tenLoaiXe;
        this.soGheMacDinh = soGheMacDinh;
    }

    public int getMaLoaiXe() {
        return maLoaiXe;
    }

    public void setMaLoaiXe(int maLoaiXe) {
        this.maLoaiXe = maLoaiXe;
    }

    public String getTenLoaiXe() {
        return tenLoaiXe;
    }

    public void setTenLoaiXe(String tenLoaiXe) {
        this.tenLoaiXe = tenLoaiXe;
    }

    public int getSoGheMacDinh() {
        return soGheMacDinh;
    }

    public void setSoGheMacDinh(int soGheMacDinh) {
        this.soGheMacDinh = soGheMacDinh;
    }

    @Override
    public String toString() {
        // Quan trong: toString() nay se duoc dung khi hien thi LoaiXe trong JComboBox
        return tenLoaiXe;
    }
}