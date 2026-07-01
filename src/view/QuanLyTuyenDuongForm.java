package ui;

import dao.TuyenDuongDAO;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.TuyenDuong;

/**
 * Form quan ly Tuyen Duong: them / sua / xoa / tim kiem theo diem di - diem den.
 */
public class QuanLyTuyenDuongForm extends JFrame {

    private final TuyenDuongDAO tuyenDuongDAO = new TuyenDuongDAO();

    private ArrayList<TuyenDuong> danhSachTuyen = new ArrayList<>();

    private JTextField txtDiemDi, txtDiemDen, txtQuangDuong, txtGiaCoBan, txtTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private Integer maTuyenDangChon = null;

    public QuanLyTuyenDuongForm() {
        setTitle("Quan ly Tuyen Duong");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        taiDuLieu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin Tuyen Duong"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Diem Di:"), gbc);
        gbc.gridx = 1;
        txtDiemDi = new JTextField(15);
        panelForm.add(txtDiemDi, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panelForm.add(new JLabel("Diem Den:"), gbc);
        gbc.gridx = 3;
        txtDiemDen = new JTextField(15);
        panelForm.add(txtDiemDen, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Quang Duong (km):"), gbc);
        gbc.gridx = 1;
        txtQuangDuong = new JTextField(15);
        panelForm.add(txtQuangDuong, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        panelForm.add(new JLabel("Gia Co Ban (VND):"), gbc);
        gbc.gridx = 3;
        txtGiaCoBan = new JTextField(15);
        panelForm.add(txtGiaCoBan, gbc);

        JPanel panelButton = new JPanel(new FlowLayout());
        btnThem = new JButton("Them moi");
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
        panelSearch.add(new JLabel("Tim theo diem di / diem den:"));
        txtTimKiem = new JTextField(15);
        panelSearch.add(txtTimKiem);
        btnTimKiem = new JButton("Tim kiem");
        panelSearch.add(btnTimKiem);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panelForm.add(panelSearch, gbc);

        tableModel = new DefaultTableModel(
                new Object[]{"Ma Tuyen", "Diem Di", "Diem Den", "Quang Duong (km)", "Gia Co Ban (VND)"}, 0) {
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
                TuyenDuong td = danhSachTuyen.get(row);
                txtDiemDi.setText(td.getDiemDi());
                txtDiemDen.setText(td.getDiemDen());
                txtQuangDuong.setText(String.valueOf(td.getQuangDuong()));
                txtGiaCoBan.setText(String.valueOf(td.getGiaCoBan()));
                maTuyenDangChon = td.getMaTuyen();
            }
        });
    }

    private void taiDuLieu() {
        danhSachTuyen = tuyenDuongDAO.getAll();
        hienThiLenBang(danhSachTuyen);
    }

    private void hienThiLenBang(ArrayList<TuyenDuong> list) {
        tableModel.setRowCount(0);
        for (TuyenDuong td : list) {
            tableModel.addRow(new Object[]{
                    td.getMaTuyen(), td.getDiemDi(), td.getDiemDen(), td.getQuangDuong(), td.getGiaCoBan()
            });
        }
    }

    /**
     * Doc va kiem tra Quang Duong / Gia Co Ban tu cac o nhap.
     * Tra ve mang double[]{quangDuong, giaCoBan} neu hop le, hoac null neu loi
     * (da hien thi thong bao loi ben trong ham nay).
     */
    private double[] docVaKiemTraSo() {
        double quangDuong, giaCoBan;
        try {
            quangDuong = Double.parseDouble(txtQuangDuong.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quang Duong phai la so.",
                    "Sai dinh dang", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            giaCoBan = Double.parseDouble(txtGiaCoBan.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Gia Co Ban phai la so.",
                    "Sai dinh dang", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (quangDuong <= 0) {
            JOptionPane.showMessageDialog(this, "Quang Duong phai lon hon 0.",
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (giaCoBan <= 0) {
            JOptionPane.showMessageDialog(this, "Gia Co Ban phai lon hon 0.",
                    "Sai du lieu", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new double[]{quangDuong, giaCoBan};
    }

    private void themMoi() {
        String diemDi = txtDiemDi.getText().trim();
        String diemDen = txtDiemDen.getText().trim();

        if (diemDi.isEmpty() || diemDen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap Diem Di va Diem Den.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double[] so = docVaKiemTraSo();
        if (so == null) return;

        TuyenDuong tuyenMoi = new TuyenDuong(diemDi, diemDen, so[0], so[1]);

        if (tuyenDuongDAO.insert(tuyenMoi)) {
            JOptionPane.showMessageDialog(this, "Them moi thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Them moi that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (maTuyenDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot Tuyen Duong trong bang de cap nhat.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String diemDi = txtDiemDi.getText().trim();
        String diemDen = txtDiemDen.getText().trim();

        if (diemDi.isEmpty() || diemDen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap day du thong tin.",
                    "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double[] so = docVaKiemTraSo();
        if (so == null) return;

        TuyenDuong td = new TuyenDuong(diemDi, diemDen, so[0], so[1]);
        td.setMaTuyen(maTuyenDangChon);

        if (tuyenDuongDAO.update(td)) {
            JOptionPane.showMessageDialog(this, "Cap nhat thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cap nhat that bai.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        if (maTuyenDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot Tuyen Duong trong bang de xoa.",
                    "Chua chon du lieu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Ban co chac muon xoa Tuyen Duong nay?", "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION);
        if (xacNhan != JOptionPane.YES_OPTION) return;

        if (tuyenDuongDAO.delete(maTuyenDangChon)) {
            JOptionPane.showMessageDialog(this, "Xoa thanh cong!");
            taiDuLieu();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Khong the xoa! Tuyen Duong nay co the dang co Chuyen Xe lien quan.",
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            taiDuLieu();
            return;
        }
        danhSachTuyen = tuyenDuongDAO.search(keyword);
        hienThiLenBang(danhSachTuyen);
    }

    private void lamMoiForm() {
        txtDiemDi.setText("");
        txtDiemDen.setText("");
        txtQuangDuong.setText("");
        txtGiaCoBan.setText("");
        txtTimKiem.setText("");
        maTuyenDangChon = null;
        table.clearSelection();
        taiDuLieu();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyTuyenDuongForm().setVisible(true));
    }
}
