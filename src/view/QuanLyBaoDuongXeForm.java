package ui;

import dao.BaoDuongXeDAO;
import dao.XeDAO;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.BaoDuongXe;
import model.Xe;

/**
 * Form quan ly Bao Duong Xe: them lich bao duong / danh dau hoan tat / sua / xoa.
 *
 * Khi THEM MOI: goi BaoDuongXeDAO.insertWithUpdateTrangThaiXe(...) de dong thoi
 * chuyen Xe.TrangThai sang 'BaoDuong' (transaction, xem giai thich trong DAO).
 *
 * Khi HOAN TAT: goi BaoDuongXeDAO.hoanTatBaoDuong(...) de dong thoi
 * chuyen Xe.TrangThai ve 'HoatDong'.
 *
 * Ngay thang nhap theo dinh dang yyyy-MM-dd (vi du 2025-01-31).
 */
public class QuanLyBaoDuongXeForm extends JFrame {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BaoDuongXeDAO baoDuongXeDAO = new BaoDuongXeDAO();
    private final XeDAO xeDAO = new XeDAO();

    private ArrayList<BaoDuongXe> danhSachBaoDuong = new ArrayList<>();
    private ArrayList<Xe> danhSachXe = new ArrayList<>();

    private JComboBox<Xe> cboXe;
    private JTextField txtNgayBatDau, txtNgayKetThuc, txtNoiDung;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnHoanTat, btnXoa, btnLamMoi;

    private Integer maBDDangChon = null;

