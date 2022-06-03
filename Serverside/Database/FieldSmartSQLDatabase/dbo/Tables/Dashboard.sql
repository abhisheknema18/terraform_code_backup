CREATE TABLE [dbo].[Dashboard] (
    [ItemID]      INT          NOT NULL,
    [UserCode]    VARCHAR (32) NOT NULL,
    [Item]        IMAGE        NOT NULL,
    [Title]       VARCHAR (40) NOT NULL,
    [MonitorType] VARCHAR (20) NOT NULL,
    [DisplayType] VARCHAR (10) NOT NULL,
    CONSTRAINT [PK_Dashboard] PRIMARY KEY CLUSTERED ([ItemID] ASC, [UserCode] ASC)
);

