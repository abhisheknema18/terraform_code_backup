CREATE TABLE [dbo].[ScriptVersions] (
    [ScriptID]      INT           NOT NULL,
    [ScriptCodeID]  INT           NOT NULL,
    [VersionNumber] INT           NOT NULL,
    [DateCreated]   INT           NOT NULL,
    [UserCreated]   VARCHAR (10)  NULL,
    [ItemCount]     INT           NULL,
    [OnlineStatus]  INT           NULL,
    [LastModDate]   INT           NULL,
    [LastModUser]   VARCHAR (10)  NULL,
    [OnlineDate]    INT           NULL,
    [AddText]       VARCHAR (100) NULL,
    CONSTRAINT [PK_SCRIPTVERSIONS] PRIMARY KEY CLUSTERED ([ScriptID] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_SCRIPTVERSION_ONLINEDATE]
    ON [dbo].[ScriptVersions]([OnlineDate] ASC);


GO
CREATE NONCLUSTERED INDEX [I_SCRIPTVERSION_ONLINESTATUS]
    ON [dbo].[ScriptVersions]([OnlineStatus] ASC);


GO
CREATE NONCLUSTERED INDEX [I_SCRIPTVERSIONS_SCRIPTCODEID]
    ON [dbo].[ScriptVersions]([ScriptCodeID] ASC);

