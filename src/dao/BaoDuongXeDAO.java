package dao;

import connection.DBConnection;
import model.BaoDuongXe;
import model.Xe;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * DAO cho bang BaoDuongXe.
 *
 * Day la phan quan trong nhat ve "logic nghiep vu chinh" cua TV2:
 * khi them lich bao duong moi, phai dong thoi cap nhat Xe.TrangThai = 'BaoDuong'
 * (va nguoc lai, khi danh dau bao duong da xong, tra Xe.TrangThai ve 'HoatDong').
 * Vi day la 2 cau lenh UPDATE/INSERT tren 2 bang khac nhau nhung phai cung thanh cong
 * hoac cung thien bai (atomic), nen dung TRANSACTION (Connection.setAutoCommit(false)).
 */
public class BaoDuongXeDAO {

    private static final String SELECT_BASE =
            "SELECT bd.MaBD, bd.MaXe, x.BienSo, bd.NgayBatDau, bd.NgayKetThuc, bd.NoiDung " +
            "FROM BaoDuongXe bd " +
            "INNER JOIN Xe x ON bd.MaXe = x.MaXe ";

    public ArrayList<BaoDuongXe> getAll() {
        ArrayList<BaoDuongXe> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY bd.MaBD DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BaoDuongXeDAO] Loi getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lay danh sach lich su bao duong cua 1 xe cu the.
     */
    public ArrayList<BaoDuongXe> getByMaXe(int maXe) {
        ArrayList<BaoDuongXe> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE bd.MaXe = ? ORDER BY bd.NgayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maXe);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BaoDuongXeDAO] Loi getByMaXe(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Them moi 1 lich bao duong VA dong thoi cap nhat Xe.TrangThai = 'BaoDuong'.
     * Dung TRANSACTION de bao dam 2 thao tac nay cung thanh cong hoac cung roll back,
     * tranh tinh trang du lieu khong dong bo (vi du them duoc lich bao duong nhung
     * trang thai xe van la HoatDong do loi giua duong).
     */
    public boolean insertWithUpdateTrangThaiXe(BaoDuongXe bd) {
        String sqlInsert = "INSERT INTO BaoDuongXe(MaXe, NgayBatDau, NgayKetThuc, NoiDung) " +
                            "VALUES (?, ?, ?, ?)";
        String sqlUpdateXe = "UPDATE Xe SET TrangThai = ? WHERE MaXe = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // bat dau transaction

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, bd.getMaXe());
                psInsert.setDate(2, bd.getNgayBatDau() != null ? Date.valueOf(bd.getNgayBatDau()) : null);
                psInsert.setDate(3, bd.getNgayKetThuc() != null ? Date.valueOf(bd.getNgayKetThuc()) : null);
                psInsert.setString(4, bd.getNoiDung());
                psInsert.executeUpdate();
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateXe)) {
                psUpdate.setString(1, Xe.TRANG_THAI_BAO_DUONG);
                psUpdate.setInt(2, bd.getMaXe());
                psUpdate.executeUpdate();
            }

            conn.commit(); // ca 2 thao tac thanh cong -> luu vinh vien
            return true;

        } catch (SQLException e) {
            System.err.println("[BaoDuongXeDAO] Loi insertWithUpdateTrangThaiXe(): " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // co loi -> huy toan bo thao tac
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // tra lai trang thai mac dinh cho connection
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Danh dau bao duong da hoan tat: cap nhat NgayKetThuc cho ban ghi bao duong
     * VA dong thoi tra Xe.TrangThai ve 'HoatDong'. Cung dung TRANSACTION nhu tren.
     */
    public boolean hoanTatBaoDuong(int maBD, int maXe, LocalDate ngayKetThuc) {
        String sqlUpdateBD = "UPDATE BaoDuongXe SET NgayKetThuc = ? WHERE MaBD = ?";
        String sqlUpdateXe = "UPDATE Xe SET TrangThai = ? WHERE MaXe = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psBD = conn.prepareStatement(sqlUpdateBD)) {
                psBD.setDate(1, Date.valueOf(ngayKetThuc));
                psBD.setInt(2, maBD);
                psBD.executeUpdate();
            }

            try (PreparedStatement psXe = conn.prepareStatement(sqlUpdateXe)) {
                psXe.setString(1, Xe.TRANG_THAI_HOAT_DONG);
                psXe.setInt(2, maXe);
                psXe.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("[BaoDuongXeDAO] Loi hoanTatBaoDuong(): " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cap nhat thong tin 1 ban ghi bao duong (khong dong bo trang thai xe,
     * dung cho truong hop chi sua noi dung/ngay thang, khong doi trang thai).
     */
    public boolean update(BaoDuongXe bd) {
        String sql = "UPDATE BaoDuongXe SET MaXe = ?, NgayBatDau = ?, NgayKetThuc = ?, NoiDung = ? " +
                     "WHERE MaBD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bd.getMaXe());
            ps.setDate(2, bd.getNgayBatDau() != null ? Date.valueOf(bd.getNgayBatDau()) : null);
            ps.setDate(3, bd.getNgayKetThuc() != null ? Date.valueOf(bd.getNgayKetThuc()) : null);
            ps.setString(4, bd.getNoiDung());
            ps.setInt(5, bd.getMaBD());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BaoDuongXeDAO] Loi update(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int maBD) {
        String sql = "DELETE FROM BaoDuongXe WHERE MaBD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maBD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BaoDuongXeDAO] Loi delete(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private BaoDuongXe mapRow(ResultSet rs) throws SQLException {
        Date ngayBatDau = rs.getDate("NgayBatDau");
        Date ngayKetThuc = rs.getDate("NgayKetThuc");

        return new BaoDuongXe(
                rs.getInt("MaBD"),
                rs.getInt("MaXe"),
                rs.getString("BienSo"),
                ngayBatDau != null ? ngayBatDau.toLocalDate() : null,
                ngayKetThuc != null ? ngayKetThuc.toLocalDate() : null,
                rs.getString("NoiDung")
        );
    }
}