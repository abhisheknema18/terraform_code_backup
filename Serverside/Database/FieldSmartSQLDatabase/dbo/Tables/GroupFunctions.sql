CREATE TABLE [dbo].[GroupFunctions] (
    [GroupCode]    VARCHAR (10) NOT NULL,
    [FunctionCode] VARCHAR (6)  NOT NULL,
    CONSTRAINT [PK_GROUPFUNCTIONS] PRIMARY KEY CLUSTERED ([GroupCode] ASC, [FunctionCode] ASC)
);

