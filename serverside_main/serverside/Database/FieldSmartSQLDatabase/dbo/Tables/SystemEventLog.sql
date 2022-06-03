CREATE TABLE [dbo].[SystemEventLog] (
    [EventID]       INT            NOT NULL,
    [EventDate]     INT            NOT NULL,
    [EventTime]     VARCHAR (6)    NOT NULL,
    [EventCategory] VARCHAR (20)   NOT NULL,
    [EventType]     VARCHAR (10)   NOT NULL,
    [EventSummary]  VARCHAR (80)   NOT NULL,
    [EventDesc]     VARCHAR (1000) NULL,
    [SourceSystem]  VARCHAR (15)   NOT NULL,
    [Application]   VARCHAR (30)   NOT NULL,
    [Severity]      INT            NULL,
    [UserCode]      VARCHAR (32)   NULL,
    [ErrorCode]     VARCHAR (20)   NULL,
    [EventStatus]   VARCHAR (30)   NOT NULL,
    [ReturnID]      INT            NULL,
    [WorkOrderNo]   VARCHAR (20)   NULL,
    [DistrictCode]  VARCHAR (4)    NULL,
    CONSTRAINT [PK_SystemEventLog] PRIMARY KEY CLUSTERED ([EventID] ASC)
);

