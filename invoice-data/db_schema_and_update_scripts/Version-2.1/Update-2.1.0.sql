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
ALTER TABLE `invoice_data_event` DROP COLUMN `pending`;
ALTER TABLE `invoice_data` ADD COLUMN `versionField` bigint(20) NOT NULL;

DROP INDEX invoice_data_event_query_ix_2 ON invoice_data_event;
ALTER TABLE `invoice_data` MODIFY COLUMN `pending` bit(1) DEFAULT TRUE NULL;
UPDATE `invoice_data` SET `pending`=NULL where `pending`=false;