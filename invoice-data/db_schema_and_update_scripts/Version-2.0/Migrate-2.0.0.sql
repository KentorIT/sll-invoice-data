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

-- Add costCenter in invoice_data_event and invoice_data
ALTER TABLE `invoice_data` ADD COLUMN `cost_center` varchar(64) NOT NULL;
ALTER TABLE `invoice_data_event` ADD COLUMN `cost_center` varchar(64) NOT NULL;

ALTER TABLE `invoice_data` ADD COLUMN `pending` bit(1) DEFAULT TRUE;
UPDATE `invoice_data` SET `pending`=false;
UPDATE `invoice_data_event` SET `cost_center`='230' WHERE `payment_responsible`='HSF';
UPDATE `invoice_data` SET `cost_center`='230' WHERE `payment_responsible`='HSF';

--INSERT INTO operation_access_config VALUES(7, 'GET_PENDING_INVOICE_DATA', 'SE2321000016-A27K', '*');
--INSERT INTO operation_access_config VALUES(6, 'GET_PENDING_INVOICE_DATA', 'SE2321000016-7P7D', '*');






