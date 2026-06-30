-- TẠO DATABASE

CREATE DATABASE QuanLyXeKhach;
GO

USE QuanLyXeKhach;
GO

-- TẠO TABLE

  -- 1. VAI TRO

CREATE TABLE VaiTro(
    MaVaiTro INT IDENTITY(1,1) PRIMARY KEY,
    TenVaiTro NVARCHAR(50) NOT NULL UNIQUE
);

-- 2. TAI KHOAN

CREATE TABLE TaiKhoan(
    MaTK INT IDENTITY(1,1) PRIMARY KEY,

    TenDangNhap VARCHAR(50)
        NOT NULL UNIQUE,

    MatKhau VARCHAR(255)
        NOT NULL,

    TrangThai NVARCHAR(20)
        DEFAULT N'HoatDong'
        CHECK(
            TrangThai IN
            (
                N'HoatDong',
                N'Khoa'
            )
        ),

    NgayTao DATETIME
        DEFAULT GETDATE()
);

-- 3. NHAN VIEN

CREATE TABLE NhanVien(
    MaNV INT IDENTITY(1,1) PRIMARY KEY,

    HoTen NVARCHAR(100) NOT NULL,

    NgaySinh DATE,

    GioiTinh NVARCHAR(10)
        CHECK(
            GioiTinh IN
            (
                N'Nam',
                N'Nu'
            )
        ),

    CCCD VARCHAR(12) UNIQUE,

    SDT VARCHAR(10) UNIQUE,

    Email VARCHAR(100),

    DiaChi NVARCHAR(255),

    NgayVaoLam DATE
        DEFAULT GETDATE(),

    LuongCoBan DECIMAL(18,2)
        CHECK(LuongCoBan >= 0),

    MaVaiTro INT NOT NULL,

    MaTK INT UNIQUE,

    CONSTRAINT FK_NV_VaiTro
        FOREIGN KEY(MaVaiTro)
        REFERENCES VaiTro(MaVaiTro),

    CONSTRAINT FK_NV_TK
        FOREIGN KEY(MaTK)
        REFERENCES TaiKhoan(MaTK)
);

-- 4. KHACH HANG

CREATE TABLE KhachHang(
    MaKH INT IDENTITY(1,1) PRIMARY KEY,

    HoTen NVARCHAR(100) NOT NULL,

    SDT VARCHAR(10)
        NOT NULL UNIQUE,

    CCCD VARCHAR(12) UNIQUE,

    Email VARCHAR(100),

    DiaChi NVARCHAR(255),

    MaTK INT UNIQUE,

    CONSTRAINT FK_KH_TK
        FOREIGN KEY(MaTK)
        REFERENCES TaiKhoan(MaTK)
);

-- 5. LOAI XE

CREATE TABLE LoaiXe(
    MaLoaiXe INT IDENTITY(1,1) PRIMARY KEY,

    TenLoaiXe NVARCHAR(50)
        NOT NULL UNIQUE,

    SoGheMacDinh INT
        CHECK(SoGheMacDinh > 0)
);

-- 6. XE

CREATE TABLE Xe(
    MaXe INT IDENTITY(1,1) PRIMARY KEY,

    BienSo VARCHAR(15)
        NOT NULL UNIQUE,

    MaLoaiXe INT NOT NULL,

    TrangThai NVARCHAR(30)
        DEFAULT N'HoatDong'
        CHECK(
            TrangThai IN
            (
                N'HoatDong',
                N'BaoDuong',
                N'NgungHoatDong'
            )
        ),

    CONSTRAINT FK_Xe_LoaiXe
        FOREIGN KEY(MaLoaiXe)
        REFERENCES LoaiXe(MaLoaiXe)
);

-- 7. TUYEN DUONG

CREATE TABLE TuyenDuong(
    MaTuyen INT IDENTITY(1,1) PRIMARY KEY,

    DiemDi NVARCHAR(100) NOT NULL,

    DiemDen NVARCHAR(100) NOT NULL,

    QuangDuong FLOAT
        CHECK(QuangDuong > 0),

    GiaCoBan DECIMAL(18,2)
        CHECK(GiaCoBan > 0)
);

-- 8. CHUYEN XE

