package dao;

import connection.DBConnection;
import model.LoaiXe;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO (Data Access Object) cho bang LoaiXe.
 * Moi method tuong tac truc tiep voi SQL Server qua JDBC,
 * tra ve / nhan vao ArrayList<LoaiXe> de Form (UI) de su dung.
 */
public class LoaiXeDAO {

    /**
     * Lay toan bo danh sach LoaiXe tu DB, tra ve dang ArrayList
     * de hien thi len JTable hoac JComboBox.
     */
    public ArrayList<LoaiXe> getAll() {
        ArrayList<LoaiXe> list = new ArrayList<>();
        String sql = "SELECT MaLoaiXe, TenLoaiXe, SoGheMacDinh FROM LoaiXe ORDER BY MaLoaiXe";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiXe lx = new LoaiXe(
                        rs.getInt("MaLoaiXe"),
                        rs.getString("TenLoaiXe"),
                        rs.getInt("SoGheMacDinh")
                );
                list.add(lx);
            }
        } catch (SQLException e) {
            System.err.println("[LoaiXeDAO] Loi getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Them moi 1 LoaiXe. Tra ve true neu thanh cong.
     */
    public boolean insert(LoaiXe lx) {
        String sql = "INSERT INTO LoaiXe(TenLoaiXe, SoGheMacDinh) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lx.getTenLoaiXe());
            ps.setInt(2, lx.getSoGheMacDinh());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LoaiXeDAO] Loi insert(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cap nhat 1 LoaiXe theo MaLoaiXe. Tra ve true neu thanh cong.
     */
    public boolean update(LoaiXe lx) {
        String sql = "UPDATE LoaiXe SET TenLoaiXe = ?, SoGheMacDinh = ? WHERE MaLoaiXe = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lx.getTenLoaiXe());
            ps.setInt(2, lx.getSoGheMacDinh());
            ps.setInt(3, lx.getMaLoaiXe());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LoaiXeDAO] Loi update(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xoa 1 LoaiXe theo MaLoaiXe.
     * Chu y: vi LoaiXe co FK duoc Xe tham chieu toi (FK_Xe_LoaiXe),
     * neu dang co Xe nao dung LoaiXe nay thi SQL Server se bao loi rang buoc FK.
     * Can bat loi nay len cho nguoi dung biet ("Loai xe nay dang duoc su dung").
     */
    public boolean delete(int maLoaiXe) {
        String sql = "DELETE FROM LoaiXe WHERE MaLoaiXe = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maLoaiXe);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LoaiXeDAO] Loi delete(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiem tra ten loai xe da ton tai chua (vi TenLoaiXe co rang buoc UNIQUE).
     * Dung de bao loi som tren UI truoc khi insert/update, thay vi de SQL Server bao loi.
     */
    public boolean isTenLoaiXeExists(String tenLoaiXe, Integer excludeMaLoaiXe) {
        String sql = "SELECT COUNT(*) FROM LoaiXe WHERE TenLoaiXe = ? AND MaLoaiXe <> ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenLoaiXe);
            ps.setInt(2, excludeMaLoaiXe == null ? -1 : excludeMaLoaiXe);

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
}