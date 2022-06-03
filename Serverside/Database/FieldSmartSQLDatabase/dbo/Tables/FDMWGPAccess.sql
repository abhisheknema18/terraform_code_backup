CREATE TABLE [dbo].[FDMWGPAccess] (
    [GroupCode]     VARCHAR (10) NOT NULL,
    [WorkgroupCode] VARCHAR (10) NOT NULL,
    CONSTRAINT [PK_FDMWGPACCESS] PRIMARY KEY CLUSTERED ([GroupCode] ASC, [WorkgroupCode] ASC)
);

