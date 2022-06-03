CREATE TABLE [dbo].[UserActivityStatus] (
    [UserCode]     VARCHAR (32)  NOT NULL,
    [AppCode]      VARCHAR (4)   NOT NULL,
    [DeviceID]     VARCHAR (255) NOT NULL,
    [ActivityDate] INT           NOT NULL,
    [ActivityTime] VARCHAR (6)   NOT NULL,
    [ActivityType] VARCHAR (32)  NOT NULL,
    CONSTRAINT [PK_UserActivityStatus] PRIMARY KEY CLUSTERED ([UserCode] ASC, [AppCode] ASC, [DeviceID] ASC)
);

