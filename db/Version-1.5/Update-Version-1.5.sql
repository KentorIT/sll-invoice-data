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

# Remove old index och update it with new one
DROP INDEX invoice_data_event_query_ix_1 ON invoice_data_event;
DROP INDEX invoice_data_event_query_ix_3 ON invoice_data_event;

CREATE INDEX invoice_data_event_query_ix_1 ON invoice_data_event (
  `supplier_id`,
  `payment_responsible`,
  `pending`,
  `start_time`);
  
CREATE INDEX invoice_data_event_query_ix_3 ON invoice_data_event (
  `acknowledgement_id`,
  `pending`);
  
CREATE INDEX invoice_data_event_query_ix_4 ON invoice_data_event (
  `supplier_id`,
  `payment_responsible`,
  `acknowledgement_id`,
  `pending`);
