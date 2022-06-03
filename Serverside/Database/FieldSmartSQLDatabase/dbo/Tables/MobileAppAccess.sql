CREATE TABLE [dbo].[MobileAppAccess] (
    [WorkGroupCode] VARCHAR (10) NOT NULL,
    [AppCode]       VARCHAR (4)  NOT NULL,
    CONSTRAINT [PK_MobileAppAccess] PRIMARY KEY CLUSTERED ([WorkGroupCode] ASC, [AppCode] ASC)
);