CREATE TABLE ChuyenXe(
    MaChuyen INT IDENTITY(1,1) PRIMARY KEY,

    MaTuyen INT NOT NULL,

    MaXe INT NOT NULL,

    GioKhoiHanh DATETIME NOT NULL,

    GioDenDuKien DATETIME,

    TongSoGhe INT
        CHECK(TongSoGhe > 0),

    SoGheConLai INT
        CHECK(SoGheConLai >= 0),

    GiaVe DECIMAL(18,2)
        CHECK(GiaVe > 0),

    TrangThai NVARCHAR(30)
        DEFAULT N'SapKhoiHanh'
        CHECK(
            TrangThai IN
            (
                N'SapKhoiHanh',
                N'DangChay',
                N'HoanThanh',
                N'Huy'
            )
        ),

    CONSTRAINT CK_GioChuyen
        CHECK(GioDenDuKien > GioKhoiHanh),

    CONSTRAINT FK_ChuyenXe_Tuyen
        FOREIGN KEY(MaTuyen)
        REFERENCES TuyenDuong(MaTuyen),

    CONSTRAINT FK_ChuyenXe_Xe
        FOREIGN KEY(MaXe)
        REFERENCES Xe(MaXe)
);

-- 9. PHAN CONG

CREATE TABLE PhanCong(
    MaPC INT IDENTITY(1,1) PRIMARY KEY,

    MaChuyen INT NOT NULL,

    MaTaiXe INT NOT NULL,

    MaPhuXe INT NULL,

    NgayPhanCong DATETIME
        DEFAULT GETDATE(),

    CONSTRAINT FK_PC_Chuyen
        FOREIGN KEY(MaChuyen)
        REFERENCES ChuyenXe(MaChuyen),

    CONSTRAINT FK_PC_TaiXe
        FOREIGN KEY(MaTaiXe)
        REFERENCES NhanVien(MaNV),

    CONSTRAINT FK_PC_PhuXe
        FOREIGN KEY(MaPhuXe)
        REFERENCES NhanVien(MaNV),

    CONSTRAINT UQ_TaiXe_Chuyen
        UNIQUE(MaChuyen, MaTaiXe)
);

-- 10. VE

CREATE TABLE Ve(
    MaVe INT IDENTITY(1,1) PRIMARY KEY,

    MaKH INT NOT NULL,

    MaChuyen INT NOT NULL,

    MaNVLapVe INT NULL,

    SoGhe INT NOT NULL,

    NgayDat DATETIME
        DEFAULT GETDATE(),

    TrangThai NVARCHAR(30)
        DEFAULT N'ChoThanhToan'
        CHECK(
            TrangThai IN
            (
                N'ChoThanhToan',
                N'DaThanhToan',
                N'DaHuy'
            )
        ),

    CONSTRAINT FK_Ve_KhachHang
        FOREIGN KEY(MaKH)
        REFERENCES KhachHang(MaKH),

    CONSTRAINT FK_Ve_ChuyenXe
        FOREIGN KEY(MaChuyen)
        REFERENCES ChuyenXe(MaChuyen),

    CONSTRAINT FK_Ve_NhanVien
        FOREIGN KEY(MaNVLapVe)
        REFERENCES NhanVien(MaNV),

    CONSTRAINT UQ_Ghe_Chuyen
        UNIQUE(MaChuyen, SoGhe)
);

-- 11. THANH TOAN

CREATE TABLE ThanhToan(
    MaTT INT IDENTITY(1,1) PRIMARY KEY,

    MaVe INT NOT NULL,

    MaNVXacNhan INT NULL,

    SoTien DECIMAL(18,2)
        NOT NULL,

    PhuongThuc NVARCHAR(30)
        CHECK(
            PhuongThuc IN
            (
                N'TienMat',
                N'ChuyenKhoan',
                N'ViDienTu'
            )
        ),

    MaGiaoDich VARCHAR(100),

    NgayThanhToan DATETIME
        DEFAULT GETDATE(),

    TrangThai NVARCHAR(20)
        DEFAULT N'ThanhCong'
        CHECK(
            TrangThai IN
            (
                N'ThanhCong',
                N'ThatBai'
            )
        ),

    CONSTRAINT FK_ThanhToan_Ve
        FOREIGN KEY(MaVe)
        REFERENCES Ve(MaVe),

    CONSTRAINT FK_ThanhToan_NV
        FOREIGN KEY(MaNVXacNhan)
        REFERENCES NhanVien(MaNV)
);

