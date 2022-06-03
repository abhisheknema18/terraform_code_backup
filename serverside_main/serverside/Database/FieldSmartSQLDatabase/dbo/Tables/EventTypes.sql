CREATE TABLE [dbo].[EventTypes] (
    [EventID]   INT           NOT NULL,
    [EventName] VARCHAR (255) NOT NULL,
    CONSTRAINT [PK_EventTypes] PRIMARY KEY CLUSTERED ([EventID] ASC)
);

