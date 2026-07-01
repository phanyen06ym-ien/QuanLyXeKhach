package factory;

import model.LoaiXe;
import model.Xe;

/**
 * FACTORY PATTERN cho module Quan ly Xe.
 *
 * Y tuong: viec tao mot doi tuong Xe phu thuoc vao LoaiXe duoc chon
 * (vi du: "Xe giuong nam" se mac dinh trang thai HoatDong va lay
 * SoGheMacDinh tu LoaiXe; cac loai xe khac nhau co the can xu ly
 * khac nhau trong tuong lai - vd phu thu them phi, kiem tra dieu kien
 * rieng cho xe limousine...).
 *
 * Factory nay giup tach logic "tao xe moi" ra khoi Form (UI) va DAO,
 * dung mo hinh Factory de de mo rong them loai xe moi sau nay
 * ma khong phai sua code o nhieu noi.
 */
public class XeFactory {

    // Khong cho tao instance, chi dung static method (Simple Factory)
    private XeFactory() {
    }

    /**
     * Tao moi doi tuong Xe dua tren LoaiXe duoc chon.
     *
     * @param bienSo   bien so xe nguoi dung nhap
     * @param loaiXe   doi tuong LoaiXe da chon tu JComboBox (lay tu DB)
     * @return doi tuong Xe da duoc khoi tao day du, san sang luu xuong DB
     */
    public static Xe taoXe(String bienSo, LoaiXe loaiXe) {
        if (loaiXe == null) {
            throw new IllegalArgumentException("Phai chon Loai Xe truoc khi tao Xe moi.");
        }

        Xe xeMoi;

        // Phan nhanh theo ten loai xe - noi de mo rong logic rieng cho tung loai trong tuong lai
        String tenLoai = loaiXe.getTenLoaiXe() == null ? "" : loaiXe.getTenLoaiXe().trim().toLowerCase();

        switch (tenLoai) {
            case "limousine":
                xeMoi = taoXeLimousine(bienSo, loaiXe);
                break;
            case "giuong nam":
            case "xe giuong nam":
                xeMoi = taoXeGiuongNam(bienSo, loaiXe);
                break;
            default:
                xeMoi = taoXeMacDinh(bienSo, loaiXe);
                break;
        }

        return xeMoi;
    }

    // --- Cac phuong thuc tao rieng cho tung loai, de san sua/mo rong logic dac thu ---

    private static Xe taoXeMacDinh(String bienSo, LoaiXe loaiXe) {
        Xe xe = new Xe(bienSo, loaiXe.getMaLoaiXe(), Xe.TRANG_THAI_HOAT_DONG);
        xe.setTenLoaiXe(loaiXe.getTenLoaiXe());
        xe.setSoGhe(loaiXe.getSoGheMacDinh());
        return xe;
    }

    private static Xe taoXeLimousine(String bienSo, LoaiXe loaiXe) {
        // Vi du logic rieng: xe limousine co the gioi han so ghe toi da 12,
        // neu LoaiXe trong DB cau hinh sai thi factory tu dieu chinh lai cho hop ly.
        Xe xe = new Xe(bienSo, loaiXe.getMaLoaiXe(), Xe.TRANG_THAI_HOAT_DONG);
        xe.setTenLoaiXe(loaiXe.getTenLoaiXe());
        int soGhe = loaiXe.getSoGheMacDinh();
        xe.setSoGhe(Math.min(soGhe, 12));
        return xe;
    }

    private static Xe taoXeGiuongNam(String bienSo, LoaiXe loaiXe) {
        Xe xe = new Xe(bienSo, loaiXe.getMaLoaiXe(), Xe.TRANG_THAI_HOAT_DONG);
        xe.setTenLoaiXe(loaiXe.getTenLoaiXe());
        xe.setSoGhe(loaiXe.getSoGheMacDinh());
        return xe;
    }
}