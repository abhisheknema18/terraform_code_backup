CREATE TABLE [dbo].[FDMResEdit] (
    [GroupCode]      VARCHAR (10) NOT NULL,
    [EditAccessCode] VARCHAR (2)  NOT NULL,
    CONSTRAINT [PK_FDMRESEDIT] PRIMARY KEY CLUSTERED ([GroupCode] ASC, [EditAccessCode] ASC)
);

