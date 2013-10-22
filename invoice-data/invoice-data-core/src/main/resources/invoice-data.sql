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

USE [master]
GO

/****** Object:  Database [sll-invoice-data]    Script Date: 10/17/2013 10:10:02 ******/
IF  EXISTS (SELECT name FROM sys.databases WHERE name = N'sll-invoice-data')
DROP DATABASE [sll-invoice-data]
GO

USE [master]
GO

/****** Object:  Database [sll-invoice-data]    Script Date: 10/17/2013 10:10:02 ******/
CREATE DATABASE [sll-invoice-data]
GO

ALTER DATABASE [sll-invoice-data] MODIFY FILE
( NAME = N'sll-invoice-data',SIZE = 524288KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )


ALTER DATABASE [sll-invoice-data] SET COMPATIBILITY_LEVEL = 100
GO

ALTER DATABASE [sll-invoice-data] SET ANSI_NULL_DEFAULT OFF 
GO

ALTER DATABASE [sll-invoice-data] SET ANSI_NULLS OFF 
GO

ALTER DATABASE [sll-invoice-data] SET ANSI_PADDING OFF 
GO

ALTER DATABASE [sll-invoice-data] SET ANSI_WARNINGS OFF 
GO

ALTER DATABASE [sll-invoice-data] SET ARITHABORT OFF 
GO

ALTER DATABASE [sll-invoice-data] SET AUTO_CLOSE OFF 
GO

ALTER DATABASE [sll-invoice-data] SET AUTO_CREATE_STATISTICS ON 
GO

ALTER DATABASE [sll-invoice-data] SET AUTO_SHRINK OFF 
GO

ALTER DATABASE [sll-invoice-data] SET AUTO_UPDATE_STATISTICS ON 
GO

ALTER DATABASE [sll-invoice-data] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO

ALTER DATABASE [sll-invoice-data] SET CURSOR_DEFAULT  GLOBAL 
GO

ALTER DATABASE [sll-invoice-data] SET CONCAT_NULL_YIELDS_NULL OFF 
GO

ALTER DATABASE [sll-invoice-data] SET NUMERIC_ROUNDABORT OFF 
GO

ALTER DATABASE [sll-invoice-data] SET QUOTED_IDENTIFIER OFF 
GO

ALTER DATABASE [sll-invoice-data] SET RECURSIVE_TRIGGERS OFF 
GO

ALTER DATABASE [sll-invoice-data] SET  DISABLE_BROKER 
GO

ALTER DATABASE [sll-invoice-data] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO

ALTER DATABASE [sll-invoice-data] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO

ALTER DATABASE [sll-invoice-data] SET TRUSTWORTHY OFF 
GO

ALTER DATABASE [sll-invoice-data] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO

ALTER DATABASE [sll-invoice-data] SET PARAMETERIZATION SIMPLE 
GO

ALTER DATABASE [sll-invoice-data] SET READ_COMMITTED_SNAPSHOT OFF 
GO

ALTER DATABASE [sll-invoice-data] SET HONOR_BROKER_PRIORITY OFF 
GO

ALTER DATABASE [sll-invoice-data] SET  READ_WRITE 
GO

ALTER DATABASE [sll-invoice-data] SET RECOVERY FULL 
GO

ALTER DATABASE [sll-invoice-data] SET  MULTI_USER 
GO

ALTER DATABASE [sll-invoice-data] SET PAGE_VERIFY CHECKSUM  
GO

ALTER DATABASE [sll-invoice-data] SET DB_CHAINING OFF 
GO

------------------------------------------------------------------
--- Creating tables and indexes 
------------------------------------------------------------------
 

