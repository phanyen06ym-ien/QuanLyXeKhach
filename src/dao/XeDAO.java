package dao;

import connection.DBConnection;
import model.Xe;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO cho bang Xe.
 * Tat ca query SELECT deu JOIN voi LoaiXe de lay ten loai xe + so ghe mac dinh,
 * giup hien thi day du thong tin tren JTable ma khong can goi them DAO khac.
 */
public class XeDAO {

    private static final String SELECT_BASE =
            "SELECT x.MaXe, x.BienSo, x.MaLoaiXe, lx.TenLoaiXe, lx.SoGheMacDinh, x.TrangThai " +
            "FROM Xe x " +
            "INNER JOIN LoaiXe lx ON x.MaLoaiXe = lx.MaLoaiXe ";

    /**
     * Lay toan bo danh sach Xe (kem ten loai xe, so ghe) tu DB.
     */
    public ArrayList<Xe> getAll() {
        ArrayList<Xe> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY x.MaXe";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[XeDAO] Loi getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tim kiem xe theo bien so (LIKE), dung cho chuc nang tra cuu/loc tren Form.
     */
    public ArrayList<Xe> searchByBienSo(String keyword) {
        ArrayList<Xe> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE x.BienSo LIKE ? ORDER BY x.MaXe";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[XeDAO] Loi searchByBienSo(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Them moi 1 Xe (doi tuong Xe nen duoc tao tu XeFactory truoc khi goi method nay).
     */
    public boolean insert(Xe xe) {
        String sql = "INSERT INTO Xe(BienSo, MaLoaiXe, TrangThai) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, xe.getBienSo());
            ps.setInt(2, xe.getMaLoaiXe());
            ps.setString(3, xe.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[XeDAO] Loi insert(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cap nhat thong tin Xe (bien so, loai xe, trang thai).
     */
    public boolean update(Xe xe) {
        String sql = "UPDATE Xe SET BienSo = ?, MaLoaiXe = ?, TrangThai = ? WHERE MaXe = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, xe.getBienSo());
            ps.setInt(2, xe.getMaLoaiXe());
            ps.setString(3, xe.getTrangThai());
            ps.setInt(4, xe.getMaXe());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[XeDAO] Loi update(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cap nhat CHI TrangThai cua xe.
     * Day la logic nghiep vu chinh ghi trong bang phan cong:
     * "cap nhat trang thai xe khi bao duong" - duoc goi tu module BaoDuongXe
     * khi nguoi dung them lich bao duong moi hoac khi bao duong hoan tat.
     */
    public boolean updateTrangThai(int maXe, String trangThaiMoi) {
        String sql = "UPDATE Xe SET TrangThai = ? WHERE MaXe = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maXe);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[XeDAO] Loi updateTrangThai(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xoa 1 Xe theo MaXe.
     * Chu y: Xe duoc tham chieu boi ChuyenXe (FK_ChuyenXe_Xe) va BaoDuongXe (FK_BDX_Xe).
     * Neu xe da co chuyen hoac lich su bao duong, SQL Server se chan xoa (loi FK).
     */
    public boolean delete(int maXe) {
        String sql = "DELETE FROM Xe WHERE MaXe = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maXe);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[XeDAO] Loi delete(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiem tra bien so da ton tai chua (BienSo co rang buoc UNIQUE trong DB).
     */
    public boolean isBienSoExists(String bienSo, Integer excludeMaXe) {
        String sql = "SELECT COUNT(*) FROM Xe WHERE BienSo = ? AND MaXe <> ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bienSo);
            ps.setInt(2, excludeMaXe == null ? -1 : excludeMaXe);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper: chuyen 1 dong ResultSet thanh doi tuong Xe
    private Xe mapRow(ResultSet rs) throws SQLException {
        Xe xe = new Xe();
        xe.setMaXe(rs.getInt("MaXe"));
        xe.setBienSo(rs.getString("BienSo"));
        xe.setMaLoaiXe(rs.getInt("MaLoaiXe"));
        xe.setTenLoaiXe(rs.getString("TenLoaiXe"));
        xe.setSoGhe(rs.getInt("SoGheMacDinh"));
        xe.setTrangThai(rs.getString("TrangThai"));
        return xe;
    }
}