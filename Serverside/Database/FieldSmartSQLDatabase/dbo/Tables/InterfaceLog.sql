CREATE TABLE [dbo].[InterfaceLog] (
    [ReturnID] INT           NULL,
    [LogDate]  INT           NULL,
    [LogText]  VARCHAR (100) NULL
);


GO
CREATE NONCLUSTERED INDEX [I_INTERFACELOG_RTNID]
    ON [dbo].[InterfaceLog]([ReturnID] ASC);