USE [sll-invoice-data]
GO
/****** Object:  Table [dbo].[invoice_data_pricelist]    Script Date: 10/17/2013 10:07:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[invoice_data_pricelist](
	[id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[service_code] [varchar](64) NOT NULL,
	[supplier_id] [varchar](64) NOT NULL,
	[valid_from] [datetime] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[service_code] ASC,
	[supplier_id] ASC,
	[valid_from] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[service_code] ASC,
	[supplier_id] ASC,
	[valid_from] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[invoice_data]    Script Date: 10/17/2013 10:07:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[invoice_data](
	[id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[created_by] [varchar](64) NOT NULL,
	[created_timestamp] [datetime] NOT NULL,
	[payment_responsible] [varchar](64) NOT NULL,
	[supplier_id] [varchar](64) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[invoice_data_event]    Script Date: 10/17/2013 10:07:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[invoice_data_event](
	[id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[acknowledged_by] [varchar](64) NOT NULL,
	[acknowledged_time] [datetime] NOT NULL,
	[acknowledgement_id] [varchar](64) NOT NULL,
	[created_timestamp] [datetime] NOT NULL,
	[credit] [tinyint] NULL,
	[credited] [tinyint] NULL,
	[end_time] [datetime] NOT NULL,
	[event_id] [varchar](64) NOT NULL,
	[healthcare_commission] [varchar](64) NOT NULL,
	[HEALTHCARE_FACILITY] [varchar](64) NOT NULL,
	[payment_responsible] [varchar](64) NOT NULL,
	[pending] [tinyint] NULL,
	[refContract_Id] [varchar](64) NOT NULL,
	[service_code] [varchar](64) NOT NULL,
	[start_time] [datetime] NOT NULL,
	[supplier_id] [varchar](64) NOT NULL,
	[supplier_name] [varchar](256) NOT NULL,
	[invoice_data_id] [numeric](19, 0) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'invoice_data_event_query_ix_1')
CREATE NONCLUSTERED INDEX [invoice_data_event_query_ix_1] ON [dbo].[invoice_data_event] 
(
	[supplier_id] ASC,
	[pending] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'invoice_data_event_query_ix_2')
CREATE NONCLUSTERED INDEX [invoice_data_event_query_ix_2] ON [dbo].[invoice_data_event] 
(
	[event_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'invoice_data_event_query_ix_3')
CREATE NONCLUSTERED INDEX [invoice_data_event_query_ix_3] ON [dbo].[invoice_data_event] 
(
	[acknowledgement_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[invoice_data_pricelist_item]    Script Date: 10/17/2013 10:07:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[invoice_data_pricelist_item](
	[id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[item_id] [varchar](64) NOT NULL,
	[price] [numeric](8, 2) NOT NULL,
	[price_list_id] [numeric](19, 0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[item_id] ASC,
	[price_list_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[item_id] ASC,
	[price_list_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[invoice_data_event_item]    Script Date: 10/17/2013 10:07:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event_item]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[invoice_data_event_item](
	[id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[description] [varchar](256) NOT NULL,
	[item_id] [varchar](64) NOT NULL,
	[price] [numeric](8, 2) NULL,
	[qty] [numeric](8, 2) NULL,
	[event_id] [numeric](19, 0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  ForeignKey [FKC29022178FF8CD73]    Script Date: 10/17/2013 10:07:43 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FKC29022178FF8CD73]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_event]'))
ALTER TABLE [dbo].[invoice_data_event]  WITH CHECK ADD  CONSTRAINT [FKC29022178FF8CD73] FOREIGN KEY([invoice_data_id])
REFERENCES [dbo].[invoice_data] ([id])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FKC29022178FF8CD73]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_event]'))
ALTER TABLE [dbo].[invoice_data_event] CHECK CONSTRAINT [FKC29022178FF8CD73]
GO
/****** Object:  ForeignKey [FK88E0C83BB8B18D8]    Script Date: 10/17/2013 10:07:43 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK88E0C83BB8B18D8]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_event_item]'))
ALTER TABLE [dbo].[invoice_data_event_item]  WITH CHECK ADD  CONSTRAINT [FK88E0C83BB8B18D8] FOREIGN KEY([event_id])
REFERENCES [dbo].[invoice_data_event] ([id])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK88E0C83BB8B18D8]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_event_item]'))
ALTER TABLE [dbo].[invoice_data_event_item] CHECK CONSTRAINT [FK88E0C83BB8B18D8]
GO
/****** Object:  ForeignKey [FKED35540EC367E1EB]    Script Date: 10/17/2013 10:07:43 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FKED35540EC367E1EB]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]'))
ALTER TABLE [dbo].[invoice_data_pricelist_item]  WITH CHECK ADD  CONSTRAINT [FKED35540EC367E1EB] FOREIGN KEY([price_list_id])
REFERENCES [dbo].[invoice_data_pricelist] ([id])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FKED35540EC367E1EB]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]'))
ALTER TABLE [dbo].[invoice_data_pricelist_item] CHECK CONSTRAINT [FKED35540EC367E1EB]
GO
