--
--  Copyright (c) 2013 SLL <http://sll.se/>
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--        http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.

/**
 * CREATE DATABASE 
 */

USE [master]
GO

/****** Object:  Database [invoice-data]    Script Date: 10/16/2013 17:04:45 ******/
IF  EXISTS (SELECT name FROM sys.databases WHERE name = N'invoice-data')
DROP DATABASE [invoice-data]
GO

USE [master]
GO

/****** Object:  Database [invoice-data]    Script Date: 10/16/2013 17:04:45 ******/
CREATE DATABASE [invoice-data] ON  PRIMARY 
( NAME = N'invoice-data')
 LOG ON 
( NAME = N'invoice-data_log')
GO

ALTER DATABASE [invoice-data] SET COMPATIBILITY_LEVEL = 100
GO

IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [invoice-data].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO

ALTER DATABASE [invoice-data] SET ANSI_NULL_DEFAULT OFF 
GO

ALTER DATABASE [invoice-data] SET ANSI_NULLS OFF 
GO

ALTER DATABASE [invoice-data] SET ANSI_PADDING OFF 
GO

ALTER DATABASE [invoice-data] SET ANSI_WARNINGS OFF 
GO

ALTER DATABASE [invoice-data] SET ARITHABORT OFF 
GO

ALTER DATABASE [invoice-data] SET AUTO_CLOSE OFF 
GO

ALTER DATABASE [invoice-data] SET AUTO_CREATE_STATISTICS ON 
GO

ALTER DATABASE [invoice-data] SET AUTO_SHRINK OFF 
GO

ALTER DATABASE [invoice-data] SET AUTO_UPDATE_STATISTICS ON 
GO

ALTER DATABASE [invoice-data] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO

ALTER DATABASE [invoice-data] SET CURSOR_DEFAULT  GLOBAL 
GO

ALTER DATABASE [invoice-data] SET CONCAT_NULL_YIELDS_NULL OFF 
GO

ALTER DATABASE [invoice-data] SET NUMERIC_ROUNDABORT OFF 
GO

ALTER DATABASE [invoice-data] SET QUOTED_IDENTIFIER OFF 
GO

ALTER DATABASE [invoice-data] SET RECURSIVE_TRIGGERS OFF 
GO

ALTER DATABASE [invoice-data] SET  DISABLE_BROKER 
GO

ALTER DATABASE [invoice-data] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO

ALTER DATABASE [invoice-data] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO

ALTER DATABASE [invoice-data] SET TRUSTWORTHY OFF 
GO

ALTER DATABASE [invoice-data] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO

ALTER DATABASE [invoice-data] SET PARAMETERIZATION SIMPLE 
GO

ALTER DATABASE [invoice-data] SET READ_COMMITTED_SNAPSHOT OFF 
GO

ALTER DATABASE [invoice-data] SET HONOR_BROKER_PRIORITY OFF 
GO

ALTER DATABASE [invoice-data] SET  READ_WRITE 
GO

ALTER DATABASE [invoice-data] SET RECOVERY FULL 
GO

ALTER DATABASE [invoice-data] SET  MULTI_USER 
GO

ALTER DATABASE [invoice-data] SET PAGE_VERIFY CHECKSUM  
GO

ALTER DATABASE [invoice-data] SET DB_CHAINING OFF 
GO

----------------------------------------------------------------------------
/**
 * CREATE TABLES
 */

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data]    Script Date: 10/16/2013 17:07:07 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data]') AND type in (N'U'))
DROP TABLE [dbo].[invoice_data]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data]    Script Date: 10/16/2013 17:07:07 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

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

GO

SET ANSI_PADDING OFF
GO


USE [invoice-data]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FKC29022178FF8CD73]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_event]'))
ALTER TABLE [dbo].[invoice_data_event] DROP CONSTRAINT [FKC29022178FF8CD73]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_event]    Script Date: 10/16/2013 17:07:33 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND type in (N'U'))
DROP TABLE [dbo].[invoice_data_event]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_event]    Script Date: 10/16/2013 17:07:33 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

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

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[invoice_data_event]  WITH CHECK ADD  CONSTRAINT [FKC29022178FF8CD73] FOREIGN KEY([invoice_data_id])
REFERENCES [dbo].[invoice_data] ([id])
GO

