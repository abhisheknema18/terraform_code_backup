CREATE TABLE [dbo].[SoftVersions] (
    [Version]  VARCHAR (255) NOT NULL,
    [ProgCode] VARCHAR (4)  NOT NULL,
    [AllowUse] INT          NULL,
    CONSTRAINT [PK_SOFTVERSIONS] PRIMARY KEY CLUSTERED ([ProgCode] ASC, [Version] ASC)
);

