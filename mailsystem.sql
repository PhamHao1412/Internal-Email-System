-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th2 16, 2024 lúc 09:07 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `mailsystem`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `email`
--

CREATE TABLE `email` (
  `id` int(11) NOT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `sender` varchar(30) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `email`
--

INSERT INTO `email` (`id`, `subject`, `sender`, `timestamp`) VALUES
(37, 'test bcc ', 'hao1412', '2024-01-15 05:25:38'),
(38, 'test bcc 2', 'hao1412', '2024-01-15 05:30:31');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reply`
--

CREATE TABLE `reply` (
  `id` int(11) NOT NULL,
  `email_id` int(11) DEFAULT NULL,
  `sender` varchar(30) DEFAULT NULL,
  `recipient` varchar(30) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `noti_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `reply`
--

INSERT INTO `reply` (`id`, `email_id`, `sender`, `recipient`, `content`, `timestamp`, `noti_id`) VALUES
(96, 37, 'hao1412', 'hanh', 'test 1', '2024-01-15 05:25:38', 72),
(97, 37, 'hao1412', 'khaile', 'test 1', '2024-01-15 05:25:38', 73),
(98, 38, 'hao1412', 'hanh', ' test 1', '2024-01-15 05:30:31', 74),
(99, 38, 'hao1412', 'khaile', ' test 1', '2024-01-15 05:30:31', 75),
(100, 38, 'hao1412', 'tuquyen', ' test 1', '2024-01-15 05:30:31', 76);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user`
--

CREATE TABLE `user` (
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `fullname` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `user`
--

INSERT INTO `user` (`email`, `password`, `fullname`, `gender`) VALUES
('hanh', '123', 'Hoàng Anh', 'Nữ'),
('hao1412', '123', 'Phạm Anh Hào', 'Nam'),
('khaile', '123', 'Khải Lê', 'Nam'),
('tuquyen', '123', 'Tú Quyên', 'Nữ');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `email`
--
ALTER TABLE `email`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sender` (`sender`);

--
-- Chỉ mục cho bảng `reply`
--
ALTER TABLE `reply`
  ADD PRIMARY KEY (`id`),
  ADD KEY `email_id` (`email_id`),
  ADD KEY `fk_reply_notify` (`noti_id`);

--
-- Chỉ mục cho bảng `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`email`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `email`
--
ALTER TABLE `email`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT cho bảng `reply`
--
ALTER TABLE `reply`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `email`
--
ALTER TABLE `email`
  ADD CONSTRAINT `email_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `user` (`email`);

--
-- Các ràng buộc cho bảng `reply`
--
ALTER TABLE `reply`
  ADD CONSTRAINT `fk_reply_notify` FOREIGN KEY (`noti_id`) REFERENCES `notify` (`id`),
  ADD CONSTRAINT `reply_ibfk_1` FOREIGN KEY (`email_id`) REFERENCES `email` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