ALTER TABLE [dbo].[invoice_data_event] CHECK CONSTRAINT [FKC29022178FF8CD73]
GO


USE [invoice-data]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK88E0C83BB8B18D8]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_event_item]'))
ALTER TABLE [dbo].[invoice_data_event_item] DROP CONSTRAINT [FK88E0C83BB8B18D8]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_event_item]    Script Date: 10/16/2013 17:07:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event_item]') AND type in (N'U'))
DROP TABLE [dbo].[invoice_data_event_item]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_event_item]    Script Date: 10/16/2013 17:07:52 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

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

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[invoice_data_event_item]  WITH CHECK ADD  CONSTRAINT [FK88E0C83BB8B18D8] FOREIGN KEY([event_id])
REFERENCES [dbo].[invoice_data_event] ([id])
GO

ALTER TABLE [dbo].[invoice_data_event_item] CHECK CONSTRAINT [FK88E0C83BB8B18D8]
GO


USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_pricelist]    Script Date: 10/16/2013 17:08:22 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist]') AND type in (N'U'))
DROP TABLE [dbo].[invoice_data_pricelist]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_pricelist]    Script Date: 10/16/2013 17:08:22 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

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
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


USE [invoice-data]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FKED35540EC367E1EB]') AND parent_object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]'))
ALTER TABLE [dbo].[invoice_data_pricelist_item] DROP CONSTRAINT [FKED35540EC367E1EB]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_pricelist_item]    Script Date: 10/16/2013 17:08:42 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]') AND type in (N'U'))
DROP TABLE [dbo].[invoice_data_pricelist_item]
GO

USE [invoice-data]
GO

/****** Object:  Table [dbo].[invoice_data_pricelist_item]    Script Date: 10/16/2013 17:08:42 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

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
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[invoice_data_pricelist_item]  WITH CHECK ADD  CONSTRAINT [FKED35540EC367E1EB] FOREIGN KEY([price_list_id])
REFERENCES [dbo].[invoice_data_pricelist] ([id])
GO

ALTER TABLE [dbo].[invoice_data_pricelist_item] CHECK CONSTRAINT [FKED35540EC367E1EB]
GO

------------------------------------------------------------------------------------------------------------------------------

/**
 * CREATE INDEXES
 */
 
 USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F66EA454A]    Script Date: 10/16/2013 17:09:21 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data]') AND name = N'PK__invoice___3213E83F66EA454A')
ALTER TABLE [dbo].[invoice_data] DROP CONSTRAINT [PK__invoice___3213E83F66EA454A]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F66EA454A]    Script Date: 10/16/2013 17:09:21 ******/
ALTER TABLE [dbo].[invoice_data] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO


USE [invoice-data]
GO

/****** Object:  Index [invoice_data_event_query_ix_1]    Script Date: 10/16/2013 17:10:04 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'invoice_data_event_query_ix_1')
DROP INDEX [invoice_data_event_query_ix_1] ON [dbo].[invoice_data_event] WITH ( ONLINE = OFF )
GO

USE [invoice-data]
GO

/****** Object:  Index [invoice_data_event_query_ix_1]    Script Date: 10/16/2013 17:10:04 ******/
CREATE NONCLUSTERED INDEX [invoice_data_event_query_ix_1] ON [dbo].[invoice_data_event] 
(
	[supplier_id] ASC,
	[pending] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [invoice_data_event_query_ix_2]    Script Date: 10/16/2013 17:11:02 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'invoice_data_event_query_ix_2')
DROP INDEX [invoice_data_event_query_ix_2] ON [dbo].[invoice_data_event] WITH ( ONLINE = OFF )
GO

USE [invoice-data]
GO

