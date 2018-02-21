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
package se.sll.invoicedata

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import se.sll.invoicedata.utils.Conf
import se.sll.invoicedata.utils.Headers

object Scenarios {

    val rampUpTimeSecs = 10
    val minWaitMs      = 500 milliseconds
    val maxWaitMs      = 1500 milliseconds

	/*
	 *	PingForConfiguration
     */	
	val scn_PingForConfiguration_Ok_Http = scenario("Ping invoicedata-web-app expected result OK scenario")
      .during(Conf.testTimeSecs) {     
        exec(
          http("PingForConfiguration")
            .post("/invoicedata-web-app/ws/pingForConfiguration/v1")
            .headers(Headers.pingForConfiguration_Http_Headers)
            .body(RawFileBody("data/PingForConfiguration_OK.xml")).asXML
            .check(status.is(200))
            .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
            .check(xpath("//pr:PingForConfigurationResponse", List("pr" -> "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1")).count.is(1))
          )
        .pause(minWaitMs, maxWaitMs)
    }
	def uuid = java.util.UUID.randomUUID.toString
	// RegisterInvoiceData OK scenario without discount item
	val scn_RegisterInvoiceData_No_DiscountItem_OK_Http = scenario("RegisterInvoiceData OK scenario without discount item")
	  .during(Conf.testTimeSecs) { 		
	    exec(session => {session.set("uuid", java.util.UUID.randomUUID.toString)})
		.exec(
	      http("RegisterInvoiceData_No_DiscountItem")
	         .post("/invoicedata-web-app/ws/registerInvoiceData/v1")
			 .headers(Headers.registerInvoiceData_Http_headers)
		     .body(ElFileBody("data/RegisterInvoiceData_No_DiscountItem_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:RegisterInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:RegisterInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// RegisterInvoiceData OK scenario with discount Item
	val scn_RegisterInvoiceData_With_DiscountItem_OK_Http = scenario("RegisterInvoiceData OK scenario with discount item")
	  .during(Conf.testTimeSecs) { 		
	    exec(session => {session.set("uuid", java.util.UUID.randomUUID.toString)})
		.exec(
	      http("RegisterInvoiceData_With_DiscountItem")
	         .post("/invoicedata-web-app/ws/registerInvoiceData/v1")
			 .headers(Headers.registerInvoiceData_Http_headers)
		     .body(RawFileBody("data/RegisterInvoiceData_With_DiscountItem_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:RegisterInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:RegisterInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// RegisterInvoiceData ERROR scenario with no payment responsible
	val scn_RegisterInvoiceData_No_PaymentResponsible_ERROR_Http = scenario("RegisterInvoiceData ERROR scenario with no payment responsible")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("RegisterInvoiceData")
	        .post("/invoicedata-web-app/ws/registerInvoiceData/v1")
			 .headers(Headers.registerInvoiceData_Http_headers)
		     .body(RawFileBody("data/RegisterInvoiceData_No_PaymentResponsible_ERROR.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:RegisterInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:RegisterInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// RegisterInvoiceData ERROR scenario with no items
	val scn_RegisterInvoiceData_No_Items_ERROR_Http = scenario("RegisterInvoiceData ERROR scenario with no items")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("RegisterInvoiceData")
	        .post("/invoicedata-web-app/ws/registerInvoiceData/v1")
			 .headers(Headers.registerInvoiceData_Http_headers)
		     .body(RawFileBody("data/RegisterInvoiceData_No_Items_ERROR.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:RegisterInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:RegisterInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// CreateInvoiceData OK scenario
	val scn_CreateInvoiceData_OK_Http = scenario("CreateInvoiceData OK scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("CreateInvoiceData")
	        .post("/invoicedata-web-app/ws/createInvoiceData/v1")
			 .headers(Headers.createInvoiceData_Http_Headers)
		     .body(RawFileBody("data/CreateInvoiceData_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:CreateInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:CreateInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// CreateInvoiceData ERROR scenario
	val scn_CreateInvoiceData_ERROR_Http = scenario("CreateInvoiceData ERROR scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("CreateInvoiceData")
	        .post("/invoicedata-web-app/ws/createInvoiceData/v1")
			 .headers(Headers.createInvoiceData_Http_Headers)
		     .body(RawFileBody("data/CreateInvoiceData_ERROR.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:CreateInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:CreateInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// ViewInvoiceData OK scenario
	val scn_ViewInvoiceData_OK_Http = scenario("ViewInvoiceData OK scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("ViewInvoiceData")
	        .post("/invoicedata-web-app/ws/viewInvoiceData/v1")
			 .headers(Headers.viewInvoiceData_Http_Headers)
		     .body(RawFileBody("data/ViewInvoiceData_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:ViewInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:ViewInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// ViewInvoiceData ERROR scenario
	val scn_ViewInvoiceData_ERROR_Http = scenario("ViewInvoiceData ERROR scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("ViewInvoiceData")
	        .post("/invoicedata-web-app/ws/viewInvoiceData/v1")
			 .headers(Headers.viewInvoiceData_Http_Headers)
		     .body(RawFileBody("data/ViewInvoiceData_ERROR.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:ViewInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:ViewInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// ListInvoiceData OK scenario
	val scn_ListInvoiceData_OK_Http = scenario("ListInvoiceData OK scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("ListInvoiceData")
	        .post("/invoicedata-web-app/ws/listInvoiceData/v1")
			 .headers(Headers.listInvoiceData_Http_Headers)
		     .body(RawFileBody("data/ListInvoiceData_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:ListInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:ListInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// ListInvoiceData ERROR scenario
	val scn_ListInvoiceData_ERROR_Http = scenario("ListInvoiceData ERROR scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("ListInvoiceData")
	        .post("/invoicedata-web-app/ws/listInvoiceData/v1")
			 .headers(Headers.listInvoiceData_Http_Headers)
		     .body(RawFileBody("data/ListInvoiceData_ERROR.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:ListInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:ListInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// GetInvoiceData OK scenario
	val scn_GetInvoiceData_OK_Http = scenario("GetInvoiceData OK scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("GetInvoiceData")
	        .post("/invoicedata-web-app/ws/getInvoiceData/v1")
			 .headers(Headers.getInvoiceData_Http_Headers)
		     .body(RawFileBody("data/GetInvoiceData_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:GetInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:GetInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// GetPendingInvoiceData OK scenario
	val scn_GetPendingInvoiceData_OK_Http = scenario("GetPendingInvoiceData OK scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("GetPendingInvoiceData")
	        .post("/invoicedata-web-app/ws/getPendingInvoiceData/v1")
			 .headers(Headers.getPendingInvoiceData_Http_Headers)
		     .body(RawFileBody("data/GetPendingInvoiceData_OK.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:GetPendingInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:GetPendingInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// GetInvoiceData ERROR scenario
	val scn_GetInvoiceData_ERROR_Http = scenario("GetInvoiceData ERROR scenario")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("GetInvoiceData")
	        .post("/invoicedata-web-app/ws/getInvoiceData/v1")
			 .headers(Headers.getInvoiceData_Http_Headers)
		     .body(RawFileBody("data/GetInvoiceData_ERROR.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:GetInvoiceDataResponse", List("pr" -> "urn:riv:sll:invoicedata:GetInvoiceDataResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
}