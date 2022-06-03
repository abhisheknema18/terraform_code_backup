CREATE TABLE [dbo].[AccessGroups] (
    [GroupCode]   VARCHAR (10) NOT NULL,
    [GroupDesc]   VARCHAR (50) NOT NULL,
    [ProgCode]    VARCHAR (4)  NOT NULL,
    [HPCWGPAdmin] INT          NULL,
    CONSTRAINT [PK_ACCESSGROUPS] PRIMARY KEY CLUSTERED ([GroupCode] ASC)
);