-- 12. BAO DUONG XE

CREATE TABLE BaoDuongXe(
    MaBD INT IDENTITY(1,1) PRIMARY KEY,

    MaXe INT NOT NULL,

    NgayBatDau DATE,

    NgayKetThuc DATE,

    NoiDung NVARCHAR(255),

    CONSTRAINT FK_BDX_Xe
        FOREIGN KEY(MaXe)
        REFERENCES Xe(MaXe)
);

-- 13. THONG BAO

CREATE TABLE ThongBao(
    MaTB INT IDENTITY(1,1) PRIMARY KEY,

    TieuDe NVARCHAR(200),

    NoiDung NVARCHAR(MAX),

    NgayGui DATETIME
        DEFAULT GETDATE(),

    MaKH INT NULL,

    MaNV INT NULL,

    CONSTRAINT FK_TB_KH
        FOREIGN KEY(MaKH)
        REFERENCES KhachHang(MaKH),

    CONSTRAINT FK_TB_NV
        FOREIGN KEY(MaNV)
        REFERENCES NhanVien(MaNV)
);


-- INDEX CHO HỆ THỐNG QUẢN LÝ XE KHÁCH


-- 1. CHUYẾN XE

-- Tìm chuyến xe theo ngày khởi hành và trạng thái
CREATE INDEX IX_ChuyenXe_GioKhoiHanh_TrangThai
ON ChuyenXe (GioKhoiHanh, TrangThai)
INCLUDE (MaTuyen, MaXe, SoGheConLai, GiaVe);

-- Tìm chuyến xe theo tuyến đường
CREATE INDEX IX_ChuyenXe_MaTuyen_TrangThai
ON ChuyenXe (MaTuyen, TrangThai)
INCLUDE (GioKhoiHanh, GioDenDuKien, SoGheConLai, GiaVe);

-- 2. VÉ XE

-- Danh sách vé của một chuyến
CREATE INDEX IX_Ve_MaChuyen_TrangThai
ON Ve (MaChuyen, TrangThai)
INCLUDE (SoGhe, MaKH, NgayDat);

-- Lịch sử đặt vé của khách hàng
CREATE INDEX IX_Ve_MaKH_NgayDat
ON Ve (MaKH, NgayDat DESC)
INCLUDE (MaChuyen, SoGhe, TrangThai);

-- Thống kê vé do nhân viên lập
CREATE INDEX IX_Ve_MaNVLapVe
ON Ve (MaNVLapVe)
INCLUDE (MaChuyen, NgayDat);

-- 3. THANH TOÁN

-- Báo cáo doanh thu theo thời gian
CREATE INDEX IX_ThanhToan_NgayThanhToan_TrangThai
ON ThanhToan (NgayThanhToan, TrangThai)
INCLUDE (SoTien, PhuongThuc);

-- Thống kê theo phương thức thanh toán
CREATE INDEX IX_ThanhToan_PhuongThuc_TrangThai
ON ThanhToan (PhuongThuc, TrangThai)
INCLUDE (SoTien);

-- 4. PHÂN CÔNG

-- Xem lịch làm việc tài xế
CREATE INDEX IX_PhanCong_MaTaiXe_NgayPhanCong
ON PhanCong (MaTaiXe, NgayPhanCong DESC)
INCLUDE (MaChuyen, MaPhuXe);

-- 5. NHÂN VIÊN

-- Lọc nhân viên theo vai trò
CREATE INDEX IX_NhanVien_MaVaiTro
ON NhanVien (MaVaiTro)
INCLUDE (HoTen, SDT, Email);


-- 6. XE

-- Điều phối xe theo trạng thái
CREATE INDEX IX_Xe_TrangThai_MaLoaiXe
ON Xe (TrangThai, MaLoaiXe)
INCLUDE (BienSo);


-- VIEW

-- 1 CHUYẾN XE ĐẦY ĐỦ THÔNG TIN
-- Dùng cho: màn hình tìm chuyến, bán vé, lịch chạy

