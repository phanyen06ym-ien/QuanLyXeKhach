package model;

import java.time.LocalDate;

/**
 * Model tuong ung bang BaoDuongXe trong SQL Server:
 *   MaBD INT IDENTITY(1,1) PRIMARY KEY,
 *   MaXe INT NOT NULL (FK -> Xe),
 *   NgayBatDau DATE,
 *   NgayKetThuc DATE,
 *   NoiDung NVARCHAR(255)
 *
 * Luu them bienSoXe de hien thi tren JTable (lay tu JOIN voi Xe), khong luu xuong DB.
 */
public class BaoDuongXe {

    private int maBD;
    private int maXe;
    private String bienSoXe;     // chi de hien thi
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String noiDung;

    public BaoDuongXe() {
    }

    public BaoDuongXe(int maBD, int maXe, String bienSoXe, LocalDate ngayBatDau,
                       LocalDate ngayKetThuc, String noiDung) {
        this.maBD = maBD;
        this.maXe = maXe;
        this.bienSoXe = bienSoXe;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.noiDung = noiDung;
    }

    // Constructor dung khi them moi (chua co MaBD)
    public BaoDuongXe(int maXe, LocalDate ngayBatDau, LocalDate ngayKetThuc, String noiDung) {
        this.maXe = maXe;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.noiDung = noiDung;
    }

    public int getMaBD() {
        return maBD;
    }

    public void setMaBD(int maBD) {
        this.maBD = maBD;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public String getBienSoXe() {
        return bienSoXe;
    }

    public void setBienSoXe(String bienSoXe) {
        this.bienSoXe = bienSoXe;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    /**
     * Kiem tra xe co dang trong qua trinh bao duong tai thoi diem hien tai khong
     * (NgayKetThuc null hoac >= hom nay nghia la van con dang bao duong).
     */
    public boolean dangBaoDuong() {
        LocalDate today = LocalDate.now();
        boolean daBatDau = ngayBatDau != null && !ngayBatDau.isAfter(today);
        boolean chuaKetThuc = ngayKetThuc == null || !ngayKetThuc.isBefore(today);
        return daBatDau && chuaKetThuc;
    }

    @Override
    public String toString() {
        return "BD#" + maBD + " - Xe " + bienSoXe;
    }
}