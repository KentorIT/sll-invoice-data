/**
 * Copyright (c) 2013 SLL. <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice-Data is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Invoice-Data is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Invoice-Data.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package se.sll.invoicedata.utils

object Headers {

	// HTTP Headers
	val pingForConfiguration_Http_Headers = Map(
      "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1:PingForConfiguration",
	  "Keep-Alive" -> "115")
	
	val registerInvoiceData_Http_headers = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:sll:invoicedata:RegisterInvoiceDataResponder:1:RegisterInvoiceData",
	  "Keep-Alive" -> "115")  
	  
	val createInvoiceData_Http_Headers = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:sll:invoicedata:CreateInvoiceDataResponder:1:CreateInvoiceData",
	  "Keep-Alive" -> "115")
	  
	val viewInvoiceData_Http_Headers = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:sll:invoicedata:ViewInvoiceDataResponder:1:ViewInvoiceData",
	  "Keep-Alive" -> "115")
	  
	val listInvoiceData_Http_Headers = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:sll:invoicedata:ListInvoiceDataResponder:1:ListInvoiceData",
	  "Keep-Alive" -> "115")
	  
	val getInvoiceData_Http_Headers = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:sll:invoicedata:GetInvoiceDataResponder:1:GetInvoiceData",
	  "Keep-Alive" -> "115")
	
  }