CREATE OR ALTER VIEW vw_ChuyenXeDayDu
AS
SELECT
    cx.MaChuyen,
    cx.GioKhoiHanh,
    cx.GioDenDuKien,
    cx.TrangThai         AS TrangThaiChuyen,
    cx.TongSoGhe,
    cx.SoGheConLai,
    cx.GiaVe,

    td.MaTuyen,
    td.DiemDi,
    td.DiemDen,
    td.QuangDuong,

    x.MaXe,
    x.BienSo,
    x.TrangThai          AS TrangThaiXe,

    lx.TenLoaiXe,
    lx.SoGheMacDinh,

    -- Tài xế
    nv.HoTen             AS TenTaiXe,
    nv.SDT               AS SDTTaiXe,

    -- Phụ xe
    px.HoTen             AS TenPhuXe

FROM ChuyenXe cx
JOIN TuyenDuong td  ON td.MaTuyen  = cx.MaTuyen
JOIN Xe         x   ON x.MaXe      = cx.MaXe
JOIN LoaiXe     lx  ON lx.MaLoaiXe = x.MaLoaiXe
LEFT JOIN PhanCong pc ON pc.MaChuyen = cx.MaChuyen
LEFT JOIN NhanVien nv  ON nv.MaNV    = pc.MaTaiXe
LEFT JOIN NhanVien px  ON px.MaNV    = pc.MaPhuXe;
GO



-- 2 VÉ ĐẦY ĐỦ THÔNG TIN
-- Dùng cho: tra cứu vé, in vé, lịch sử đặt chỗ

CREATE OR ALTER VIEW vw_VeDayDu
AS
SELECT
    v.MaVe,
    v.SoGhe,
    v.NgayDat,
    v.TrangThai          AS TrangThaiVe,

    -- Khách hàng
    kh.MaKH,
    kh.HoTen             AS TenKhachHang,
    kh.SDT               AS SDTKhachHang,
    kh.CCCD              AS CCCDKhachHang,

    -- Chuyến xe
    cx.MaChuyen,
    cx.GioKhoiHanh,
    cx.GioDenDuKien,
    cx.GiaVe,

    -- Tuyến
    td.DiemDi,
    td.DiemDen,

    -- Xe
    x.BienSo,
    lx.TenLoaiXe,

    -- Nhân viên lập vé
    nv.HoTen             AS TenNVLapVe

FROM Ve v
JOIN KhachHang  kh  ON kh.MaKH     = v.MaKH
JOIN ChuyenXe   cx  ON cx.MaChuyen = v.MaChuyen
JOIN TuyenDuong td  ON td.MaTuyen  = cx.MaTuyen
JOIN Xe         x   ON x.MaXe      = cx.MaXe
JOIN LoaiXe     lx  ON lx.MaLoaiXe = x.MaLoaiXe
LEFT JOIN NhanVien nv ON nv.MaNV   = v.MaNVLapVe;
GO

--3 THANH TOÁN ĐẦY ĐỦ THÔNG TIN
-- Dùng cho: đối soát, báo cáo thu ngân


CREATE OR ALTER VIEW vw_ThanhToanDayDu
AS
SELECT
    tt.MaTT,
    tt.SoTien,
    tt.PhuongThuc,
    tt.MaGiaoDich,
    tt.NgayThanhToan,
    tt.TrangThai         AS TrangThaiTT,

    -- Vé
    v.MaVe,
    v.SoGhe,

    -- Khách hàng
    kh.HoTen             AS TenKhachHang,
    kh.SDT               AS SDTKhachHang,

    -- Chuyến
    cx.GioKhoiHanh,
    td.DiemDi,
    td.DiemDen,

    -- Nhân viên xác nhận thanh toán
    nv.HoTen             AS TenNVXacNhan

FROM ThanhToan tt
JOIN Ve         v   ON v.MaVe      = tt.MaVe
JOIN KhachHang  kh  ON kh.MaKH     = v.MaKH
JOIN ChuyenXe   cx  ON cx.MaChuyen = v.MaChuyen
JOIN TuyenDuong td  ON td.MaTuyen  = cx.MaTuyen
LEFT JOIN NhanVien nv ON nv.MaNV   = tt.MaNVXacNhan;
GO


