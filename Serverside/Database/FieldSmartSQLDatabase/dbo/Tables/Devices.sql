CREATE TABLE [dbo].[Devices] (
    [DeviceID]    VARCHAR (255) NOT NULL,
    [Status]      VARCHAR (1)   NULL,
    [DeviceClass] VARCHAR (40)  NULL,
    [Comments]    VARCHAR (255) NULL,
    CONSTRAINT [PK_DEVICES] PRIMARY KEY CLUSTERED ([DeviceID] ASC)
);

