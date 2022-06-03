CREATE TABLE [dbo].[ViewDefn] (
    [ViewID]     INT          NOT NULL,
    [FieldName]  VARCHAR (20) NOT NULL,
    [FieldValue] VARCHAR (32) NOT NULL,
    CONSTRAINT [PK_VIEWDEFN] PRIMARY KEY CLUSTERED ([ViewID] ASC, [FieldName] ASC, [FieldValue] ASC)
);

