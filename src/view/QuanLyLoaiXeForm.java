package ui;

import dao.LoaiXeDAO;
import model.LoaiXe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Form quan ly Loai Xe: them / sua / xoa / hien thi danh sach.
 * Du lieu duoc cache trong ArrayList<LoaiXe>, dong bo voi SQL Server qua LoaiXeDAO.
 */
public class QuanLyLoaiXeForm extends JFrame {

    private final LoaiXeDAO loaiXeDAO = new LoaiXeDAO();
    private ArrayList<LoaiXe> danhSachLoaiXe = new ArrayList<>();

    private JTextField txtTenLoaiXe;
    private JSpinner spnSoGhe;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    private Integer maLoaiXeDangChon = null; // null = dang them moi, khac null = dang sua

    public QuanLyLoaiXeForm() {
        setTitle("Quan ly Loai Xe");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ----- Panel nhap lieu (phia tren) -----
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin Loai Xe"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Ten Loai Xe:"), gbc);
        gbc.gridx = 1;
        txtTenLoaiXe = new JTextField(20);
        panelForm.add(txtTenLoaiXe, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("So Ghe Mac Dinh:"), gbc);
        gbc.gridx = 1;
        spnSoGhe = new JSpinner(new SpinnerNumberModel(45, 1, 100, 1));
        panelForm.add(spnSoGhe, gbc);

        // ----- Panel nut bam -----
        JPanel panelButton = new JPanel(new FlowLayout());
        btnThem = new JButton("Them moi");
        btnSua = new JButton("Cap nhat");
        btnXoa = new JButton("Xoa");
        btnLamMoi = new JButton("Lam moi");

        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnXoa);
        panelButton.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(panelButton, gbc);

        // ----- Bang danh sach -----
        tableModel = new DefaultTableModel(
                new Object[]{"Ma Loai Xe", "Ten Loai Xe", "So Ghe Mac Dinh"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // khong cho sua truc tiep tren bang
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panelForm, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ----- Gan su kien -----
        btnThem.addActionListener(e -> themMoi());
        btnSua.addActionListener(e -> capNhat());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                LoaiXe lx = danhSachLoaiXe.get(row);
                txtTenLoaiXe.setText(lx.getTenLoaiXe());
                spnSoGhe.setValue(lx.getSoGheMacDinh());
                maLoaiXeDangChon = lx.getMaLoaiXe();
            }
        });
    }

    /**
     * Tai du lieu tu SQL Server vao ArrayList, sau do do len JTable.
     */
    private void taiDuLieu() {
        danhSachLoaiXe = loaiXeDAO.getAll();
        hienThiLenBang();
    }

    private void hienThiLenBang() {
        tableModel.setRowCount(0);
        for (LoaiXe lx : danhSachLoaiXe) {
            tableModel.addRow(new Object[]{
                    lx.getMaLoaiXe(), lx.getTenLoaiXe(), lx.getSoGheMacDinh()
            });
        }
    }

    private void themMoi() {
        String ten = txtTenLoaiXe.getText().trim();
        int soGhe = (Integer) spnSoGhe.getValue();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap Ten Loai Xe.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (loaiXeDAO.isTenLoaiXeExists(ten, null)) {
            JOptionPane.showMessageDialog(this, "Ten Loai Xe nay da ton tai.",
                    "Trung du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LoaiXe lxMoi = new LoaiXe(ten, soGhe);
        if (loaiXeDAO.insert(lxMoi)) {
            JOptionPane.showMessageDialog(this, "Them moi thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Them moi that bai. Kiem tra lai du lieu.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (maLoaiXeDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot Loai Xe trong bang de cap nhat.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ten = txtTenLoaiXe.getText().trim();
        int soGhe = (Integer) spnSoGhe.getValue();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap Ten Loai Xe.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (loaiXeDAO.isTenLoaiXeExists(ten, maLoaiXeDangChon)) {
            JOptionPane.showMessageDialog(this, "Ten Loai Xe nay da ton tai.",
                    "Trung du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LoaiXe lx = new LoaiXe(maLoaiXeDangChon, ten, soGhe);
        if (loaiXeDAO.update(lx)) {
            JOptionPane.showMessageDialog(this, "Cap nhat thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cap nhat that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        if (maLoaiXeDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot Loai Xe trong bang de xoa.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Ban co chac muon xoa Loai Xe nay?", "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION);
        if (xacNhan != JOptionPane.YES_OPTION) return;

        if (loaiXeDAO.delete(maLoaiXeDangChon)) {
            JOptionPane.showMessageDialog(this, "Xoa thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            // Loi thuong gap nhat: loai xe nay dang duoc 1 hoac nhieu Xe su dung (rang buoc FK)
            JOptionPane.showMessageDialog(this,
                    "Khong the xoa! Loai Xe nay co the dang duoc su dung boi mot hoac nhieu Xe.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoiForm() {
        txtTenLoaiXe.setText("");
        spnSoGhe.setValue(45);
        maLoaiXeDangChon = null;
        table.clearSelection();
    }

    // Cho phep chay rieng form nay de test doc lap
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyLoaiXeForm().setVisible(true));
    }
}