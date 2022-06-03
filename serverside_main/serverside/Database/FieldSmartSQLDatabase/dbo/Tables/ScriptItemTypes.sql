CREATE TABLE [dbo].[ScriptItemTypes] (
    [ItemType] VARCHAR (40) NOT NULL,
    [OrderNo]  INT          NOT NULL,
    CONSTRAINT [PK_SCRIPTITEMTYPES] PRIMARY KEY CLUSTERED ([ItemType] ASC, [OrderNo] ASC)
);

