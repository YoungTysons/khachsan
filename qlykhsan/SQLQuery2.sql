USE master;
GO

-- 1. XÓA DATABASE CŨ NẾU TỒN TẠI (Reset sạch sẽ)
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'QlyKhachSanDB')
BEGIN
    ALTER DATABASE QlyKhachSanDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QlyKhachSanDB;
END
GO

-- 2. TẠO DATABASE MỚI
CREATE DATABASE QlyKhachSanDB;
GO

USE QlyKhachSanDB;
GO

-- --- PHẦN 1: TẠO BẢNG ---

-- 1. Bảng NguoiDung (Đã bỏ CCCD, Email)
CREATE TABLE NguoiDung (
    MaNguoiDung INT PRIMARY KEY IDENTITY(1,1),
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL,
    HoTen NVARCHAR(100),
    SoDienThoai VARCHAR(15),
    VaiTro VARCHAR(20) DEFAULT 'KhachHang' -- 'Admin' hoặc 'KhachHang'
);
GO

-- 2. Bảng Phong
CREATE TABLE Phong (
    MaPhong INT PRIMARY KEY IDENTITY(1,1),
    SoPhong VARCHAR(10) NOT NULL UNIQUE,
    LoaiPhong NVARCHAR(50), 
    GiaPhong DECIMAL(18, 2) NOT NULL,
    TrangThai NVARCHAR(50) DEFAULT N'Trong' 
);
GO

-- 3. Bảng DatPhong
CREATE TABLE DatPhong (
    MaDatPhong INT PRIMARY KEY IDENTITY(1,1),
    MaNguoiDung INT,
    MaPhong INT,
    NgayDat DATETIME DEFAULT GETDATE(),
    NgayCheckIn DATE NOT NULL,
    NgayCheckOut DATE NOT NULL,
    TongTien DECIMAL(18, 2),
    TrangThai NVARCHAR(50) DEFAULT N'ChoDuyet',
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung),
    FOREIGN KEY (MaPhong) REFERENCES Phong(MaPhong)
);
GO

-- 4. Bảng HopDong (Đã bỏ NgayTaoHopDong, NguoiLap)
CREATE TABLE HopDong (
    MaHopDong INT PRIMARY KEY IDENTITY(1,1),
    MaDatPhong INT UNIQUE,
    NoiDungHopDong NVARCHAR(2000), 
    FOREIGN KEY (MaDatPhong) REFERENCES DatPhong(MaDatPhong)
);
GO

-- 5. Bảng PhanAnh
CREATE TABLE PhanAnh (
    MaPhanAnh INT PRIMARY KEY IDENTITY(1,1),
    MaNguoiDung INT,
    TieuDe NVARCHAR(200),
    NoiDung NVARCHAR(2000),
    NgayGui DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(50) DEFAULT N'ChuaXuLy',
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- 6. Bảng ThongBao
CREATE TABLE ThongBao (
    MaThongBao INT PRIMARY KEY IDENTITY(1,1),
    MaNguoiDung INT NULL, 
    TieuDe NVARCHAR(200),
    NoiDung NVARCHAR(2000),
    NgayTao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- --- PHẦN 2: INSERT DỮ LIỆU MẪU (3 DÒNG MỖI BẢNG) ---
-- Lưu ý: Phải có chữ N đứng trước chuỗi tiếng Việt (N'...')

-- 1. Insert NguoiDung
INSERT INTO NguoiDung (TenDangNhap, MatKhau, HoTen, SoDienThoai, VaiTro) VALUES 
('admin', '123456', N'Nguyễn Quản Trị', '0901234567', 'Admin'),
('nguyenvana', '123456', N'Nguyễn Văn A', '0912345678', 'KhachHang'),
('tranthib', '123456', N'Trần Thị B', '0987654321', 'KhachHang');

-- 2. Insert Phong
INSERT INTO Phong (SoPhong, LoaiPhong, GiaPhong, TrangThai) VALUES 
('P101', N'Phòng Đơn', 500000, N'Trong'),
('P102', N'Phòng Đôi', 800000, N'DaDat'),
('P201', N'Phòng VIP', 1500000, N'Trong');

-- 3. Insert DatPhong
-- Định dạng ngày tháng trong SQL Server mặc định là YYYY-MM-DD
INSERT INTO DatPhong (MaNguoiDung, MaPhong, NgayDat, NgayCheckIn, NgayCheckOut, TongTien, TrangThai) VALUES 
(2, 1, '2023-12-01 08:00:00', '2023-12-05', '2023-12-07', 1000000, N'ChoDuyet'),
(3, 2, '2023-12-02 09:30:00', '2023-12-10', '2023-12-12', 1600000, N'DaDuyet'),
(2, 3, '2023-11-20 10:00:00', '2023-11-25', '2023-11-26', 1500000, N'HoanThanh');

-- 4. Insert HopDong
INSERT INTO HopDong (MaDatPhong, NoiDungHopDong) VALUES 
(2, N'Hợp đồng thuê phòng ngắn hạn P102. Bên A cam kết giữ gìn tài sản...'),
(3, N'Hợp đồng thuê phòng VIP P201. Đã thanh toán đầy đủ...'),
(1, N'Dự thảo hợp đồng cho P101 (Chờ ký)'); 

-- 5. Insert PhanAnh
INSERT INTO PhanAnh (MaNguoiDung, TieuDe, NoiDung, NgayGui, TrangThai) VALUES 
(2, N'Máy lạnh hỏng', N'Phòng P101 máy lạnh không mát, đề nghị kiểm tra.', '2023-12-05 14:00:00', N'ChuaXuLy'),
(3, N'Thái độ nhân viên', N'Nhân viên lễ tân rất nhiệt tình, cảm ơn khách sạn.', '2023-12-12 08:00:00', N'DaXuLy'),
(2, N'Hỏi về dịch vụ giặt ủi', N'Khách sạn có dịch vụ giặt ủi lấy ngay không?', '2023-12-06 09:00:00', N'DaXuLy');

-- 6. Insert ThongBao
INSERT INTO ThongBao (MaNguoiDung, TieuDe, NoiDung, NgayTao) VALUES 
(NULL, N'Bảo trì hệ thống điện', N'Khách sạn sẽ ngắt điện từ 2h-4h sáng ngày mai để bảo trì.', '2023-12-15 08:00:00'),
(2, N'Xác nhận đặt phòng', N'Đơn đặt phòng P101 của quý khách đang được xử lý.', '2023-12-01 08:05:00'),
(3, N'Chúc mừng sinh nhật', N'Khách sạn gửi tặng quý khách voucher giảm giá 10%.', '2023-12-10 07:00:00');
GO