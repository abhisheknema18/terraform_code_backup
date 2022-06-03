CREATE TABLE [dbo].[MonitorAlertNotes] (
    [Id]         INT           NOT NULL,
    [AlertId]    INT           NOT NULL,
    [NoteText]   VARCHAR (150) NOT NULL,
    [UserCode]   VARCHAR (10)  NOT NULL,
    [CreateDate] INT           NOT NULL,
    [CreateTime] VARCHAR (6)   NOT NULL,
    CONSTRAINT [PK_MonitorAlertNotes] PRIMARY KEY CLUSTERED ([Id] ASC)
);

