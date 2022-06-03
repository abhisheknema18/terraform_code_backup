CREATE TABLE [dbo].[UserLocationHistory] (
    [UserCode]     VARCHAR (32)  NOT NULL,
    [AppCode]      VARCHAR (4)   NOT NULL,
    [DeviceID]     VARCHAR (255) NOT NULL,
    [LocationDate] INT           NOT NULL,
    [LocationTime] VARCHAR (6)   NOT NULL,
    [Latitude]     VARCHAR (255) NOT NULL,
    [Longitude]    VARCHAR (255) NOT NULL,
    CONSTRAINT [PK_UserLocationHistory] PRIMARY KEY CLUSTERED ([UserCode] ASC, [AppCode] ASC, [DeviceID] ASC, [LocationDate] ASC, [LocationTime] ASC)
);