-- 4 TỔNG HỢP DOANH THU THEO NGÀY
-- Dùng cho: dashboard báo cáo tài chính
  
CREATE OR ALTER VIEW vw_DoanhThuTheoNgay
AS
SELECT
    CAST(NgayThanhToan AS DATE)  AS Ngay,
    PhuongThuc,
    COUNT(*)                     AS SoGiaoDich,
    SUM(SoTien)                  AS TongDoanhThu,
    AVG(SoTien)                  AS DoanhThuTrungBinh
FROM ThanhToan
WHERE TrangThai = N'ThanhCong'
GROUP BY
    CAST(NgayThanhToan AS DATE),
    PhuongThuc;
GO


-- 5 TÌNH TRẠNG GHẾ THEO CHUYẾN
-- Dùng cho: sơ đồ ghế, bán vé thời gian thực

CREATE OR ALTER VIEW vw_TinhTrangGhe
AS
SELECT
    cx.MaChuyen,
    cx.GioKhoiHanh,
    td.DiemDi,
    td.DiemDen,
    x.BienSo,
    lx.TenLoaiXe,
    cx.TongSoGhe,
    cx.SoGheConLai,
    cx.TongSoGhe - cx.SoGheConLai   AS SoGheDaDat,
    CASE
        WHEN cx.SoGheConLai = 0 THEN N'HetVe'
        WHEN cx.SoGheConLai <= 5 THEN N'GanHet'
        ELSE N'ConVe'
    END                              AS TinhTrangVe
FROM ChuyenXe cx
JOIN TuyenDuong td  ON td.MaTuyen  = cx.MaTuyen
JOIN Xe         x   ON x.MaXe      = cx.MaXe
JOIN LoaiXe     lx  ON lx.MaLoaiXe = x.MaLoaiXe
WHERE cx.TrangThai IN (N'SapKhoiHanh', N'DangChay');
GO



-- 6 LỊCH PHÂN CÔNG TÀI XẾ
-- Dùng cho: quản lý điều phối, xem lịch cá nhân tài xế

CREATE OR ALTER VIEW vw_LichPhanCong
AS
SELECT
    pc.MaPC,
    pc.NgayPhanCong,

    -- Chuyến
    cx.MaChuyen,
    cx.GioKhoiHanh,
    cx.GioDenDuKien,
    cx.TrangThai         AS TrangThaiChuyen,

    -- Tuyến
    td.DiemDi,
    td.DiemDen,

    -- Xe
    x.BienSo,
    lx.TenLoaiXe,

    -- Tài xế
    tx.MaNV              AS MaTaiXe,
    tx.HoTen             AS TenTaiXe,
    tx.SDT               AS SDTTaiXe,

    -- Phụ xe
    px.MaNV              AS MaPhuXe,
    px.HoTen             AS TenPhuXe

FROM PhanCong pc
JOIN ChuyenXe   cx  ON cx.MaChuyen = pc.MaChuyen
JOIN TuyenDuong td  ON td.MaTuyen  = cx.MaTuyen
JOIN Xe         x   ON x.MaXe      = cx.MaXe
JOIN LoaiXe     lx  ON lx.MaLoaiXe = x.MaLoaiXe
JOIN NhanVien   tx  ON tx.MaNV     = pc.MaTaiXe
LEFT JOIN NhanVien px ON px.MaNV   = pc.MaPhuXe;
GO

-- 7 THỐNG KÊ CHUYẾN XE THEO TUYẾN
-- Dùng cho: phân tích hiệu quả kinh doanh

CREATE OR ALTER VIEW vw_ThongKeChuyenTheoTuyen
AS
SELECT
    td.MaTuyen,
    td.DiemDi,
    td.DiemDen,
    td.QuangDuong,
    td.GiaCoBan,
    COUNT(cx.MaChuyen)          AS TongSoChuyen,
    SUM(CASE WHEN cx.TrangThai = N'HoanThanh' THEN 1 ELSE 0 END)  AS ChuyenHoanThanh,
    SUM(CASE WHEN cx.TrangThai = N'Huy'       THEN 1 ELSE 0 END)  AS ChuyenHuy,
    AVG(cx.GiaVe)               AS GiaVeTrungBinh
