package dao;

import connection.DBConnection;
import model.TuyenDuong;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO cho bang TuyenDuong.
 */
public class TuyenDuongDAO {

    public ArrayList<TuyenDuong> getAll() {
        ArrayList<TuyenDuong> list = new ArrayList<>();
        String sql = "SELECT MaTuyen, DiemDi, DiemDen, QuangDuong, GiaCoBan " +
                     "FROM TuyenDuong ORDER BY MaTuyen";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TuyenDuongDAO] Loi getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tim tuyen duong theo diem di hoac diem den (dung cho thanh tim kiem tren Form).
     */
    public ArrayList<TuyenDuong> search(String keyword) {
        ArrayList<TuyenDuong> list = new ArrayList<>();
        String sql = "SELECT MaTuyen, DiemDi, DiemDen, QuangDuong, GiaCoBan " +
                     "FROM TuyenDuong WHERE DiemDi LIKE ? OR DiemDen LIKE ? ORDER BY MaTuyen";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[TuyenDuongDAO] Loi search(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(TuyenDuong td) {
        String sql = "INSERT INTO TuyenDuong(DiemDi, DiemDen, QuangDuong, GiaCoBan) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, td.getDiemDi());
            ps.setString(2, td.getDiemDen());
            ps.setDouble(3, td.getQuangDuong());
            ps.setDouble(4, td.getGiaCoBan());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TuyenDuongDAO] Loi insert(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(TuyenDuong td) {
        String sql = "UPDATE TuyenDuong SET DiemDi = ?, DiemDen = ?, QuangDuong = ?, GiaCoBan = ? " +
                     "WHERE MaTuyen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, td.getDiemDi());
            ps.setString(2, td.getDiemDen());
            ps.setDouble(3, td.getQuangDuong());
            ps.setDouble(4, td.getGiaCoBan());
            ps.setInt(5, td.getMaTuyen());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TuyenDuongDAO] Loi update(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xoa 1 TuyenDuong theo MaTuyen.
     * Chu y: TuyenDuong duoc tham chieu boi ChuyenXe (FK_ChuyenXe_Tuyen).
     * Neu tuyen nay da co chuyen xe nao dung, SQL Server se chan xoa.
     */
    public boolean delete(int maTuyen) {
        String sql = "DELETE FROM TuyenDuong WHERE MaTuyen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maTuyen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TuyenDuongDAO] Loi delete(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private TuyenDuong mapRow(ResultSet rs) throws SQLException {
        return new TuyenDuong(
                rs.getInt("MaTuyen"),
                rs.getString("DiemDi"),
                rs.getString("DiemDen"),
                rs.getDouble("QuangDuong"),
                rs.getDouble("GiaCoBan")
        );
    }
}