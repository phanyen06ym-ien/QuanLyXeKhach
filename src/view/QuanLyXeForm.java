package ui;

import dao.LoaiXeDAO;
import dao.XeDAO;
import factory.XeFactory;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.LoaiXe;
import model.Xe;

/**
 * Form quan ly Xe: them / sua / xoa / tim kiem theo bien so.
 * Khi them moi, doi tuong Xe duoc tao thong qua XeFactory (Factory Pattern)
 * dua tren LoaiXe da chon, dam bao logic tao xe duoc tap trung mot noi.
 */
public class QuanLyXeForm extends JFrame {

    private final XeDAO xeDAO = new XeDAO();
    private final LoaiXeDAO loaiXeDAO = new LoaiXeDAO();

    private ArrayList<Xe> danhSachXe = new ArrayList<>();
    private ArrayList<LoaiXe> danhSachLoaiXe = new ArrayList<>();

    private JTextField txtBienSo, txtTimKiem;
    private JComboBox<LoaiXe> cboLoaiXe;
    private JComboBox<String> cboTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private Integer maXeDangChon = null;

    public QuanLyXeForm() {
        setTitle("Quan ly Xe");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        taiDanhSachLoaiXe();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin Xe"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Bien So:"), gbc);
        gbc.gridx = 1;
        txtBienSo = new JTextField(15);
        panelForm.add(txtBienSo, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panelForm.add(new JLabel("Loai Xe:"), gbc);
        gbc.gridx = 3;
        cboLoaiXe = new JComboBox<>();
        panelForm.add(cboLoaiXe, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Trang Thai:"), gbc);
        gbc.gridx = 1;
        // Dung dung 3 gia tri theo CHECK constraint trong DB
        cboTrangThai = new JComboBox<>(new String[]{
                Xe.TRANG_THAI_HOAT_DONG, Xe.TRANG_THAI_BAO_DUONG, Xe.TRANG_THAI_NGUNG_HOAT_DONG
        });
        panelForm.add(cboTrangThai, gbc);

        JPanel panelButton = new JPanel(new FlowLayout());
        btnThem = new JButton("Them moi (Factory)");
        btnSua = new JButton("Cap nhat");
        btnXoa = new JButton("Xoa");
        btnLamMoi = new JButton("Lam moi");
        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnXoa);
        panelButton.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panelForm.add(panelButton, gbc);

        // Panel tim kiem
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSearch.add(new JLabel("Tim theo bien so:"));
        txtTimKiem = new JTextField(15);
        panelSearch.add(txtTimKiem);
        btnTimKiem = new JButton("Tim kiem");
        panelSearch.add(btnTimKiem);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panelForm.add(panelSearch, gbc);

        tableModel = new DefaultTableModel(
                new Object[]{"Ma Xe", "Bien So", "Loai Xe", "So Ghe", "Trang Thai"}, 0) {
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
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnTimKiem.addActionListener(e -> timKiem());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Xe xe = danhSachXe.get(row);
                txtBienSo.setText(xe.getBienSo());
                chonLoaiXeTrongCombo(xe.getMaLoaiXe());
                cboTrangThai.setSelectedItem(xe.getTrangThai());
                maXeDangChon = xe.getMaXe();
            }
        });
    }

    private void taiDanhSachLoaiXe() {
        danhSachLoaiXe = loaiXeDAO.getAll();
        cboLoaiXe.removeAllItems();
        for (LoaiXe lx : danhSachLoaiXe) {
            cboLoaiXe.addItem(lx);
        }
        if (danhSachLoaiXe.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chua co Loai Xe nao trong he thong. Vui long them Loai Xe truoc.",
                    "Thieu du lieu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void chonLoaiXeTrongCombo(int maLoaiXe) {
        for (int i = 0; i < cboLoaiXe.getItemCount(); i++) {
            if (cboLoaiXe.getItemAt(i).getMaLoaiXe() == maLoaiXe) {
                cboLoaiXe.setSelectedIndex(i);
                return;
            }
        }
    }

    private void taiDuLieu() {
        danhSachXe = xeDAO.getAll();
        hienThiLenBang(danhSachXe);
    }

    private void hienThiLenBang(ArrayList<Xe> list) {
        tableModel.setRowCount(0);
        for (Xe xe : list) {
            tableModel.addRow(new Object[]{
                    xe.getMaXe(), xe.getBienSo(), xe.getTenLoaiXe(), xe.getSoGhe(), xe.getTrangThai()
            });
        }
    }

    /**
     * THEM MOI - day la noi the hien ro Factory Pattern:
     * khong tu tay "new Xe(...)" o Form, ma goi XeFactory.taoXe(...)
     * de logic tao xe (gan so ghe theo loai, dieu chinh quy tac rieng tung loai...)
     * duoc tap trung trong factory, Form chi lo phan UI va goi DAO luu xuong DB.
     */
    private void themMoi() {
        String bienSo = txtBienSo.getText().trim();
        LoaiXe loaiXeChon = (LoaiXe) cboLoaiXe.getSelectedItem();

        if (bienSo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap Bien So.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (loaiXeChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon Loai Xe.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (xeDAO.isBienSoExists(bienSo, null)) {
            JOptionPane.showMessageDialog(this, "Bien So nay da ton tai.",
                    "Trung du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ===== Goi Factory de tao doi tuong Xe =====
        Xe xeMoi = XeFactory.taoXe(bienSo, loaiXeChon);

        if (xeDAO.insert(xeMoi)) {
            JOptionPane.showMessageDialog(this, "Them moi thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Them moi that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (maXeDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot Xe trong bang de cap nhat.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bienSo = txtBienSo.getText().trim();
        LoaiXe loaiXeChon = (LoaiXe) cboLoaiXe.getSelectedItem();
        String trangThai = (String) cboTrangThai.getSelectedItem();

        if (bienSo.isEmpty() || loaiXeChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long nhap day du thong tin.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (xeDAO.isBienSoExists(bienSo, maXeDangChon)) {
            JOptionPane.showMessageDialog(this, "Bien So nay da ton tai.",
                    "Trung du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Xe xe = new Xe(bienSo, loaiXeChon.getMaLoaiXe(), trangThai);
        xe.setMaXe(maXeDangChon);

        if (xeDAO.update(xe)) {
            JOptionPane.showMessageDialog(this, "Cap nhat thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cap nhat that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        if (maXeDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot Xe trong bang de xoa.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Ban co chac muon xoa Xe nay?", "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION);
        if (xacNhan != JOptionPane.YES_OPTION) return;

        if (xeDAO.delete(maXeDangChon)) {
            JOptionPane.showMessageDialog(this, "Xoa thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Khong the xoa! Xe nay co the dang co Chuyen Xe hoac Lich Su Bao Duong lien quan.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            taiDuLieu();
            return;
        }
        danhSachXe = xeDAO.searchByBienSo(keyword);
        hienThiLenBang(danhSachXe);
    }

    private void lamMoiForm() {
        txtBienSo.setText("");
        txtTimKiem.setText("");
        if (cboLoaiXe.getItemCount() > 0) cboLoaiXe.setSelectedIndex(0);
        cboTrangThai.setSelectedItem(Xe.TRANG_THAI_HOAT_DONG);
        maXeDangChon = null;
        table.clearSelection();
        taiDuLieu();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyXeForm().setVisible(true));
    }
}