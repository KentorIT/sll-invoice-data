# Remove old index och update it with new one
DROP INDEX invoice_data_event_query_ix_3 ON invoice_data_event;

CREATE INDEX invoice_data_event_query_ix_3 ON invoice_data_event (
  `acknowledgement_id`,
  `pending`);
  
CREATE INDEX invoice_data_event_query_ix_4 ON invoice_data_event (
  `supplier_id`,
  `payment_responsible`,
  `acknowledgement_id`,
  `pending`);
