CREATE TABLE [dbo].[ValidationType] (
    [ValidationType] VARCHAR (15)  NOT NULL,
    [ValidationDesc] VARCHAR (100) NULL,
    CONSTRAINT [PK_VALIDATIONTYPE] PRIMARY KEY CLUSTERED ([ValidationType] ASC)
);

