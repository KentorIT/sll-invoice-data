--
-- Copyright (c) 2013 SLL. <http://sll.se>
--
-- This file is part of Invoice-Data.
--
--     Invoice-Data is free software: you can redistribute it and/or modify
--     it under the terms of the GNU Lesser General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--
--     Invoice-Data is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU Lesser General Public License for more details.
--
--     You should have received a copy of the GNU Lesser General Public License
--     along with Invoice-Data.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
--

DROP TABLE IF EXISTS `invoice_data_event_discount_item`;
CREATE TABLE `invoice_data_event_discount_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `discount_in_percentage` int(11) NOT NULL,
  `order_of_discount` int(11) NOT NULL,
  `event_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6C0DF29B8B18D8` (`event_id`),
  CONSTRAINT `FK6C0DF29B8B18D8` FOREIGN KEY (`event_id`) REFERENCES `invoice_data_event` (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `invoice_data_event_reference_item`;
CREATE TABLE `invoice_data_event_reference_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `qty` int(11) NOT NULL,
  `reference_item_id` varchar(255) NOT NULL,
  `discount_item_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK562AC6AF2A7105A5` (`discount_item_id`),
  CONSTRAINT `FK562AC6AF2A7105A5` FOREIGN KEY (`discount_item_id`) REFERENCES `invoice_data_event_discount_item` (`id`)
) ENGINE=InnoDB;