    public QuanLyBaoDuongXeForm() {
        setTitle("Quan ly Bao Duong Xe");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        taiDanhSachXe();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin Bao Duong"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Xe:"), gbc);
        gbc.gridx = 1;
        cboXe = new JComboBox<>();
        panelForm.add(cboXe, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panelForm.add(new JLabel("Ngay Bat Dau (yyyy-MM-dd):"), gbc);
        gbc.gridx = 3;
        txtNgayBatDau = new JTextField(12);
        panelForm.add(txtNgayBatDau, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Ngay Ket Thuc (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        txtNgayKetThuc = new JTextField(12);
        panelForm.add(txtNgayKetThuc, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        panelForm.add(new JLabel("Noi Dung:"), gbc);
        gbc.gridx = 3;
        txtNoiDung = new JTextField(20);
        panelForm.add(txtNoiDung, gbc);

        JPanel panelButton = new JPanel(new FlowLayout());
        btnThem = new JButton("Them lich bao duong");
        btnSua = new JButton("Cap nhat");
        btnHoanTat = new JButton("Danh dau hoan tat");
        btnXoa = new JButton("Xoa");
        btnLamMoi = new JButton("Lam moi");
        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnHoanTat);
        panelButton.add(btnXoa);
        panelButton.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panelForm.add(panelButton, gbc);

        tableModel = new DefaultTableModel(
                new Object[]{"Ma BD", "Bien So Xe", "Ngay Bat Dau", "Ngay Ket Thuc", "Noi Dung", "Trang Thai"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panelForm, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnThem.addActionListener(e -> themMoi());
        btnSua.addActionListener(e -> capNhat());
        btnHoanTat.addActionListener(e -> hoanTat());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                BaoDuongXe bd = danhSachBaoDuong.get(row);
                chonXeTrongCombo(bd.getMaXe());
                txtNgayBatDau.setText(bd.getNgayBatDau() != null ? bd.getNgayBatDau().format(DATE_FORMAT) : "");
                txtNgayKetThuc.setText(bd.getNgayKetThuc() != null ? bd.getNgayKetThuc().format(DATE_FORMAT) : "");
                txtNoiDung.setText(bd.getNoiDung());
                maBDDangChon = bd.getMaBD();
            }
        });
    }

    private void taiDanhSachXe() {
        danhSachXe = xeDAO.getAll();
        cboXe.removeAllItems();
        for (Xe xe : danhSachXe) {
            cboXe.addItem(xe);
        }
        if (danhSachXe.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chua co Xe nao trong he thong. Vui long them Xe truoc.",
                    "Thieu du lieu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void chonXeTrongCombo(int maXe) {
        for (int i = 0; i < cboXe.getItemCount(); i++) {
            if (cboXe.getItemAt(i).getMaXe() == maXe) {
                cboXe.setSelectedIndex(i);
                return;
            }
        }
    }

    private void taiDuLieu() {
        danhSachBaoDuong = baoDuongXeDAO.getAll();
        hienThiLenBang(danhSachBaoDuong);
    }

    private void hienThiLenBang(ArrayList<BaoDuongXe> list) {
        tableModel.setRowCount(0);
        for (BaoDuongXe bd : list) {
            String trangThai = bd.dangBaoDuong() ? "Dang bao duong" : "Da hoan tat";
            tableModel.addRow(new Object[]{
                    bd.getMaBD(), bd.getBienSoXe(),
                    bd.getNgayBatDau() != null ? bd.getNgayBatDau().format(DATE_FORMAT) : "",
                    bd.getNgayKetThuc() != null ? bd.getNgayKetThuc().format(DATE_FORMAT) : "",
                    bd.getNoiDung(), trangThai
            });
        }
    }

    /**
     * Parse 1 chuoi ngay thang theo dinh dang yyyy-MM-dd.
     * Cho phep rong (tra ve null) doi voi Ngay Ket Thuc, nhung neu chuoi khong rong
     * ma sai dinh dang thi bao loi va tra ve mot gia tri danh dau loi thong qua exception.
     */
    private LocalDate parseNgay(String text, String tenTruong, boolean batBuoc) {
        if (text == null || text.trim().isEmpty()) {
            if (batBuoc) {
                throw new IllegalArgumentException(tenTruong + " khong duoc de trong.");
            }
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(tenTruong + " sai dinh dang, phai la yyyy-MM-dd.");
        }
    }

    private void themMoi() {
        Xe xeChon = (Xe) cboXe.getSelectedItem();
        if (xeChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon Xe.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngayBatDau, ngayKetThuc;
        try {
            ngayBatDau = parseNgay(txtNgayBatDau.getText(), "Ngay Bat Dau", true);
            ngayKetThuc = parseNgay(txtNgayKetThuc.getText(), "Ngay Ket Thuc", false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ngayKetThuc != null && ngayKetThuc.isBefore(ngayBatDau)) {
            JOptionPane.showMessageDialog(this, "Ngay Ket Thuc phai sau hoac bang Ngay Bat Dau.",
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String noiDung = txtNoiDung.getText().trim();
        BaoDuongXe bdMoi = new BaoDuongXe(xeChon.getMaXe(), ngayBatDau, ngayKetThuc, noiDung);

        if (baoDuongXeDAO.insertWithUpdateTrangThaiXe(bdMoi)) {
            JOptionPane.showMessageDialog(this,
                    "Them lich bao duong thanh cong! Trang thai Xe da chuyen sang 'BaoDuong'.");
            taiDanhSachXe();
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Them moi that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (maBDDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot ban ghi Bao Duong trong bang de cap nhat.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Xe xeChon = (Xe) cboXe.getSelectedItem();
        if (xeChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon Xe.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngayBatDau, ngayKetThuc;
        try {
            ngayBatDau = parseNgay(txtNgayBatDau.getText(), "Ngay Bat Dau", true);
            ngayKetThuc = parseNgay(txtNgayKetThuc.getText(), "Ngay Ket Thuc", false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ngayKetThuc != null && ngayKetThuc.isBefore(ngayBatDau)) {
            JOptionPane.showMessageDialog(this, "Ngay Ket Thuc phai sau hoac bang Ngay Bat Dau.",
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String noiDung = txtNoiDung.getText().trim();
        BaoDuongXe bd = new BaoDuongXe(xeChon.getMaXe(), ngayBatDau, ngayKetThuc, noiDung);
        bd.setMaBD(maBDDangChon);

        // Chi cap nhat thong tin ban ghi Bao Duong, khong tu dong doi trang thai Xe.
        // Dung nut "Danh dau hoan tat" neu muon dong bo trang thai Xe ve HoatDong.
        if (baoDuongXeDAO.update(bd)) {
            JOptionPane.showMessageDialog(this, "Cap nhat thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cap nhat that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Danh dau ban ghi bao duong dang chon la da hoan tat: gan Ngay Ket Thuc = hom nay
     * (neu chua nhap) va dong thoi tra Xe.TrangThai ve 'HoatDong'.
     */
    private void hoanTat() {
        if (maBDDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot ban ghi Bao Duong trong bang.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Xe xeChon = (Xe) cboXe.getSelectedItem();
        if (xeChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon Xe.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngayKetThuc;
        try {
            ngayKetThuc = parseNgay(txtNgayKetThuc.getText(), "Ngay Ket Thuc", false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ngayKetThuc == null) {
            ngayKetThuc = LocalDate.now();
        }

        if (baoDuongXeDAO.hoanTatBaoDuong(maBDDangChon, xeChon.getMaXe(), ngayKetThuc)) {
            JOptionPane.showMessageDialog(this,
                    "Da danh dau hoan tat! Trang thai Xe da tra ve 'HoatDong'.");
            taiDanhSachXe();
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Danh dau hoan tat that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        if (maBDDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot ban ghi Bao Duong trong bang de xoa.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Ban co chac muon xoa ban ghi Bao Duong nay?\n" +
                        "(Luu y: xoa o day khong tu dong doi lai Trang Thai cua Xe.)",
                "Xac nhan xoa", JOptionPane.YES_NO_OPTION);
        if (xacNhan != JOptionPane.YES_OPTION) return;

        if (baoDuongXeDAO.delete(maBDDangChon)) {
            JOptionPane.showMessageDialog(this, "Xoa thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xoa that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoiForm() {
        if (cboXe.getItemCount() > 0) cboXe.setSelectedIndex(0);
        txtNgayBatDau.setText("");
        txtNgayKetThuc.setText("");
        txtNoiDung.setText("");
        maBDDangChon = null;
        table.clearSelection();
        taiDuLieu();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyBaoDuongXeForm().setVisible(true));
    }
}
