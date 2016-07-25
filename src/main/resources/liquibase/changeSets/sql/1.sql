-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u1
-- http://www.phpmyadmin.net
--
-- Generation Time: Jul 23, 2016 at 01:36 AM
-- Server version: 10.0.25-MariaDB-0+deb8u1
-- PHP Version: 5.6.22-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Table structure for table `hibernate_sequence`
--

CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hibernate_sequence`
--

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64),
  (64);

-- --------------------------------------------------------

--
-- Table structure for table `Asset`
--

CREATE TABLE IF NOT EXISTS `Asset` (
  `id` int(11) NOT NULL,
  `availabilityEnd` time DEFAULT NULL,
  `availabilityStart` time DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `needsApproval` bit(1) DEFAULT NULL,
  `photo` longblob,
  `tenant_id` int(11) NOT NULL,
  `assetType_id` int(11) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `AssetType`
--

CREATE TABLE IF NOT EXISTS `AssetType` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Asset_Tag`
--

CREATE TABLE IF NOT EXISTS `Asset_Tag` (
  `assets_id` int(11) NOT NULL,
  `tags_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Notification`
--

CREATE TABLE IF NOT EXISTS `Notification` (
  `id` bigint(20) NOT NULL,
  `body` varchar(2048) DEFAULT NULL,
  `creation` datetime DEFAULT NULL,
  `notif_read` bit(1) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Permission`
--

CREATE TABLE IF NOT EXISTS `Permission` (
  `id` int(11) NOT NULL,
  `descriptor` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Permission_User`
--

CREATE TABLE IF NOT EXISTS `Permission_User` (
  `permissions_id` int(11) NOT NULL,
  `users_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Permission_UserGroup`
--

CREATE TABLE IF NOT EXISTS `Permission_UserGroup` (
  `permissions_id` int(11) NOT NULL,
  `userGroups_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Property`
--

CREATE TABLE IF NOT EXISTS `Property` (
  `id` int(11) NOT NULL,
  `propertyKey` int(11) DEFAULT NULL,
  `propertyValue` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Reservation`
--

CREATE TABLE IF NOT EXISTS `Reservation` (
  `id` int(11) NOT NULL,
  `date` date DEFAULT NULL,
  `dateCreated` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `timeEnd` time DEFAULT NULL,
  `timeStart` time DEFAULT NULL,
  `title` varchar(32) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL,
  `asset_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `ReservationDecision`
--

CREATE TABLE IF NOT EXISTS `ReservationDecision` (
  `id` int(11) NOT NULL,
  `approved` bit(1) NOT NULL,
  `comment` varchar(512) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL,
  `reservation_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `userGroup_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `ReservationField`
--

CREATE TABLE IF NOT EXISTS `ReservationField` (
  `id` int(11) NOT NULL,
  `description` longtext,
  `largeField` bit(1) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL,
  `assetType_id` int(11) DEFAULT NULL,
  `Order` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `ReservationFieldEntry`
--

CREATE TABLE IF NOT EXISTS `ReservationFieldEntry` (
  `id` int(11) NOT NULL,
  `content` longtext,
  `tenant_id` int(11) NOT NULL,
  `field_id` int(11) DEFAULT NULL,
  `reservation_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Tag`
--

CREATE TABLE IF NOT EXISTS `Tag` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL,
  `assetType_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Tenant`
--

CREATE TABLE IF NOT EXISTS `Tenant` (
  `id` int(11) NOT NULL,
  `slug` varchar(32) NOT NULL,
  `subscriptionId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `User`
--

CREATE TABLE IF NOT EXISTS `User` (
  `id` int(11) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `password` tinyblob,
  `phoneNumber` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `UserGroup`
--

CREATE TABLE IF NOT EXISTS `UserGroup` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tenant_id` int(11) NOT NULL,
  `parent_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `User_UserGroup`
--

CREATE TABLE IF NOT EXISTS `User_UserGroup` (
  `users_id` int(11) NOT NULL,
  `userGroups_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Asset`
--
ALTER TABLE `Asset`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_Asset_Tenant` (`tenant_id`), ADD KEY `FK_Asset_AssetType` (`assetType_id`), ADD KEY `FK_Asset_UserGroup` (`owner_id`);

--
-- Indexes for table `AssetType`
--
ALTER TABLE `AssetType`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_AssetType_Tenant` (`tenant_id`);

--
-- Indexes for table `Asset_Tag`
--
ALTER TABLE `Asset_Tag`
 ADD KEY `FK_Asset_Tag_Tag` (`tags_id`), ADD KEY `FK_Asset_Tag_Asset` (`assets_id`);

--
-- Indexes for table `Notification`
--
ALTER TABLE `Notification`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_Notification_Tenant` (`tenant_id`), ADD KEY `FK_Notification_User` (`user_id`);

--
-- Indexes for table `Permission`
--
ALTER TABLE `Permission`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_Permission_Tenant` (`tenant_id`);

--
-- Indexes for table `Permission_User`
--
ALTER TABLE `Permission_User`
 ADD KEY `FK_Permission_User_User` (`users_id`), ADD KEY `FK_Permission_User_Permission` (`permissions_id`);

--
-- Indexes for table `Permission_UserGroup`
--
ALTER TABLE `Permission_UserGroup`
 ADD KEY `FK_Permission_UserGroup_UserGroup` (`userGroups_id`), ADD KEY `FK_Permission_UserGroup_Permission` (`permissions_id`);

--
-- Indexes for table `Property`
--
ALTER TABLE `Property`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_Property_Tenant` (`tenant_id`);

--
-- Indexes for table `Reservation`
--
ALTER TABLE `Reservation`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_Reservation_Tenant` (`tenant_id`), ADD KEY `FK_Reservation_Asset` (`asset_id`), ADD KEY `FK_Reservation_User` (`user_id`);

--
-- Indexes for table `ReservationDecision`
--
ALTER TABLE `ReservationDecision`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_ReservationDecision_Tenant` (`tenant_id`), ADD KEY `FK_ReservationDecision_Reservation` (`reservation_id`), ADD KEY `FK_ReservationDecision_User` (`user_id`), ADD KEY `FK_ReservationDecision_UserGroup` (`userGroup_id`);

--
-- Indexes for table `ReservationField`
--
ALTER TABLE `ReservationField`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_ReservationField_Tenant` (`tenant_id`), ADD KEY `FK_ReservationField_AssetType` (`assetType_id`);

--
-- Indexes for table `ReservationFieldEntry`
--
ALTER TABLE `ReservationFieldEntry`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_ReservationFieldEntry_Tenant` (`tenant_id`), ADD KEY `FK_ReservationFieldEntry_ReservationField` (`field_id`), ADD KEY `FK_ReservationFieldEntry_Reservation` (`reservation_id`);

--
-- Indexes for table `Tag`
--
ALTER TABLE `Tag`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_Tag_Tenant` (`tenant_id`), ADD KEY `FK_Tag_AssetType` (`assetType_id`);

--
-- Indexes for table `Tenant`
--
ALTER TABLE `Tenant`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `UK_slug` (`slug`), ADD UNIQUE KEY `UK_subscriptionId` (`subscriptionId`);

--
-- Indexes for table `User`
--
ALTER TABLE `User`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_User_Tenant` (`tenant_id`);

--
-- Indexes for table `UserGroup`
--
ALTER TABLE `UserGroup`
 ADD PRIMARY KEY (`id`), ADD KEY `FK_UserGroup_Tenant` (`tenant_id`), ADD KEY `FK_UserGroup_UserGroup` (`parent_id`);

--
-- Indexes for table `User_UserGroup`
--
ALTER TABLE `User_UserGroup`
 ADD KEY `FK_User_UserGroup_UserGroup` (`userGroups_id`), ADD KEY `FK_User_UserGroup_User` (`users_id`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Asset`
--
ALTER TABLE `Asset`
ADD CONSTRAINT `FK_Asset_AssetType` FOREIGN KEY (`assetType_id`) REFERENCES `AssetType` (`id`),
ADD CONSTRAINT `FK_Asset_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`),
ADD CONSTRAINT `FK_Asset_UserGroup` FOREIGN KEY (`owner_id`) REFERENCES `UserGroup` (`id`);

--
-- Constraints for table `AssetType`
--
ALTER TABLE `AssetType`
ADD CONSTRAINT `FK_AssetType_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `Asset_Tag`
--
ALTER TABLE `Asset_Tag`
ADD CONSTRAINT `FK_Asset_Tag_Asset` FOREIGN KEY (`assets_id`) REFERENCES `Asset` (`id`),
ADD CONSTRAINT `FK_Asset_Tag_Tag` FOREIGN KEY (`tags_id`) REFERENCES `Tag` (`id`);

--
-- Constraints for table `Notification`
--
ALTER TABLE `Notification`
ADD CONSTRAINT `FK_Notification_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`),
ADD CONSTRAINT `FK_Notification_User` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`);

--
-- Constraints for table `Permission`
--
ALTER TABLE `Permission`
ADD CONSTRAINT `FK_Permission_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `Permission_User`
--
ALTER TABLE `Permission_User`
ADD CONSTRAINT `FK_Permission_User_Permission` FOREIGN KEY (`permissions_id`) REFERENCES `Permission` (`id`),
ADD CONSTRAINT `FK_Permission_User_User` FOREIGN KEY (`users_id`) REFERENCES `User` (`id`);

--
-- Constraints for table `Permission_UserGroup`
--
ALTER TABLE `Permission_UserGroup`
ADD CONSTRAINT `FK_Permission_UserGroup_Permission` FOREIGN KEY (`permissions_id`) REFERENCES `Permission` (`id`),
ADD CONSTRAINT `FK_Permission_UserGroup_UserGroup` FOREIGN KEY (`userGroups_id`) REFERENCES `UserGroup` (`id`);

--
-- Constraints for table `Property`
--
ALTER TABLE `Property`
ADD CONSTRAINT `FK_Property_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `Reservation`
--
ALTER TABLE `Reservation`
ADD CONSTRAINT `FK_Reservation_Asset` FOREIGN KEY (`asset_id`) REFERENCES `Asset` (`id`),
ADD CONSTRAINT `FK_Reservation_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`),
ADD CONSTRAINT `FK_Reservation_User` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`);

--
-- Constraints for table `ReservationDecision`
--
ALTER TABLE `ReservationDecision`
ADD CONSTRAINT `FK_ReservationDecision_Reservation` FOREIGN KEY (`reservation_id`) REFERENCES `Reservation` (`id`),
ADD CONSTRAINT `FK_ReservationDecision_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`),
ADD CONSTRAINT `FK_ReservationDecision_User` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
ADD CONSTRAINT `FK_ReservationDecision_UserGroup` FOREIGN KEY (`userGroup_id`) REFERENCES `UserGroup` (`id`);

--
-- Constraints for table `ReservationField`
--
ALTER TABLE `ReservationField`
ADD CONSTRAINT `FK_ReservationField_AssetType` FOREIGN KEY (`assetType_id`) REFERENCES `AssetType` (`id`),
ADD CONSTRAINT `FK_ReservationField_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `ReservationFieldEntry`
--
ALTER TABLE `ReservationFieldEntry`
ADD CONSTRAINT `FK_ReservationFieldEntry_Reservation` FOREIGN KEY (`reservation_id`) REFERENCES `Reservation` (`id`),
ADD CONSTRAINT `FK_ReservationFieldEntry_ReservationField` FOREIGN KEY (`field_id`) REFERENCES `ReservationField` (`id`),
ADD CONSTRAINT `FK_ReservationFieldEntry_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `Tag`
--
ALTER TABLE `Tag`
ADD CONSTRAINT `FK_Tag_AssetType` FOREIGN KEY (`assetType_id`) REFERENCES `AssetType` (`id`),
ADD CONSTRAINT `FK_Tag_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `User`
--
ALTER TABLE `User`
ADD CONSTRAINT `FK_User_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`);

--
-- Constraints for table `UserGroup`
--
ALTER TABLE `UserGroup`
ADD CONSTRAINT `FK_UserGroup_Tenant` FOREIGN KEY (`tenant_id`) REFERENCES `Tenant` (`id`),
ADD CONSTRAINT `FK_UserGroup_UserGroup` FOREIGN KEY (`parent_id`) REFERENCES `UserGroup` (`id`);

--
-- Constraints for table `User_UserGroup`
--
ALTER TABLE `User_UserGroup`
ADD CONSTRAINT `FK_User_UserGroup_User` FOREIGN KEY (`users_id`) REFERENCES `User` (`id`),
ADD CONSTRAINT `FK_User_UserGroup_UserGroup` FOREIGN KEY (`userGroups_id`) REFERENCES `UserGroup` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