/****** Object:  Index [invoice_data_event_query_ix_2]    Script Date: 10/16/2013 17:11:02 ******/
CREATE NONCLUSTERED INDEX [invoice_data_event_query_ix_2] ON [dbo].[invoice_data_event] 
(
	[event_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [invoice_data_event_query_ix_3]    Script Date: 10/16/2013 17:11:11 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'invoice_data_event_query_ix_3')
DROP INDEX [invoice_data_event_query_ix_3] ON [dbo].[invoice_data_event] WITH ( ONLINE = OFF )
GO

USE [invoice-data]
GO

/****** Object:  Index [invoice_data_event_query_ix_3]    Script Date: 10/16/2013 17:11:11 ******/
CREATE NONCLUSTERED INDEX [invoice_data_event_query_ix_3] ON [dbo].[invoice_data_event] 
(
	[acknowledgement_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F6ABAD62E]    Script Date: 10/16/2013 17:11:20 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event]') AND name = N'PK__invoice___3213E83F6ABAD62E')
ALTER TABLE [dbo].[invoice_data_event] DROP CONSTRAINT [PK__invoice___3213E83F6ABAD62E]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F6ABAD62E]    Script Date: 10/16/2013 17:11:20 ******/
ALTER TABLE [dbo].[invoice_data_event] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F6E8B6712]    Script Date: 10/16/2013 17:11:41 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_event_item]') AND name = N'PK__invoice___3213E83F6E8B6712')
ALTER TABLE [dbo].[invoice_data_event_item] DROP CONSTRAINT [PK__invoice___3213E83F6E8B6712]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F6E8B6712]    Script Date: 10/16/2013 17:11:41 ******/
ALTER TABLE [dbo].[invoice_data_event_item] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F725BF7F6]    Script Date: 10/16/2013 17:11:55 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist]') AND name = N'PK__invoice___3213E83F725BF7F6')
ALTER TABLE [dbo].[invoice_data_pricelist] DROP CONSTRAINT [PK__invoice___3213E83F725BF7F6]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F725BF7F6]    Script Date: 10/16/2013 17:11:55 ******/
ALTER TABLE [dbo].[invoice_data_pricelist] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [UQ__invoice___F2859E4C753864A1]    Script Date: 10/16/2013 17:12:04 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist]') AND name = N'UQ__invoice___F2859E4C753864A1')
ALTER TABLE [dbo].[invoice_data_pricelist] DROP CONSTRAINT [UQ__invoice___F2859E4C753864A1]
GO

USE [invoice-data]
GO

/****** Object:  Index [UQ__invoice___F2859E4C753864A1]    Script Date: 10/16/2013 17:12:04 ******/
ALTER TABLE [dbo].[invoice_data_pricelist] ADD UNIQUE NONCLUSTERED 
(
	[service_code] ASC,
	[supplier_id] ASC,
	[valid_from] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F7908F585]    Script Date: 10/16/2013 17:12:19 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]') AND name = N'PK__invoice___3213E83F7908F585')
ALTER TABLE [dbo].[invoice_data_pricelist_item] DROP CONSTRAINT [PK__invoice___3213E83F7908F585]
GO

USE [invoice-data]
GO

/****** Object:  Index [PK__invoice___3213E83F7908F585]    Script Date: 10/16/2013 17:12:19 ******/
ALTER TABLE [dbo].[invoice_data_pricelist_item] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

USE [invoice-data]
GO

/****** Object:  Index [UQ__invoice___9A23290C7BE56230]    Script Date: 10/16/2013 17:12:29 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[invoice_data_pricelist_item]') AND name = N'UQ__invoice___9A23290C7BE56230')
ALTER TABLE [dbo].[invoice_data_pricelist_item] DROP CONSTRAINT [UQ__invoice___9A23290C7BE56230]
GO

USE [invoice-data]
GO

/****** Object:  Index [UQ__invoice___9A23290C7BE56230]    Script Date: 10/16/2013 17:12:29 ******/
ALTER TABLE [dbo].[invoice_data_pricelist_item] ADD UNIQUE NONCLUSTERED 
(
	[item_id] ASC,
	[price_list_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO






--

