CREATE TABLE [dbo].[HPCWorkGroups] (
    [WorkGroupCode] VARCHAR (10) NOT NULL,
    [WorkGroupDesc] VARCHAR (40) NOT NULL,
    [EmailTo]       VARCHAR (80) NULL,
    [EmailCC]       VARCHAR (80) NULL,
    [CSV]           VARCHAR (2)  NULL,
    [WGCatId]       INT          NOT NULL,
    [Active]        INT          NULL,
    [WGClassA]      VARCHAR (80) NULL,
    [WGClassB]      VARCHAR (80) NULL,
    [ProfileID]     INT          NULL,
    CONSTRAINT [PK_HPCWORKGROUPS] PRIMARY KEY CLUSTERED ([WorkGroupCode] ASC, [WGCatId] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_HPCWORKGROUPS_WGCATID]
    ON [dbo].[HPCWorkGroups]([WGCatId] ASC);

