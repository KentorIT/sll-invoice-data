-- MySQL dump 10.13  Distrib 5.6.14, for Linux (x86_64)
--
-- Host: localhost    Database: vsfunderlag
-- ------------------------------------------------------
-- Server version 5.6.14-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `invoice_data`
--

DROP TABLE IF EXISTS `invoice_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(64) NOT NULL,
  `created_timestamp` datetime NOT NULL,
  `end_date` date NOT NULL,
  `payment_responsible` varchar(64) NOT NULL,
  `start_date` date NOT NULL,
  `supplier_id` varchar(64) NOT NULL,
  `total_amount` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_data_event`
--

DROP TABLE IF EXISTS `invoice_data_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_data_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `acknowledged_by` varchar(64) NOT NULL,
  `acknowledged_time` datetime NOT NULL,
  `acknowledgement_id` varchar(64) NOT NULL,
  `created_timestamp` datetime NOT NULL,
  `credit` bit(1) DEFAULT NULL,
  `credited` bit(1) DEFAULT NULL,
  `end_time` datetime NOT NULL,
  `event_id` varchar(64) NOT NULL,
  `healthcare_commission` varchar(64) NOT NULL,
  `healthcare_facility` varchar(64) NOT NULL,
  `payment_responsible` varchar(64) NOT NULL,
  `pending` bit(1) DEFAULT NULL,
  `ref_contract_id` varchar(64) NOT NULL,
  `service_code` varchar(64) NOT NULL,
  `start_time` datetime NOT NULL,
  `supplier_id` varchar(64) NOT NULL,
  `supplier_name` longtext NOT NULL,
  `invoice_data_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `invoice_data_event_query_ix_2` (`event_id`),
  KEY `invoice_data_event_query_ix_1` (`supplier_id`,`pending`),
  KEY `invoice_data_event_query_ix_3` (`acknowledgement_id`),
  KEY `FKC29022178FF8CD73` (`invoice_data_id`),
  CONSTRAINT `FKC29022178FF8CD73` FOREIGN KEY (`invoice_data_id`) REFERENCES `invoice_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_data_event_item`
--

DROP TABLE IF EXISTS `invoice_data_event_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_data_event_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `item_id` varchar(64) NOT NULL,
  `price` decimal(8,2) DEFAULT NULL,
  `qty` decimal(8,2) DEFAULT NULL,
  `event_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK88E0C83BB8B18D8` (`event_id`),
  CONSTRAINT `FK88E0C83BB8B18D8` FOREIGN KEY (`event_id`) REFERENCES `invoice_data_event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_data_pricelist`
--

DROP TABLE IF EXISTS `invoice_data_pricelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_data_pricelist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_code` varchar(64) NOT NULL,
  `supplier_id` varchar(64) NOT NULL,
  `valid_from` date NOT NULL,
  `supplier_name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_code` (`service_code`,`supplier_id`,`valid_from`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_data_pricelist_item`
--

DROP TABLE IF EXISTS `invoice_data_pricelist_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_data_pricelist_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_id` varchar(64) NOT NULL,
  `price` decimal(8,2) NOT NULL,
  `price_list_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_id` (`item_id`,`price_list_id`),
  KEY `FKED35540EC367E1EB` (`price_list_id`),
  CONSTRAINT `FKED35540EC367E1EB` FOREIGN KEY (`price_list_id`) REFERENCES `invoice_data_pricelist` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1841 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-11-14 11:10:00
