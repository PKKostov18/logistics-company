USE [master]
GO
/****** Object:  Database [LogisticsCompany]    Script Date: 2/12/2026 4:32:45 AM ******/
CREATE DATABASE [LogisticsCompany]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'LogisticsCompany', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQL\DATA\LogisticsCompany.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'LogisticsCompany_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQL\DATA\LogisticsCompany_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [LogisticsCompany] SET COMPATIBILITY_LEVEL = 170
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [LogisticsCompany].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [LogisticsCompany] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [LogisticsCompany] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [LogisticsCompany] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [LogisticsCompany] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [LogisticsCompany] SET ARITHABORT OFF 
GO
ALTER DATABASE [LogisticsCompany] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [LogisticsCompany] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [LogisticsCompany] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [LogisticsCompany] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [LogisticsCompany] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [LogisticsCompany] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [LogisticsCompany] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [LogisticsCompany] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [LogisticsCompany] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [LogisticsCompany] SET  DISABLE_BROKER 
GO
ALTER DATABASE [LogisticsCompany] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [LogisticsCompany] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [LogisticsCompany] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [LogisticsCompany] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [LogisticsCompany] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [LogisticsCompany] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [LogisticsCompany] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [LogisticsCompany] SET RECOVERY FULL 
GO
ALTER DATABASE [LogisticsCompany] SET  MULTI_USER 
GO
ALTER DATABASE [LogisticsCompany] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [LogisticsCompany] SET DB_CHAINING OFF 
GO
ALTER DATABASE [LogisticsCompany] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [LogisticsCompany] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [LogisticsCompany] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [LogisticsCompany] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [LogisticsCompany] SET OPTIMIZED_LOCKING = OFF 
GO
EXEC sys.sp_db_vardecimal_storage_format N'LogisticsCompany', N'ON'
GO
ALTER DATABASE [LogisticsCompany] SET QUERY_STORE = ON
GO
ALTER DATABASE [LogisticsCompany] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [LogisticsCompany]
GO
/****** Object:  Table [dbo].[companies]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[companies](
	[company_id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[vat_number] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[company_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_l0qs8okshb0nou6jumjnhoppr] UNIQUE NONCLUSTERED 
(
	[vat_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[customers]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[customers](
	[customer_id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NULL,
	[name] [varchar](255) NOT NULL,
	[phone_number] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[customer_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_6v6x92wb400iwh6unf5rwiim4] UNIQUE NONCLUSTERED 
(
	[phone_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[employees]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[employees](
	[hire_date] [date] NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[office_id] [bigint] NULL,
	[employee_type] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[employee_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[offices]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[offices](
	[company_id] [bigint] NOT NULL,
	[office_id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NOT NULL,
	[address] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[office_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[packages]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[packages](
	[price] [numeric](10, 2) NOT NULL,
	[weight_kg] [float] NOT NULL,
	[assigned_courier_id] [bigint] NULL,
	[created_at] [datetimeoffset](6) NOT NULL,
	[destination_office_id] [bigint] NULL,
	[package_id] [bigint] IDENTITY(1,1) NOT NULL,
	[received_at] [datetimeoffset](6) NULL,
	[receiver_customer_id] [bigint] NOT NULL,
	[registered_by_employee_id] [bigint] NOT NULL,
	[sender_customer_id] [bigint] NOT NULL,
	[tracking_number] [varchar](20) NOT NULL,
	[delivery_address] [varchar](255) NULL,
	[delivery_type] [varchar](255) NOT NULL,
	[description] [varchar](255) NULL,
	[status] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[package_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_eoq3w5c9d428h0jlkr2h9wa47] UNIQUE NONCLUSTERED 
(
	[tracking_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[roles]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[roles](
	[role_id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[role_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_ofx66keruapi6vyqpv6f2or37] UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[users]    Script Date: 2/12/2026 4:32:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[users](
	[created_at] [datetime2](6) NOT NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[role_id] [bigint] NOT NULL,
	[phone_number] [varchar](20) NULL,
	[first_name] [varchar](50) NOT NULL,
	[last_name] [varchar](50) NOT NULL,
	[username] [varchar](50) NOT NULL,
	[email] [varchar](100) NOT NULL,
	[password] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_6dotkott2kjsp8vw4d0m25fb7] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_r43af9ap4edm43mmtq01oddj6] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Index [UK_euat1oase6eqv195jvb71a93s]    Script Date: 2/12/2026 4:32:46 AM ******/
