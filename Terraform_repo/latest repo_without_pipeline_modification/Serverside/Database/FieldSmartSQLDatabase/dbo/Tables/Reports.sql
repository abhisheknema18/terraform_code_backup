CREATE TABLE [dbo].[Reports] (
    [ReportID]       INT          NOT NULL,
    [ReportName]     VARCHAR (60) NOT NULL,
    [ReportTemplate] VARCHAR (40) NOT NULL,
    [AvailFlag]      VARCHAR (2)  NOT NULL,
    [ReportClass]    VARCHAR (10) NULL,
    CONSTRAINT [PK_REPORTS] PRIMARY KEY CLUSTERED ([ReportID] ASC)
);

