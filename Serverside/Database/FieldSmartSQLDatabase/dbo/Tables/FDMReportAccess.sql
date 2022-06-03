CREATE TABLE [dbo].[FDMReportAccess] (
    [GroupCode] VARCHAR (10) NOT NULL,
    [ReportID]  INT          NOT NULL,
    CONSTRAINT [PK_FDMREPORTACCESS] PRIMARY KEY CLUSTERED ([GroupCode] ASC, [ReportID] ASC)
);