CREATE UNIQUE NONCLUSTERED INDEX [UK_euat1oase6eqv195jvb71a93s] ON [dbo].[customers]
(
	[user_id] ASC
)
WHERE ([user_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[customers]  WITH CHECK ADD  CONSTRAINT [FKrh1g1a20omjmn6kurd35o3eit] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[customers] CHECK CONSTRAINT [FKrh1g1a20omjmn6kurd35o3eit]
GO
ALTER TABLE [dbo].[employees]  WITH CHECK ADD  CONSTRAINT [FKcelobek54amw1bedldhp6f98r] FOREIGN KEY([office_id])
REFERENCES [dbo].[offices] ([office_id])
GO
ALTER TABLE [dbo].[employees] CHECK CONSTRAINT [FKcelobek54amw1bedldhp6f98r]
GO
ALTER TABLE [dbo].[employees]  WITH CHECK ADD  CONSTRAINT [FKhhbl7vhnb6o6h5u71nwdcme6q] FOREIGN KEY([employee_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[employees] CHECK CONSTRAINT [FKhhbl7vhnb6o6h5u71nwdcme6q]
GO
ALTER TABLE [dbo].[offices]  WITH CHECK ADD  CONSTRAINT [FKqfab8bwemwg53e2aeli3fvr4j] FOREIGN KEY([company_id])
REFERENCES [dbo].[companies] ([company_id])
GO
ALTER TABLE [dbo].[offices] CHECK CONSTRAINT [FKqfab8bwemwg53e2aeli3fvr4j]
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD  CONSTRAINT [FKe4n1ggcat5g10i4tv3dcurdqw] FOREIGN KEY([sender_customer_id])
REFERENCES [dbo].[customers] ([customer_id])
GO
ALTER TABLE [dbo].[packages] CHECK CONSTRAINT [FKe4n1ggcat5g10i4tv3dcurdqw]
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD  CONSTRAINT [FKe5h7tl6jagyqw9tq3lkdtbn43] FOREIGN KEY([registered_by_employee_id])
REFERENCES [dbo].[employees] ([employee_id])
GO
ALTER TABLE [dbo].[packages] CHECK CONSTRAINT [FKe5h7tl6jagyqw9tq3lkdtbn43]
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD  CONSTRAINT [FKfvyuwryylcnyqceou1sh79tuh] FOREIGN KEY([destination_office_id])
REFERENCES [dbo].[offices] ([office_id])
GO
ALTER TABLE [dbo].[packages] CHECK CONSTRAINT [FKfvyuwryylcnyqceou1sh79tuh]
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD  CONSTRAINT [FKkej8eoc202pk4khpynbbq2kqp] FOREIGN KEY([assigned_courier_id])
REFERENCES [dbo].[employees] ([employee_id])
GO
ALTER TABLE [dbo].[packages] CHECK CONSTRAINT [FKkej8eoc202pk4khpynbbq2kqp]
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD  CONSTRAINT [FKt47ori3kosuqg02jv1f3sgj1o] FOREIGN KEY([receiver_customer_id])
REFERENCES [dbo].[customers] ([customer_id])
GO
ALTER TABLE [dbo].[packages] CHECK CONSTRAINT [FKt47ori3kosuqg02jv1f3sgj1o]
GO
ALTER TABLE [dbo].[users]  WITH CHECK ADD  CONSTRAINT [FKp56c1712k691lhsyewcssf40f] FOREIGN KEY([role_id])
REFERENCES [dbo].[roles] ([role_id])
GO
ALTER TABLE [dbo].[users] CHECK CONSTRAINT [FKp56c1712k691lhsyewcssf40f]
GO
ALTER TABLE [dbo].[employees]  WITH CHECK ADD CHECK  (([employee_type]='COURIER' OR [employee_type]='OFFICE'))
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD CHECK  (([delivery_type]='TO_ADDRESS' OR [delivery_type]='TO_OFFICE'))
GO
ALTER TABLE [dbo].[packages]  WITH CHECK ADD CHECK  (([status]='RECEIVED' OR [status]='DELIVERED' OR [status]='IN_TRANSIT' OR [status]='REGISTERED'))
GO
ALTER TABLE [dbo].[roles]  WITH CHECK ADD CHECK  (([name]='ADMIN' OR [name]='COURIER' OR [name]='OFFICE_EMPLOYEE' OR [name]='CUSTOMER'))
GO
USE [master]
GO
ALTER DATABASE [LogisticsCompany] SET  READ_WRITE 
GO