FROM TuyenDuong td
LEFT JOIN ChuyenXe cx ON cx.MaTuyen = td.MaTuyen
GROUP BY
    td.MaTuyen,
    td.DiemDi,
    td.DiemDen,
    td.QuangDuong,
    td.GiaCoBan;
GO

-- 8 THỐNG KÊ NHÂN VIÊN BÁN VÉ
-- Dùng cho: đánh giá hiệu suất nhân viên

CREATE OR ALTER VIEW vw_ThongKeNhanVienBanVe
AS
SELECT
    nv.MaNV,
    nv.HoTen,
    vt.TenVaiTro,
    COUNT(v.MaVe)               AS TongVeDaLap,
    SUM(tt.SoTien)              AS TongDoanhThu,
    COUNT(DISTINCT v.MaChuyen)  AS SoChuyenPhucVu
FROM NhanVien nv
JOIN VaiTro vt ON vt.MaVaiTro = nv.MaVaiTro
LEFT JOIN Ve v  ON v.MaNVLapVe  = nv.MaNV
LEFT JOIN ThanhToan tt
    ON  tt.MaVe      = v.MaVe
    AND tt.TrangThai = N'ThanhCong'
GROUP BY
    nv.MaNV,
    nv.HoTen,
    vt.TenVaiTro;
GO

  --  STORED PROCEDURE

CREATE PROC sp_DatVe
(
    @MaKH INT,
    @MaChuyen INT,
    @SoGhe INT
)
AS
BEGIN
    BEGIN TRY
        BEGIN TRAN

        IF NOT EXISTS(
            SELECT 1
            FROM ChuyenXe
            WHERE MaChuyen=@MaChuyen
        )
        BEGIN
            RAISERROR(N'Không tồn tại chuyến xe',16,1);
            ROLLBACK TRAN;
            RETURN;
        END

        IF EXISTS(
            SELECT 1
            FROM Ve
            WHERE MaChuyen=@MaChuyen
              AND SoGhe=@SoGhe
              AND TrangThai<>N'DaHuy'
        )
        BEGIN
            RAISERROR(N'Ghế đã được đặt',16,1);
            ROLLBACK TRAN;
            RETURN;
        END

        INSERT INTO Ve
        (
            MaKH,
            MaChuyen,
            SoGhe
        )
        VALUES
        (
            @MaKH,
            @MaChuyen,
            @SoGhe
        );

        UPDATE ChuyenXe
        SET SoGheConLai = SoGheConLai - 1
        WHERE MaChuyen=@MaChuyen;

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        ROLLBACK TRAN;
        THROW;
    END CATCH
END
GO


CREATE PROC sp_HuyVe
(
    @MaVe INT
)
AS
BEGIN
    DECLARE @MaChuyen INT;

    IF EXISTS(
        SELECT 1
        FROM Ve
        WHERE MaVe=@MaVe
          AND TrangThai=N'DaHuy'
    )
    BEGIN
        RAISERROR(N'Vé đã hủy trước đó',16,1);
        RETURN;
    END

    SELECT @MaChuyen=MaChuyen
    FROM Ve
    WHERE MaVe=@MaVe;

    UPDATE Ve
    SET TrangThai=N'DaHuy'
    WHERE MaVe=@MaVe;

    UPDATE ChuyenXe
    SET SoGheConLai=SoGheConLai+1
    WHERE MaChuyen=@MaChuyen;
END
GO



CREATE PROC sp_ThanhToanVe
(
    @MaVe INT,
    @SoTien DECIMAL(18,2),
    @PhuongThuc NVARCHAR(30),
    @MaNV INT
)
AS
BEGIN
    IF EXISTS(
        SELECT 1
        FROM Ve
        WHERE MaVe=@MaVe
          AND TrangThai=N'DaThanhToan'
    )
    BEGIN
        RAISERROR(N'Vé đã thanh toán',16,1);
        RETURN;
    END

    INSERT INTO ThanhToan
    (
        MaVe,
        SoTien,
        PhuongThuc,
        MaNVXacNhan
    )
    VALUES
    (
        @MaVe,
        @SoTien,
        @PhuongThuc,
        @MaNV
    );

    UPDATE Ve
    SET TrangThai=N'DaThanhToan'
    WHERE MaVe=@MaVe;
