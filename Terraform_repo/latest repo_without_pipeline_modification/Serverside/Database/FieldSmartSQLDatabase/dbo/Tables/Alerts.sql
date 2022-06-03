CREATE TABLE [dbo].[Alerts] (
    [AlertID]        INT           NOT NULL,
    [UserCode]       VARCHAR (10)  NULL,
    [SysWorkGroup]   VARCHAR (10)  NULL,
    [ReturnID]       INT           NULL,
    [SequenceNumber] INT           NULL,
    [Equipno]        VARCHAR (30)  NULL,
    [AlertScope]     CHAR (1)      NULL,
    [AlertType]      VARCHAR (15)  NULL,
    [AlertDate]      INT           NULL,
    [AlertText]      VARCHAR (50)  NULL,
    [Status]         CHAR (1)      NULL,
    [CheckUser]      VARCHAR (10)  NULL,
    [CheckDate]      INT           NULL,
    [CheckText]      VARCHAR (150) NULL,
    CONSTRAINT [PK_ALERTS] PRIMARY KEY CLUSTERED ([AlertID] ASC)
);

