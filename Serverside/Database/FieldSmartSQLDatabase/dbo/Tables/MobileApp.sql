CREATE TABLE [dbo].[MobileApp] (
    [AppCode]     VARCHAR (4)  NOT NULL,
    [AppCodeDesc] VARCHAR (30) NOT NULL,
    CONSTRAINT [PK_MobileApp] PRIMARY KEY CLUSTERED ([AppCode] ASC)
);

