package model;

/**
 * Model tuong ung bang Xe trong SQL Server:
 *   MaXe INT IDENTITY(1,1) PRIMARY KEY,
 *   BienSo VARCHAR(15) NOT NULL UNIQUE,
 *   MaLoaiXe INT NOT NULL (FK -> LoaiXe),
 *   TrangThai NVARCHAR(30) DEFAULT 'HoatDong'
 *              CHECK IN ('HoatDong', 'BaoDuong', 'NgungHoatDong')
 *
 * Luu them ten loai xe (tenLoaiXe) de tien hien thi tren JTable
 * (tranh phai JOIN lai mỗi lan render bang), khong luu xuong DB.
 */
public class Xe {

    // 3 trang thai hop le theo CHECK constraint trong DB - dung lam hang so de tranh sai chinh ta
    public static final String TRANG_THAI_HOAT_DONG = "HoatDong";
    public static final String TRANG_THAI_BAO_DUONG = "BaoDuong";
    public static final String TRANG_THAI_NGUNG_HOAT_DONG = "NgungHoatDong";

    private int maXe;
    private String bienSo;
    private int maLoaiXe;
    private String tenLoaiXe;   // chi de hien thi, lay tu JOIN voi LoaiXe
    private int soGhe;          // lay tu LoaiXe.SoGheMacDinh thong qua Factory, chi de hien thi
    private String trangThai;

    public Xe() {
    }

    public Xe(int maXe, String bienSo, int maLoaiXe, String tenLoaiXe, int soGhe, String trangThai) {
        this.maXe = maXe;
        this.bienSo = bienSo;
        this.maLoaiXe = maLoaiXe;
        this.tenLoaiXe = tenLoaiXe;
        this.soGhe = soGhe;
        this.trangThai = trangThai;
    }

    // Constructor dung khi them moi xe (chua co MaXe vi la IDENTITY)
    public Xe(String bienSo, int maLoaiXe, String trangThai) {
        this.bienSo = bienSo;
        this.maLoaiXe = maLoaiXe;
        this.trangThai = trangThai;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
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

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return bienSo + " (" + trangThai + ")";
    }
}