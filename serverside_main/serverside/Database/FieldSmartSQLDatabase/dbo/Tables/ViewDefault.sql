CREATE TABLE [dbo].[ViewDefault] (
    [UserCode] VARCHAR (12) NOT NULL,
    [ViewId]   INT          NOT NULL,
    CONSTRAINT [PK_VIEWDEFAULT] PRIMARY KEY CLUSTERED ([UserCode] ASC, [ViewId] ASC)
);

