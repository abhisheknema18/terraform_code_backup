CREATE TABLE [dbo].[CarryThroughRes] (
    [ReturnID]   INT           NOT NULL,
    [FieldName]  VARCHAR (40)  NOT NULL,
    [FieldValue] VARCHAR (100) NULL,
    CONSTRAINT [PK_CARRYTHROUGHRES] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [FieldName] ASC)
);