END
GO



CREATE PROC sp_PhanCongTaiXe
(
    @MaChuyen INT,
    @MaTaiXe INT,
    @MaPhuXe INT
)
AS
BEGIN

    -- Kiểm tra tài xế

    IF NOT EXISTS
    (
        SELECT 1
        FROM NhanVien nv
        JOIN VaiTro vt
            ON nv.MaVaiTro = vt.MaVaiTro
        WHERE nv.MaNV = @MaTaiXe
        AND vt.TenVaiTro = N'TaiXe'
    )
    BEGIN
        RAISERROR(N'Nhân viên được chọn không phải tài xế',16,1);
        RETURN;
    END

    -- Kiểm tra phụ xe

    IF NOT EXISTS
    (
        SELECT 1
        FROM NhanVien nv
        JOIN VaiTro vt
            ON nv.MaVaiTro = vt.MaVaiTro
        WHERE nv.MaNV = @MaPhuXe
        AND vt.TenVaiTro = N'PhuXe'
    )
    BEGIN
        RAISERROR(N'Nhân viên được chọn không phải phụ xe',16,1);
        RETURN;
    END

    INSERT INTO PhanCong
    (
        MaChuyen,
        MaTaiXe,
        MaPhuXe
    )
    VALUES
    (
        @MaChuyen,
        @MaTaiXe,
        @MaPhuXe
    );

    PRINT N'Phân công thành công';

END
GO



CREATE PROC sp_TraCuuChuyenXe
(
    @DiemDi NVARCHAR(100),
    @DiemDen NVARCHAR(100)
)
AS
BEGIN

    SELECT
        cx.MaChuyen,
        td.DiemDi,
        td.DiemDen,
        cx.GioKhoiHanh,
        cx.GioDenDuKien,
        cx.GiaVe,
        cx.SoGheConLai,
        cx.TrangThai
    FROM ChuyenXe cx
    INNER JOIN TuyenDuong td
        ON cx.MaTuyen = td.MaTuyen
    WHERE td.DiemDi = @DiemDi
    AND td.DiemDen = @DiemDen
    AND cx.TrangThai <> N'Huy';

END
GO


CREATE PROC sp_DoanhThuTheoNgay
(
    @Ngay DATE
)
AS
BEGIN

    SELECT
        COUNT(*) AS SoGiaoDich,
        SUM(SoTien) AS TongDoanhThu
    FROM ThanhToan
    WHERE CAST(NgayThanhToan AS DATE) = @Ngay
    AND TrangThai = N'ThanhCong';

END
GO

--  TRIGGER

-- 1. KIỂM TRA SỐ GHẾ HỢP LỆ


CREATE TRIGGER trg_KiemTraSoGhe
ON Ve
AFTER INSERT
AS
BEGIN

    IF EXISTS
    (
        SELECT 1
        FROM inserted i
        JOIN ChuyenXe cx
            ON i.MaChuyen = cx.MaChuyen
        WHERE i.SoGhe > cx.TongSoGhe
           OR i.SoGhe <= 0
    )
    BEGIN
        RAISERROR
        (
            N'Số ghế không hợp lệ',
            16,
            1
        );

        ROLLBACK TRANSACTION;
    END

END
GO


-- 2. XE CHUYỂN SANG BẢO DƯỠNG


CREATE TRIGGER trg_BaoDuongXe
ON BaoDuongXe
AFTER INSERT
AS
BEGIN

    UPDATE Xe
    SET TrangThai = N'BaoDuong'
    WHERE MaXe IN
    (
        SELECT MaXe
        FROM inserted
    );

END
GO

  -- 3. THANH TOÁN THÀNH CÔNG -> CẬP NHẬT VÉ


CREATE TRIGGER trg_CapNhatTrangThaiVe
ON ThanhToan
AFTER INSERT
AS
BEGIN

    UPDATE Ve
    SET TrangThai = N'DaThanhToan'
    WHERE MaVe IN
    (
        SELECT MaVe
        FROM inserted
        WHERE TrangThai = N'ThanhCong'
    );

END
GO
