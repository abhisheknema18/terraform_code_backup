CREATE TABLE [dbo].[ReportScript] (
    [ReportId]     INT NOT NULL,
    [ScriptCodeID] INT NOT NULL,
    CONSTRAINT [PK_REPORTSCRIPT] PRIMARY KEY CLUSTERED ([ReportId] ASC, [ScriptCodeID] ASC)
);

